package com.marcus.electronics.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderDetailResponseDTO {
    private Long detailId;
    private String productName;
    private String skuCode;
    private String variantDetail;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal unitPrice; // Giá chốt tại thời điểm mua (không phải giá hiện tại)
    private BigDecimal lineTotal; // Thành tiền của dòng này
}