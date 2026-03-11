package com.AmryaTube.app.DTOs.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email cannot be Empty")
    @Email(message = "Invalid Email, Please Enter Valid Email")
    private String email;
    @NotBlank(message = "Password cannot be Empty")
    private String password;

    boolean rememberMe;
}
