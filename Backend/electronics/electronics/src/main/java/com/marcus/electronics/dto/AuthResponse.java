// AuthResponse.java
package com.marcus.electronics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role; // Trả về role để Frontend biết đường chuyển hướng (Admin/User)
}