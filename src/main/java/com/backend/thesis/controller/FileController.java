package com.backend.thesis.controller;

import com.backend.thesis.service.DatasetService;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    private final DatasetService datasetService;

    public FileController(final DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/dataset/upload")
    public ResponseEntity<String> handleDatasetUpload(
            @RequestParam(name = "startDateTime") final String startDateTime,
            @RequestParam(name = "file") final MultipartFile file,
            @RequestParam(name = "frequency") final String frequency,
            @RequestParam(name = "hasHeader") final String hasHeader,
            @RequestParam(name = "hasDateColumn") final String hasDateColumn
    ) {
        this.datasetService.saveDataset(
                Helper.stringToLocalDateTime(startDateTime),
                file,
                Helper.stringToFrequency(frequency),
                Helper.stringToBoolean(hasHeader),
                Helper.stringToBoolean(hasDateColumn)
        );

        return Helper.prepareResponse("Hello", HttpStatus.OK);
    }
}
