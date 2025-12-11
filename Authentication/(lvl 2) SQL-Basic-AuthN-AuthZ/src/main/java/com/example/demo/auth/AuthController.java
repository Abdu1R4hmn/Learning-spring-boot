package com.example.demo.auth;

import com.example.demo.user.Role;
import com.example.demo.user.RoleRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController{
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String login(){
        return "Login in here";
    }

    @PostMapping("/register")
    public void RegisterUser(@RequestBody User user) {

        Role userRole = roleRepository.findByName("USER").get();
        Set<Role> roles = Collections.singleton(userRole);

        User saveUser = new User(
                user.getUsername(),
                user.getEmail(),
                passwordEncoder.encode(user.getPassword()),
                roles
        );

        userRepository.save(saveUser);
    }

}
