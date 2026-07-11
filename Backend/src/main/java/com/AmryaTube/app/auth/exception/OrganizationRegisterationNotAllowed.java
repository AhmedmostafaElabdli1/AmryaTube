package com.AmryaTube.app.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class OrganizationRegisterationNotAllowed extends AuthenticationException {
    public OrganizationRegisterationNotAllowed(String endMail) {
        super("Organization with mail ending by " + endMail + " is not allowed to register");
    }
}
