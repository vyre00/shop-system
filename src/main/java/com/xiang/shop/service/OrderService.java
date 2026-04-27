package com.xiang.shop.service;

import com.xiang.shop.dto.OrderRequest;
import com.xiang.shop.entity.Order;
import com.xiang.shop.entity.OrderItem;
import com.xiang.shop.entity.Product;
import com.xiang.shop.exception.AppException;
import com.xiang.shop.repository.OrderRepository;
import com.xiang.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(String account, OrderRequest request) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = new Order();

        // 遍歷訂單中所有商品
        for (OrderRequest.ProductItemRequest itemReq : request.getItems()) {

            // 確認商品是否存在
            Product product = productService.getProductById(itemReq.getProductId());

            // 確認庫存並更新
            int updatedRows = productRepository.deductStock(product.getId(), itemReq.getQuantity());
            if (updatedRows == 0) {
                throw new AppException(400, "商品 [" + product.getProductName() + "] 庫存不足！");
            }

            // 計算小計
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                    .setScale(0, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(subtotal);

            // 建立明細快照
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getProductName());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(subtotal);

            orderItems.add(orderItem);
        }

        // 建立訂單主檔
        order.setAccount(account);
        order.setTotal_amount(totalAmount);
        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    // 查詢歷史訂單
    public List<Order> getMyOrders(String account) {

        return orderRepository.findAllByAccount(account);

    }
}