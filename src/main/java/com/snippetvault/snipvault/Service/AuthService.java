package com.snippetvault.snipvault.Service;

import com.snippetvault.snipvault.DTO.AuthResponse;
import com.snippetvault.snipvault.DTO.LoginRequest;
import com.snippetvault.snipvault.DTO.RegisterRequest;
import com.snippetvault.snipvault.Security.JwtUtil;
import com.snippetvault.snipvault.model.User;
import com.snippetvault.snipvault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if(userRepository.existsByUsername(request.getUsername())){
            throw new IllegalStateException("Username is already taken");
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalStateException("Email is already Registered");
        }

        User user =User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
        userRepository.save(user);

        String token=jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token,user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token= jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token,request.getUsername());
    }
}
