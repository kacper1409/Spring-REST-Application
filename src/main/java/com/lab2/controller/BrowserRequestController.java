package com.lab2.controller;

import com.lab2.service.BrowserRequestService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@Controller
@Slf4j
public class BrowserRequestController {

    private final BrowserRequestService browserRequestService;

    @GetMapping("/controller")
    public ResponseEntity<String> getPlayerData(@RequestParam(value = "nickname") String nickname) {
        log.info(nickname);
        return ResponseEntity.ok(browserRequestService.getPlayerData(nickname));
    }
}
