package com.AmryaTube.app.Exception.CustomUserException;

public class EmailAlreadyRegistered extends RuntimeException {
    public EmailAlreadyRegistered(String email) {
      super("Email '" + email + "' is already registered");
    }
}
