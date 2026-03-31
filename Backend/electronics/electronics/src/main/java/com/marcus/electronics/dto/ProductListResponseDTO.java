package com.marcus.electronics.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private String thumbnailUrl;
    private String categoryName;
    private BigDecimal basePrice;
    private Boolean active;

    private Integer totalStock;
}