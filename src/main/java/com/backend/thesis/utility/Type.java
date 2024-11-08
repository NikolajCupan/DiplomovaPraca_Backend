package com.backend.thesis.utility;

public class Type {
    public record ActionResult(
            boolean success,
            String error
    ) {
        public ActionResult(final boolean success) {
            this(success, "");
        }

        public ActionResult(final boolean success, final String error) {
            this.success = success;
            this.error = error;
        }
    }
}
