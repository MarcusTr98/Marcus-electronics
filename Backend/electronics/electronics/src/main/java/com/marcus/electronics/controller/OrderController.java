package com.marcus.electronics.controller;

import com.marcus.electronics.dto.OrderRequestDTO;
import com.marcus.electronics.model.Order;
import com.marcus.electronics.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import com.marcus.electronics.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenProvider tokenProvider;

    private Long getUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (tokenProvider.validateToken(token)) {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(tokenProvider.getSignInKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                Object userIdObj = claims.get("userId");
                if (userIdObj != null) {
                    return ((Number) userIdObj).longValue();
                }
            }
        }
        return null;
    }

    // 1. ĐẶT HÀNG (Dành cho mọi người, kể cả chưa đăng nhập)
    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid OrderRequestDTO orderDTO,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Long userId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (tokenProvider.validateToken(token)) {
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(tokenProvider.getSignInKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
                    Object idObj = claims.get("userId");
                    if (idObj instanceof Number) {
                        userId = ((Number) idObj).longValue();
                    }
                }
            }

            Order newOrder = orderService.createOrder(orderDTO, userId);
            return ResponseEntity.ok("Đặt hàng thành công! Mã đơn: " + newOrder.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi Server: " + e.getMessage());
        }
    }

    // 2. LẤY ĐƠN HÀNG CỦA TÔI
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập để xem lịch sử!");
            }
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi truy vấn đơn hàng: " + e.getMessage());
        }
    }

    // 3. KHÁCH HÀNG TỰ HỦY ĐƠN
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelMyOrder(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Long userId = getUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(401).body("Vui lòng đăng nhập!");
            }
            orderService.cancelOrderByClient(id, userId);
            return ResponseEntity.ok("Hủy đơn hàng thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi hủy đơn: " + e.getMessage());
        }
    }
}