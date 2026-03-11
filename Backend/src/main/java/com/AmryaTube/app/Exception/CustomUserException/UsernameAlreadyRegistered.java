package com.AmryaTube.app.Exception.CustomUserException;

public class UsernameAlreadyRegistered extends RuntimeException{

    public UsernameAlreadyRegistered(String username) {
        super("Username '" + username + "' is already registered");
    }
}
