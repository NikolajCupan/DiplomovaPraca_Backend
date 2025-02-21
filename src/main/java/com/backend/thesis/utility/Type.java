package com.backend.thesis.utility;

import org.springframework.http.HttpStatus;

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

        public HttpStatus getHttpStatus() {
            return switch (this.resultType) {
                case SUCCESS -> HttpStatus.OK;
                case FAILURE -> HttpStatus.BAD_REQUEST;
                case TIMEOUT -> HttpStatus.REQUEST_TIMEOUT;
            };
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
