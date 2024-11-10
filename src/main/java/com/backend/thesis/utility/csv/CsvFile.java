package com.backend.thesis.utility.csv;

import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.other.RequestException;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvFile {
    public static CsvFile readFromFile(final String fileName) throws RequestException {
        final File file = new File(Constants.STORAGE_DATASET_PATH, fileName + ".csv");
        CsvFile csvFile = new CsvFile();

        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final String[] header = reader.readLine().split(Constants.CSV_DELIMITER);
            csvFile.setDateColumnName(header[0]);
            csvFile.setDataColumnName(header[1]);

            while (true) {
                final String row = reader.readLine();
                if (row == null) {
                    break;
                }

                final String[] rowContent = row.split(Constants.CSV_DELIMITER);

                if (rowContent.length == 1) {
                    csvFile.addRow(Helper.stringToLocalDateTime(rowContent[0]), "");
                } else {
                    csvFile.addRow(Helper.stringToLocalDateTime(rowContent[0]), rowContent[1]);
                }
            }

            return csvFile;
        } catch (final Exception exception) {
            throw new RequestException("Chyba pri načítavaní súboru");
        }
    }

    private record Row(
            LocalDateTime dateTime,
            String value
    ) {
    }

    private String dateColumnName;
    private String dataColumnName;
    private final List<Row> data;

    public CsvFile() {
        this.dateColumnName = "";
        this.dataColumnName = "";
        this.data = new ArrayList<>();
    }

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
            writter.write(this.dateColumnName + Constants.CSV_DELIMITER + this.dataColumnName + "\n");

            for (int i = 0; i < this.data.size(); i++) {
                final Row row = this.data.get(i);
                writter.write(Helper.localDateTimeToString(row.dateTime) + Constants.CSV_DELIMITER + row.value);

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

    public void setDateColumnName(final String dateColumnName) {
        this.dateColumnName = dateColumnName;
    }

    public void setDataColumnName(final String dataColumnName) {
        this.dataColumnName = dataColumnName;
    }
}
