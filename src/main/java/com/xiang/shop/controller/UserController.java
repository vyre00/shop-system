package com.xiang.shop.controller;

import com.xiang.shop.dto.LoginRequest;
import com.xiang.shop.dto.ApiResponse;
import com.xiang.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 登入 (POST /users/login)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequest request) {

        String token = userService.login(request);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "登入成功", Map.of("token", token)));
    }
}