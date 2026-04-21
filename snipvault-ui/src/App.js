import { useState, useEffect, useRef, useCallback } from "react";

const API = "http://localhost:8080";

// ── STYLES ────────────────────────────────────────────────────────────────────
const css = `
  @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:ital,wght@0,300;0,400;0,600;0,700;1,400&family=Orbitron:wght@400;700;900&display=swap');

  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

  :root {
    --bg:        #080b0f;
    --bg2:       #0d1117;
    --bg3:       #111820;
    --border:    #1e2d3d;
    --border2:   #243447;
    --green:     #00ff88;
    --green2:    #00cc6a;
    --green-dim: #00ff8822;
    --cyan:      #00d4ff;
    --cyan-dim:  #00d4ff18;
    --red:       #ff4466;
    --yellow:    #ffd700;
    --text:      #c9d1d9;
    --text2:     #8b949e;
    --text3:     #4a5568;
    --glow:      0 0 20px #00ff8844;
    --glow2:     0 0 40px #00ff8822;
  }

  html, body, #root { height: 100%; background: var(--bg); color: var(--text); font-family: 'JetBrains Mono', monospace; }

  ::-webkit-scrollbar { width: 4px; }
  ::-webkit-scrollbar-track { background: var(--bg); }
  ::-webkit-scrollbar-thumb { background: var(--border2); border-radius: 2px; }

  /* ── SCANLINE OVERLAY ── */
  body::before {
    content: '';
    position: fixed; inset: 0; pointer-events: none; z-index: 9999;
    background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,0,0,0.03) 2px, rgba(0,0,0,0.03) 4px);
  }

  /* ── LAYOUT ── */
  .app { display: grid; grid-template-rows: 56px 1fr; height: 100vh; overflow: hidden; }
  .main { display: grid; grid-template-columns: 260px 1fr 300px; overflow: hidden; }

  /* ── TOPBAR ── */
  .topbar {
    display: flex; align-items: center; gap: 16px;
    padding: 0 20px; border-bottom: 1px solid var(--border);
    background: var(--bg2); position: relative; overflow: hidden;
  }
  .topbar::after {
    content: ''; position: absolute; bottom: 0; left: 0; right: 0; height: 1px;
    background: linear-gradient(90deg, transparent, var(--green), transparent);
    animation: scanh 4s linear infinite;
  }
  @keyframes scanh { 0% { transform: translateX(-100%); } 100% { transform: translateX(100%); } }

  .logo { font-family: 'Orbitron', monospace; font-weight: 900; font-size: 18px; color: var(--green); letter-spacing: 3px; text-shadow: var(--glow); }
  .logo span { color: var(--cyan); }
  .topbar-right { margin-left: auto; display: flex; align-items: center; gap: 12px; }
  .user-badge { font-size: 11px; color: var(--green); border: 1px solid var(--green); padding: 4px 10px; border-radius: 2px; }
  .btn-logout { background: none; border: 1px solid var(--border2); color: var(--text2); font-family: inherit; font-size: 11px; padding: 4px 10px; cursor: pointer; border-radius: 2px; transition: all 0.2s; }
  .btn-logout:hover { border-color: var(--red); color: var(--red); }

  /* ── SIDEBAR ── */
  .sidebar {
    border-right: 1px solid var(--border); background: var(--bg2);
    display: flex; flex-direction: column; overflow: hidden;
  }
  .sidebar-section { padding: 16px; border-bottom: 1px solid var(--border); }
  .sidebar-label { font-size: 10px; color: var(--text3); letter-spacing: 2px; text-transform: uppercase; margin-bottom: 10px; }

  .search-box { position: relative; }
  .search-box input {
    width: 100%; background: var(--bg3); border: 1px solid var(--border);
    color: var(--text); font-family: inherit; font-size: 12px;
    padding: 8px 10px 8px 28px; border-radius: 2px; outline: none; transition: border 0.2s;
  }
  .search-box input:focus { border-color: var(--green); box-shadow: 0 0 8px var(--green-dim); }
  .search-icon { position: absolute; left: 9px; top: 50%; transform: translateY(-50%); color: var(--text3); font-size: 12px; }

  .lang-filters { display: flex; flex-direction: column; gap: 4px; max-height: 200px; overflow-y: auto; }
  .lang-btn {
    background: none; border: none; color: var(--text2); font-family: inherit; font-size: 11px;
    padding: 5px 8px; cursor: pointer; text-align: left; border-radius: 2px; transition: all 0.15s;
    display: flex; align-items: center; gap: 8px;
  }
  .lang-btn:hover, .lang-btn.active { background: var(--green-dim); color: var(--green); }
  .lang-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--green); flex-shrink: 0; }

  .btn-new {
    width: 100%; background: var(--green-dim); border: 1px solid var(--green);
    color: var(--green); font-family: inherit; font-size: 12px; font-weight: 600;
    padding: 10px; cursor: pointer; border-radius: 2px; letter-spacing: 1px;
    transition: all 0.2s; text-transform: uppercase;
  }
  .btn-new:hover { background: var(--green); color: var(--bg); box-shadow: var(--glow); }

  /* ── SNIPPET LIST ── */
  .snippet-list { flex: 1; overflow-y: auto; padding: 8px; display: flex; flex-direction: column; gap: 4px; }
  .snippet-card {
    background: var(--bg3); border: 1px solid var(--border);
    padding: 12px; border-radius: 2px; cursor: pointer;
    transition: all 0.15s; position: relative; overflow: hidden;
  }
  .snippet-card::before { content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 2px; background: transparent; transition: background 0.15s; }
  .snippet-card:hover { border-color: var(--border2); }
  .snippet-card:hover::before, .snippet-card.active::before { background: var(--green); }
  .snippet-card.active { border-color: var(--green); background: var(--bg2); box-shadow: inset 0 0 20px var(--green-dim); }

  .card-title { font-size: 12px; font-weight: 600; color: var(--text); margin-bottom: 4px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
  .card-lang { font-size: 10px; color: var(--cyan); letter-spacing: 1px; }
  .card-date { font-size: 10px; color: var(--text3); margin-top: 4px; }

  /* ── MAIN PANEL ── */
  .panel { display: flex; flex-direction: column; overflow: hidden; }
  .panel-header { padding: 16px 20px; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: 12px; background: var(--bg2); }
  .panel-title { font-family: 'Orbitron', monospace; font-size: 13px; color: var(--green); letter-spacing: 2px; }
  .panel-actions { margin-left: auto; display: flex; gap: 8px; }

  .btn-icon {
    background: none; border: 1px solid var(--border2); color: var(--text2);
    font-family: inherit; font-size: 11px; padding: 5px 10px; cursor: pointer;
    border-radius: 2px; transition: all 0.2s;
  }
  .btn-icon:hover { border-color: var(--cyan); color: var(--cyan); }
  .btn-icon.danger:hover { border-color: var(--red); color: var(--red); }
  .btn-icon.primary { border-color: var(--green); color: var(--green); }
  .btn-icon.primary:hover { background: var(--green); color: var(--bg); box-shadow: var(--glow); }

  .panel-body { flex: 1; overflow-y: auto; padding: 20px; }

  /* ── CODE VIEWER ── */
  .code-block {
    background: var(--bg2); border: 1px solid var(--border);
    border-radius: 2px; overflow: hidden; margin-top: 16px;
  }
  .code-header { padding: 8px 14px; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: 8px; }
  .code-dots { display: flex; gap: 5px; }
  .dot { width: 8px; height: 8px; border-radius: 50%; }
  .dot-r { background: #ff5f57; } .dot-y { background: #febc2e; } .dot-g { background: #28c840; }
  .code-lang-tag { font-size: 10px; color: var(--cyan); margin-left: auto; letter-spacing: 1px; }
  .code-content { padding: 16px; font-size: 12px; line-height: 1.7; overflow-x: auto; white-space: pre; color: var(--text); }

  /* ── FORM ── */
  .form-grid { display: flex; flex-direction: column; gap: 14px; }
  .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
  .form-group { display: flex; flex-direction: column; gap: 6px; }
  .form-label { font-size: 10px; color: var(--text3); letter-spacing: 2px; text-transform: uppercase; }
  .form-input, .form-select, .form-textarea {
    background: var(--bg3); border: 1px solid var(--border);
    color: var(--text); font-family: inherit; font-size: 12px;
    padding: 9px 12px; border-radius: 2px; outline: none; transition: border 0.2s;
    width: 100%;
  }
  .form-input:focus, .form-select:focus, .form-textarea:focus { border-color: var(--green); box-shadow: 0 0 8px var(--green-dim); }
  .form-select { cursor: pointer; }
  .form-select option { background: var(--bg3); }
  .form-textarea { resize: vertical; min-height: 200px; line-height: 1.6; }
  .form-actions { display: flex; gap: 10px; justify-content: flex-end; padding-top: 4px; }

  .btn-save {
    background: var(--green-dim); border: 1px solid var(--green); color: var(--green);
    font-family: inherit; font-size: 12px; font-weight: 600; padding: 9px 20px;
    cursor: pointer; border-radius: 2px; transition: all 0.2s; letter-spacing: 1px;
  }
  .btn-save:hover { background: var(--green); color: var(--bg); box-shadow: var(--glow); }
  .btn-cancel {
    background: none; border: 1px solid var(--border2); color: var(--text2);
    font-family: inherit; font-size: 12px; padding: 9px 20px;
    cursor: pointer; border-radius: 2px; transition: all 0.2s;
  }
  .btn-cancel:hover { border-color: var(--red); color: var(--red); }

  /* ── ACTIVITY FEED ── */
  .feed { border-left: 1px solid var(--border); background: var(--bg2); display: flex; flex-direction: column; overflow: hidden; }
  .feed-header { padding: 16px; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: 8px; }
  .feed-title { font-family: 'Orbitron', monospace; font-size: 11px; color: var(--cyan); letter-spacing: 2px; }
  .feed-dot { width: 6px; height: 6px; border-radius: 50%; background: var(--green); animation: pulse 2s infinite; margin-left: auto; }
  @keyframes pulse { 0%,100%{opacity:1;box-shadow:0 0 4px var(--green);} 50%{opacity:0.4;box-shadow:none;} }

  .feed-list { flex: 1; overflow-y: auto; padding: 8px; display: flex; flex-direction: column; gap: 4px; }
  .feed-item {
    padding: 10px 12px; border-radius: 2px; font-size: 11px; line-height: 1.5;
    border-left: 2px solid transparent; background: var(--bg3);
    animation: slideIn 0.3s ease;
  }
  @keyframes slideIn { from { opacity:0; transform: translateX(10px); } to { opacity:1; transform: translateX(0); } }
  .feed-item.CREATED { border-color: var(--green); }
  .feed-item.UPDATED { border-color: var(--yellow); }
  .feed-item.DELETED { border-color: var(--red); }
  .feed-action { font-weight: 700; }
  .feed-action.CREATED { color: var(--green); }
  .feed-action.UPDATED { color: var(--yellow); }
  .feed-action.DELETED { color: var(--red); }
  .feed-meta { color: var(--text3); font-size: 10px; margin-top: 3px; }

  .feed-empty { color: var(--text3); font-size: 11px; text-align: center; padding: 40px 20px; line-height: 2; }

  /* ── AUTH ── */
  .auth-screen {
    min-height: 100vh; display: flex; align-items: center; justify-content: center;
    background: var(--bg); position: relative; overflow: hidden;
  }
  .auth-bg {
    position: absolute; inset: 0; pointer-events: none;
    background:
      radial-gradient(ellipse 60% 40% at 20% 50%, #00ff8808 0%, transparent 70%),
      radial-gradient(ellipse 40% 60% at 80% 30%, #00d4ff06 0%, transparent 70%);
  }
  .auth-grid {
    position: absolute; inset: 0; pointer-events: none; opacity: 0.04;
    background-image: linear-gradient(var(--border) 1px, transparent 1px), linear-gradient(90deg, var(--border) 1px, transparent 1px);
    background-size: 40px 40px;
  }
  .auth-card {
    background: var(--bg2); border: 1px solid var(--border);
    padding: 40px; width: 380px; position: relative;
    box-shadow: 0 0 60px #00ff8810;
  }
  .auth-card::before {
    content: ''; position: absolute; top: 0; left: 0; right: 0; height: 2px;
    background: linear-gradient(90deg, transparent, var(--green), transparent);
  }
  .auth-logo { font-family: 'Orbitron', monospace; font-weight: 900; font-size: 24px; color: var(--green); letter-spacing: 4px; text-align: center; margin-bottom: 4px; text-shadow: var(--glow); }
  .auth-logo span { color: var(--cyan); }
  .auth-sub { font-size: 11px; color: var(--text3); text-align: center; letter-spacing: 2px; margin-bottom: 32px; }
  .auth-tabs { display: flex; border-bottom: 1px solid var(--border); margin-bottom: 24px; }
  .auth-tab {
    flex: 1; background: none; border: none; color: var(--text3); font-family: inherit;
    font-size: 12px; padding: 10px; cursor: pointer; letter-spacing: 1px; transition: all 0.2s;
    border-bottom: 2px solid transparent; margin-bottom: -1px;
  }
  .auth-tab.active { color: var(--green); border-bottom-color: var(--green); }
  .auth-error { background: #ff446622; border: 1px solid var(--red); color: var(--red); font-size: 11px; padding: 8px 12px; border-radius: 2px; margin-bottom: 14px; }

  /* ── EMPTY STATE ── */
  .empty { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: var(--text3); gap: 12px; }
  .empty-icon { font-size: 32px; opacity: 0.3; }
  .empty-text { font-size: 12px; letter-spacing: 1px; }
  .empty-sub { font-size: 11px; color: var(--text3); opacity: 0.6; }

  /* ── DESCRIPTION ── */
  .desc-block { font-size: 12px; color: var(--text2); line-height: 1.8; padding: 12px 0; border-bottom: 1px solid var(--border); margin-bottom: 4px; }
  .meta-row { display: flex; gap: 20px; padding: 10px 0; font-size: 11px; color: var(--text3); border-bottom: 1px solid var(--border); margin-bottom: 4px; }
  .meta-item strong { color: var(--cyan); }

  /* ── TOAST ── */
  .toast {
    position: fixed; bottom: 24px; right: 24px; z-index: 9999;
    background: var(--bg2); border: 1px solid var(--green); color: var(--green);
    font-size: 12px; padding: 12px 20px; border-radius: 2px;
    box-shadow: var(--glow); animation: toastIn 0.3s ease;
  }
  .toast.error { border-color: var(--red); color: var(--red); box-shadow: 0 0 20px #ff446644; }
  @keyframes toastIn { from{opacity:0;transform:translateY(10px)} to{opacity:1;transform:translateY(0)} }

  /* ── CONFIRM DIALOG ── */
  .overlay { position: fixed; inset: 0; background: #000000aa; z-index: 999; display: flex; align-items: center; justify-content: center; }
  .dialog { background: var(--bg2); border: 1px solid var(--border); padding: 28px; width: 320px; }
  .dialog-title { font-size: 13px; color: var(--red); margin-bottom: 10px; font-weight: 600; }
  .dialog-body { font-size: 12px; color: var(--text2); margin-bottom: 20px; line-height: 1.6; }
  .dialog-actions { display: flex; gap: 10px; justify-content: flex-end; }

  /* ── PAGINATION ── */
  .pagination { display: flex; align-items: center; gap: 8px; padding: 12px 20px; border-top: 1px solid var(--border); background: var(--bg2); }
  .page-btn { background: none; border: 1px solid var(--border2); color: var(--text2); font-family: inherit; font-size: 11px; padding: 4px 10px; cursor: pointer; border-radius: 2px; transition: all 0.15s; }
  .page-btn:hover:not(:disabled) { border-color: var(--green); color: var(--green); }
  .page-btn:disabled { opacity: 0.3; cursor: default; }
  .page-info { font-size: 11px; color: var(--text3); margin: 0 auto; }

  /* ── LOADING ── */
  .loading { display: flex; align-items: center; gap: 8px; color: var(--text3); font-size: 12px; padding: 40px; justify-content: center; }
  .spinner { width: 14px; height: 14px; border: 2px solid var(--border2); border-top-color: var(--green); border-radius: 50%; animation: spin 0.8s linear infinite; }
  @keyframes spin { to { transform: rotate(360deg); } }

  .tag { display: inline-block; background: var(--cyan-dim); border: 1px solid var(--cyan); color: var(--cyan); font-size: 10px; padding: 2px 8px; border-radius: 2px; letter-spacing: 1px; }
`;

// ── LANGUAGES ─────────────────────────────────────────────────────────────────
const LANGS = ["Java","Python","JavaScript","TypeScript","Go","Rust","C++","C","C#","Kotlin","Swift","Ruby","PHP","SQL","Bash","YAML","JSON","HTML","CSS","Other"];

// ── API HELPERS ────────────────────────────────────────────────────────────────
async function apiFetch(path, opts = {}, token = null) {
  const headers = { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}), ...opts.headers };
  const res = await fetch(`${API}${path}`, { ...opts, headers });
  if (!res.ok) { const e = await res.text(); throw new Error(e || res.statusText); }
  if (res.status === 204) return null;
  return res.json();
}

// ── TOAST ─────────────────────────────────────────────────────────────────────
function Toast({ msg, type, onDone }) {
  useEffect(() => { const t = setTimeout(onDone, 3000); return () => clearTimeout(t); }, [onDone]);
  return <div className={`toast ${type === "error" ? "error" : ""}`}>{msg}</div>;
}

// ── AUTH SCREEN ────────────────────────────────────────────────────────────────
function AuthScreen({ onAuth }) {
  const [tab, setTab] = useState("login");
  const [form, setForm] = useState({ username: "", password: "", email: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    setError(""); setLoading(true);
    try {
      if (tab === "login") {
        const data = await apiFetch("/api/auth/login", { method: "POST", body: JSON.stringify({ username: form.username, password: form.password }) });
        onAuth(data.token, form.username);
      } else {
        await apiFetch("/api/auth/register", { method: "POST", body: JSON.stringify(form) });
        setTab("login"); setError(""); setForm(f => ({ ...f, email: "" }));
      }
    } catch (e) { setError(e.message); }
    setLoading(false);
  };

  const field = (k, placeholder, type = "text") => (
    <div className="form-group">
      <label className="form-label">{k}</label>
      <input className="form-input" type={type} placeholder={placeholder} value={form[k]} onChange={e => setForm(f => ({ ...f, [k]: e.target.value }))} onKeyDown={e => e.key === "Enter" && submit()} />
    </div>
  );

  return (
    <div className="auth-screen">
      <div className="auth-bg" /><div className="auth-grid" />
      <div className="auth-card">
        <div className="auth-logo">SNIP<span>VAULT</span></div>
        <div className="auth-sub">{"// CODE SNIPPET MANAGER"}</div>
        <div className="auth-tabs">
          {["login","register"].map(t => <button key={t} className={`auth-tab ${tab===t?"active":""}`} onClick={()=>{setTab(t);setError("")}}>{t.toUpperCase()}</button>)}
        </div>
        {error && <div className="auth-error">⚠ {error}</div>}
        <div className="form-grid">
          {field("username", "monish")}
          {tab === "register" && field("email", "monish@example.com")}
          {field("password", "••••••••", "password")}
        </div>
        <div style={{marginTop:20}}>
          <button className="btn-save" style={{width:"100%"}} onClick={submit} disabled={loading}>
            {loading ? "..." : tab === "login" ? "LOGIN →" : "REGISTER →"}
          </button>
        </div>
      </div>
    </div>
  );
}

// ── SNIPPET FORM ───────────────────────────────────────────────────────────────
function SnippetForm({ initial, onSave, onCancel }) {
  const [form, setForm] = useState(initial || { title: "", language: "Java", description: "", code: "" });
  const set = k => e => setForm(f => ({ ...f, [k]: e.target.value }));
  return (
    <div className="form-grid">
      <div className="form-row">
        <div className="form-group">
          <label className="form-label">Title</label>
          <input className="form-input" placeholder="Binary Search Tree" value={form.title} onChange={set("title")} />
        </div>
        <div className="form-group">
          <label className="form-label">Language</label>
          <select className="form-select" value={form.language} onChange={set("language")}>
            {LANGS.map(l => <option key={l}>{l}</option>)}
          </select>
        </div>
      </div>
      <div className="form-group">
        <label className="form-label">Description</label>
        <input className="form-input" placeholder="What does this snippet do?" value={form.description} onChange={set("description")} />
      </div>
      <div className="form-group">
        <label className="form-label">Code</label>
        <textarea className="form-textarea" placeholder="// paste your code here..." value={form.code} onChange={set("code")} />
      </div>
      <div className="form-actions">
        <button className="btn-cancel" onClick={onCancel}>CANCEL</button>
        <button className="btn-save" onClick={() => onSave(form)}>SAVE →</button>
      </div>
    </div>
  );
}

// ── SNIPPET DETAIL ─────────────────────────────────────────────────────────────
function SnippetDetail({ snippet, onEdit, onDelete }) {
  const fmt = d => d ? new Date(d).toLocaleString() : "—";
  return (
    <>
      <div className="meta-row">
        <span><strong>ID</strong> #{snippet.id}</span>
        <span><strong>LANG</strong> <span className="tag">{snippet.language}</span></span>
        <span><strong>CREATED</strong> {fmt(snippet.createdAt)}</span>
      </div>
      {snippet.description && <div className="desc-block">{snippet.description}</div>}
      <div className="code-block">
        <div className="code-header">
          <div className="code-dots"><div className="dot dot-r"/><div className="dot dot-y"/><div className="dot dot-g"/></div>
          <span className="code-lang-tag">{snippet.language.toUpperCase()}</span>
        </div>
        <pre className="code-content">{snippet.code}</pre>
      </div>
    </>
  );
}

// ── MAIN APP ───────────────────────────────────────────────────────────────────
export default function App() {
  const [token, setToken] = useState(() => localStorage.getItem("sv_token"));
  const [username, setUsername] = useState(() => localStorage.getItem("sv_user"));
  const [snippets, setSnippets] = useState([]);
  const [selected, setSelected] = useState(null);
  const [view, setView] = useState("detail"); // detail | edit | new
  const [search, setSearch] = useState("");
  const [langFilter, setLangFilter] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [feed, setFeed] = useState([]);
  const [toast, setToast] = useState(null);
  const [confirm, setConfirm] = useState(null);
  const stompRef = useRef(null);
  const searchTimer = useRef(null);

  // ── AUTH ──
  const handleAuth = (tok, user) => {
    localStorage.setItem("sv_token", tok);
    localStorage.setItem("sv_user", user);
    setToken(tok); setUsername(user);
  };

  const logout = () => {
    localStorage.removeItem("sv_token"); localStorage.removeItem("sv_user");
    setToken(null); setUsername(null); setSnippets([]); setSelected(null);
    if (stompRef.current) {
      try { if (stompRef.current.connected) stompRef.current.disconnect(); } catch (e) {}
    }
  };

  // ── TOAST ──
  const showToast = (msg, type = "ok") => setToast({ msg, type });

  // ── FETCH SNIPPETS ──
  const fetchSnippets = useCallback(async (p = 0) => {
    if (!token) return;
    setLoading(true);
    try {
      let data;
      if (search) {
        data = await apiFetch(`/api/snippets/search?keyword=${encodeURIComponent(search)}&page=${p}&size=10&sort=createdAt,desc`, {}, token);
      } else if (langFilter) {
        const list = await apiFetch(`/api/snippets/language?languages=${encodeURIComponent(langFilter)}`, {}, token);
        setSnippets(list); setTotalPages(1); setPage(0); setLoading(false); return;
      } else {
        data = await apiFetch(`/api/snippets?page=${p}&size=10&sort=createdAt,desc`, {}, token);
      }
      setSnippets(data.content || []);
      setTotalPages(data.totalPages || 1);
      setPage(p);
    } catch (e) { showToast(e.message, "error"); }
    setLoading(false);
  }, [token, search, langFilter]);

  // ── SYNC FETCH REF so WebSocket always calls the latest version ──
  const fetchSnippetsRef = useRef(fetchSnippets);
  useEffect(() => { fetchSnippetsRef.current = fetchSnippets; }, [fetchSnippets]);

  // ── INITIAL LOAD + LANG FILTER ──
  useEffect(() => { if (token) fetchSnippets(0); }, [token, langFilter, fetchSnippets]);

  // ── DEBOUNCED SEARCH ──
  useEffect(() => {
    clearTimeout(searchTimer.current);
    searchTimer.current = setTimeout(() => fetchSnippets(0), 400);
    return () => clearTimeout(searchTimer.current);
  }, [search, fetchSnippets]);

  // ── WEBSOCKET — only re-runs on login/logout, never on page or fetchSnippets changes ──
useEffect(() => {
  if (!token) return;
  let stomp = null;
  let active = true; // tracks if this effect instance is still valid

  const loadStomp = async () => {
    if (!window.SockJS || !window.Stomp) return;
    const sock = new window.SockJS(`${API}/ws`);
    stomp = window.Stomp.over(sock);
    stomp.debug = null;
    stomp.connect({}, () => {
      // Connection finished AFTER cleanup already ran → kill it immediately
      if (!active) {
        try { stomp.disconnect(); } catch (e) {}
        return;
      }
      stomp.subscribe("/topic/activity", msg => {
        if (!active) return; // extra guard on incoming messages
        try {
          const evt = JSON.parse(msg.body);
          setFeed(f => [evt, ...f].slice(0, 50));
          fetchSnippetsRef.current(0);
        } catch (_) {}
      });
    });
    stompRef.current = stomp;
  };
  loadStomp();

  return () => {
    active = false; // immediately invalidate this instance
    if (stomp) {
      try {
        if (stomp.connected) stomp.disconnect();
      } catch (e) {}
    }
  };
}, [token]);
  // ── CRUD ──
  const createSnippet = async (form) => {
    try {
      await apiFetch("/api/snippets", { method: "POST", body: JSON.stringify(form) }, token);
      showToast("Snippet created");
      fetchSnippets(0); setView("detail");
    } catch (e) { showToast(e.message, "error"); }
  };

  const updateSnippet = async (form) => {
    try {
      const updated = await apiFetch(`/api/snippets/${selected.id}`, { method: "PUT", body: JSON.stringify(form) }, token);
      setSelected(updated); showToast("Snippet updated");
      fetchSnippets(page); setView("detail");
    } catch (e) { showToast(e.message, "error"); }
  };

  const deleteSnippet = async () => {
    try {
      await apiFetch(`/api/snippets/${selected.id}`, { method: "DELETE" }, token);
      showToast("Snippet deleted");
      setSelected(null); fetchSnippets(page); setConfirm(null);
    } catch (e) { showToast(e.message, "error"); setConfirm(null); }
  };

  const fmtTime = ts => {
    if (!ts) return "";
    try { return new Date(ts).toLocaleTimeString(); } catch { return ""; }
  };

  if (!token) return (
    <>
      <style>{css}</style>
      <AuthScreen onAuth={handleAuth} />
    </>
  );

  return (
    <>
      <style>{css}</style>

      <div className="app">
        {/* TOPBAR */}
        <div className="topbar">
          <div className="logo">SNIP<span>VAULT</span></div>
          <span style={{fontSize:11,color:"var(--text3)"}}>{"// code snippet manager"}</span>
          <div className="topbar-right">
            <div className="user-badge">▶ {username}</div>
            <button className="btn-logout" onClick={logout}>LOGOUT</button>
          </div>
        </div>

        <div className="main">
          {/* SIDEBAR */}
          <div className="sidebar">
            <div className="sidebar-section">
              <div className="sidebar-label">Search</div>
              <div className="search-box">
                <span className="search-icon">⌕</span>
                <input placeholder="search snippets..." value={search} onChange={e => { setSearch(e.target.value); setLangFilter(""); }} />
              </div>
            </div>

            <div className="sidebar-section">
              <div className="sidebar-label">Language</div>
              <div className="lang-filters">
                <button className={`lang-btn ${!langFilter?"active":""}`} onClick={() => { setLangFilter(""); setSearch(""); }}>
                  <span className="lang-dot" style={{background:"var(--text3)"}}/>All
                </button>
                {LANGS.slice(0,10).map(l => (
                  <button key={l} className={`lang-btn ${langFilter===l?"active":""}`} onClick={() => { setLangFilter(l); setSearch(""); }}>
                    <span className="lang-dot"/>{l}
                  </button>
                ))}
              </div>
            </div>

            <div className="sidebar-section">
              <button className="btn-new" onClick={() => { setView("new"); setSelected(null); }}>+ NEW SNIPPET</button>
            </div>

            <div className="snippet-list">
              {loading && <div className="loading"><div className="spinner"/> loading...</div>}
              {!loading && snippets.length === 0 && (
                <div className="empty" style={{padding:20}}>
                  <div className="empty-icon">∅</div>
                  <div className="empty-text" style={{fontSize:11}}>no snippets found</div>
                </div>
              )}
              {snippets.map(s => (
                <div key={s.id} className={`snippet-card ${selected?.id===s.id?"active":""}`} onClick={() => { setSelected(s); setView("detail"); }}>
                  <div className="card-title">{s.title}</div>
                  <div className="card-lang">{s.language}</div>
                  <div className="card-date">{s.createdAt ? new Date(s.createdAt).toLocaleDateString() : ""}</div>
                </div>
              ))}
            </div>

            {totalPages > 1 && (
              <div className="pagination">
                <button className="page-btn" disabled={page===0} onClick={() => fetchSnippets(page-1)}>←</button>
                <span className="page-info">{page+1} / {totalPages}</span>
                <button className="page-btn" disabled={page>=totalPages-1} onClick={() => fetchSnippets(page+1)}>→</button>
              </div>
            )}
          </div>

          {/* MAIN PANEL */}
          <div className="panel">
            <div className="panel-header">
              <div className="panel-title">
                {view==="new" ? "// NEW SNIPPET" : view==="edit" ? "// EDIT SNIPPET" : selected ? `// ${selected.title}` : "// SELECT A SNIPPET"}
              </div>
              {view==="detail" && selected && (
                <div className="panel-actions">
                  <button className="btn-icon primary" onClick={() => setView("edit")}>EDIT</button>
                  <button className="btn-icon danger" onClick={() => setConfirm(true)}>DELETE</button>
                </div>
              )}
            </div>

            <div className="panel-body">
              {view==="new" && <SnippetForm onSave={createSnippet} onCancel={() => setView("detail")} />}
              {view==="edit" && selected && <SnippetForm initial={selected} onSave={updateSnippet} onCancel={() => setView("detail")} />}
              {view==="detail" && !selected && (
                <div className="empty">
                  <div className="empty-icon">{"</>"}</div>
                  <div className="empty-text">select a snippet or create one</div>
                  <div className="empty-sub">your code vault awaits</div>
                </div>
              )}
              {view==="detail" && selected && <SnippetDetail snippet={selected} onEdit={() => setView("edit")} onDelete={() => setConfirm(true)} />}
            </div>
          </div>

          {/* ACTIVITY FEED */}
          <div className="feed">
            <div className="feed-header">
              <div className="feed-title">LIVE FEED</div>
              <div className="feed-dot"/>
            </div>
            <div className="feed-list">
              {feed.length === 0 && (
                <div className="feed-empty">
                  <div>⬡</div>
                  <div>waiting for events...</div>
                  <div style={{fontSize:10,marginTop:8}}>create, edit, or delete<br/>a snippet to see it here</div>
                </div>
              )}
              {feed.map((evt, i) => (
                <div key={i} className={`feed-item ${evt.action}`}>
                  <span className={`feed-action ${evt.action}`}>{evt.action}</span>
                  {" "}<span style={{color:"var(--text)"}}>"{evt.snippetTitle}"</span>
                  <div className="feed-meta">by {evt.username} · {fmtTime(evt.timestamp)} · <span className="tag">{evt.language}</span></div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* CONFIRM DIALOG */}
      {confirm && (
        <div className="overlay">
          <div className="dialog">
            <div className="dialog-title">⚠ CONFIRM DELETE</div>
            <div className="dialog-body">Delete <strong>"{selected?.title}"</strong>? This cannot be undone.</div>
            <div className="dialog-actions">
              <button className="btn-cancel" onClick={() => setConfirm(null)}>CANCEL</button>
              <button className="btn-save" style={{borderColor:"var(--red)",color:"var(--red)",background:"#ff446622"}} onClick={deleteSnippet}>DELETE</button>
            </div>
          </div>
        </div>
      )}

      {/* TOAST */}
      {toast && <Toast msg={toast.msg} type={toast.type} onDone={() => setToast(null)} />}
    </>
  );
}