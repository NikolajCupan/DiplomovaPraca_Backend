package com.backend.thesis.utility;

import com.backend.thesis.domain.dto.Frequency;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Helper {
    private Helper() {
    }

    private static String normalizeDateFormat(final String dateFormat) {
        StringBuilder builder = new StringBuilder();
        for (char c : dateFormat.toCharArray()) {
            switch (c) {
                case 'Y':
                    builder.append('y');
                    break;
                case 'm':
                    builder.append('M');
                    break;
                case 'D':
                    builder.append('d');
                    break;
                case 'h':
                    builder.append('H');
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }

        return builder.toString();
    }

    private static boolean patternIncludesHours(final String pattern) {
        return pattern.contains("h") || pattern.contains("H");
    }

    public static String getUniqueID() {
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }

    public static String localDateTimeToString(final LocalDateTime dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DEFAULT_DATE_TIME_FORMAT);
        return dateTime.format(formatter);
    }

    public static Long stringToLong(final String string) {
        return Long.parseLong(string);
    }

    public static boolean stringToBoolean(final String stringBoolean) {
        return Boolean.parseBoolean(stringBoolean);
    }

    public static LocalDateTime stringToLocalDateTime(final String stringLocalDate) {
        return Helper.stringToLocalDateTime(stringLocalDate, Constants.DEFAULT_DATE_TIME_FORMAT);
    }

    public static LocalDateTime stringToLocalDateTime(final String stringLocalDate, final String dateFormat) {
        final String normalizedDateFormat = Helper.normalizeDateFormat(dateFormat);
        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern(normalizedDateFormat);

        if (Helper.patternIncludesHours(normalizedDateFormat)) {
            return LocalDateTime.parse(stringLocalDate, pattern);
        } else {
            return LocalDate.parse(stringLocalDate, pattern).atStartOfDay();
        }
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

    public static boolean stringIsNumeric(final String stringToCheck) {
        try {
            Double.parseDouble(stringToCheck);
            return true;
        } catch (final Exception exception) {
            return false;
        }
    }
}
