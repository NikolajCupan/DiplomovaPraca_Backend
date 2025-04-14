package com.backend.thesis.utility;

import java.time.LocalDateTime;

public class Constants {
    private Constants() {
    }

    public static final String SESSION_COOKIE_NAME = "session_cookie_id";

    public static final LocalDateTime MAXIMUM_DATE_TIME = LocalDateTime.of(2250, 1, 1, 0, 0);
    public static final int MAXIMUM_ROWS_COUNT = 25_000;

    public static final String DEFAULT_DATESET_NAME = "dataset";
    public static final String DEFAULT_DATE_COLUMN_NAME = "date";
    public static final String DEFAULT_DATA_COLUMN_NAME = "data";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd-HH";

    public static final String STORAGE_DATASET_PATH = "storage/dataset";

    public static final String CSV_DELIMITER = ",";
    public static final String EMPTY_VALUE = "-";

    public static final int NEURAL_NETWORK_TIMEOUT_MS = 300_000;
}
