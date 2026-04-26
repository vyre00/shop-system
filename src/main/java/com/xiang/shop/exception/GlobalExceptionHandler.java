package com.xiang.shop.exception;

import com.xiang.shop.repository.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {

        ApiResponse<Void> response = new ApiResponse<>(404, e.getMessage(), null);
        return ResponseEntity.status(404).body(response);

    }

}
