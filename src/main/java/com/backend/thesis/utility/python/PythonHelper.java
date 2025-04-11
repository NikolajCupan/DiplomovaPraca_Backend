package com.backend.thesis.utility.python;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class PythonHelper {
    public static String convertToPythonFrequencyType(final String frequency) {
        return switch (frequency) {
            case "hourly" -> "H";
            case "daily" -> "D";
            case "weekly" -> "7D";
            case "monthly" -> "MS";
            case "quarterly" -> "QS";
            case "yearly" -> "YS";
            default -> "";
        };
    }

    public static <T> void appendIfAvailable(
            final JSONObject json,
            final String key,
            final Optional<T> optionalValue
    ) throws JSONException {
        if (optionalValue.isPresent()) {
            final T value = optionalValue.get();

            if (value instanceof String stringValue) {
                if (!stringValue.isEmpty()) {
                    json.put(key, stringValue);
                }
            } else {
                json.put(key, value);
            }
        }
    }

    public enum PythonExecutionResult {
        SUCCESS,
        FAILURE,
        TIMEOUT
    }
}
