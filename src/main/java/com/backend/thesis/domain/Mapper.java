package com.backend.thesis.domain;

import com.backend.thesis.domain.dto.DatasetForEditingDto;
import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.entity.FrequencyEntity;
import com.backend.thesis.domain.repository.DatasetRepository;
import com.backend.thesis.domain.repository.FrequencyRepository;
import com.backend.thesis.domain.repository.UserRepository;
import com.backend.thesis.utility.csv.CsvFile;
import com.backend.thesis.utility.other.RequestException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Mapper {
    private final UserRepository userRepository;
    private final DatasetRepository datasetRepository;
    private final FrequencyRepository frequencyRepository;

    public Mapper(final UserRepository userRepository, final DatasetRepository datasetRepository, final FrequencyRepository frequencyRepository) {
        this.userRepository = userRepository;
        this.datasetRepository = datasetRepository;
        this.frequencyRepository = frequencyRepository;
    }

    public DatasetInfoDto datasetEntityToDatasetInfoDto(final DatasetEntity datasetEntity) {
        DatasetInfoDto datasetInfoDto = new DatasetInfoDto();
        datasetInfoDto.setIdDataset(datasetEntity.getIdDataset());
        datasetInfoDto.setColumnName(datasetEntity.getColumnName());
        datasetInfoDto.setDatasetName(datasetEntity.getDatasetName());
        datasetInfoDto.setRowsCount(datasetEntity.getRowsCount());

        final Optional<FrequencyEntity> frequencyEntity = this.frequencyRepository.findById(datasetEntity.getIdFrequency());
        assert (frequencyEntity.isPresent());

        datasetInfoDto.setFrequencyType(frequencyEntity.get().getFrequencyType());
        return datasetInfoDto;
    }

    public DatasetForEditingDto datasetEntityToDatasetForEditingDto(final DatasetEntity datasetEntity, final boolean includeData) throws RequestException {
        final DatasetInfoDto datasetInfoDto = this.datasetEntityToDatasetInfoDto(datasetEntity);

        DatasetForEditingDto datasetForEditingDto = new DatasetForEditingDto();
        datasetForEditingDto.setDatasetInfoDto(datasetInfoDto);

        if (includeData) {
            final CsvFile csvFile = CsvFile.readFromFile(datasetEntity.getFileName());
            datasetForEditingDto.setRows(csvFile.getData());
        }

        return datasetForEditingDto;
    }
}
