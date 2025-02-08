package com.backend.thesis.service;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.python.PythonConstants;
import com.backend.thesis.utility.python.PythonExecutor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestService {
    public TestService() {
    }

    private static <T> void appendIfAvailable(
            final JSONObject json,
            final String key,
            final Optional<T> optionalValue
    ) throws JSONException {
        if (optionalValue.isPresent()) {
            final T value = optionalValue.get();

            if (value instanceof String stringValue) {
                if (!stringValue.isEmpty()) {
                    json.put(key, stringValue);
                }
            } else {
                json.put(key, value);
            }
        }
    }

    private static Type.ActionResult<JSONObject> handleTest(final JSONObject json) {
        JSONObject outputJson = new JSONObject();
        boolean success;

        try {
            final String inputJsonString = json.toString();
            final String jsonFileName = PythonExecutor.saveJson(inputJsonString);
            success = PythonExecutor.executeAction(jsonFileName);

            if (success) {
                outputJson = PythonExecutor.readJson(jsonFileName);
            }

            PythonExecutor.deleteFiles(jsonFileName);
        } catch (final Exception exception) {
            success = false;
        }

        if (success) {
            return new Type.ActionResult<>(true, "Test bol úspešne vykonaný", outputJson);
        } else {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní akcie", null);
        }
    }

    public Type.ActionResult<JSONObject> dickeyFullerTest(
            final DatasetEntity datasetEntity,
            final double pValue,
            final Optional<Integer> maxlag,
            final Optional<String> regression,
            final Optional<String> autolag
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_DICKEY_FULLER_TEST);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(PythonConstants.P_VALUE_KEY, pValue);
            TestService.appendIfAvailable(json, "maxlag", maxlag);
            TestService.appendIfAvailable(json, "regression", regression);
            TestService.appendIfAvailable(json, "autolag", autolag);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return TestService.handleTest(json);
    }

    public Type.ActionResult<JSONObject> kpssTest(
            final DatasetEntity datasetEntity,
            final double pValue,
            final Optional<String> regression,
            final Optional<Integer> nlags
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_KPSS_TEST);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(PythonConstants.P_VALUE_KEY, pValue);
            TestService.appendIfAvailable(json, "regression", regression);
            TestService.appendIfAvailable(json, "nlags", nlags);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return TestService.handleTest(json);
    }

    public Type.ActionResult<JSONObject> seasonalDecompose(
            final DatasetEntity datasetEntity,
            final Optional<Integer> period,
            final Optional<String> modelType
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_SEASONAL_DECOMPOSE);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            TestService.appendIfAvailable(json, "period", period);
            TestService.appendIfAvailable(json, "model", modelType);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return TestService.handleTest(json);
    }

    public Type.ActionResult<JSONObject> periodogram(
            final DatasetEntity datasetEntity,
            final Optional<Double> samplingFrequency,
            final Optional<Integer> fft,
            final Optional<Boolean> spectrum,
            final Optional<String> scaling
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_PERIODOGRAM);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            TestService.appendIfAvailable(json, "fs", samplingFrequency);
            TestService.appendIfAvailable(json, "nfft", fft);
            TestService.appendIfAvailable(json, "spectrum", spectrum);
            TestService.appendIfAvailable(json, "scaling", scaling);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return TestService.handleTest(json);
    }

    public Type.ActionResult<JSONObject> archTest(
            final DatasetEntity datasetEntity,
            final double pValue,
            final Optional<Integer> maxLag,
            final Optional<Integer> dfCount
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_ARCH_TEST);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(PythonConstants.P_VALUE_KEY, pValue);
            TestService.appendIfAvailable(json, "nlags", maxLag);
            TestService.appendIfAvailable(json, "ddof", dfCount);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return TestService.handleTest(json);
    }

    public Type.ActionResult<JSONObject> ljungBoxTest(
            final DatasetEntity datasetEntity,
            final double pValue,
            final Optional<Integer> period,
            final Optional<Integer> lagsCount,
            final Optional<Boolean> autoLag,
            final Optional<Integer> dfCount
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_LJUNG_BOX_TEST);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(PythonConstants.P_VALUE_KEY, pValue);
            TestService.appendIfAvailable(json, "period", period);
            TestService.appendIfAvailable(json, "lags", lagsCount);
            TestService.appendIfAvailable(json, "auto_lag", autoLag);
            TestService.appendIfAvailable(json, "model_df", dfCount);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return TestService.handleTest(json);
    }
}
