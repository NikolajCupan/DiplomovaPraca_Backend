package com.backend.thesis.service;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.python.PythonConstants;
import com.backend.thesis.utility.python.PythonExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public Type.ActionResult<JSONObject> dickeyFullerTest(
            final DatasetEntity datasetEntity,
            final double pValue,
            final Optional<Integer> maxLag,
            final Optional<String> regression,
            final Optional<String> autolag) {
        final JSONObject json = new JSONObject();

        try {
            json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_DICKEY_FULLER_TEST);
            json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
            json.put(PythonConstants.P_VALUE_KEY, pValue);
            TestService.appendIfAvailable(json, "maxLag", maxLag);
            TestService.appendIfAvailable(json, "regression", regression);
            TestService.appendIfAvailable(json, "autolag", autolag);
        } catch (final Exception ignore) {
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }

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
            return new Type.ActionResult<>(false, "Chyba pri vykonávaní testu", null);
        }
    }
}
