package com.backend.thesis.service;

import com.backend.thesis.domain.Mapper;
import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.dto.Frequency;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.entity.FrequencyEntity;
import com.backend.thesis.domain.entity.UserEntity;
import com.backend.thesis.domain.repository.IDatasetRepository;
import com.backend.thesis.domain.repository.IFrequencyRepository;
import com.backend.thesis.domain.repository.IUserRepository;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.csv.CsvFile;
import com.backend.thesis.utility.csv.CsvParser;
import com.backend.thesis.utility.other.RequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DatasetService {
    private final Mapper mapper;
    private final IDatasetRepository datasetRepository;
    private final IUserRepository userRepository;
    private final IFrequencyRepository frequencyRepository;

    public DatasetService(final Mapper mapper, final IDatasetRepository datasetRepository, final IUserRepository userRepository, final IFrequencyRepository frequencyRepository) {
        this.mapper = mapper;
        this.datasetRepository = datasetRepository;
        this.userRepository = userRepository;
        this.frequencyRepository = frequencyRepository;
    }

    public Type.ActionResult<DatasetInfoDto> tryToSaveDataset(
            final String cookie,
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
            final CsvFile csv = CsvParser.parseCsv(
                    file, startDateTime, dateFormat, frequency, dateColumnName, dataColumnName, datasetHasDateColumn, datasetHasHeader, datasetHasMissingValues
            );

            final String finalDatasetName = datasetName.isEmpty() ? Constants.DEFAULT_DATESET_NAME : datasetName;
            final String fileName = finalDatasetName + Helper.getUniqueID();
            csv.saveToFile(fileName);

            final UserEntity userEntity = this.userRepository.findByCookie(cookie);
            final FrequencyEntity frequencyEntity = this.frequencyRepository.findByFrequencyType(frequency.toString());

            final DatasetEntity dataset = new DatasetEntity(
                    userEntity.getIdUser(),
                    frequencyEntity.getIdFrequency(),
                    finalDatasetName,
                    (dataColumnName.isEmpty() || dataColumnName.get().isEmpty()) ? Constants.DEFAULT_DATA_COLUMN_NAME : dataColumnName.get(),
                    fileName,
                    csv.getStartDateTime(),
                    csv.getEndDateTime()
            );
            this.datasetRepository.save(dataset);

            final DatasetInfoDto datasetInfoDto = this.mapper.datasetEntityToDatasetInfoDto(dataset);
            return new Type.ActionResult<>(true, "Dataset bol úspešne uložený", datasetInfoDto);
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(false, exception.getMessage(), null);
        } catch (final Exception exception) {
            return new Type.ActionResult<>(false, "Neznáma chyba", null);
        }
    }

    public Type.ActionResult<List<DatasetInfoDto>> getDatasetsOfUser(final String cookie) {
        final UserEntity userEntity = this.userRepository.findByCookie(cookie);
        final List<DatasetEntity> datasetEntities = this.datasetRepository.findByIdUser(userEntity.getIdUser());

        final List<DatasetInfoDto> datasetInfoDtos = new ArrayList<>();
        for (final DatasetEntity datasetEntity : datasetEntities) {
            datasetInfoDtos.add(this.mapper.datasetEntityToDatasetInfoDto(datasetEntity));
        }

        return new Type.ActionResult<>(true, "Datasety boli načítané", datasetInfoDtos);
    }
}
