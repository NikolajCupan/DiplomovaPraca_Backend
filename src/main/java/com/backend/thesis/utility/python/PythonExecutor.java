package com.backend.thesis.utility.python;

import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class PythonExecutor {
    private PythonExecutor() {
    }

    public static String saveJson(final String json) throws Exception {
        final String jsonFileName = Helper.getUniqueID() + ".json";

        final PrintWriter printWriter = new PrintWriter(PythonConstants.DATASET_INPUT_PATH + "/" + jsonFileName);
        printWriter.write(json);
        printWriter.close();

        return jsonFileName;
    }

    public static JSONObject readJson(final String jsonFileName) throws Exception {
        try (final FileInputStream inputStream = new FileInputStream(
                PythonConstants.DATASET_OUTPUT_PATH + "/" + jsonFileName
        )) {
            final String rawJson = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return new JSONObject(rawJson);
        }
    }

    public static void deleteFiles(final String jsonFileName) {
        try {
            final Path inputJsonFilePath = Paths.get(PythonConstants.DATASET_INPUT_PATH + "/" + jsonFileName);
            Files.delete(inputJsonFilePath);
        } catch (final Exception ignore) {
        }

        try {
            final Path outputJsonFilePath = Paths.get(PythonConstants.DATASET_OUTPUT_PATH + "/" + jsonFileName);
            Files.delete(outputJsonFilePath);
        } catch (final Exception ignore) {
        }
    }

    public static PythonHelper.PythonExecutionResult executeAction(final String jsonFileName) {
        PythonHelper.PythonExecutionResult executionResult = PythonHelper.PythonExecutionResult.FAILURE;

        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(
                    "python",
                    PythonConstants.PYTHON_BASE_DIRECTORY_NAME + "/" + PythonConstants.PYTHON_MAIN_SCRIPT_NAME,
                    jsonFileName
            );
            final Process process = processBuilder.redirectErrorStream(true).start();

            Thread printingThread = null;
            if (PythonConstants.PYTHON_ENABLE_PRINTING) {
                printingThread = new Thread(() -> {
                    try {
                        process.getInputStream().transferTo(System.out);
                    } catch (final Exception ignore) {
                    }
                });

                printingThread.start();
            }

            if (!process.waitFor(PythonConstants.PYTHON_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                process.destroy();
                executionResult = PythonHelper.PythonExecutionResult.TIMEOUT;
            }

            if (process.exitValue() == 0) {
                executionResult = PythonHelper.PythonExecutionResult.SUCCESS;
            }

            if (PythonConstants.PYTHON_ENABLE_PRINTING) {
                printingThread.join();
            }
        } catch (final Exception ignore) {
        }

        return executionResult;
    }

    public static Type.ActionResult<JSONObject> handleAction(final JSONObject json) {
        JSONObject outputJson = new JSONObject();
        PythonHelper.PythonExecutionResult executionResult;

        try {
            final String inputJsonString = json.toString();
            final String jsonFileName = PythonExecutor.saveJson(inputJsonString);
            executionResult = PythonExecutor.executeAction(jsonFileName);

            if (executionResult == PythonHelper.PythonExecutionResult.SUCCESS) {
                outputJson = PythonExecutor.readJson(jsonFileName);
            }

            PythonExecutor.deleteFiles(jsonFileName);
        } catch (final Exception exception) {
            executionResult = PythonHelper.PythonExecutionResult.FAILURE;
        }

        if (executionResult == PythonHelper.PythonExecutionResult.SUCCESS) {
            return new Type.ActionResult<>(Type.ActionResultType.SUCCESS, "Akcie bola úspešne vykonaná", outputJson);
        } else if (executionResult == PythonHelper.PythonExecutionResult.TIMEOUT) {
            return new Type.ActionResult<>(Type.ActionResultType.TIMEOUT, "Timeout", null);
        } else {
            return new Type.ActionResult<>(Type.ActionResultType.FAILURE, "Chyba pri vykonávaní akcie", null);
        }
    }
}
