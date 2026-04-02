package com.marcus.electronics.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
}