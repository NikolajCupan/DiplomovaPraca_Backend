package com.backend.thesis.controller;

import com.backend.thesis.domain.dto.DatasetForEditingDto;
import com.backend.thesis.domain.dto.DatasetInfoDto;
import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.service.DatasetService;
import com.backend.thesis.utility.Constants;
import com.backend.thesis.utility.Helper;
import com.backend.thesis.utility.Type;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
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
            @RequestParam(name = "datasetHasHeader") final String datasetHasHeader
    ) {
        final Type.ActionResult<DatasetInfoDto> result = this.datasetService.tryToSaveDataset(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                datasetName,
                file,
                startDateTime.map(Helper::stringToLocalDateTime),
                dateFormat,
                Helper.stringToFrequency(frequency),
                dateColumnName,
                dataColumnName,
                Helper.stringToBoolean(datasetHasDateColumn),
                Helper.stringToBoolean(datasetHasHeader)
        );

        if (result.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = {Constants.SESSION_COOKIE_NAME, "dataset-name"})
    @PostMapping(path = "/dataset/download")
    public ResponseEntity<InputStreamResource> handleDatasetDownload(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @RequestParam(name = "idDataset") final String idDataset
    ) {
        final Type.ActionResult<DatasetEntity> result = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(), Helper.stringToLong(idDataset)
        );

        if (result.success()) {
            try {
                final File rawFile = new File(Constants.STORAGE_DATASET_PATH, result.data().getFileName() + ".csv");
                final InputStreamResource resource = new InputStreamResource(new FileInputStream(rawFile));

                response.setHeader("dataset-name", result.data().getDatasetName());

                return ResponseEntity
                        .ok()
                        .contentLength(rawFile.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } catch (final Exception exception) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @GetMapping(path = "/dataset/get")
    public ResponseEntity<Type.RequestResult<List<DatasetInfoDto>>> handleDatasetGet(
            final HttpServletRequest request
    ) {
        final Type.ActionResult<List<DatasetInfoDto>> result = this.datasetService.getAllDatasetsOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString()
        );

        if (result.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/dataset/get-for-editing")
    public ResponseEntity<Type.RequestResult<DatasetForEditingDto>> handleDatasetGetForEditing(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset
    ) {
        final Type.ActionResult<DatasetForEditingDto> result = this.datasetService.getDatasetOfUserForEditing(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset),
                true
        );

        if (result.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/dataset/edit/v2")
    public ResponseEntity<Type.RequestResult<DatasetForEditingDto>> handleDatasetEditV2(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "newDatasetName") final Optional<String> newDatasetName,
            @RequestParam(name = "newColumnName") final Optional<String> newColumnName
    ) {
        final Type.ActionResult<DatasetForEditingDto> result = this.datasetService.editDatasetV2(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset),
                newDatasetName,
                newColumnName
        );

        if (result.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @DeleteMapping(path = "/dataset/delete")
    public ResponseEntity<Type.RequestResult<DatasetInfoDto>> handleDatasetDelete(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset
    ) {
        final Type.ActionResult<DatasetInfoDto> result = this.datasetService.deleteDataset(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (result.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
