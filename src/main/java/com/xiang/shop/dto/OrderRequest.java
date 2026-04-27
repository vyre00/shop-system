package com.xiang.shop.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {

    private List<ProductItemRequest> items;

    @Data
    public static class ProductItemRequest {
        private Long productId;
        private Integer quantity;
    }

}