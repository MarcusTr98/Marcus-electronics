package com.marcus.electronics.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    // Đường dẫn tuyệt đối đến thư mục img của Frontend Marcus
    private final String uploadDir = "C:/path/to/marcus-frontend/public/img/";

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path targetLocation = Paths.get(uploadDir).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return "/img/" + fileName; // Trả về đường dẫn tương đối để VueJS dùng được luôn
    }
}
