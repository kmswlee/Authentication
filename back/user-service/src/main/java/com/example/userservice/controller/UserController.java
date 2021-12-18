package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jwts.JwtToken;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class UserController {
    UserService userService;
    JwtToken jwtToken;
    @Autowired
    public UserController(UserService userService,JwtToken jwtToken) {
        this.userService = userService;
        this.jwtToken = jwtToken;
    }
    /* 회원가입 */
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // 매칭 설정
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto = userService.createUser(userDto);
        if (userDto.getState() == 4){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ResponseUser responseUser = modelMapper.map(userDto,ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    /* 회원 탈퇴 */
    @DeleteMapping("/users/{userId}")
    public void deleteByUserId(@PathVariable("userId") String userId) {
        userService.deleteByUserId(userId);
    }

    /* 회원정보 수정 */
    @PutMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> updateUser(@PathVariable("userId") String userId,
                                                   @RequestBody RequestUser requestUser) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userService.getUserByUserId(userId),UserDto.class);
        UserDto requestDto = modelMapper.map(requestUser,UserDto.class);
        userService.updateUser(userDto,requestDto);
        ResponseUser responseUser = modelMapper.map(userDto,ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    /* 로그인 */
    @PostMapping("/login")
    public ResponseEntity<ResponseUser> login(@RequestBody RequestLogin requestLogin,
                                              HttpServletResponse response) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userService.getUserByUserEmail(requestLogin.getEmail()),UserDto.class);
        if (userDto.getState() == 3) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,observe");
        response.setHeader("Access-Control-Expose-Headers","userId,AccessToken,RefreshToken");
        userDto = userService.addJwt(userDto);
        response.addHeader("userId",userDto.getUserId());
        response.addHeader("AccessToken",userDto.getAccessToken());
        response.addHeader("RefreshToken",userDto.getRefreshToken());
        ResponseUser responseUser = modelMapper.map(userDto,ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }
    @GetMapping("/token/refresh/{userId}")
    public ResponseEntity<ResponseUser> refreshToken(@PathVariable("userId") String userId, HttpServletResponse response) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userService.getUserByUserId(userId),UserDto.class);
        userDto = userService.addJwt(userDto);
        response.addHeader("AccessToken", userDto.getAccessToken());
        response.addHeader("RefreshToken",userDto.getRefreshToken());
        ResponseUser responseUser = modelMapper.map(userDto,ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }
    @GetMapping("/test/{userId}")
    public ResponseEntity<ResponseUser> test(@PathVariable("userId") String userId) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userService.getUserByUserId(userId),UserDto.class);
        ResponseUser responseUser = modelMapper.map(userDto,ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }
}
