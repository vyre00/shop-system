package com.xiang.shop.service;

import com.xiang.shop.dto.OrderRequest;
import com.xiang.shop.entity.Product;
import com.xiang.shop.exception.AppException;
import com.xiang.shop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testConcurrentCheckout_NoOversell() throws InterruptedException {
        // 建立數量限制10個的商品
        Product flashSaleProduct = new Product();
        flashSaleProduct.setProductName("西瓜");
        flashSaleProduct.setPrice(new BigDecimal("150"));
        flashSaleProduct.setStockQuantity(150); //
        flashSaleProduct = productRepository.save(flashSaleProduct);

        Long productId = flashSaleProduct.getId();

        // 測試多個用戶同時下單
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 紀錄成功與失敗下單人數
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 模擬多用戶併發請求
        for (int i = 0; i < threadCount; i++) {
            String account = "buyer_" + i;

            OrderRequest request = new OrderRequest();
            OrderRequest.ProductItemRequest itemReq = new OrderRequest.ProductItemRequest();
            itemReq.setProductId(productId);
            itemReq.setQuantity(1);
            request.setItems(List.of(itemReq));

            executorService.submit(() -> {
                try {
                    orderService.createOrder(account, request);
                    successCount.incrementAndGet();
                } catch (AppException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待全數請求處理完畢
        latch.await();
        executorService.shutdown();

        // 驗證
        Product finalProduct = productRepository.findById(productId).orElseThrow();

        System.out.println("=====================================");
        System.out.println("測試結束！");
        System.out.println("成功人數: " + successCount.get());
        System.out.println("失敗人數: " + failCount.get());
        System.out.println("剩餘庫存: " + finalProduct.getStockQuantity());
        System.out.println("=====================================");

        assertEquals(150, successCount.get(), "成功人數正確");
        assertEquals(850, failCount.get(), "失敗人數正確");
        assertEquals(0, finalProduct.getStockQuantity(), "剩餘庫存正確");
    }
}