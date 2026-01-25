package backend.Habit_Tracker.security.auth.components;


import backend.Habit_Tracker.entity.User;
import backend.Habit_Tracker.repository.UserRepository;
import backend.Habit_Tracker.security.execption.handlers.ResourseNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws ResourseNotFound {

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourseNotFound("Email " + email));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),authorities
        );
    }
}
