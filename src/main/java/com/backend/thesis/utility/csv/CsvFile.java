package com.backend.thesis.utility.csv;

import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.other.RequestException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

    public void saveToFile(final String datasetName) throws RequestException {
        final File file = new File(Constants.STORAGE_DATASET_PATH, datasetName + ".csv");
        try (final BufferedWriter writter = new BufferedWriter(new FileWriter(file))) {
            writter.write(this.dateColumnName + "," + this.dataColumnName + "\n");

            for (int i = 0; i < this.data.size(); i++) {
                final Row row = this.data.get(i);
                writter.write(Helper.localDateTimeToString(row.dateTime) + "," + row.value);

                if (i != this.data.size() - 1) {
                    writter.write("\n");
                }
            }
        } catch (final Exception exception) {
            throw new RequestException("Súbor sa nepodarilo uložiť");
        }
    }

    public LocalDateTime getStartDateTime() {
        return this.data.getFirst().dateTime();
    }

    public LocalDateTime getEndDateTime() {
        return this.data.getLast().dateTime();
    }
}
