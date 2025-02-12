package com.backend.thesis.service;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.python.PythonConstants;
import com.backend.thesis.utility.python.PythonExecutor;
import com.backend.thesis.utility.python.PythonHelper;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestService {
    public TestService() {
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
            PythonHelper.appendIfAvailable(json, "maxlag", maxlag);
            PythonHelper.appendIfAvailable(json, "regression", regression);
            PythonHelper.appendIfAvailable(json, "autolag", autolag);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
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
            PythonHelper.appendIfAvailable(json, "regression", regression);
            PythonHelper.appendIfAvailable(json, "nlags", nlags);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
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
            PythonHelper.appendIfAvailable(json, "period", period);
            PythonHelper.appendIfAvailable(json, "model", modelType);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
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
            PythonHelper.appendIfAvailable(json, "fs", samplingFrequency);
            PythonHelper.appendIfAvailable(json, "nfft", fft);
            PythonHelper.appendIfAvailable(json, "return_onesided", spectrum);
            PythonHelper.appendIfAvailable(json, "scaling", scaling);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
    }

    public Type.ActionResult<JSONObject> acf(
            final DatasetEntity datasetEntity,
            final Optional<Double> alpha,
            final Optional<Boolean> autocovariance,
            final Optional<Integer> lagsCount,
            final Optional<Boolean> useFft,
            final Optional<Boolean> useBartlettFormula
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_CORRELOGRAM_ACF);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            PythonHelper.appendIfAvailable(json, "alpha", alpha);
            PythonHelper.appendIfAvailable(json, "adjusted", autocovariance);
            PythonHelper.appendIfAvailable(json, "nlags", lagsCount);
            PythonHelper.appendIfAvailable(json, "fft", useFft);
            PythonHelper.appendIfAvailable(json, "bartlett_confint", useBartlettFormula);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
    }

    public Type.ActionResult<JSONObject> pacf(
            final DatasetEntity datasetEntity,
            final Optional<Double> alpha,
            final Optional<Integer> lagsCount,
            final Optional<String> method
    ) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_CORRELOGRAM_PACF);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            PythonHelper.appendIfAvailable(json, "alpha", alpha);
            PythonHelper.appendIfAvailable(json, "nlags", lagsCount);
            PythonHelper.appendIfAvailable(json, "method", method);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
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
            PythonHelper.appendIfAvailable(json, "nlags", maxLag);
            PythonHelper.appendIfAvailable(json, "ddof", dfCount);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
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
            PythonHelper.appendIfAvailable(json, "period", period);
            PythonHelper.appendIfAvailable(json, "lags", lagsCount);
            PythonHelper.appendIfAvailable(json, "auto_lag", autoLag);
            PythonHelper.appendIfAvailable(json, "model_df", dfCount);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

        return PythonExecutor.handleAction(json);
    }
}
