package backend.Habit_Tracker.security.auth.components;

import backend.Habit_Tracker.entity.Role;
import backend.Habit_Tracker.entity.User;
import backend.Habit_Tracker.repository.RoleRepository;
import backend.Habit_Tracker.repository.UserRepository;
import backend.Habit_Tracker.security.auth.jwt.JwtService;
import backend.Habit_Tracker.security.auth.refreshToken.RefreshTokenService;
import backend.Habit_Tracker.security.auth.refreshToken.RotatationResult;
import backend.Habit_Tracker.security.execption.handlers.RefreshTokenExpired;
import backend.Habit_Tracker.security.execption.handlers.ResourseAlreadyExists;
import backend.Habit_Tracker.security.execption.handlers.ResourseNotFound;
import backend.Habit_Tracker.security.securityDto.LoginRequestDto;
import backend.Habit_Tracker.security.securityDto.LoginResponseDto;
import backend.Habit_Tracker.security.securityDto.RegisterDto;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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
                .httpOnly(false)
                .secure(false)
                .path("/api/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return new LoginResponseDto(accessToken);

    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterDto dto) throws ResourseAlreadyExists, ResourseNotFound{

        if (userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new ResourseAlreadyExists( dto.getEmail());
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

        RotatationResult newRefreshToken = refreshTokenService.validateRefreshToken(refreshToken);

        UserDetails user = userDetailsService.loadUserByUsername(newRefreshToken.user().getEmail());

        String newAccessToken = jwtService.generateToken(user);

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken",newRefreshToken.token())
                .httpOnly(false)
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
                .httpOnly(false)
                .secure(false) // true in prod
                .path("/api/auth")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        SecurityContextHolder.clearContext();
    }

}
