package com.example.demo.Jwt;

import com.example.demo.user.User;

public record RotatationResult(
        User user,
        String token
) {
}
