package com.marcus.electronics.service;

import com.marcus.electronics.dto.CustomerResponseDTO;
import com.marcus.electronics.model.Order;
import com.marcus.electronics.model.User;
import com.marcus.electronics.repository.OrderRepository;
import com.marcus.electronics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        // 1. Lấy tất cả user không phải ADMIN
        List<User> users = userRepository.findAll().stream()
                .filter(u -> !u.getRole().getName().contains("ADMIN"))
                .collect(Collectors.toList());

        // 2. TỐI ƯU HIỆU NĂNG: Lấy toàn bộ đơn hàng 1 lần duy nhất để tránh lỗi N+1
        // Query
        List<Order> allOrders = orderRepository.findAll();

        // 3. Map sang DTO và tính toán trên RAM
        return users.stream().map(user -> {
            // Lọc ra các đơn hàng thuộc về user này
            List<Order> userOrders = allOrders.stream()
                    .filter(o -> o.getUser() != null && o.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());

            int totalOrders = userOrders.size();

            // Tính tổng chi tiêu (Chỉ cộng tiền những đơn Đã Giao Thành Công)
            BigDecimal totalSpent = userOrders.stream()
                    .filter(o -> "DELIVERED".equals(o.getStatus()))
                    .map(Order::getTotalMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return CustomerResponseDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .isActive(user.getIsActive())
                    .createdAt(user.getCreatedAt())
                    .totalOrders(totalOrders)
                    .totalSpent(totalSpent)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void toggleCustomerStatus(Long customerId, Boolean isActive) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Chặn không cho vô tình khóa nhầm Admin qua endpoint này
        if (user.getRole().getName().contains("ADMIN")) {
            throw new RuntimeException("Không được phép thay đổi trạng thái Admin từ luồng này");
        }

        user.setIsActive(isActive);
        userRepository.save(user);
    }
}