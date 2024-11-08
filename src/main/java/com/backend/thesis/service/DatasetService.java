package com.backend.thesis.service;

import com.backend.thesis.domain.dto.Frequency;
import com.backend.thesis.domain.repository.IDatasetRepository;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.csv.CsvFile;
import com.backend.thesis.utility.csv.CsvParser;
import com.backend.thesis.utility.other.RequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DatasetService {
    private final IDatasetRepository datasetRepository;

    public DatasetService(final IDatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public Type.ActionResult tryToSaveDataset(
            final String datasetName,
            final MultipartFile file,
            final Optional<LocalDateTime> startDateTime,
            final Optional<String> dateFormat,
            final Frequency frequency,
            final Optional<String> dateColumnName,
            final Optional<String> dataColumnName,
            final boolean datasetHasDateColumn,
            final boolean datasetHasHeader,
            final boolean datasetHasMissingValues
    ) {
        try {
            final CsvFile csv = CsvParser.parseCsv(file, startDateTime, dateFormat, frequency, dateColumnName, dataColumnName, datasetHasDateColumn, datasetHasHeader, datasetHasMissingValues);
            return new Type.ActionResult(true);
        } catch (final RequestException exception) {
            return new Type.ActionResult(false, exception.getMessage());
        } catch (final Exception exception) {
            return new Type.ActionResult(false, "Neznáma chyba");
        }
    }
}
