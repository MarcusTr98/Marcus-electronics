package com.marcus.electronics.controller;

import com.marcus.electronics.dto.LoginRequest;
import com.marcus.electronics.model.User;
import com.marcus.electronics.repository.RoleRepository;
import com.marcus.electronics.repository.UserRepository;
import com.marcus.electronics.security.JwtTokenProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 1. Xác thực Username & Password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2. Lấy thông tin User từ DB để nhồi vào Token
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            // 3. Gọi hàm generateToken MỚI (truyền đủ 3 tham số)
            String jwt = tokenProvider.generateToken(user.getUsername(), user.getRole().getName(), user.getId());

            // 4. Trả về Frontend (Giả sử bạn dùng AuthResponse, hoặc dùng Map)
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("token", jwt);
            response.put("role", user.getRole().getName());
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(401).body("Sai tên đăng nhập hoặc mật khẩu");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody com.marcus.electronics.dto.UserRegisterDTO registerDTO) {
        // 1. Kiểm tra trùng lặp
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng!");
        }

        // 2. Lấy quyền mặc định là USER cho người mới đăng ký
        com.marcus.electronics.model.Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy Role USER."));

        // 3. Tạo Entity User và Mã hóa mật khẩu
        com.marcus.electronics.model.User newUser = com.marcus.electronics.model.User.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword())) // Băm mật khẩu bằng BCrypt
                .fullName(registerDTO.getFullName())
                .email(registerDTO.getEmail())
                .phoneNumber(registerDTO.getPhoneNumber())
                .role(userRole)
                .isActive(true)
                .build();

        // 4. Lưu xuống Database
        userRepository.save(newUser);

        return ResponseEntity.ok("Đăng ký tài khoản thành công!");
    }
}