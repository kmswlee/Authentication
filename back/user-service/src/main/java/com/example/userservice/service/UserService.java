package com.example.userservice.service;

import com.example.userservice.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    void deleteByUserId(String userId);
    UserDto getUserByUserId(String userId);
    void updateUser(UserDto userDto,UserDto requestDto);
}
