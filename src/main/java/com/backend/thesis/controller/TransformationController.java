package com.backend.thesis.controller;

import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.service.DatasetService;
import com.backend.thesis.service.TransformationService;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TransformationController {
    private final DatasetService datasetService;
    private final TransformationService transformationService;

    public TransformationController(final DatasetService datasetService, final TransformationService transformationService) {
        this.datasetService = datasetService;
        this.transformationService = transformationService;
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/transformation/difference")
    public ResponseEntity<Type.RequestResult<DatasetInfoDto>> handleDifferenceTransformation(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "transformedDatasetName") final String transformedDatasetName,
            @RequestParam(name = "differenceLevel") final String differenceLevel
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<DatasetInfoDto> result = this.transformationService.difference(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                datasetResult.data(),
                transformedDatasetName,
                Helper.stringToLong(differenceLevel)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/transformation/logarithm")
    public ResponseEntity<Type.RequestResult<DatasetInfoDto>> handleLogarithmTransformation(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "transformedDatasetName") final String transformedDatasetName,
            @RequestParam(name = "useNaturalLog") final String useNaturalLog,
            @RequestParam(name = "base", required = false) final Optional<String> base

    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<DatasetInfoDto> result = this.transformationService.logarithm(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                datasetResult.data(),
                transformedDatasetName,
                Helper.stringToBoolean(useNaturalLog),
                Helper.tryStringToDouble(base)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/transformation/normalization")
    public ResponseEntity<Type.RequestResult<DatasetInfoDto>> handleNormalizationTransformation(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "transformedDatasetName") final String transformedDatasetName,
            @RequestParam(name = "min") final String min,
            @RequestParam(name = "max") final String max

    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<DatasetInfoDto> result = this.transformationService.normalization(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                datasetResult.data(),
                transformedDatasetName,
                Helper.stringToDouble(min),
                Helper.stringToDouble(max)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/transformation/standardization")
    public ResponseEntity<Type.RequestResult<DatasetInfoDto>> handleStandardizationTransformation(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "transformedDatasetName") final String transformedDatasetName,
            @RequestParam(name = "mean") final String mean,
            @RequestParam(name = "standard_deviation") final String standardDeviation

    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<DatasetInfoDto> result = this.transformationService.standardization(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                datasetResult.data(),
                transformedDatasetName,
                Helper.stringToDouble(mean),
                Helper.stringToDouble(standardDeviation)
        );

        if (result.isSuccess()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
