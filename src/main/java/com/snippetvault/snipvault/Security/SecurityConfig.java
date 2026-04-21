package com.snippetvault.snipvault.Security;

import com.snippetvault.snipvault.Service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration          //Tells Spring that this class is a source of bean definitions. Spring will process this class to generate the objects (beans) needed for your app context.
@EnableWebSecurity      //This is the "Master Switch." It turns on Spring Security’s web security support and allows you to define a custom SecurityFilterChain to override the default "lock everything" behavior.
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean       //This is used on methods inside a @Configuration class. It tells Spring: "Execute this method, take the object it returns, and manage it as a 'Bean' in the application context." This allows you to @Autowired these objects elsewhere in your code.
    public SecurityFilterChain filterChain(HttpSecurity http,AuthenticationProvider authenticationProvider) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**","/ws/**").permitAll()
                        .anyRequest().authenticated() )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return  http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
