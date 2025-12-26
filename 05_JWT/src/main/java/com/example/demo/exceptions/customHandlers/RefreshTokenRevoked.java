package com.example.demo.exceptions.customHandlers;

import com.example.demo.exceptions.BussinessException;

public class RefreshTokenRevoked extends BussinessException {
    public RefreshTokenRevoked() {
        super("Refresh Token is Revoked!");
    }
}
