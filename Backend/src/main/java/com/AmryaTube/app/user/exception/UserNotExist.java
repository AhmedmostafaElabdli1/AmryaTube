package com.AmryaTube.app.user.exception;

public class UserNotExist extends RuntimeException {
    public UserNotExist(String email) {
        super("User with email '" + email + "' does not exist. Please register first or check your email.");
    }
}
