package com.backend.thesis.controller;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.service.DatasetService;
import com.backend.thesis.service.TestService;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TestController {
    private final DatasetService datasetService;
    private final TestService testService;

    public TestController(final DatasetService datasetService, final TestService testService) {
        this.datasetService = datasetService;
        this.testService = testService;
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/dickey-fuller-test")
    public ResponseEntity<Type.RequestResult<String>> handleDickeyFullerTest(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "pValue") final String pValue,
            @RequestParam(name = "maxlag", required = false) final Optional<String> maxlag,
            @RequestParam(name = "regression", required = false) final Optional<String> regression,
            @RequestParam(name = "autolag", required = false) final Optional<String> autolag
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.dickeyFullerTest(
                datasetResult.data(),
                Helper.stringToDouble(pValue),
                Helper.tryStringToInt(maxlag),
                regression,
                autolag
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/kpss-test")
    public ResponseEntity<Type.RequestResult<String>> handleKPSSTest(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "pValue") final String pValue,
            @RequestParam(name = "regression", required = false) final Optional<String> regression,
            @RequestParam(name = "nlags", required = false) final Optional<String> nlags
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.kpssTest(
                datasetResult.data(),
                Helper.stringToDouble(pValue),
                regression,
                Helper.tryStringToInt(nlags)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/seasonal-decompose")
    public ResponseEntity<Type.RequestResult<String>> handleSeasonalDecompose(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "period", required = false) final Optional<String> period,
            @RequestParam(name = "model", required = false) final Optional<String> modelType
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.seasonalDecompose(
                datasetResult.data(),
                Helper.tryStringToInt(period),
                modelType
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/periodogram")
    public ResponseEntity<Type.RequestResult<String>> handlePeriodogram(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "fs", required = false) final Optional<String> samplingFrequency,
            @RequestParam(name = "nfft", required = false) final Optional<String> fft,
            @RequestParam(name = "return_onesided", required = false) final Optional<String> spectrum,
            @RequestParam(name = "scaling", required = false) final Optional<String> scaling
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.periodogram(
                datasetResult.data(),
                Helper.tryStringToDouble(samplingFrequency),
                Helper.tryStringToInt(fft),
                Helper.tryStringToBoolean(spectrum),
                scaling
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/correlogram-acf")
    public ResponseEntity<Type.RequestResult<String>> handleCorrelogramAcf(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "alpha", required = false) final Optional<String> alpha,
            @RequestParam(name = "adjusted", required = false) final Optional<String> autocovariance,
            @RequestParam(name = "nlags", required = false) final Optional<String> lagsCount,
            @RequestParam(name = "fft", required = false) final Optional<String> useFft,
            @RequestParam(name = "bartlett_confint", required = false) final Optional<String> useBartlettFormula
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.acf(
                datasetResult.data(),
                Helper.tryStringToDouble(alpha),
                Helper.tryStringToBoolean(autocovariance),
                Helper.tryStringToInt(lagsCount),
                Helper.tryStringToBoolean(useFft),
                Helper.tryStringToBoolean(useBartlettFormula)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/correlogram-pacf")
    public ResponseEntity<Type.RequestResult<String>> handleCorrelogramPacf(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "alpha", required = false) final Optional<String> alpha,
            @RequestParam(name = "nlags", required = false) final Optional<String> lagsCount,
            @RequestParam(name = "method", required = false) final Optional<String> method
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.pacf(
                datasetResult.data(),
                Helper.tryStringToDouble(alpha),
                Helper.tryStringToInt(lagsCount),
                method
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/arch-test")
    public ResponseEntity<Type.RequestResult<String>> handleArchTest(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "pValue") final String pValue,
            @RequestParam(name = "nlags", required = false) final Optional<String> maxLag,
            @RequestParam(name = "ddof", required = false) final Optional<String> dfCount
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.archTest(
                datasetResult.data(),
                Helper.stringToDouble(pValue),
                Helper.tryStringToInt(maxLag),
                Helper.tryStringToInt(dfCount)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/ljung-box-test")
    public ResponseEntity<Type.RequestResult<String>> handleLjungBoxTest(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "pValue") final String pValue,
            @RequestParam(name = "period", required = false) final Optional<String> period,
            @RequestParam(name = "lags", required = false) final Optional<String> lagsCount,
            @RequestParam(name = "auto_lag", required = false) final Optional<String> autoLag,
            @RequestParam(name = "model_df", required = false) final Optional<String> dfCount
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.testService.ljungBoxTest(
                datasetResult.data(),
                Helper.stringToDouble(pValue),
                Helper.tryStringToInt(period),
                Helper.tryStringToInt(lagsCount),
                Helper.tryStringToBoolean(autoLag),
                Helper.tryStringToInt(dfCount)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
