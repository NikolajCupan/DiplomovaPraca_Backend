package com.backend.thesis.utility;

import com.backend.thesis.domain.dto.Frequency;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

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

    public static String trimTrailingZeroes(final String string) {
        return !string.contains(".") ? string : string.replaceAll("0*$", "").replaceAll("\\.$", "");
    }

    public static Long stringToLong(final String string) {
        return Long.parseLong(string);
    }

    public static Double stringToDouble(final String string) {
        return Double.parseDouble(string);
    }

    public static Optional<Integer> tryStringToInt(final Optional<String> string) {
        if (string.isPresent() && !string.get().isEmpty()) {
            return Optional.of(Integer.parseInt(string.get()));
        }

        return Optional.empty();
    }

    public static Optional<Double> tryStringToDouble(final Optional<String> string) {
        if (string.isPresent() && !string.get().isEmpty()) {
            return Optional.of(Double.parseDouble(string.get()));
        }

        return Optional.empty();
    }

    public static Optional<Boolean> tryStringToBoolean(final Optional<String> string) {
        if (string.isPresent() && !string.get().isEmpty()) {
            return Optional.of(Boolean.parseBoolean(string.get()));
        }

        return Optional.empty();
    }

    public static Long intToLong(final Integer integer) {
        return Long.valueOf(integer);
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
            case "quarterly" -> Frequency.QUARTERLY;
            case "yearly" -> Frequency.YEARLY;
            default -> throw new IllegalArgumentException("Invalid string frequency " + stringFrequency);
        };
    }

    public static MultipartFile fileToMultipartFile(final File file) throws IOException {
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            final byte[] fileBytes = fileInputStream.readAllBytes();

            return new MockMultipartFile(
                    "file",
                    file.getName(),
                    "text/plain",
                    fileBytes
            );
        }
    }

    public static boolean stringIsNumeric(final String stringToCheck) {
        try {
            Double.parseDouble(stringToCheck);
            return true;
        } catch (final Exception exception) {
            return false;
        }
    }

    public static List<Type.DatasetRow> rawRowsToRows(final String rawRows) {
        final List<Type.DatasetRow> parsedRows = new ArrayList<>();

        try {
            final JSONArray json = new JSONArray(rawRows);
            for (int i = 0; i < json.length(); ++i) {
                final JSONObject row = json.getJSONObject(i);

                final LocalDateTime dateTime = Helper.stringToLocalDateTime(row.get("date").toString());
                final String valueString = row.get("value").toString().equals(Constants.EMPTY_VALUE) ? "" : row.get("value").toString();

                parsedRows.add(new Type.DatasetRow(dateTime, valueString));
            }

            return parsedRows;
        } catch (final Exception exception) {
            return parsedRows;
        }
    }

    public static LocalDateTime getNextDate(final LocalDateTime previousDateTime, final Frequency frequency) {
        return switch (frequency) {
            case Frequency.HOURLY -> previousDateTime.plusHours(1);
            case Frequency.DAILY -> previousDateTime.plusDays(1);
            case Frequency.WEEKLY -> previousDateTime.plusWeeks(1);
            case Frequency.MONTHLY -> previousDateTime.plusMonths(1);
            case Frequency.QUARTERLY -> previousDateTime.plusMonths(3);
            case Frequency.YEARLY -> previousDateTime.plusYears(1);
        };
    }

    public static LocalDateTime getPreviousDate(final LocalDateTime currentDateTime, final Frequency frequency) {
        return switch (frequency) {
            case Frequency.HOURLY -> currentDateTime.minusHours(1);
            case Frequency.DAILY -> currentDateTime.minusDays(1);
            case Frequency.WEEKLY -> currentDateTime.minusWeeks(1);
            case Frequency.MONTHLY -> currentDateTime.minusMonths(1);
            case Frequency.QUARTERLY -> currentDateTime.minusMonths(3);
            case Frequency.YEARLY -> currentDateTime.minusYears(1);
        };
    }

    public static boolean isInvalidDate(final LocalDateTime date) {
        return date.isAfter(Constants.MAXIMUM_DATE_TIME);
    }
}
