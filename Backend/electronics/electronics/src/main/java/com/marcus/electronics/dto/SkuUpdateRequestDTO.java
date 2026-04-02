package com.marcus.electronics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SkuUpdateRequestDTO {
    private String skuCode;

    @NotNull(message = "Giá SKU bắt buộc")
    @Min(value = 0, message = "Giá không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stock;

    private String imageUrl;
}