package com.AmryaTube.app.user.exception;

public class UsernameAlreadyRegistered extends RuntimeException {
    public UsernameAlreadyRegistered(String username) {
        super("Username '" + username + "' is already registered");
    }
}
