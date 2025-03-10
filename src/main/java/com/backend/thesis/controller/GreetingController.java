package com.backend.thesis.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {
    @MessageMapping("/response")
    @SendTo("/communication/request")
    public String greeting(String message) {
        return "bruh xD: " + message;
    }
}
