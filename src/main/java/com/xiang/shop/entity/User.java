package com.xiang.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "national_id", unique = true, nullable = false)
    private String nationalId; // 身分證ID

    @Column(name = "name", nullable = false)
    private String name; // 姓名

    @Column(name = "email", unique = true, nullable = false)
    private String email; // E-MAIL

    @Column(name = "account", unique = true, nullable = false)
    private String account; // 帳號

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // 密碼

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; // 角色

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); //建立時間

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now(); // 更新時間

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 刪除時間

    public enum Role {
        ADMIN, USER
    }
}