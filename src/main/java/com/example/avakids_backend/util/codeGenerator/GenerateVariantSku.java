package com.example.avakids_backend.util.codeGenerator;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.List;

public final class GenerateVariantSku {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateBase(String baseCode, List<String> values) {
        StringBuilder builder = new StringBuilder(baseCode);

        for (String value : values) {
            String code = normalize(value);
            if (!code.isEmpty()) {
                builder.append("-").append(code);
            }
        }

        return builder.toString();
    }

    public static String generateWithRandom(String baseCode, List<String> values, int randomLength) {
        String base = generateBase(baseCode, values);
        return base + "-" + randomAlphaNumeric(randomLength);
    }

    public static String randomAlphaNumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private static String normalize(String input) {
        if (input == null || input.isBlank()) return "";

        String noAccent =
                Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return noAccent.toUpperCase().replaceAll("[^A-Z0-9]", "").substring(0, Math.min(3, noAccent.length()));
    }
}
