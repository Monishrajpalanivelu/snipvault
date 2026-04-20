package com.snippetvault.snipvault.Controller;

import com.snippetvault.snipvault.DTO.AuthResponse;
import com.snippetvault.snipvault.DTO.LoginRequest;
import com.snippetvault.snipvault.DTO.RegisterRequest;
import com.snippetvault.snipvault.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "file:///M:/Projects/snipvault/test.html")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
