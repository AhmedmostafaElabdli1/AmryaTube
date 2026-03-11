package com.AmryaTube.app.Exception.CustomUserException;

public class UserNotExist extends RuntimeException {
    public UserNotExist(String email) {

        super("User with email '" + email + "' does not exist, Please Register First or Check Your Email");
    }
}
