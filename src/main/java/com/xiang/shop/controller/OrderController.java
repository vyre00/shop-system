package com.xiang.shop.controller;

import com.xiang.shop.dto.ApiResponse;
import com.xiang.shop.dto.OrderRequest;
import com.xiang.shop.entity.Order;
import com.xiang.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    // 新增訂單 (POST /orders)
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(Authentication authentication, @RequestBody OrderRequest request) {

        String userAccount = authentication.getName();
        Order order = orderService.createOrder(userAccount, request);
        return ResponseEntity.ok(new ApiResponse<>(201, "新增訂單成功", order));
    }

    // 查詢歷史訂單 (GET /orders/me)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders(Authentication authentication) {

        String userAccount = authentication.getName();
        List<Order> orders = orderService.getMyOrders(userAccount);
        return ResponseEntity.ok(new ApiResponse<>(200, "查詢歷史訂單成功", orders));

    }
}