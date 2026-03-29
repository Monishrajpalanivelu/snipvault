package com.snippetvault.snipvault.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration          //Tells Spring that this class is a source of bean definitions. Spring will process this class to generate the objects (beans) needed for your app context.
@EnableWebSecurity      //This is the "Master Switch." It turns on Spring Security’s web security support and allows you to define a custom SecurityFilterChain to override the default "lock everything" behavior.
public class SecurityConfig {

    @Bean       //This is used on methods inside a @Configuration class. It tells Spring: "Execute this method, take the object it returns, and manage it as a 'Bean' in the application context." This allows you to @Autowired these objects elsewhere in your code.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/api/**").permitAll()
                        .anyRequest().authenticated() );
        return  http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
