package com.backend.thesis.controller;

import com.backend.thesis.service.DatasetService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
public class FileController {
    private final DatasetService datasetService;

    public FileController(final DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/dataset/upload")
    public ResponseEntity<String> handleDatasetUpload(
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
        Type.ActionResult result = this.datasetService.tryToSaveDataset(
                request.getHeader(Constants.SESSION_COOKIE_NAME),
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
            return Helper.prepareResponse("Dataset bol úspešne uložený", HttpStatus.OK);
        } else {
            return Helper.prepareResponse(result.error(), HttpStatus.BAD_REQUEST);
        }
    }
}
