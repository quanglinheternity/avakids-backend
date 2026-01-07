package com.example.avakids_backend.util.codeGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class CodeGenerator {

    private static final DateTimeFormatter ORDER_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private CodeGenerator() {}

    public static String generateCode(String prefix) {
        String timestamp = LocalDateTime.now().format(ORDER_TIME_FORMAT);
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return prefix + timestamp + random;
    }
}
