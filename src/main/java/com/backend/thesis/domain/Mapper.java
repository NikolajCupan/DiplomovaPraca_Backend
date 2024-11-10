package com.backend.thesis.domain;

import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.entity.FrequencyEntity;
import com.backend.thesis.domain.repository.IDatasetRepository;
import com.backend.thesis.domain.repository.IFrequencyRepository;
import com.backend.thesis.domain.repository.IUserRepository;
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
        datasetInfoDto.setDatasetName(datasetEntity.getDatasetName());

        final Optional<FrequencyEntity> frequencyEntity = this.frequencyRepository.findById(datasetEntity.getIdFrequency());
        assert (frequencyEntity.isPresent());

        datasetInfoDto.setFrequencyType(frequencyEntity.get().getFrequencyType());
        return datasetInfoDto;
    }
}
