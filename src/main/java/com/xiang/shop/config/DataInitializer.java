package com.xiang.shop.config;

import com.xiang.shop.entity.User;
import com.xiang.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            log.info("資料庫已有user資料，跳過初始化建立");
            return;
        }

        log.info("建立測試帳號...");

        // 建立 ADMIN 測試帳號
        User admin = new User();
        admin.setNationalId("A0000000000");
        admin.setAccount("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin"));
        admin.setName("系統管理員");
        admin.setEmail("admin@test.com");
        admin.setRole(User.Role.ADMIN);

        // 建立 USER 測試帳號
        User user = new User();
        user.setNationalId("A0000000001");
        user.setAccount("user");
        user.setPasswordHash(passwordEncoder.encode("user"));
        user.setName("測試帳號");
        user.setEmail("user@test.com");
        user.setRole(User.Role.USER);

        userRepository.saveAll(List.of(admin, user));

        log.info("建立完成！");
        log.info("[ADMIN] 測試帳號: admin / 密碼: admin");
        log.info("[USER] 測試帳號: user / 密碼: user");
    }
}