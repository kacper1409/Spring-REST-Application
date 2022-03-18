package com.lab2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BrowserRequestController {

    Logger logger = LoggerFactory.getLogger(BrowserRequestController.class);

    @GetMapping("/controller")
    public ResponseEntity<String> testGetController() {
        logger.info("controller works");
        return ResponseEntity.ok("Reply");
    }
}
