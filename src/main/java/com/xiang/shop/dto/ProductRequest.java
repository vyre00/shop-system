package com.xiang.shop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    private String productName;
    private Integer stockQuantity;
    private BigDecimal price;

}
