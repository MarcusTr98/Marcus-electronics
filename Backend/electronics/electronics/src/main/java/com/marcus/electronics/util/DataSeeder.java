package com.marcus.electronics.util;

import com.marcus.electronics.model.Role;
import com.marcus.electronics.model.User;
import com.marcus.electronics.repository.RoleRepository;
import com.marcus.electronics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. ĐẢM BẢO ROLE "ADMIN" TỒN TẠI (Nếu chưa có thì tạo, có rồi thì lấy ra)
        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ADMIN");
            newRole.setDescription("Quản trị viên hệ thống");
            return roleRepository.save(newRole);
        });

        // 2. ĐẢM BẢO ROLE "USER" TỒN TẠI
        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("USER");
            newRole.setDescription("Khách hàng");
            return roleRepository.save(newRole);
        });

        // 3. TẠO TÀI KHOẢN ADMIN (Nếu chưa có)
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFullName("Marcus Administrator");
            admin.setEmail("admin@marcus.com");
            admin.setPhoneNumber("0907640098");

            // Mã hóa pass 123456
            admin.setPassword(passwordEncoder.encode("123456"));

            // Gán Role (Lúc này chắc chắn adminRole không null nữa)
            admin.setRole(adminRole);

            admin.setIsActive(true);

            userRepository.save(admin);

            System.out.println("---------------------------------------------");
            System.out.println("ĐÃ TẠO TÀI KHOẢN ADMIN MẪU THÀNH CÔNG!");
            System.out.println("Username: admin");
            System.out.println("Password: 123456");
            System.out.println("---------------------------------------------");
        } else {
            System.out.println("Tai khoan admin da ton tai. Bo qua buoc seed data.");
        }
    }
}