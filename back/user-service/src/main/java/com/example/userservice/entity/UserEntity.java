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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String userName;
    @Column(nullable = false, unique = true)
    private String encryptedPwd;
    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private LocalDate createdAt;

    @Column(nullable = true, updatable = true, insertable = true)
    private LocalDate modifiedAt;
    @Column(nullable = true, updatable = true, insertable = true)
    private LocalDate deletedAt;
}
