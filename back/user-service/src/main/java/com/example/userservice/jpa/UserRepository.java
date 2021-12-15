package com.example.userservice.jpa;

import com.example.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByEmail(String email);
    void deleteByUserId(String userId);
    UserEntity findByUserId(String userId);
}
