package com.backend.thesis.utility.csv;

import com.backend.thesis.domain.dto.Frequency;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.other.RequestException;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class CsvFile {
    public static CsvFile readFromFile(final String fileName) throws RequestException {
        final File file = new File(Constants.STORAGE_DATASET_PATH, fileName + ".csv");
        CsvFile csvFile = new CsvFile(fileName);

        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
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

    public static void deleteFile(final String fileName) throws RequestException {
        final File file = new File(Constants.STORAGE_DATASET_PATH, fileName + ".csv");

        if (!file.delete()) {
            throw new RequestException("Súbor nebol zmazaný");
        }
    }

    private final String fileName;
    private List<Type.DatasetRow> data;

    public CsvFile(final String fileName) {
        this.fileName = fileName;
        this.data = new ArrayList<>();
    }

    public boolean hasMissingValues() {
        for (final Type.DatasetRow row : this.data) {
            if (row.value().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void fillDates(final Frequency frequency) throws RequestException {
        final List<Type.DatasetRow> newData = new ArrayList<>();

        for (int rowIndex = 1; rowIndex < this.data.size(); ++rowIndex) {
            final Type.DatasetRow previousRow = this.data.get(rowIndex - 1);
            newData.add(previousRow);

            final Type.DatasetRow nextRow = this.data.get(rowIndex);
            final LocalDateTime expectedNextDate = Helper.getNextDate(previousRow.dateTime(), frequency);

            if (nextRow.dateTime().isAfter(expectedNextDate)) {
                // Fill interval with empty values
                LocalDateTime currentDate = expectedNextDate;

                while (!currentDate.isEqual(nextRow.dateTime())) {
                    newData.add(new Type.DatasetRow(currentDate, ""));
                    currentDate = Helper.getNextDate(currentDate, frequency);

                    if (Helper.isInvalidDate(currentDate)) {
                        throw new RequestException("Pri spracovaní dátumov nastala chyba");
                    }
                }
            }
        }

        final LocalDateTime newDataLastDate = newData.getLast().dateTime();
        final LocalDateTime originalDataLastDate = this.data.getLast().dateTime();

        if (newDataLastDate.isBefore(originalDataLastDate)) {
            final LocalDateTime nextDate = Helper.getNextDate(newDataLastDate, frequency);

            if (nextDate.isEqual(originalDataLastDate)) {
                newData.add(this.data.getLast());
            }
        }

        this.data = newData;
    }

    public boolean trim() throws RequestException {
        final Long originalCount = this.getRowsCount();

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

        final Long modifiedCount = this.getRowsCount();
        return !Objects.equals(originalCount, modifiedCount);
    }

    public Optional<Integer> getRowIndex(final LocalDateTime dateTime) {
        for (int i = 0; i < this.data.size(); ++i) {
            if (this.data.get(i).dateTime().isEqual(dateTime)) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    public void addRow(final LocalDateTime dateTime, final String value) {
        this.data.add(new Type.DatasetRow(dateTime, value));
    }

    public void editRow(final LocalDateTime dateTime, final String newValue, final Frequency frequency) throws RequestException {
        final Optional<Integer> rowIndex = this.getRowIndex(dateTime);
        if (rowIndex.isPresent()) {
            this.data.set(rowIndex.get(), new Type.DatasetRow(dateTime, newValue));
            return;
        }

        final LocalDateTime originalFirstDateTime = this.data.getFirst().dateTime();
        if (dateTime.isBefore(originalFirstDateTime)) {
            Collections.reverse(this.data);

            LocalDateTime activeFirstDateTime = originalFirstDateTime;
            while (true) {
                activeFirstDateTime = Helper.getPreviousDate(activeFirstDateTime, frequency);

                if (activeFirstDateTime.isEqual(dateTime)) {
                    this.data.add(new Type.DatasetRow(activeFirstDateTime, newValue));
                    break;
                } else {
                    this.data.add(new Type.DatasetRow(activeFirstDateTime, ""));
                }
            }

            Collections.reverse(this.data);
            return;
        }

        final LocalDateTime originalLastDateTime = this.data.getLast().dateTime();
        if (dateTime.isAfter(originalLastDateTime)) {
            LocalDateTime activeLastDateTime = originalLastDateTime;

            while (true) {
                activeLastDateTime = Helper.getNextDate(activeLastDateTime, frequency);

                if (activeLastDateTime.isEqual(dateTime)) {
                    this.data.add(new Type.DatasetRow(activeLastDateTime, newValue));
                    break;
                } else {
                    this.data.add(new Type.DatasetRow(activeLastDateTime, ""));
                }
            }
        }
    }

    public void saveToFile() throws RequestException {
        final File file = new File(Constants.STORAGE_DATASET_PATH, this.fileName + ".csv");
        try (final BufferedWriter writter = new BufferedWriter(new FileWriter(file))) {
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

    public Long getRowsCount() {
        return Helper.intToLong(this.data.size());
    }
}
