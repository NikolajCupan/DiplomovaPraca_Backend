package com.backend.thesis.service;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.utility.Type;
import com.backend.thesis.utility.python.PythonConstants;
import com.backend.thesis.utility.python.PythonExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TestService {
    private final ObjectMapper objectMapper;

    public TestService(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Type.ActionResult<JSONObject> dickeyFullerTest(
            final DatasetEntity datasetEntity,
            final double pValue) {
        final Map<String, String> json = new HashMap<>();

        json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_DICKEY_FULLER_TEST);
        json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
        json.put(PythonConstants.P_VALUE_KEY, String.valueOf(pValue));

        JSONObject outputJson = new JSONObject();
        boolean success;

        try {
            final String inputJsonString = this.objectMapper.writeValueAsString(json);
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
