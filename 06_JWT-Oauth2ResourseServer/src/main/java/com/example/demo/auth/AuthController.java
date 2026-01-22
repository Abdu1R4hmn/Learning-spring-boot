package com.example.demo.auth;

import com.example.demo.Dto.LoginResponseDto;
import com.example.demo.Jwt.JwtService;
import com.example.demo.refreshToken.RefreshToken;
import com.example.demo.refreshToken.RefreshTokenService;
import com.example.demo.exceptions.customHandlers.EmailAlreadyExists;
import com.example.demo.exceptions.customHandlers.RefreshTokenExpired;
import com.example.demo.exceptions.customHandlers.ResourseNotFound;
import com.example.demo.role.Role;
import com.example.demo.role.RoleRepository;
import com.example.demo.Dto.LoginRequestDto;
import com.example.demo.Dto.RegisterDto;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.springframework.http.CacheControl.maxAge;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController{

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserDetailsService userDetailsService;


    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto dto, HttpServletResponse response){

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new ResourseNotFound("User"));

        String refreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return new LoginResponseDto(accessToken);

    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterDto dto) throws EmailAlreadyExists, ResourseNotFound{

        if (userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new EmailAlreadyExists( dto.getEmail());
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER").orElseThrow(() ->
                new ResourseNotFound("User Role"));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(defaultRole));

        userRepository.save(user);
    }

    @PostMapping("/refresh")
    public LoginResponseDto refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken ,HttpServletResponse response){

        if (refreshToken == null){
            throw new RefreshTokenExpired();
        }

        RefreshToken newRefreshToken = refreshTokenService.validateRefreshToken(refreshToken);
        String email = newRefreshToken.getUser().getEmail();

        UserDetails user = userDetailsService.loadUserByUsername(email);

        String newAccessToken = jwtService.generateToken(user);

        String refreshtoken = newRefreshToken.toString();
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken",refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE,responseCookie.toString());


        return new LoginResponseDto(newAccessToken);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null) {
            refreshTokenService.revokeByRawToken(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // true in prod
                .path("/api/auth")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        SecurityContextHolder.clearContext();
    }

}
