package com.AmryaTube.app.user.exception;

public class EmailAlreadyRegistered extends RuntimeException {
    public EmailAlreadyRegistered(String email) {
        super("Email '" + email + "' is already registered");
    }
}
