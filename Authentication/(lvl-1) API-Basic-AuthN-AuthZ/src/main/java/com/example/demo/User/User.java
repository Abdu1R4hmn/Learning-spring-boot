package com.example.demo.User;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class User {
    private String username;
    private String password;
    private String role;

// Constructor
    // @AllArgsConstructor

// Getters and Sitters
    // @Data

    // Static method to simulate database
    public static List<User> getUsers() {
        return List.of(
                new User("user1", "1234", "USER"),
                new User("admin", "admin", "ADMIN")
        );
    }
}