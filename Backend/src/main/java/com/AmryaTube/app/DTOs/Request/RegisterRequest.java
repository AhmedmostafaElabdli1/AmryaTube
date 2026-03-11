package com.AmryaTube.app.DTOs.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
public class RegisterRequest {

    @NotBlank(message = "Name cannot be Empty")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    String name;

    @NotBlank(message = "Username cannot be Empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String username;

    @NotBlank(message = "Email cannot be Empty")
    @Email(message = "Invalid Email, Please Enter Valid Email")
    private String email;

    @NotBlank(message = "Password cannot be Empty")
    @Size(min = 8, message = "Password must be at least 6 characters")
    private String password;

}
