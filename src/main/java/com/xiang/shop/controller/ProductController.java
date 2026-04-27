package com.xiang.shop.controller;

import com.xiang.shop.dto.ProductRequest;
import com.xiang.shop.entity.Product;
import com.xiang.shop.dto.ApiResponse;
import com.xiang.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // 新增 (POST /products)
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody ProductRequest request) {

        Product createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "商品新增成功", createdProduct));

    }

    // 修改 (PUT /products/{id})
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {

        Product updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "商品資料已更新", updatedProduct));

    }

    // 刪除 (DELETE /products/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "商品下架成功", null));

    }

    // 查詢單一商品 (GET /products/{id})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {

        Product product = productService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "查詢成功", product));

    }

    // 查詢商品列表 (GET /products?page=0&size=10)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(new ApiResponse<>(200, "查詢成功", products));

    }

}