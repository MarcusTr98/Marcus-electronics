package com.marcus.electronics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    private String slug;

    @JsonProperty("parentId")
    private Integer parentId;

    @JsonProperty("parentName")
    private String parentName;

    @JsonProperty("isActive")
    private Boolean active;
}