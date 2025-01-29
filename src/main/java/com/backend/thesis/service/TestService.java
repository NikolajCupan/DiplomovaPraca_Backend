package com.backend.thesis.service;

import com.backend.thesis.domain.entity.DatasetEntity;
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

    public int dickeyFullerTest(
            final DatasetEntity datasetEntity,
            final double pValue) {
        final Map<String, String> json = new HashMap<>();

        json.put(PythonConstants.ACTION_KEY, PythonConstants.ACTION_DICKEY_FULLER_TEST);
        json.put(PythonConstants.FILE_NAME_KEY, datasetEntity.getFileName());
        json.put(PythonConstants.P_VALUE_KEY, String.valueOf(pValue));

        try {
            final String inputJsonString = this.objectMapper.writeValueAsString(json);
            final String jsonFileName = PythonExecutor.saveJson(inputJsonString);
            final boolean success = PythonExecutor.executeAction(jsonFileName);

            if (success) {
                final JSONObject outputJson = PythonExecutor.readJson(jsonFileName);
                System.out.println(outputJson);
            }

            PythonExecutor.deleteFiles(jsonFileName);
        } catch (final Exception exception) {
            return -1;
        }

        return 0;
    }
}
