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
            return new Type.ActionResult<>(true, "Dataset bol úspešne uložený", datasetInfoDto);
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(false, exception.getMessage(), null);
        } catch (final Exception exception) {
            return new Type.ActionResult<>(false, "Neznáma chyba", null);
        }
    }

    public Type.ActionResult<DatasetEntity> getDatasetOfUser(final String cookie, final Long idDataset) {
        try {
            final Optional<DatasetEntity> datasetEntity = this.datasetRepository.findById(idDataset);
            if (datasetEntity.isEmpty()) {
                return new Type.ActionResult<>(false, "Dataset nebol nájdený", null);
            }

            final Optional<UserEntity> userEntity = this.userRepository.findById(datasetEntity.get().getIdUser());
            if (userEntity.isEmpty()) {
                return new Type.ActionResult<>(false, "Používateľ nebol nájdený", null);
            }

            if (!userEntity.get().getCookie().equals(cookie)) {
                return new Type.ActionResult<>(false, "Používateľ nemá oprávnenie na prístup k danému súboru", null);
            }

            return new Type.ActionResult<>(true, "Dataset bol odoslaný", datasetEntity.get());
        } catch (final Exception exception) {
            return new Type.ActionResult<>(false, "Neznáma chyba", null);
        }
    }

    public Type.ActionResult<List<DatasetInfoDto>> getAllDatasetsOfUser(final String cookie) {
        final UserEntity userEntity = this.userRepository.findByCookie(cookie);
        final List<DatasetEntity> datasetEntities = this.datasetRepository.findByIdUser(userEntity.getIdUser());

        final List<DatasetInfoDto> datasetInfoDtos = new ArrayList<>();
        for (final DatasetEntity datasetEntity : datasetEntities) {
            datasetInfoDtos.add(this.mapper.datasetEntityToDatasetInfoDto(datasetEntity));
        }

        return new Type.ActionResult<>(true, "Datasety boli odoslané", datasetInfoDtos);
    }

    public Type.ActionResult<DatasetForEditingDto> getDatasetOfUserForEditing(final String cookie, final Long idDataset) {
        final Type.ActionResult<DatasetEntity> result = this.getDatasetOfUser(cookie, idDataset);

        if (!result.success()) {
            return new Type.ActionResult<>(false, result.message(), null);
        }

        try {
            return new Type.ActionResult<>(
                    true, "Dataset bol odoslaný na editáciu", this.mapper.datasetEntityToDatasetForEditingDto(result.data())
            );
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(false, exception.getMessage(), null);
        }
    }

    public Type.ActionResult<DatasetForEditingDto> editDataset(
            final String cookie,
            final Long idDataset,
            final Optional<String> newColumnName,
            final List<Type.DatasetRow> rows
    ) {
        final Type.ActionResult<DatasetEntity> result = this.getDatasetOfUser(cookie, idDataset);
        if (!result.success()) {
            return new Type.ActionResult<>(false, result.message(), null);
        }

        try {
            final DatasetEntity datasetEntity = this.datasetRepository.findById(idDataset).get();
            final FrequencyEntity frequencyEntity = this.frequencyRepository.findById(datasetEntity.getIdFrequency()).get();

            final CsvFile csvFile = CsvFile.readFromFile(result.data().getFileName());
            for (final Type.DatasetRow editedRow : rows) {
                csvFile.editRow(editedRow.dateTime(), editedRow.value(), Helper.stringToFrequency(frequencyEntity.getFrequencyType()));
            }
            csvFile.trim();
            csvFile.saveToFile();

            datasetEntity.setRowsCount(csvFile.getRowsCount());
            datasetEntity.setStartAt(csvFile.getStartDateTime());
            datasetEntity.setEndAt(csvFile.getEndDateTime());

            if (newColumnName.isPresent() && !newColumnName.get().isEmpty()) {
                datasetEntity.setColumnName(newColumnName.get());
            }

            this.datasetRepository.save(datasetEntity);
            final Type.ActionResult<DatasetForEditingDto> updatedDatasetForEditing = this.getDatasetOfUserForEditing(cookie, idDataset);
            if (updatedDatasetForEditing.success()) {
                return new Type.ActionResult<>(true, "Dataset bol úspešne editovaný", null);
            } else {
                return new Type.ActionResult<>(false, result.message(), null);
            }
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(false, exception.getMessage(), null);
        }
    }

    public Type.ActionResult<DatasetInfoDto> deleteDataset(final String cookie, final Long idDataset) {
        final Type.ActionResult<DatasetEntity> result = this.getDatasetOfUser(cookie, idDataset);
        if (!result.success()) {
            return new Type.ActionResult<>(false, result.message(), null);
        }

        try {
            this.datasetRepository.deleteByIdDataset(idDataset);

            final DatasetInfoDto datasetInfoDto = this.mapper.datasetEntityToDatasetInfoDto(result.data());
            return new Type.ActionResult<>(true, "Dataset bol úspešne zmazaný", datasetInfoDto);
        } catch (final RequestException exception) {
            return new Type.ActionResult<>(false, result.message(), null);
        }
    }
}
