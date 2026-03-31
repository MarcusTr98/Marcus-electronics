package com.marcus.electronics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.marcus.electronics.service.FileStorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/files")
@CrossOrigin("*")
public class FileController {
    @Autowired
    private FileStorageService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String filePath = fileService.storeFile(file);
        return ResponseEntity.ok(filePath);
    }
}
