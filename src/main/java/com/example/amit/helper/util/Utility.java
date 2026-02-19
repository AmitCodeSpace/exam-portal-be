package com.example.amit.helper.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Utility {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JavaTimeModule JAVA_TIME_MODULE = new JavaTimeModule();

    public static ObjectMapper getMapper() {
        MAPPER.registerModule(JAVA_TIME_MODULE);
        return MAPPER;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
