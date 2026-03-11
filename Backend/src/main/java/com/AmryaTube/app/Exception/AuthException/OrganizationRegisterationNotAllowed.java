package com.AmryaTube.app.Exception.AuthException;

import org.springframework.security.core.AuthenticationException;

public class OrganizationRegisterationNotAllowed extends AuthenticationException {

    public OrganizationRegisterationNotAllowed(String endMail) {
        super("Your Organization with mail end by "+ endMail +" is not allowed to register");
    }
}
