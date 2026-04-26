package com.xiang.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName; // 商品名稱

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity; // 商品庫存

    @Column(name = "price", nullable = false)
    private BigDecimal price; // 金額

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 建立時間

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now(); // 更新時間

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 刪除時間

}