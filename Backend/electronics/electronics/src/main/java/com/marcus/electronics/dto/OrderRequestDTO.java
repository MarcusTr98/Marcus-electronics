package com.marcus.electronics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    @JsonProperty("full_name")
    @NotBlank(message = "Tên người nhận bắt buộc")
    private String fullName;

    @JsonProperty("phone_number")
    @NotBlank(message = "SĐT bắt buộc")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ bắt buộc")
    private String address;

    private String note;

    @JsonProperty("payment_method")
    private String paymentMethod; // COD, VNPAY

    @JsonProperty("cart_items")
    @NotEmpty(message = "Giỏ hàng không được trống")
    private List<CartItemDTO> cartItems;

    // DTO con: 1 dòng trong giỏ hàng
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItemDTO {
        @JsonProperty("sku_id")
        @NotNull(message = "Phải có mã sản phẩm")
        private Long skuId;

        @Min(value = 1, message = "Số lượng ít nhất là 1")
        private Integer quantity;
    }
}