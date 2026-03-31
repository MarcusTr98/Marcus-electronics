package com.marcus.electronics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Tên sản phẩm bắt buộc")
    private String name;

    private String description;

    @NotNull(message = "Phải chọn danh mục")
    private Integer categoryId;

    private String thumbnailUrl;

    @Min(value = 0, message = "Giá không được âm")
    private BigDecimal basePrice;

    // Các thông số ship
    private Integer weightG;
    private Integer lengthCm;
    private Integer widthCm;
    private Integer heightCm;

    @NotBlank(message = "Đường dẫn không được trống")
    private String slug;

    private Boolean active;
}