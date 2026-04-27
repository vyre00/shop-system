package com.xiang.shop.exception;

import com.xiang.shop.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 自定義的錯誤處理
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException e) {

        ApiResponse<Void> response = new ApiResponse<>(e.getStatus(), e.getMessage(), null);
        return ResponseEntity.status(e.getStatus()).body(response);

    }

    // 未預期的錯誤處理
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {

        ApiResponse<Void> response = new ApiResponse<>(500, "系統發生未預期錯誤，請聯絡管理員", null);
        return ResponseEntity.status(500).body(response);

    }
}
