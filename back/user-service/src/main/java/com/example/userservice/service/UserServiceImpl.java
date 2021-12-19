package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.jwts.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
    JwtToken jwtToken;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,JwtToken jwtToken) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtToken = jwtToken;
    }

    public boolean isValidRefresh(String token) {
        try {
            Claims accessClaims = Jwts.parser()
                    .setSigningKey("token_refresh")
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (ExpiredJwtException exception) {
            return false;
        } catch (JwtException exception) {
            return false;
        } catch (NullPointerException exception) {
            return false;
        }
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        if(userRepository.findByEmail(userDto.getEmail()) == null) {
            userDto.setUserId(UUID.randomUUID().toString());
            userDto.setState(2);
            UserEntity userEntity = modelMapper.map(userDto,UserEntity.class);
            userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userRepository.save(userEntity);
            return userDto;
        }
        userDto.setState(4);
        return userDto;
    }

    @Override
    public void deleteByUserId(String userId) {
        Date date = new Date();
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = userRepository.findByUserId(userId);
        UserDto userDto = modelMapper.map(userEntity,UserDto.class);
        userDto.setState(3);
        userDto.setDeletedAt(localDate);
        userEntity = modelMapper.map(userDto,UserEntity.class);
        userRepository.save(userEntity);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = userRepository.findByUserId(userId);
        UserDto userDto = modelMapper.map(userEntity,UserDto.class);
        return userDto;
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

    @Override
    public UserDto getUserByUserEmail(String email) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = userRepository.findByEmail(email);
        UserDto userDto = modelMapper.map(userEntity,UserDto.class);
        return userDto;
    }

    @Override
    public UserDto addJwt(String email) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userRepository.findByEmail(email),UserDto.class);
        String accessToken = jwtToken.createToken(userDto.getUserId());
        if(userDto.getRefreshToken() == null || !isValidRefresh(userDto.getRefreshToken())) {
            String refreshToken = jwtToken.createRefreshToken(userDto.getUserId());
            userDto.setRefreshToken(refreshToken);
        }
        userDto.setAccessToken(accessToken);
        UserEntity userEntity = modelMapper.map(userDto,UserEntity.class);
        userRepository.save(userEntity);
        return userDto;
    }

    @Override
    public boolean requestLogin(String email,String password) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = userRepository.findByEmail(email);
        UserDto userDto = modelMapper.map(userEntity,UserDto.class);
        if (userDto.getState() == 3 || !bCryptPasswordEncoder.matches(password,userDto.getPassword())){
            return false;
        }
        return true;
    }
}
