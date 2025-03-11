package com.backend.thesis.controller;

import com.backend.thesis.utility.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @CrossOrigin(exposedHeaders = Constants.SESSION_COOKIE_NAME)
    @GetMapping(path = "/user/get-cookie")
    public ResponseEntity<String> handleGetCookie(final HttpServletRequest request) {
        final String cookie = request.getAttribute(Constants.SESSION_COOKIE_NAME).toString();

        if (cookie.isEmpty()) {
            return new ResponseEntity<>("Cookie was assigned", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cookie could not be assigned", HttpStatus.BAD_REQUEST);
        }
    }
}
