package com.marcus.electronics.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @JsonProperty("full_name") // Frontend gửi "full_name" -> Java map vào "fullName"
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;
}