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
    private BigDecimal price; // Giá hiển thị (thường là giá thấp nhất)
    private String categoryName;
}