package com.marcus.electronics.security;

import com.marcus.electronics.model.User;
import com.marcus.electronics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tìm user trong DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. Chuyển đổi sang UserDetails của Spring Security
        // Lưu ý: role.getName() cần có prefix "ROLE_" nếu dùng hasRole, hoặc để nguyên
        // nếu dùng hasAuthority
        // Ở đây mình giả sử Role name là "ADMIN", "USER"
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections
                        .singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase())));
    }
}