package com.backend.thesis.controller;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.service.DatasetService;
import com.backend.thesis.service.ModelService;
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

@RestController
public class ModelController {
    private final DatasetService datasetService;
    private final ModelService modelService;

    public ModelController(final DatasetService datasetService, final ModelService modelService) {
        this.datasetService = datasetService;
        this.modelService = modelService;
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/model/arima")
    public ResponseEntity<Type.RequestResult<String>> handleArimaModel(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "train_percent") final String trainPercent,
            @RequestParam(name = "season_length") final String seasonLength,
            @RequestParam(name = "normal_p") final String normal_p,
            @RequestParam(name = "normal_d") final String normal_d,
            @RequestParam(name = "normal_q") final String normal_q,
            @RequestParam(name = "seasonal_p") final String seasonal_p,
            @RequestParam(name = "seasonal_d") final String seasonal_d,
            @RequestParam(name = "seasonal_q") final String seasonal_q,
            @RequestParam(name = "forecast_count") final String forecastCount,
            @RequestParam(name = "pValueTests") final String pValueTests
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.modelService.arima(
                datasetResult.data(),
                Helper.stringToLong(trainPercent),
                Helper.stringToLong(seasonLength),
                Helper.stringToLong(normal_p),
                Helper.stringToLong(normal_d),
                Helper.stringToLong(normal_q),
                Helper.stringToLong(seasonal_p),
                Helper.stringToLong(seasonal_d),
                Helper.stringToLong(seasonal_q),
                Helper.stringToLong(forecastCount),
                Helper.stringToDouble(pValueTests)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), result.getHttpStatus());
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/model/simple-exp-smoothing")
    public ResponseEntity<Type.RequestResult<String>> handleSimpleExpSmoothing(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "train_percent") final String trainPercent,
            @RequestParam(name = "alpha") final String alpha,
            @RequestParam(name = "forecast_count") final String forecastCount
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.modelService.simpleExpSmoothing(
                datasetResult.data(),
                Helper.stringToLong(trainPercent),
                Helper.stringToDouble(alpha),
                Helper.stringToLong(forecastCount)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), result.getHttpStatus());
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/model/holt-winter")
    public ResponseEntity<Type.RequestResult<String>> handleHoltWinterModel(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "train_percent") final String trainPercent,
            @RequestParam(name = "season_length") final String seasonLength,
            @RequestParam(name = "trend_type") final String trendType,
            @RequestParam(name = "season_type") final String seasonType,
            @RequestParam(name = "alpha") final String alpha,
            @RequestParam(name = "beta") final String beta,
            @RequestParam(name = "gamma") final String gamma,
            @RequestParam(name = "forecast_count") final String forecastCount
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<JSONObject> result = this.modelService.holtWinter(
                datasetResult.data(),
                Helper.stringToLong(trainPercent),
                Helper.stringToLong(seasonLength),
                trendType,
                seasonType,
                Helper.stringToDouble(alpha),
                Helper.stringToDouble(beta),
                Helper.stringToDouble(gamma),
                Helper.stringToLong(forecastCount)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data().toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), result.getHttpStatus());
        }
    }
}
