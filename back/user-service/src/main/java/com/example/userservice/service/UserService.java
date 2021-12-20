package com.example.userservice.service;

import com.example.userservice.dto.EmailDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Iterable<UserEntity> getUserByAll(Pageable pageable);
    UserDto createUser(UserDto userDto);
    void deleteByUserId(String userId);
    void deleteByEmail(String email);
    UserDto getUserByUserId(String userId);
    void updateUser(UserDto requestDto);
    UserDto getUserByUserEmail(String email);
    UserDto addJwt(String email);
    boolean requestLogin(String email,String password);
    void sendEmail(EmailDto emailDto);
    boolean findPassword(String email,String userName);
    void updatePassword(UserDto userDto,EmailDto randomPassword);
}
