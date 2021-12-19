package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestModify {
    @NotNull(message = "name can not be null")
    @Size(min = 2, message = "name not be less than 2 characters")
    private String userName;

    @NotNull(message = "password can not be null")
    @Size(min = 4, message = "password not be less than 4 characters")
    private String password;
}
