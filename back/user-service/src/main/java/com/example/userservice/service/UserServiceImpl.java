package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.jpa.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        if(!Optional.of(userRepository.findByEmail(userDto.getEmail())).isPresent()) {
            userDto.setUserId(UUID.randomUUID().toString());
            UserEntity userEntity = modelMapper.map(userDto,UserEntity.class);
            userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userRepository.save(userEntity);
            return userDto;
        }
        return null;
    }

    @Override
    public void deleteByUserId(String userId) {
        userRepository.deleteByUserId(userId);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = userRepository.findByUserId(userId);
        UserDto userDto = modelMapper.map(userEntity,UserDto.class);
        return Optional.ofNullable(userDto).orElseThrow();
    }

    @Override
    public void updateUser(UserDto userDto, UserDto requestDto) {
        Date date = new Date();
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        userDto.setUserName(requestDto.getUserName());
        userDto.setPassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
        userDto.setModifiedAt(localDate);
        UserEntity userEntity = modelMapper.map(userDto,UserEntity.class);
        userRepository.save(userEntity);
    }
}
