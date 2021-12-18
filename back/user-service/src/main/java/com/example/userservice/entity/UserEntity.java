package com.example.userservice.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private String userId;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String encryptedPwd;
    @Column
    private String refreshToken;
    @Column // 1 : admin, 2 : user, 3 : secession
    private Integer state;
    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private LocalDate createdAt;
    @Column(nullable = true, updatable = true, insertable = true)
    private LocalDate modifiedAt;
    @Column(nullable = true, updatable = true, insertable = true)
    private LocalDate deleteAt;
}
