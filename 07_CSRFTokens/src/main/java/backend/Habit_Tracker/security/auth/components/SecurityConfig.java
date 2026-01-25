package backend.Habit_Tracker.security.auth.components;


import backend.Habit_Tracker.security.auth.jwt.JwtConfig;
import backend.Habit_Tracker.security.auth.jwt.JwtService;
import backend.Habit_Tracker.security.execption.authHandler.RestAccessDeniedHandler;
import backend.Habit_Tracker.security.execption.authHandler.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

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
            // ---- CSRF ----
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers(
                            "/api/auth/login",
                            "/api/auth/register"
                    )
            )

            // ---- Security Headers ----
            .headers(headers -> headers
                    .xssProtection(x -> x.disable())
                    .contentTypeOptions(Customizer.withDefaults())
                    .frameOptions(frame -> frame.deny())
                    .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
            )

            // ---- Authorization ----
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                    .anyRequest().authenticated()
            )

            // ---- OAuth2 Resource Server ----
            .oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(Customizer.withDefaults())
            )

            // ---- Stateless ----
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

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
