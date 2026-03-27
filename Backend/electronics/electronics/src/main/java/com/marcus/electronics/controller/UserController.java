package com.marcus.electronics.controller;

import com.marcus.electronics.dto.UserRegisterDTO;
import com.marcus.electronics.model.User;
import com.marcus.electronics.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    // POST /api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        try {
            User newUser = userService.register(registerDTO);
            return ResponseEntity.ok("Đăng ký thành công tài khoản: " + newUser.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}