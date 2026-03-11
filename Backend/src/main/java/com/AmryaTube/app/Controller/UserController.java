package com.AmryaTube.app.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {



    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @GetMapping("/fail")
    public String failureLogin(){
        return "Failed Authentication";
    }


}
