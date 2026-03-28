package com.marcus.electronics.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class SlugUtils {

    private SlugUtils() {
    }

    public static String generate(String input) {
        if (input == null || input.isBlank())
            return "";

        // 1. Trim và thay khoảng trắng bằng dấu gạch ngang
        String base = input.trim().replaceAll("\\s+", "-");

        // 2. Tách dấu ra khỏi ký tự gốc (NFD decomposition)
        String normalized = Normalizer.normalize(base, Normalizer.Form.NFD);

        // 3. Xóa dấu thanh / dấu phụ tiếng Việt
        String noAccent = Pattern.compile("\\p{InCombiningDiacriticalMarks}")
                .matcher(normalized)
                .replaceAll("");

        // 4. Chỉ giữ lại chữ thường, số và dấu gạch ngang
        return noAccent.replaceAll("[^a-zA-Z0-9-]", "").toLowerCase();
    }
}