package com.backend.thesis.controller;

import com.backend.thesis.domain.entity.DatasetEntity;
import com.backend.thesis.service.DatasetService;
import com.backend.thesis.service.TestService;
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
public class TestController {
    private final DatasetService datasetService;
    private final TestService testService;

    public TestController(final DatasetService datasetService, TestService testService) {
        this.datasetService = datasetService;
        this.testService = testService;
    }

    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/test/dickey-fuller-test")
    public ResponseEntity<Type.RequestResult<String>> handleDickeyFullerTest(
            final HttpServletRequest request,
            @RequestParam(name = "idDataset") final String idDataset,
            @RequestParam(name = "pValue") final String pValue
    ) {
        final Type.ActionResult<DatasetEntity> datasetResult = this.datasetService.getDatasetOfUser(
                request.getAttribute(Constants.SESSION_COOKIE_NAME).toString(),
                Helper.stringToLong(idDataset)
        );

        if (!datasetResult.success()) {
            return new ResponseEntity<>(new Type.RequestResult<>(datasetResult.message(), null), HttpStatus.BAD_REQUEST);
        }

        final var result = this.testService.dickeyFullerTest(
                datasetResult.data(),
                Helper.stringToDouble(pValue)
        );
        return new ResponseEntity<>(new Type.RequestResult<>("ok", null), HttpStatus.OK);
    }
}
