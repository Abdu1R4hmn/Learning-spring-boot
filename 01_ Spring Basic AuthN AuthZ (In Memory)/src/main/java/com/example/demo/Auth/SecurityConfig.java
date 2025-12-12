package com.example.demo.Auth;

import com.example.demo.User.User;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {


    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){

        List<UserDetails> users = User.getUsers().stream().map(u -> org.springframework.security.core.userdetails.User.withUsername(u.getUsername())
                .password(passwordEncoder.encode(u.getPassword()))
                .roles(u.getRole())
                .build()
        ).toList();
        return new InMemoryUserDetailsManager(users);
    }


    @Bean
    public PasswordEncoder passwordEncoder(){

        return NoOpPasswordEncoder.getInstance();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        http
                .csrf(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth // Authorization Starts here,
                        .requestMatchers("/api/auth/**").permitAll()           // public endpoint
                        .requestMatchers("/api/authenticated/admin").hasRole("ADMIN")       // only admins
                        .requestMatchers("/api/authenticated/user").authenticated()     // any authenticated user
                        .anyRequest().authenticated()                              // all others require authentication
                )
                .httpBasic(Customizer.withDefaults()); // use HTTP Basic auth (username/password in headers)

        return http.build();
    }
}
