package com.backend.thesis.domain;

import com.backend.thesis.domain.dto.DatasetForEditingDto;
import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.entity.FrequencyEntity;
import com.backend.thesis.domain.repository.IDatasetRepository;
import com.backend.thesis.domain.repository.IFrequencyRepository;
import com.backend.thesis.domain.repository.IUserRepository;
import com.backend.thesis.utility.csv.CsvFile;
import com.backend.thesis.utility.other.RequestException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Mapper {
    private final IUserRepository userRepository;
    private final IDatasetRepository datasetRepository;
    private final IFrequencyRepository frequencyRepository;

    public Mapper(final IUserRepository userRepository, final IDatasetRepository datasetRepository, final IFrequencyRepository frequencyRepository) {
        this.userRepository = userRepository;
        this.datasetRepository = datasetRepository;
        this.frequencyRepository = frequencyRepository;
    }

    public DatasetInfoDto datasetEntityToDatasetInfoDto(final DatasetEntity datasetEntity) {
        DatasetInfoDto datasetInfoDto = new DatasetInfoDto();
        datasetInfoDto.setIdDataset(datasetEntity.getIdDataset());
        datasetInfoDto.setColumnName(datasetEntity.getColumnName());
        datasetInfoDto.setDatasetName(datasetEntity.getDatasetName());

        final Optional<FrequencyEntity> frequencyEntity = this.frequencyRepository.findById(datasetEntity.getIdFrequency());
        assert (frequencyEntity.isPresent());

        datasetInfoDto.setFrequencyType(frequencyEntity.get().getFrequencyType());
        return datasetInfoDto;
    }

    public DatasetForEditingDto datasetEntityToDatasetForEditingDto(final DatasetEntity datasetEntity) throws RequestException {
        final DatasetInfoDto datasetInfoDto = this.datasetEntityToDatasetInfoDto(datasetEntity);

        DatasetForEditingDto datasetForEditingDto = new DatasetForEditingDto();
        datasetForEditingDto.setDatasetInfoDto(datasetInfoDto);

        final CsvFile csvFile = CsvFile.readFromFile(datasetEntity.getFileName());
        datasetForEditingDto.setRows(csvFile.getData());

        return datasetForEditingDto;
    }
}
