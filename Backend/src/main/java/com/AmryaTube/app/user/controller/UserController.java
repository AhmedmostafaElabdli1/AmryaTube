package com.AmryaTube.app.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @GetMapping("/fail")
    public String failureLogin() {
        return "Failed Authentication";
    }
}
