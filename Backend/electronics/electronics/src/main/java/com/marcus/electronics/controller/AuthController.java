package com.marcus.electronics.controller;

import com.marcus.electronics.dto.AuthResponse;
import com.marcus.electronics.dto.LoginRequest;
import com.marcus.electronics.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor // Tự động inject bean (thay cho @Autowired)
@CrossOrigin("*") // Cho phép Frontend gọi vào
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Xác thực username/password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // 2. Nếu không có lỗi thì set thông tin vào context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Tạo token
            String jwt = tokenProvider.generateToken(authentication.getName());
            String role = authentication.getAuthorities().toString();

            return ResponseEntity.ok(new AuthResponse(jwt, loginRequest.getUsername(), role));

        } catch (Exception ex) {
            // QUAN TRỌNG: Bắt lỗi BadCredentialsException ở đây
            return ResponseEntity.status(401).body("Đăng nhập thất bại: Sai Username hoặc Password");
        }
    }
}