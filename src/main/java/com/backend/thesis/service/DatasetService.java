package com.backend.thesis.service;

import com.backend.thesis.domain.dto.Frequency;
import com.backend.thesis.domain.repository.IDatasetRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class DatasetService {
    private final IDatasetRepository datasetRepository;

    public DatasetService(final IDatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public void saveDataset(
            final LocalDateTime startDateTime,
            final MultipartFile file,
            final Frequency frequency,
            final boolean hasHeader,
            final boolean hasDateColumn
    ) {
        return;
    }
}
