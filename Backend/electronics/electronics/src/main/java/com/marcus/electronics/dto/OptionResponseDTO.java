package com.marcus.electronics.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class OptionResponseDTO {
    private Long optionId;
    private String optionName;

    // ĐÂY LÀ CHÌA KHÓA: Map chứa Cặp <Tên giá trị, ID trong Database>
    // Ví dụ: {"Đỏ": 15, "Xanh": 16}
    private Map<String, Long> valueIds;
}