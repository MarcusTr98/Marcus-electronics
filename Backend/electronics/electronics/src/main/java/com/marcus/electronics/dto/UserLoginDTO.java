package com.marcus.electronics.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @NotBlank(message = "Vui lòng nhập username")
    private String username;

    @NotBlank(message = "Vui lòng nhập password")
    private String password;
}