package com.backend.thesis.utility;

import java.time.LocalDateTime;

public class Type {
    public record ActionResult<T>(
            boolean success,
            String message,
            T data
    ) {
        public ActionResult(final boolean success, final String message, final T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }

    public record RequestResult<T>(
            String message,
            T data
    ) {
        public RequestResult(final String message, final T data) {
            this.message = message;
            this.data = data;
        }
    }

    public record DatasetRow(
            LocalDateTime dateTime,
            String value
    ) {
    }
}
