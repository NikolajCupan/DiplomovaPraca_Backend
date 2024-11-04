package com.backend.thesis.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
}
