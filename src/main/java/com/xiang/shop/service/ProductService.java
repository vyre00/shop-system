package com.xiang.shop.service;

import com.xiang.shop.dto.ProductRequest;
import com.xiang.shop.entity.Product;
import com.xiang.shop.exception.AppException;
import com.xiang.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 新增
    @Transactional
    public Product createProduct(ProductRequest request) {

        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setStockQuantity(request.getStockQuantity());
        product.setPrice(request.getPrice());

        return productRepository.save(product);

    }

    // 修改
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {

        Product product = getProductById(id);
        product.setProductName(request.getProductName());
        product.setStockQuantity(request.getStockQuantity());
        product.setPrice(request.getPrice());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);

    }

    // 刪除 (Soft Delete)
    @Transactional
    public void deleteProduct(Long id) {

        Product product = getProductById(id);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);

    }

    // 查詢單一商品
    public Product getProductById(Long id) {

        return productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(404, "查無該項商品！(ID: " + id + ")"));

    }

    // 查詢商品列表
    public Page<Product> getAllProducts(Pageable pageable) {

        return productRepository.findAllByDeletedAtIsNull(pageable);

    }

}
