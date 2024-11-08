package com.backend.thesis.utility.csv;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvFile {
    private record Row(
            LocalDateTime dateTime,
            String value
    ) {
    }

    private final String dateColumnName;
    private final String dataColumnName;
    private final List<Row> data;

    public CsvFile(final String dateColumnName, final String dataColumnName) {
        this.dateColumnName = dateColumnName;
        this.dataColumnName = dataColumnName;
        this.data = new ArrayList<>();
    }

    public void addRow(final LocalDateTime dateTime, final String value) {
        this.data.add(new Row(dateTime, value));
    }
}
