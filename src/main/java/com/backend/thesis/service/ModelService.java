package com.backend.thesis.service;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.domain.entity.FrequencyEntity;
import com.backend.thesis.domain.repository.FrequencyRepository;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.python.PythonConstants;
import com.backend.thesis.utility.python.PythonExecutor;
import com.backend.thesis.utility.python.PythonHelper;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ModelService {
    private final FrequencyRepository frequencyRepository;

    public ModelService(final FrequencyRepository frequencyRepository) {
        this.frequencyRepository = frequencyRepository;
    }

    public Type.ActionResult<JSONObject> arima(
            final DatasetEntity datasetEntity,
            final Long trainPercent,
            final Long seasonLength,
            final Long normal_p,
            final Long normal_d,
            final Long normal_q,
            final Long seasonal_p,
            final Long seasonal_d,
            final Long seasonal_q,
            final Long forecastCount,
            final Double pValueTests
    ) {
        final JSONObject json = new JSONObject();
        final FrequencyEntity frequencyEntity = this.frequencyRepository.findById(datasetEntity.getIdFrequency()).get();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_ARIMA);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(PythonConstants.TRAIN_PERCENT_KEY, trainPercent);
            json.put(
                    PythonConstants.PYTHON_FREQUENCY_TYPE_KEY,
                    PythonHelper.convertToPythonFrequencyType(frequencyEntity.getFrequencyType())
            );
            json.put(
                    PythonConstants.FREQUENCY_TYPE_KEY,
                    frequencyEntity.getFrequencyType()
            );
            json.put("season_length", seasonLength);

            json.put("normal_p", normal_p);
            json.put("normal_d", normal_d);
            json.put("normal_q", normal_q);

            json.put("seasonal_p", seasonal_p);
            json.put("seasonal_d", seasonal_d);
            json.put("seasonal_q", seasonal_q);

            json.put(PythonConstants.FORECAST_COUNT_KEY, forecastCount);
            json.put(PythonConstants.P_VALUE_KEY, pValueTests);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní akcie", null);
        }

        return PythonExecutor.handleAction(json);
    }

    public Type.ActionResult<JSONObject> holtWinter(
            final DatasetEntity datasetEntity,
            final Long trainPercent,
            final Long seasonLength,
            final String trendType,
            final String seasonType,
            final Double alpha,
            final Double beta,
            final Double gamma,
            final Long forecastCount,
            final Double pValueTests
    ) {
        final JSONObject json = new JSONObject();
        final FrequencyEntity frequencyEntity = this.frequencyRepository.findById(datasetEntity.getIdFrequency()).get();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_HOLT_WINTER);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(PythonConstants.TRAIN_PERCENT_KEY, trainPercent);
            json.put(
                    PythonConstants.PYTHON_FREQUENCY_TYPE_KEY,
                    PythonHelper.convertToPythonFrequencyType(frequencyEntity.getFrequencyType())
            );
            json.put(
                    PythonConstants.FREQUENCY_TYPE_KEY,
                    frequencyEntity.getFrequencyType()
            );
            json.put("season_length", seasonLength);

            json.put("trend_type", trendType);
            json.put("season_type", seasonType);

            json.put("alpha", alpha);
            json.put("beta", beta);
            json.put("gamma", gamma);

            json.put(PythonConstants.FORECAST_COUNT_KEY, forecastCount);
            json.put(PythonConstants.P_VALUE_KEY, pValueTests);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní akcie", null);
        }

        return PythonExecutor.handleAction(json);
    }
}
