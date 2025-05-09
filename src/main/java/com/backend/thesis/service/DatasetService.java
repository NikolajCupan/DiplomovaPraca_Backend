package com.backend.thesis.service;

import com.backend.thesis.domain.Mapper;
import com.backend.thesis.domain.dto.DatasetForEditingDto;
import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.dto.Frequency;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.entity.FrequencyEntity;
import com.backend.thesis.domain.entity.UserEntity;
import com.backend.thesis.domain.repository.DatasetRepository;
import com.backend.thesis.domain.repository.FrequencyRepository;
import com.backend.thesis.domain.repository.UserRepository;
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
    private final DatasetRepository datasetRepository;
    private final UserRepository userRepository;
    private final FrequencyRepository frequencyRepository;

    public DatasetService(final Mapper mapper, final DatasetRepository datasetRepository, final UserRepository userRepository, final FrequencyRepository frequencyRepository) {
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
            final boolean datasetHasHeader
    ) {
        try {
            final String finalDatasetName = datasetName.isEmpty() ? Constants.DEFAULT_DATESET_NAME : datasetName;
            final String fileName = finalDatasetName + Helper.getUniqueID();

            final CsvFile csv = CsvParser.parseCsv(
                    file, startDateTime, dateFormat, frequency, fileName, dateColumnName, dataColumnName, datasetHasDateColumn, datasetHasHeader
            );

            if (Helper.isInvalidDate(csv.getEndDateTime())) {
                return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Dátum nachádzajúci sa v datasete nemôže presiahnuť rok 2250", null);
            }

            if (csv.hasMissingValues()) {
                return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Dataset obsahuje chýbajúce hodnoty", null);
            }

            if (csv.getData().size() > Constants.MAXIMUM_ROWS_COUNT) {
                return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Počet riadkov v datasete nesmie presiahnuť " + Constants.MAXIMUM_ROWS_COUNT, null);
            }

            csv.saveToFile();

            final UserEntity userEntity = this.userRepository.findByCookie(cookie);
            final FrequencyEntity frequencyEntity = this.frequencyRepository.findByFrequencyType(frequency.toString());

            final DatasetEntity dataset = new DatasetEntity(
                    userEntity.getIdUser(),
                    frequencyEntity.getIdFrequency(),
                    finalDatasetName,
                    (dataColumnName.isEmpty() || dataColumnName.get().isEmpty()) ? Constants.DEFAULT_DATA_COLUMN_NAME : dataColumnName.get(),
                    csv.getRowsCount(),
                    fileName,
                    csv.getStartDateTime(),
                    csv.getEndDateTime()
            );
            this.datasetRepository.save(dataset);

            final DatasetInfoDto datasetInfoDto = this.mapper.datasetEntityToDatasetInfoDto(dataset);
            return new Type.ActionResult<>(Type.ActionResultType.SUCCESS, "Dataset bol úspešne uložený", datasetInfoDto);
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, exception.getMessage(), null);
        } catch (final Exception exception) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Neznáma chyba", null);
        }
    }

    public Type.ActionResult<DatasetEntity> getDatasetOfUser(final String cookie, final Long idDataset) {
        try {
            final Optional<DatasetEntity> datasetEntity = this.datasetRepository.findById(idDataset);
            if (datasetEntity.isEmpty()) {
                return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Dataset nebol nájdený", null);
            }

            final Optional<UserEntity> userEntity = this.userRepository.findById(datasetEntity.get().getIdUser());
            if (userEntity.isEmpty()) {
                return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Používateľ nebol nájdený", null);
            }

            if (!userEntity.get().getCookie().equals(cookie)) {
                return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Používateľ nemá oprávnenie na prístup k danému súboru", null);
            }

            return new Type.ActionResult<>(Type.ActionResultType.SUCCESS, "Dataset bol odoslaný", datasetEntity.get());
        } catch (final Exception exception) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Neznáma chyba", null);
        }
    }

    public Type.ActionResult<List<DatasetInfoDto>> getAllDatasetsOfUser(final String cookie) {
        final UserEntity userEntity = this.userRepository.findByCookie(cookie);
        final List<DatasetEntity> datasetEntities = this.datasetRepository.findByIdUser(userEntity.getIdUser());

        final List<DatasetInfoDto> datasetInfoDtos = new ArrayList<>();
        for (final DatasetEntity datasetEntity : datasetEntities) {
            datasetInfoDtos.add(this.mapper.datasetEntityToDatasetInfoDto(datasetEntity));
        }

        return new Type.ActionResult<>(Type.ActionResultType.SUCCESS, "Datasety boli odoslané", datasetInfoDtos);
    }

    public Type.ActionResult<DatasetForEditingDto> getDatasetOfUserForEditing(
            final String cookie,
            final Long idDataset,
            final boolean includeData
    ) {
        final Type.ActionResult<DatasetEntity> result = this.getDatasetOfUser(cookie, idDataset);

        if (!result.isSuccess()) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, result.message(), null);
        }

        try {
            return new Type.ActionResult<>(
                    Type.ActionResultType.SUCCESS, "Dataset bol odoslaný na editáciu", this.mapper.datasetEntityToDatasetForEditingDto(result.data(), includeData)
            );
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, exception.getMessage(), null);
        }
    }

    public Type.ActionResult<DatasetForEditingDto> editDatasetV2(
            final String cookie,
            final Long idDataset,
            final Optional<String> newDatasetName,
            final Optional<String> newColumnName
    ) {
        final Type.ActionResult<DatasetEntity> result = this.getDatasetOfUser(cookie, idDataset);
        if (!result.isSuccess()) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, result.message(), null);
        }

        final DatasetEntity datasetEntity = this.datasetRepository.findById(idDataset).get();
        if (newDatasetName.isPresent() && !newDatasetName.get().isEmpty()) {
            datasetEntity.setDatasetName(newDatasetName.get());
        }
        if (newColumnName.isPresent() && !newColumnName.get().isEmpty()) {
            datasetEntity.setColumnName(newColumnName.get());
        }

        this.datasetRepository.save(datasetEntity);
        final Type.ActionResult<DatasetForEditingDto> updatedDatasetForEditing
                = this.getDatasetOfUserForEditing(cookie, idDataset, false);

        if (updatedDatasetForEditing.isSuccess()) {
            return new Type.ActionResult<>(Type.ActionResultType.SUCCESS, "Dataset bol úspešne editovaný", updatedDatasetForEditing.data());
        } else {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, result.message(), null);
        }
    }

    public Type.ActionResult<DatasetInfoDto> deleteDataset(final String cookie, final Long idDataset) {
        final Type.ActionResult<DatasetEntity> result = this.getDatasetOfUser(cookie, idDataset);
        if (!result.isSuccess()) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, result.message(), null);
        }

        try {
            this.datasetRepository.deleteByIdDataset(idDataset);

            final DatasetInfoDto datasetInfoDto = this.mapper.datasetEntityToDatasetInfoDto(result.data());
            return new Type.ActionResult<>(Type.ActionResultType.SUCCESS, "Dataset bol úspešne zmazaný", datasetInfoDto);
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, result.message(), null);
        }
    }
}
