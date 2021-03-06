package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    @Size(min = 4, message = "Email not be less than 4 characters")
    @Email
    private String email;
    @NotNull(message = "password can not be null")
    @Size(min = 4, message = "password not be less than 4 characters")
    private String password;
}
