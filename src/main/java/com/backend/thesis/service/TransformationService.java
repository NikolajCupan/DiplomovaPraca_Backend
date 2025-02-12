package com.backend.thesis.service;

import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.entity.FrequencyEntity;
import com.backend.thesis.domain.repository.FrequencyRepository;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.python.PythonConstants;
import com.backend.thesis.utility.python.PythonExecutor;
import com.backend.thesis.utility.python.PythonHelper;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class TransformationService {
    private final FrequencyRepository frequencyRepository;
    private final DatasetService datasetService;

    public TransformationService(final FrequencyRepository frequencyRepository, final DatasetService datasetService) {
        this.frequencyRepository = frequencyRepository;
        this.datasetService = datasetService;
    }

    public Type.ActionResult<DatasetInfoDto> difference(
            final String cookie,
            final DatasetEntity datasetEntity,
            final String transformedDatasetName,
            final Long differenceLevel
    ) {
        final JSONObject json = new JSONObject();
        final FrequencyEntity frequencyEntity = this.frequencyRepository.findById(datasetEntity.getIdFrequency()).get();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_DIFFERENCE);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(
                    PythonConstants.PYTHON_FREQUENCY_TYPE_KEY,
                    PythonHelper.convertToPythonFrequencyType(frequencyEntity.getFrequencyType())
            );
            json.put("difference_level", differenceLevel);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní transformácie", null);
        }

        final Type.ActionResult<JSONObject> result = PythonExecutor.handleAction(json);
        if (!result.success()) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní transformácie", null);
        }

        try {
            final String fileNameFromJson =
                    result.data().getJSONObject(PythonConstants.TRANSFORMED_FILE_NAME_KEY).getString(PythonConstants.JSON_ELEMENT_RESULT_KEY);
            final String startDateTimeFromJson =
                    result.data().getJSONObject(PythonConstants.START_DATE_TIME_KEY).getString(PythonConstants.JSON_ELEMENT_RESULT_KEY);
            final File file = new File(fileNameFromJson);

            final Type.ActionResult<DatasetInfoDto> saveResult = this.datasetService.tryToSaveDataset(
                    cookie,
                    transformedDatasetName,
                    Helper.fileToMultipartFile(file),
                    Optional.of(Helper.stringToLocalDateTime(startDateTimeFromJson)),
                    Optional.of(Constants.DEFAULT_DATE_TIME_FORMAT),
                    Helper.stringToFrequency(frequencyEntity.getFrequencyType()),
                    Optional.empty(),
                    Optional.empty(),
                    true,
                    false
            );
            file.delete();

            return saveResult;
        } catch (final Exception exception) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní transformácie", null);
        }
    }
}
