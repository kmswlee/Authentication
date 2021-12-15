package com.example.userservice.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDto {
    private String userId;
    private String userName;
    private String password;
    private String encryptedPwd;
    private String email;
    private LocalDate createdAt;
    private LocalDate modifiedAt;
    private LocalDate deletedAt;
}
