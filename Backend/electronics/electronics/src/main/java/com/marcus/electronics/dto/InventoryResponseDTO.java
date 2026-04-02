package com.marcus.electronics.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDTO {
    private Long skuId;
    private String productName;
    private String categoryName;
    private String skuCode;
    private String variantDetail; // Ví dụ: "Màu sắc: Titan Xanh, Bộ nhớ: 256GB"
    private BigDecimal price;
    private Integer stock;
}