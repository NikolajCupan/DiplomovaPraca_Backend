package com.backend.thesis.domain.repository.impl;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.repository.DatasetRepository;
import com.backend.thesis.domain.repository.DatasetRepositoryCustom;
import com.backend.thesis.utility.csv.CsvFile;
import com.backend.thesis.utility.other.RequestException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DatasetRepositoryImpl implements DatasetRepositoryCustom {
    @Autowired
    @Lazy
    private DatasetRepository datasetRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Override
    public void deleteByIdDataset(final Long idDataset) throws RequestException {
        final Optional<DatasetEntity> datasetEntity = this.datasetRepository.findById(idDataset);
        if (datasetEntity.isEmpty()) {
            throw new RequestException("Dataset s daným ID neexistuje");
        }

        try {
            CsvFile.deleteFile(datasetEntity.get().getFileName());
        } catch (final Exception ignore) {
        }

        this.entityManager.remove(datasetEntity.get());
    }
}
