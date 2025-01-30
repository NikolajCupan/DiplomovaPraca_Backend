package com.backend.thesis.utility.python;

public class PythonConstants {
    private PythonConstants() {
    }

    public static final int PYTHON_TIMEOUT_MS = 5_000;
    public static final boolean PYTHON_ENABLE_PRINTING = true;

    public static final String PYTHON_BASE_DIRECTORY_PATH = "python_backend";
    public static final String PYTHON_MAIN_SCRIPT_NAME = "Main.py";

    public static final String DATASET_INPUT_PATH = "storage/python/input";
    public static final String DATASET_OUTPUT_PATH = "storage/python/output";

    public static final String FILE_NAME_KEY = "file_name";
    public static final String P_VALUE_KEY = "p_value";
    public static final String ACTION_KEY = "action";

    public static final String ACTION_DICKEY_FULLER_TEST = "dicker_fuller_test";
}
