package com.backend.thesis.utility.csv;

import com.backend.thesis.domain.dto.Frequency;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.other.RequestException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CsvParser {
    private CsvParser() {
    }

    private static List<String[]> readCsv(final MultipartFile file) throws IOException, CsvException {
        final Reader reader = new InputStreamReader(file.getInputStream());
        final CSVReader csvReader = new CSVReader(reader);
        return csvReader.readAll();
    }

    private static List<LocalDateTime> extractDateColumn(
            final List<String[]> rawCsv,
            final int dateColumnIndex,
            final String dateFormat,
            final Frequency frequency
    ) throws RequestException {
        try {
            final List<String> rawDateColumn = CsvParser.extractColumn(rawCsv, dateColumnIndex, false);

            final List<LocalDateTime> dateColumn = new ArrayList<>();
            final LocalDateTime firstDate = CsvParser.truncateDate(Helper.stringToLocalDateTime(rawDateColumn.removeFirst(), dateFormat), frequency);
            dateColumn.add(firstDate);

            for (final String rawDate : rawDateColumn) {
                final LocalDateTime extractedDate = CsvParser.truncateDate(Helper.stringToLocalDateTime(rawDate, dateFormat), frequency);
                dateColumn.add(extractedDate);
            }

            return dateColumn;
        } catch (final IllegalArgumentException exception) {
            throw new RequestException("Chyba pri spracovaní stĺpca s dátumom (nesprávne nastavený formát dátumu)");
        } catch (final Exception exception) {
            throw new RequestException("Chyba pri spracovaní stĺpca s dátumom");
        }
    }

    private static List<String> extractColumn(final List<String[]> rawCsv, final int index, final boolean numeric) {
        List<String> column = new ArrayList<>();

        for (final String[] row : rawCsv) {
            final String value = row[index];

            if (numeric && !Helper.stringIsNumeric(value)) {
                column.add("");
            } else {
                column.add(Helper.trimTrailingZeroes(value));
            }
        }

        return column;
    }

    private static LocalDateTime truncateDate(final LocalDateTime startDateTime, final Frequency frequency) {
        if (frequency == Frequency.HOURLY) {
            return startDateTime.truncatedTo(ChronoUnit.HOURS);
        } else if (frequency == Frequency.DAILY || frequency == Frequency.WEEKLY) {
            return startDateTime.truncatedTo(ChronoUnit.DAYS);
        } else if (frequency == Frequency.MONTHLY) {
            return LocalDate.of(startDateTime.getYear(), startDateTime.getMonth(), 1).atStartOfDay();
        } else if (frequency == Frequency.QUARTERLY) {
            switch (startDateTime.getMonth()) {
                case Month.JANUARY, Month.FEBRUARY, Month.MARCH -> {
                    return LocalDate.of(startDateTime.getYear(), 1, 1).atStartOfDay();
                }
                case Month.APRIL, Month.MAY, Month.JUNE -> {
                    return LocalDate.of(startDateTime.getYear(), 4, 1).atStartOfDay();
                }
                case Month.JULY, Month.AUGUST, Month.SEPTEMBER -> {
                    return LocalDate.of(startDateTime.getYear(), 7, 1).atStartOfDay();
                }
                case Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER -> {
                    return LocalDate.of(startDateTime.getYear(), 10, 1).atStartOfDay();
                }
            }
        } else if (frequency == Frequency.YEARLY) {
            return LocalDate.of(startDateTime.getYear(), 1, 1).atStartOfDay();
        }

        return null;
    }

    private static List<LocalDateTime> generateDateColumn(
            final LocalDateTime startDateTime,
            final Frequency frequency,
            final int length) {
        final LocalDateTime startDate = CsvParser.truncateDate(startDateTime, frequency);

        final List<LocalDateTime> dateColumn = Arrays.asList(new LocalDateTime[length]);
        dateColumn.set(0, startDate);

        for (int i = 1; i < length; ++i) {
            dateColumn.set(i, Helper.getNextDate(dateColumn.get(i - 1), frequency));
        }

        return dateColumn;
    }

    public static CsvFile parseCsv(
            final MultipartFile file,
            final Optional<LocalDateTime> startDateTime,
            final Optional<String> dateFormat,
            final Frequency frequency,
            final String fileName,
            final Optional<String> dateColumnName,
            final Optional<String> dataColumnName,
            final boolean datasetHasDateColumn,
            final boolean datasetHasHeader) throws IOException, CsvException, RequestException {
        if (!datasetHasDateColumn && startDateTime.isEmpty()) {
            throw new RequestException("Začiatočný dátum musí byť zadaný alebo dataset musí obsahovať stĺpec s dátumom");
        } else if (datasetHasDateColumn && dateFormat.isEmpty()) {
            throw new RequestException("Dataset so stĺpcom s dátumom musí mať zadaný formát dátumu");
        }

        final List<String[]> rawCsv = CsvParser.readCsv(file);
        final CsvFile parsedCsv = new CsvFile(
                fileName
        );

        int indexDateColumn = 0;
        int indexDataColumn = datasetHasDateColumn ? 1 : 0;
        if (datasetHasHeader) {
            final boolean dateColumnNameValid = dateColumnName.isPresent() && !dateColumnName.get().isEmpty();
            final boolean dataColumnNameValid = dataColumnName.isPresent() && !dataColumnName.get().isEmpty();
            final String[] header = rawCsv.removeFirst();

            int currentIndex = 0;
            for (final String columnName : header) {
                if (dateColumnNameValid && columnName.equals(dateColumnName.get())) {
                    indexDateColumn = currentIndex;
                }

                if (dataColumnNameValid && columnName.equals(dataColumnName.get())) {
                    indexDataColumn = currentIndex;
                }

                ++currentIndex;
            }
        }

        final List<String> dataColumn = CsvParser.extractColumn(rawCsv, indexDataColumn, true);
        final List<LocalDateTime> dateColumn = datasetHasDateColumn ?
                CsvParser.extractDateColumn(rawCsv, indexDateColumn, dateFormat.get(), frequency) :
                CsvParser.generateDateColumn(startDateTime.get(), frequency, dataColumn.size());

        if (dataColumn.size() != dateColumn.size()) {
            throw new RequestException("Chyba pri spracovaní súboru");
        }

        if (dataColumn.stream().allMatch(String::isEmpty)) {
            throw new RequestException("Stĺpec s dátami neobsahuje platné údaje");
        }

        for (int i = 0; i < dataColumn.size(); ++i) {
            parsedCsv.addRow(dateColumn.get(i), dataColumn.get(i));
        }

        parsedCsv.fillDates(frequency);
        parsedCsv.trim();
        return parsedCsv;
    }
}
