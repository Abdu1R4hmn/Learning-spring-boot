package com.example.demo.auth;

import com.example.demo.exceptions.customHandlers.EmailAlreadyExists;
import com.example.demo.exceptions.customHandlers.ResourseNotFound;
import com.example.demo.role.Role;
import com.example.demo.role.RoleRepository;
import com.example.demo.user.Dto.LoginDto;
import com.example.demo.user.Dto.RegisterDto;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Set;

@RestController
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

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/login")
    public void login(@Valid @RequestBody LoginDto dto){
        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
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

}
