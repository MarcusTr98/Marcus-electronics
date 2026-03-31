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

    // Frontend cần ID cha để làm logic chọn Select Box trong Modal
    @JsonProperty("parent_id")
    private Integer parentId;

    // THÊM MỚI: Frontend cần Tên cha để hiển thị lên Bảng
    private String parentName;

    // THÊM MỚI: Frontend cần Trạng thái để hiển thị badge Xanh/Đỏ
    private Boolean active;
}