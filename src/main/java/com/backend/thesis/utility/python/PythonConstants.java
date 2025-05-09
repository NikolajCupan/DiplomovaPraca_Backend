package com.backend.thesis.utility.python;

public class PythonConstants {
    private PythonConstants() {
    }

    //
    // Other
    public static final int PYTHON_TIMEOUT_MS = 120_000;
    public static final boolean PYTHON_ENABLE_PRINTING = true;
    // Other end
    //


    //
    // Directories
    public static final String PYTHON_BASE_DIRECTORY_NAME = "python_backend";
    public static final String PYTHON_MAIN_SCRIPT_NAME = "Main.py";

    public static final String DATASET_INPUT_PATH = "storage/python/input";
    public static final String DATASET_OUTPUT_PATH = "storage/python/output";
    // Directories end
    //


    //
    // Actions

    // Tests
    public static final String ACTION_DICKEY_FULLER_TEST = "dicker_fuller_test";
    public static final String ACTION_KPSS_TEST = "kpss_test";
    public static final String ACTION_SEASONAL_DECOMPOSE = "seasonal_decompose";
    public static final String ACTION_PERIODOGRAM = "periodogram";
    public static final String ACTION_CORRELOGRAM_ACF = "correlogram_acf";
    public static final String ACTION_CORRELOGRAM_PACF = "correlogram_pacf";
    public static final String ACTION_ARCH_TEST = "arch_test";
    public static final String ACTION_LJUNG_BOX_TEST = "ljung_box_test";

    // Transformations
    public static final String ACTION_DIFFERENCE = "difference";
    public static final String ACTION_LOGARITHM = "logarithm";
    public static final String ACTION_NORMALIZATION = "normalization";
    public static final String ACTION_STANDARDIZATION = "standardization";

    // Models
    public static final String ACTION_ARIMA = "arima";
    public static final String ACTION_SIMPLE_EXP_SMOOTHING = "simple_exp_smoothing";
    public static final String ACTION_DOUBLE_EXP_SMOOTHING = "double_exp_smoothing";
    public static final String ACTION_HOLT_WINTER = "holt_winter";

    // Actions end
    //


    //
    // JSON input
    public static final String FILE_NAME_KEY = "file_name";
    public static final String ACTION_KEY = "action";

    public static final String P_VALUE_KEY = "p_value";

    public static final String PYTHON_FREQUENCY_TYPE_KEY = "python_frequency";
    public static final String FREQUENCY_TYPE_KEY = "frequency";

    public static final String FORECAST_COUNT_KEY = "forecast_count";
    public static final String TRAIN_PERCENT_KEY = "train_percent";
    // JSON input end
    //


    //
    // JSON
    public static final String JSON_ELEMENT_TITLE_KEY = "title";
    public static final String JSON_ELEMENT_RESULT_KEY = "result";

    public static final String SUCCESS_KEY = "success";
    public static final String EXCEPTION_KEY = "exception";

    public static final String TRANSFORMED_FILE_NAME_KEY = "transformed_file_name";
    public static final String START_DATE_TIME_KEY = "start_date_time";
    // JSON end
    //
}
