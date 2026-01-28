package com.marcus.electronics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Integer id;

    @NotEmpty(message = "Tên danh mục không được để trống")
    private String name;

    private String slug;

    @JsonProperty("parent_id")
    private Integer parentId; // Chỉ cần ID của cha là đủ
}