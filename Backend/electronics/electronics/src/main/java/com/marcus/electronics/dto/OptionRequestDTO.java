package com.marcus.electronics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class OptionRequestDTO {
    @NotBlank(message = "Tên option không được để trống")
    private String name; // VD: "Màu sắc"

    @NotEmpty(message = "Phải có ít nhất 1 giá trị")
    private List<String> values; // VD: ["Đỏ", "Xanh Dương", "Đen"]
}