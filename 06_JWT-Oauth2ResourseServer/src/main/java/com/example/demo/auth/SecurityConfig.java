package com.example.demo.auth;

import com.example.demo.Jwt.JwtConfig;
import com.example.demo.Jwt.JwtService;
import com.example.demo.exceptions.authExceptions.RestAccessDeniedHandler;
import com.example.demo.exceptions.authExceptions.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, RestAccessDeniedHandler restAccessDeniedHandler, JwtConfig jwtConfig, JwtService jwtService) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS
                ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/to/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.disable())
                .oauth2ResourceServer(outh2 ->
                        outh2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtConfig.jwtAuthenticationConverter())));

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
