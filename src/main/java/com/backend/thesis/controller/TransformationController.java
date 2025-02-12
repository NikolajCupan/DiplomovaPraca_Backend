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
            @RequestParam(name = "differenceLevel", required = false) final String differenceLevel
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final Type.ActionResult<DatasetInfoDto> result = this.transformationService.difference(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                datasetResult.data(),
                transformedDatasetName,
                Helper.stringToLong(differenceLevel)
        );

        if (result.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), result.data()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Type.RequestResult<>(result.message(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
