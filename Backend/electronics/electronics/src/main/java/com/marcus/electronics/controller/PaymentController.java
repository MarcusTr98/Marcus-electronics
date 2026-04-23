package com.marcus.electronics.controller;

import com.marcus.electronics.model.Order;
import com.marcus.electronics.model.Payment;
import com.marcus.electronics.repository.OrderRepository;
import com.marcus.electronics.repository.PaymentRepository;
import com.marcus.electronics.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // 1. API Gửi từ Frontend lên để lấy Link VNPAY
    @GetMapping("/vnpay/create-url/{orderId}")
    public ResponseEntity<?> createPaymentUrl(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            String paymentUrl = paymentService.createVnPayPaymentUrl(orderId, request);
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tạo URL thanh toán: " + e.getMessage());
        }
    }

    // 2. API để Frontend gọi lại xác nhận sau khi VNPAY chuyển hướng về
    @GetMapping("/vnpay/return")
    public ResponseEntity<?> paymentReturn(@RequestParam Map<String, String> queryParams) {
        try {
            boolean isAuthentic = paymentService.verifyVnPayCallback(queryParams);
            if (!isAuthentic) {
                return ResponseEntity.badRequest().body("Chữ ký không hợp lệ! Nghi ngờ gian lận.");
            }

            String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
            String orderIdStr = queryParams.get("vnp_TxnRef");
            String amountStr = queryParams.get("vnp_Amount");
            String transactionCode = queryParams.get("vnp_TransactionNo");

            Long orderId = Long.parseLong(orderIdStr);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

            // Nếu thanh toán thành công (Mã 00 của VNPAY)
            if ("00".equals(vnp_ResponseCode)) {
                // Cập nhật trạng thái đơn hàng
                order.setStatus("PROCESSING"); // Đã thanh toán, chuyển sang xử lý đóng gói
                orderRepository.save(order);

                // Lưu lịch sử giao dịch vào bảng Payment
                Payment payment = Payment.builder()
                        .order(order)
                        .amount(new BigDecimal(amountStr).divide(new BigDecimal(100))) // VNPAY nhân 100 nên phải chia
                                                                                       // lại
                        .transactionCode(transactionCode)
                        .status("SUCCESS")
                        .build();
                paymentRepository.save(payment);

                return ResponseEntity.ok("Thanh toán thành công!");
            } else {
                return ResponseEntity.badRequest()
                        .body("Giao dịch bị hủy hoặc thất bại (Mã lỗi: " + vnp_ResponseCode + ")");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi xử lý giao dịch: " + e.getMessage());
        }
    }
}