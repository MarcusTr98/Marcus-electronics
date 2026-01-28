package com.marcus.electronics.service;

import com.marcus.electronics.dto.UserLoginDTO;
import com.marcus.electronics.dto.UserRegisterDTO;
import com.marcus.electronics.model.Role;
import com.marcus.electronics.model.User;
import com.marcus.electronics.repository.RoleRepository;
import com.marcus.electronics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Tự động Inject Repository (khỏi cần @Autowired)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // Lưu ý: Sau này cần thêm PasswordEncoder để mã hóa mật khẩu
    // private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserRegisterDTO dto) {
        // 1. Check trùng
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        // 2. Lấy quyền User mặc định (ID 2 = ROLE_USER)
        Role roleUser = roleRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền USER"));

        // 3. Map DTO -> Entity
        User newUser = User.builder()
                .username(dto.getUsername())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .role(roleUser)
                .password(dto.getPassword()) // Lưu ý: Thực tế phải mã hóa passwordEncoder.encode(...)
                .isActive(true)
                .build();

        return userRepository.save(newUser);
    }

    public User login(UserLoginDTO dto) {
        // 1. Tìm user
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Sai username hoặc mật khẩu"));

        // 2. Check pass (So sánh thường, thực tế dùng passwordEncoder.matches)
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Sai username hoặc mật khẩu");
        }

        // 3. Nếu đúng hết thì trả về User (Controller sẽ sinh Token sau)
        return user;
    }
}