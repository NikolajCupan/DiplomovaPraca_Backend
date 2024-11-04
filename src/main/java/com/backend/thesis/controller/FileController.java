package com.backend.thesis.controller;

import com.backend.thesis.helper.Constants;
import com.backend.thesis.helper.Helper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @PostMapping(path = "/file/upload")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam(name = "startDate") final String startDate,
            @RequestParam(name = "file") final MultipartFile file,
            @RequestParam(name = "frequency") final String frequency
    ) {
        return Helper.prepareResponse("Hello", HttpStatus.OK);
    }
}
