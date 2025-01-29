package com.backend.thesis.utility.python;

import com.backend.thesis.utility.Helper;

import java.io.PrintWriter;

public class PythonExecutor {
    private PythonExecutor() {
    }

    public static String saveJson(final String json) throws Exception {
        final String jsonFileName = Helper.getUniqueID() + ".json";

        final PrintWriter printWriter = new PrintWriter(PythonConstants.PYTHON_DATASET_PATH + "/" + jsonFileName);
        printWriter.write(json);
        printWriter.close();

        return jsonFileName;
    }

    public static void executeAction(final String jsonFileName) {

    }
}
