package com.marcus.electronics.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuRequestDTO {
    @NotBlank(message = "Mã SKU không được để trống")
    private String skuCode;

    @NotNull(message = "Giá SKU bắt buộc")
    @Min(value = 0, message = "Giá không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stock;

    private String imageUrl;

    // Chứa danh sách ID của các OptionValue (VD: ID của màu Đỏ, ID của 128GB)
    @NotNull(message = "Biến thể phải có ít nhất 1 thuộc tính")
    private List<Long> optionValueIds;
}