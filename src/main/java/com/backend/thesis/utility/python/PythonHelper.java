package com.backend.thesis.utility.python;

public class PythonHelper {
    public static String convertToPythonFrequencyType(final String frequency) {
        return switch (frequency) {
            case "hourly" -> "H";
            case "daily" -> "D";
            case "weekly" -> "W";
            case "monthly" -> "MS";
            case "quarterly" -> "QS";
            case "yearly" -> "YS";
            default -> "";
        };
    }
}
