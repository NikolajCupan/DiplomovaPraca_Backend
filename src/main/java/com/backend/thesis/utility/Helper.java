package com.backend.thesis.utility;

import com.backend.thesis.domain.dto.Frequency;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Helper {
    private Helper() {
    }

    public static String getUniqueID() {
        return UUID.randomUUID().toString();
    }

    public static <T> ResponseEntity<T> prepareResponse(final T body, final HttpStatus status) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<>(body, responseHeaders, status);
    }

    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }

    public static boolean stringToBoolean(final String stringBoolean) {
        return Boolean.parseBoolean(stringBoolean);
    }

    public static LocalDateTime stringToLocalDateTime(final String stringLocalDate) {
        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH");
        return LocalDateTime.parse(stringLocalDate, pattern);
    }

    public static Frequency stringToFrequency(final String stringFrequency) {
        return switch (stringFrequency) {
            case "hourly" -> Frequency.HOURLY;
            case "daily" -> Frequency.DAILY;
            case "weekly" -> Frequency.WEEKLY;
            case "monthly" -> Frequency.MONTHLY;
            case "quarterly" -> Frequency.QUATERLY;
            case "yearly" -> Frequency.YEARLY;
            default -> throw new IllegalArgumentException("Invalid string frequency " + stringFrequency);
        };
    }
}
