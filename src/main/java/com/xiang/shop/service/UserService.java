package com.xiang.shop.service;

import com.xiang.shop.dto.LoginRequest;
import com.xiang.shop.entity.User;
import com.xiang.shop.exception.AppException;
import com.xiang.shop.repository.UserRepository;
import com.xiang.shop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 登入
    public String login(LoginRequest request) {

        // 查詢user
        User user = userRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new AppException(401,"帳號或密碼錯誤"));

        // 驗證密碼
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(401, "帳號或密碼錯誤");
        }

        return jwtUtil.generateToken(user.getAccount(), user.getRole().name());

    }
}