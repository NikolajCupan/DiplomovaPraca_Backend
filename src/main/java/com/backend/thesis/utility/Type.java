package com.backend.thesis.utility;

import java.time.LocalDateTime;

public class Type {
    public enum ActionResultType {
        SUCCESS,
        FAILURE,
        TIMEOUT
    }

    public record ActionResult<T>(
            ActionResultType resultType,
            String message,
            T data
    ) {
        public ActionResult(final ActionResultType resultType, final String message, final T data) {
            this.resultType = resultType;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return this.resultType == ActionResultType.SUCCESS;
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
