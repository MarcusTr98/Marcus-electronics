package com.marcus.electronics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/files")
@CrossOrigin("*")
public class FileController {

    // Thư mục lưu trữ thực tế trên ổ cứng (Nên để ngoài project)
    private final String UPLOAD_DIR = "C:/marcus_uploads/products/";

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty())
            return ResponseEntity.badRequest().body("File trống");

        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath))
            Files.createDirectories(uploadPath);

        // Tạo tên file duy nhất để tránh trùng lặp
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Lưu file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL để Frontend gọi (Cần cấu hình ResourceHandler ở bước sau)
        return ResponseEntity.ok("/images/products/" + fileName);
    }
}