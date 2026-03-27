package com.marcus.electronics.controller;

import com.marcus.electronics.dto.AuthResponse;
import com.marcus.electronics.dto.LoginRequest;
import com.marcus.electronics.repository.RoleRepository;
import com.marcus.electronics.repository.UserRepository;
import com.marcus.electronics.security.JwtTokenProvider;
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
@RequiredArgsConstructor // Tự động inject bean (thay cho @Autowired)
@CrossOrigin("*") // Cho phép Frontend gọi vào
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