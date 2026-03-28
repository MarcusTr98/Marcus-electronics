package com.marcus.electronics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Không tìm thấy dữ liệu → 404
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> handleDataNotFound(DataNotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // 2. Lỗi validate form (@Valid) → 400 kèm chi tiết từng field
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        Map<String, Object> body = error(HttpStatus.BAD_REQUEST, "Dữ liệu đầu vào không hợp lệ").getBody();
        if (body != null)
            body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // 3. Lỗi nghiệp vụ tường minh (xóa danh mục còn con, slug trùng...) → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
        return error(HttpStatus.CONFLICT, e.getMessage()); // 409 Conflict phù hợp hơn 400
    }

    // 4. Các RuntimeException không tường minh còn lại → 400
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 5. Lỗi hệ thống (DB crash, NullPointer...) → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobal(Exception e) {
        Map<String, Object> body = error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống nội bộ").getBody();
        // Tắt dòng debug này khi lên production để không lộ thông tin nội bộ
        if (body != null)
            body.put("debug", e.getMessage());
        return ResponseEntity.internalServerError().body(body);
    }

    // ── Helper dùng chung
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}