package com.example.userservice.controller;

import com.example.userservice.dto.EmailDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.UserEntity;
import com.example.userservice.jwts.JwtToken;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.example.userservice.vo.RequestModify;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.apache.commons.lang.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // 매칭 설정
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto = userService.createUser(userDto);
        if (userDto.getState() == 3){
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
                                                   @RequestBody @Valid RequestModify requestUser) {
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
    public ResponseEntity<ResponseUser> login(@RequestBody @Valid RequestLogin requestLogin,
                                              HttpServletResponse response) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if (userService.requestLogin(requestLogin.getEmail(), requestLogin.getPassword())) {
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,observe");
            response.setHeader("Access-Control-Expose-Headers","userId,AccessToken,RefreshToken,Authorization");
            UserDto userDto = userService.addJwt(requestLogin.getEmail());
            response.addHeader("Authorization", HttpHeaders.AUTHORIZATION);
            response.addHeader("userId",userDto.getUserId());
            response.addHeader("AccessToken",userDto.getAccessToken());
            response.addHeader("RefreshToken",userDto.getRefreshToken());
            ResponseUser responseUser = modelMapper.map(userDto,ResponseUser.class);
            return ResponseEntity.status(HttpStatus.OK).body(responseUser);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    /* 토큰 재발행 */
    @GetMapping("/token/refresh/{email}")
    public ResponseEntity<ResponseUser> refreshToken(@PathVariable("email") String email, HttpServletResponse response) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = userService.addJwt(email);
        response.addHeader("AccessToken", userDto.getAccessToken());
        response.addHeader("RefreshToken",userDto.getRefreshToken());
        ResponseUser responseUser = modelMapper.map(userDto,ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    /* 비밀번호 찾기(임시 비밀번호 발행) */
    @GetMapping("/users/find/password")
    public ResponseEntity findPassword(@RequestParam(value = "userEmail") String userEmail,
                                       @RequestParam(value = "userName") String userName) {
        boolean checkUser= userService.findPassword(userEmail,userName);
        if (!checkUser) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String randomNum = RandomStringUtils.randomAlphanumeric(10);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(userService.getUserByUserEmail(userEmail),UserDto.class);
        EmailDto emailDto = mapper.map(userDto,EmailDto.class);
        emailDto.setRandomPassword(randomNum);
        userService.updatePassword(userDto,emailDto);
        userService.sendEmail(emailDto);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser.getEmail());
    }

    /* admin 유저 목록 페이지 */
    @GetMapping("/admin/users")
    public ResponseEntity<List<ResponseUser>> adminMain(Pageable pageable) {
        Iterable<UserEntity> usersList = userService.getUserByAll(pageable);
        List<ResponseUser> responseUsersList = new ArrayList<>();

        usersList.forEach(v -> {
            responseUsersList.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(responseUsersList);
    }

    /* admin 강퇴 */
    @DeleteMapping("/admin/{userId}")
    public void deleteByAdmin(@PathVariable("userId") String userId){
        userService.deleteByUserId(userId);
    }
}
