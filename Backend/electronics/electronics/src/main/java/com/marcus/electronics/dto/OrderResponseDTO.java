package com.marcus.electronics.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String note;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalMoney;
    private String paymentMethod;
}