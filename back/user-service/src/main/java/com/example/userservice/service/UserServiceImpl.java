package com.example.userservice.service;

import com.example.userservice.dto.EmailDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;
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
    public Iterable<UserEntity> getUserByAll(Pageable pageable) {
        return userRepository.findAll(pageable);
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
        userDto.setState(3);
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
        userDto.setDeleteAt(localDate);
        userEntity = modelMapper.map(userDto,UserEntity.class);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteByEmail(String email) {
        Date date = new Date();
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = userRepository.findByEmail(email);
        UserDto userDto = modelMapper.map(userEntity,UserDto.class);
        userDto.setState(3);
        userDto.setDeleteAt(localDate);
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
    public void updateUser(UserDto requestDto) {
        Date date = new Date();
        LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto updateDto = modelMapper.map(requestDto,UserDto.class);
        updateDto.setEncryptedPwd(bCryptPasswordEncoder.encode(requestDto.getPassword()));
        updateDto.setModifiedAt(localDate);
        UserEntity userEntity = modelMapper.map(updateDto,UserEntity.class);
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
        if (userDto.getState() == 3 || !bCryptPasswordEncoder.matches(password,userDto.getEncryptedPwd())){
            return false;
        }
        return true;
    }

    @Override
    public void sendEmail(EmailDto emailDto) {
        final String fromMail = "sgssgmanager@gmail.com";
        final String fromName = "streaminggatemanager";
        final String password = "streamingsgs!";
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromMail, password);
            }
        });
        try {
            emailDto.setRandomPassword(emailDto.getRandomPassword());
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromMail, MimeUtility.encodeText(fromName, "UTF-8", "B")));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(emailDto.getEmail())
            );
            message.setSubject("임시 비밀번호입니다.");
            message.setText(emailDto.toString());
            Transport t = session.getTransport("smtp");
            t.connect(fromMail, password);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean findPassword(String email,String userName) {
        UserEntity userEntity = userRepository.findByEmailAndUserName(email,userName);
        if (userEntity == null)
            return false;
        return true;
    }

    @Override
    public void updatePassword(UserDto userDto, EmailDto emailDto){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        userDto.setEncryptedPwd(bCryptPasswordEncoder.encode(emailDto.getRandomPassword()));
        UserEntity userEntity = mapper.map(userDto,UserEntity.class);
        userRepository.save(userEntity);
    }
}
