package com.backend.thesis.utility.csv;

import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
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

    private String dateColumnName;
    private String dataColumnName;
    private final List<Type.DatasetRow> data;

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

    public void trim() throws RequestException {
        while (true) {
            if (this.data.isEmpty()) {
                throw new RequestException("Dataset po orezaní neobsahuje žiadne údaje");
            } else if (this.data.getFirst().value().isEmpty()) {
                this.data.removeFirst();
            } else {
                break;
            }
        }

        while (true) {
            if (this.data.isEmpty()) {
                throw new RequestException("Dataset po orezaní neobsahuje žiadne údaje");
            } else if (this.data.getLast().value().isEmpty()) {
                this.data.removeLast();
            } else {
                break;
            }
        }
    }

    public void addRow(final LocalDateTime dateTime, final String value) {
        this.data.add(new Type.DatasetRow(dateTime, value));
    }

    public void editRow(final LocalDateTime dateTime, final String newValue) throws RequestException {
        for (int i = 0; i < this.data.size(); ++i) {
            if (this.data.get(i).dateTime().isEqual(dateTime)) {
                this.data.set(i, new Type.DatasetRow(dateTime, newValue));
                return;
            }
        }

        throw new RequestException("Riadok s daným dátumom neexistuje");
    }

    public void saveToFile(final String datasetName) throws RequestException {
        final File file = new File(Constants.STORAGE_DATASET_PATH, datasetName + ".csv");
        try (final BufferedWriter writter = new BufferedWriter(new FileWriter(file))) {
            writter.write(this.dateColumnName + Constants.CSV_DELIMITER + this.dataColumnName + "\n");

            for (int i = 0; i < this.data.size(); i++) {
                final Type.DatasetRow datasetRow = this.data.get(i);
                writter.write(Helper.localDateTimeToString(datasetRow.dateTime()) + Constants.CSV_DELIMITER + datasetRow.value());

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

    public List<Type.DatasetRow> getData() {
        return this.data;
    }

    public void setDateColumnName(final String dateColumnName) {
        this.dateColumnName = dateColumnName;
    }

    public void setDataColumnName(final String dataColumnName) {
        this.dataColumnName = dataColumnName;
    }
}
