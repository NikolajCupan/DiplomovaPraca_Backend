package com.backend.thesis.controller;

import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.service.DatasetService;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
public class FileController {
    private final DatasetService datasetService;

    public FileController(final DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/dataset/upload")
    public ResponseEntity<Type.RequestResult<DatasetInfoDto>> handleDatasetUpload(
            final HttpServletRequest request,
            @RequestParam(name = "datasetName") final String datasetName,
            @RequestParam(name = "file") final MultipartFile file,
            @RequestParam(name = "startDateTime", required = false) final Optional<String> startDateTime,
            @RequestParam(name = "dateFormat", required = false) final Optional<String> dateFormat,
            @RequestParam(name = "frequency") final String frequency,
            @RequestParam(name = "dateColumnName", required = false) final Optional<String> dateColumnName,
            @RequestParam(name = "dataColumnName", required = false) final Optional<String> dataColumnName,
            @RequestParam(name = "datasetHasDateColumn") final String datasetHasDateColumn,
            @RequestParam(name = "datasetHasHeader") final String datasetHasHeader,
            @RequestParam(name = "datasetHasMissingValues") final String datasetHasMissingValues
    ) {
        Type.ActionResult<DatasetInfoDto> result = this.datasetService.tryToSaveDataset(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                datasetName,
                file,
                startDateTime.map(Helper::stringToLocalDateTime),
                dateFormat,
                Helper.stringToFrequency(frequency),
                dateColumnName,
                dataColumnName,
                Helper.stringToBoolean(datasetHasDateColumn),
                Helper.stringToBoolean(datasetHasHeader),
                Helper.stringToBoolean(datasetHasMissingValues)
        );

        if (result.success()) {
            return Helper.prepareResponse(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return Helper.prepareResponse(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @GetMapping(path = "/dataset/get")
    public ResponseEntity<Type.RequestResult<List<DatasetInfoDto>>> handleDatasetGet(
            final HttpServletRequest request
    ) {
        Type.ActionResult<List<DatasetInfoDto>> result = this.datasetService.getDatasetsOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString()
        );

        if (result.success()) {
            return Helper.prepareResponse(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return Helper.prepareResponse(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
