package com.marcus.electronics.controller;

import com.marcus.electronics.dto.OrderDetailResponseDTO;
import com.marcus.electronics.dto.OrderResponseDTO;
import com.marcus.electronics.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping("")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        if (!body.containsKey("status")) {
            return ResponseEntity.badRequest().body("Thiếu trạng thái đơn hàng");
        }

        String newStatus = body.get("status");
        orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công");
    }

    // Lấy chi tiết ruột đơn hàng (Sản phẩm khách mua)
    @GetMapping("/{id}/details")
    public ResponseEntity<List<OrderDetailResponseDTO>> getOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetailsAdmin(id));
    }
}