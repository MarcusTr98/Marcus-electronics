package com.marcus.electronics.service;

import com.marcus.electronics.dto.CustomerResponseDTO;
import com.marcus.electronics.model.User;
import com.marcus.electronics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        return userRepository.findAllCustomers().stream()
                .map(u -> CustomerResponseDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .fullName(u.getFullName())
                        .email(u.getEmail())
                        .phoneNumber(u.getPhoneNumber())
                        .isActive(u.getIsActive())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleCustomerStatus(Long customerId, Boolean isActive) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Cú pháp bảo mật: Chặn không cho vô tình khóa nhầm Admin qua endpoint này
        if (user.getRole().getName().contains("ADMIN")) {
            throw new RuntimeException("Không được phép thay đổi trạng thái Admin từ luồng này");
        }

        user.setIsActive(isActive);
        userRepository.save(user);
    }
}