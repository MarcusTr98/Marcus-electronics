package com.marcus.electronics.controller;

import com.marcus.electronics.dto.OrderRequestDTO;
import com.marcus.electronics.model.Order;
import com.marcus.electronics.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    // POST /api/v1/order
    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderRequestDTO orderDTO) {
        try {
            Order newOrder = orderService.createOrder(orderDTO);
            return ResponseEntity.ok("Đặt hàng thành công! Mã đơn: " + newOrder.getId());
        } catch (Exception e) {
            // Trả về lỗi 400 nếu hết hàng hoặc lỗi dữ liệu
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}