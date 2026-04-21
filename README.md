# SnipVault

> A production-style REST API for saving, tagging, searching, and managing code snippets — built to learn and demonstrate backend engineering concepts one phase at a time.

![Java](https://img.shields.io/badge/Java-21_LTS-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.3-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7-red?style=flat-square&logo=redis)
![Kafka](https://img.shields.io/badge/Kafka-4.1.1-231F20?style=flat-square&logo=apachekafka)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-brightgreen?style=flat-square)
![JWT](https://img.shields.io/badge/Auth-JWT-black?style=flat-square&logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apachemaven)
![Docker](https://img.shields.io/badge/Infra-Docker-2496ED?style=flat-square&logo=docker)

---

## What Is This?

SnipVault is a **learning-first backend project**. Each phase adds exactly one new backend concept on top of the previous — the goal is not just to ship features, but to understand every annotation, every design decision, and every trade-off.

Built on **Spring Boot 4** (not 3.x) — the bleeding-edge release targeting Spring Framework 7 + Jakarta EE 11. This means hitting real breaking changes and learning to read release notes and migrate — a skill that matters at every senior level.

**Goal 1:** Land a paid backend internship  
**Goal 2:** FAANG placement after graduation

---

## Tech Stack

| Layer | Technology | Version | Why This Choice |
|---|---|---|---|
| Language | Java | 21 LTS | Virtual threads, records, pattern matching — modern Java |
| Framework | Spring Boot | 4.0.3 | Auto-config, embedded Tomcat, production-ready defaults |
| ORM | Spring Data JPA + Hibernate | 7.2.4 | Entity management, JPQL queries, schema management |
| Security | Spring Security + jjwt | 7 + 0.12.6 | Stateless JWT auth, BCrypt password hashing |
| Cache | Spring Data Redis (Lettuce) | 4.0 | Distributed caching, 10-min TTL, survives app restart |
| Real-time | WebSockets + STOMP + SockJS | Boot 4 | Persistent connection, server-push, live activity feed |
| Messaging | Apache Kafka | 4.1.1 | Persistent event log, consumer offset replay, async processing |
| Database | PostgreSQL | 15 (Docker) | ACID compliant, full-text search, array types, JSONB |
| Validation | Spring Validation | Boot 4 | @NotBlank, @Size, @Valid — request-level guards |
| Build | Maven | 3.x | Explicit lifecycle, great for learning the build process |

---

## Project Structure

```
com.snippetvault.snipvault/
├── model/          → Snippet.java, User.java
├── repository/     → SnippetRepository.java, UserRepository.java
├── service/        → SnippetService.java, AuthService.java,
│                     CustomUserDetailsService.java
├── controller/     → SnippetController.java, AuthController.java,
│                     WebSocketController.java
├── dto/            → SnippetRequest.java, SnippetResponse.java,
│                     RegisterRequest.java, LoginRequest.java,
│                     AuthResponse.java, SnippetActivityEvent.java
├── exception/      → ResourceNotFoundException.java,
│                     ErrorResponse.java,
│                     GlobalExceptionHandler.java
├── security/       → SecurityConfig.java, JwtUtil.java,
│                     JwtAuthenticationFilter.java
├── kafka/          → SnippetEventProducer.java, SnippetEventConsumer.java
└── config/         → RedisConfig.java, WebConfig.java,
                      WebSocketConfig.java, KafkaConfig.java
```

**Layer responsibilities:**
- **Controller** — HTTP only: parse request, validate input, call service, return response
- **Service** — Business logic: orchestrate repositories, apply rules, `@Transactional`
- **Repository** — Data access: CRUD against PostgreSQL via Spring Data JPA
- **DTO** — Decouples API contract from DB schema; prevents entity exposure
- **Security** — JWT filter chain, `UserDetailsService`, `SecurityConfig`
- **Kafka** — Producer publishes events on mutations; consumer processes and logs them
- **Config** — Infrastructure wiring: Redis, WebSocket broker, Kafka, CORS

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | No | Register a new user |
| `POST` | `/api/auth/login` | No | Login — returns JWT token |
| `GET` | `/api/snippets` | Yes | Get all snippets (paginated + sorted) |
| `GET` | `/api/snippets/{id}` | Yes | Get snippet by ID *(Redis cached)* |
| `POST` | `/api/snippets` | Yes | Create a new snippet |
| `PUT` | `/api/snippets/{id}` | Yes | Update an existing snippet |
| `DELETE` | `/api/snippets/{id}` | Yes | Delete a snippet |
| `GET` | `/api/snippets/search?keyword=x` | Yes | Search across title, description, code |
| `GET` | `/api/snippets/language?lang=Java` | Yes | Filter by language *(Redis cached)* |

### Pagination & Sorting

```
GET /api/snippets                          → page=0, size=10, sort=createdAt DESC (defaults)
GET /api/snippets?page=1&size=5            → second page, 5 items
GET /api/snippets?sort=title,asc           → sorted by title ascending
GET /api/snippets/search?keyword=binary    → case-insensitive search
```

---

## Authentication Flow

All `/api/snippets/**` endpoints require a Bearer token.

```bash
# 1. Register
POST /api/auth/register
{
  "username": "monish",
  "password": "pass123",
  "email": "monish@example.com"
}

# 2. Login → copy the token
POST /api/auth/login
{
  "username": "monish",
  "password": "pass123"
}
# Response: { "token": "eyJhbGciOiJIUzI1NiIsInR5..." }

# 3. Use token in all snippet requests
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5...
```

---

## Caching Strategy

| Method | Cache Key | Annotation | Reasoning |
|---|---|---|---|
| `getSnippetById(id)` | `snippets::id::{id}` | `@Cacheable` | Most frequent read — eliminates repeated DB hits |
| `getSnippetsByLanguage(lang)` | `snippets::lang::{lang}` | `@Cacheable` | Language filter is repeated often |
| `createSnippet()` | `snippets` (all entries) | `@CacheEvict` | New data → invalidate stale cache |
| `updateSnippet(id)` | `snippets` (all entries) | `@CacheEvict` | Changed data → invalidate stale cache |
| `deleteSnippet(id)` | `snippets` (all entries) | `@CacheEvict` | Removed data → invalidate stale cache |
| `getAllSnippets()` | — | Not cached | `Page<T>` not deserializable from Redis JSON |
| `searchSnippets()` | — | Not cached | Dynamic keyword queries not suitable for caching |

**Verification:** On the second `GET /api/snippets/{id}`, the snippet SQL query disappears from the console — Redis is serving the response.

```bash
docker exec -it snipvault-redis redis-cli

KEYS *                        # See all cached keys
GET "snippets::id::1"         # See raw JSON stored for a key
PTTL "snippets::id::1"        # TTL remaining in milliseconds
FLUSHDB                       # Clear entire cache
```

---

## WebSocket Live Activity Feed

When any user creates, updates, or deletes a snippet, all connected clients receive an instant push notification — no polling required.

**Architecture:**
```
POST /api/snippets
        ↓
SnippetService.createSnippet()
        ↓
SimpMessagingTemplate.convertAndSend("/topic/activity")
        ↓
All subscribed browser clients receive instantly
```

**Connection:**
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);
client.connect({}, () => {
    client.subscribe('/topic/activity', (msg) => {
        const event = JSON.parse(msg.body);
        console.log(event.action, event.username, event.snippetTitle);
    });
});
```

Open `http://localhost:8080/test.html` while the server is running to see the live feed in your browser.

**Event payload:**
```json
{
  "action": "CREATED",
  "username": "monish",
  "snippetTitle": "Binary Search",
  "language": "Java",
  "timestamp": "2026-04-21T05:28:38"
}
```

---

## Kafka Event Streaming

Every snippet mutation is published to Kafka as a persistent event. Unlike WebSockets (ephemeral), Kafka stores events on disk — consumers that were offline replay all missed events when they reconnect.

**Architecture:**
```
SnippetService
      ↓ publishes
snippet-events topic (Kafka broker)
      ↓ consumed by
SnippetEventConsumer → logs event
```

**Why both WebSockets AND Kafka?**
They serve different layers. WebSocket delivers to currently connected browser clients in real time — ephemeral, no persistence. Kafka stores every event permanently — consumers that were offline catch up on reconnect. In production: WebSocket for the UI layer, Kafka for the service layer.

**Offset replay in action:**
```
Consumer offline → misses 5 events
Consumer restarts → Kafka replays all 5 from stored offset
Consumer catches up → continues from current position
```

**Start Kafka locally:**
```bash
# from project root
docker-compose up -d
# starts: snipvault-zookeeper (2181) + snipvault-kafka (9092)
```

**Console output on snippet create:**
```
INFO SnippetEventProducer : Published Kafka event: CREATED - Binary Search by monish
INFO SnippetEventConsumer : Consumed Kafka event: action=CREATED, user=monish, snippet=Binary Search, language=Java, time=2026-04-21T05:28:38
```

---

## Setup & Running Locally

### Prerequisites
- Java 21 (JDK)
- Maven 3.x
- Docker Desktop
- IntelliJ IDEA

### 1. Start infrastructure

```bash
# First time — create containers
docker run --name snipvault-db \
  -e POSTGRES_USER=your_username \
  -e POSTGRES_PASSWORD=your_password \
  -e POSTGRES_DB=snipvaultdb \
  -p 5432:5432 -d postgres:15

docker run --name snipvault-redis -p 6379:6379 -d redis:7

# Kafka + Zookeeper via docker-compose (from project root)
docker-compose up -d

# Every subsequent session
docker start snipvault-db
docker start snipvault-redis
docker start snipvault-zookeeper
docker start snipvault-kafka
```

### 2. IntelliJ run config

Add this JVM argument to avoid a PostgreSQL timezone error:
```
-Duser.timezone=Asia/Kolkata
```

### 3. Run the app

```bash
mvn spring-boot:run
# App starts on http://localhost:8080
```

### 4. application.properties

```properties
spring.datasource.url=db_url
spring.datasource.username=db_username
spring.datasource.password=db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=jwt_secret_key
jwt.expiration=86400000

spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.redis.time-to-live=600000

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=snipvault-group
spring.kafka.consumer.auto-offset-reset=earliest
```

---

## Phase Roadmap

| Phase | Topic | Status | Key Concepts |
|---|---|---|---|
| 1 | REST API + Entity | ✅ Complete | Spring Boot setup, JPA entity, H2 → PostgreSQL, Lombok |
| 2 | Spring Data JPA | ✅ Complete | Repository, DTO pattern, Service CRUD, pagination, custom JPQL |
| 3 | JWT Authentication | ✅ Complete | Spring Security 7, stateless sessions, BCrypt, JWT filter |
| 4 | Redis Caching | ✅ Complete | @Cacheable, @CacheEvict, TTL, serialization, Redis CLI |
| 5 | WebSockets | ✅ Complete | STOMP protocol, SimpMessagingTemplate, SockJS, live feed |
| 6 | Kafka Events | ✅ Complete | Producer/consumer, offset replay, event-driven architecture |

---

## Key Design Decisions

**Why DTOs instead of exposing entities directly?**
Exposing JPA entities causes infinite JSON serialization loops (bidirectional relationships), leaks internal fields (password hash, internal IDs), and tightly couples the API contract to the database schema. `SnippetRequest` controls what clients can send; `SnippetResponse` controls what clients see.

**Why JWT over sessions?**
Sessions require server-side state — they don't scale horizontally without sticky sessions or a shared session store. JWT is stateless: the server only needs the secret key to verify a token. No database lookup, no memory overhead, works naturally with mobile clients and API gateways.

**Why CSRF disabled?**
CSRF attacks exploit cookie-based auth. This API uses JWT in the `Authorization` header — browsers don't automatically attach headers to cross-origin requests. CSRF protection is simply not applicable here.

**Why `@CacheEvict(allEntries=true)` instead of targeted key eviction?**
When a snippet changes, multiple cache entries may be stale — `id::5`, `lang::Java`, and any related search results. Tracking all affected keys adds complexity that can go out of sync. `allEntries=true` is safe, simple, and correct for a write-light system like a snippet vault.

**Why `Page<T>` not cached?**
`PageImpl` (Spring Data's pagination implementation) has no no-args constructor — Jackson cannot deserialize it from Redis JSON. Individual entities and `List<T>` deserialize cleanly.

**Why manual `ObjectMapper` serialization for Kafka?**
Spring Kafka 4.0 deprecated `JsonSerializer` and `JsonDeserializer`. Using `StringSerializer` on both ends with `ObjectMapper.writeValueAsString()` on produce and `objectMapper.readValue()` on consume is cleaner, has zero deprecated dependencies, and makes the serialization logic explicit and testable.

**Why `@EnableKafka` and `@EnableCaching` must be explicit?**
Spring does not scan for `@KafkaListener` or `@Cacheable` unless told to. Without `@EnableKafka`, listener methods are silently ignored — no error, no warning, just no consumers. This is deliberate Spring design — opt-in features require explicit activation.

---

## Spring Boot 4 Breaking Changes — Hit & Fixed

Building on Boot 4 (not 3.x) means hitting real migration issues. Each was debugged from the stack trace.

| # | Breaking Change | Fix Applied |
|---|---|---|
| 1 | `DaoAuthenticationProvider` — `setUserDetailsService()` removed | Constructor now takes `UserDetailsService` directly |
| 2 | `GenericJackson2JsonRedisSerializer` deprecated | Migrated to `GenericJacksonJsonRedisSerializer` |
| 3 | `jackson-datatype-jsr310` removed as separate artifact | Now built into `jackson-databind` — removed from pom.xml |
| 4 | `spring-boot-starter-data-jpa-test` does not exist | Use `spring-boot-starter-test` only |
| 5 | PostgreSQL timezone error (`Asia/Calcutta`) | Added `-Duser.timezone=Asia/Kolkata` JVM arg |
| 6 | Jackson 3 package renamed | `tools.jackson` instead of `com.fasterxml.jackson` (except `jackson-annotations`) |
| 7 | `Page<T>` not cacheable in Redis | Cache individual entities and lists only |
| 8 | `@EnableCaching` must be explicit | Without it, `@Cacheable` and `@CacheEvict` are silently ignored |
| 9 | Spring Kafka `JsonSerializer` / `JsonDeserializer` deprecated in 4.0 | Use `StringSerializer` + manual `ObjectMapper` on both ends |
| 10 | `KafkaTemplate` auto-config only registers `<String, String>` | Define `KafkaConfig` manually for `<String, Object>` types |
| 11 | `@EnableKafka` must be explicit | Without it, `@KafkaListener` methods are silently ignored |

---

## Planned Improvements

- **Flyway** — versioned SQL migrations replacing `ddl-auto=update`
- **Tags** — `@ManyToMany` relationship, `GET /api/snippets?tag=Java`
- **Soft delete** — `isDeleted` flag instead of physical row deletion
- **API versioning** — `/api/v1/snippets`
- **Spring Boot Actuator** — `/actuator/health`, metrics
- **OpenAPI/Swagger** — auto-generated docs via `springdoc-openapi`
- **Refresh tokens** — short-lived access + long-lived refresh token pattern
- **MapStruct** — compile-time DTO mapping replacing manual mappers
- **Test coverage** — unit (Mockito), integration (`@DataJpaTest`), controller (`@WebMvcTest`)
- **Argon2 hashing** — stronger than BCrypt, memory-hard

---

## Author

**P. Monishraj** — 6th semester CSE student, Chennai  
GitHub: [github.com/P-Monishraj](https://github.com/P-Monishraj)