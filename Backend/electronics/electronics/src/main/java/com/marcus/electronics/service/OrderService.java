package com.marcus.electronics.service;

import com.marcus.electronics.dto.OrderRequestDTO;
import com.marcus.electronics.model.*;
import com.marcus.electronics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SkuRepository skuRepository;
    private final UserRepository userRepository;

    @Transactional // Quan trọng: Nếu lỗi ở bước nào, Rollback lại hết (không trừ kho bậy)
    public Order createOrder(OrderRequestDTO requestDTO) {

        // 1. Tạo Order cha trước
        Order order = Order.builder()
                .fullName(requestDTO.getFullName())
                .phoneNumber(requestDTO.getPhoneNumber())
                .address(requestDTO.getAddress())
                .note(requestDTO.getNote())
                .paymentMethod(requestDTO.getPaymentMethod())
                .status("PENDING")
                .totalMoney(BigDecimal.ZERO) // Tính sau
                .build();

        // Save tạm để lấy ID
        Order savedOrder = orderRepository.save(order);

        BigDecimal grandTotal = BigDecimal.ZERO;

        // 2. Duyệt qua từng món trong giỏ hàng
        for (OrderRequestDTO.CartItemDTO item : requestDTO.getCartItems()) {

            // Tìm SKU trong kho
            Sku sku = skuRepository.findById(item.getSkuId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            // CHECK TỒN KHO
            if (sku.getStock() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + sku.getSkuCode() + " không đủ hàng!");
            }

            // TRỪ KHO (Cập nhật lại kho)
            sku.setStock(sku.getStock() - item.getQuantity());
            skuRepository.save(sku);

            // Tính tiền: Giá lúc mua * Số lượng
            BigDecimal itemTotal = sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(itemTotal);

            // Tạo OrderDetail (Lưu vết giá snapshot)
            OrderDetail detail = OrderDetail.builder()
                    .order(savedOrder)
                    .sku(sku)
                    .price(sku.getPrice()) // QUAN TRỌNG: Lưu giá hiện tại vào đây
                    .numberOfProducts(item.getQuantity())
                    .totalMoney(itemTotal)
                    .build();

            orderDetailRepository.save(detail);
        }

        // 3. Update lại tổng tiền cho Order cha
        savedOrder.setTotalMoney(grandTotal);
        return orderRepository.save(savedOrder);
    }
}