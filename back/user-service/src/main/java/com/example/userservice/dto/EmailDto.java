package com.example.userservice.dto;

import lombok.Data;

@Data
public class EmailDto {
    private String email;
    private String subject;
    private String randomPassword;

    @Override
    public String toString() {
        return "임시비밀번호는 "+randomPassword+" 입니다. 로그인후 반드시 수정해주세요.";
    }
}
