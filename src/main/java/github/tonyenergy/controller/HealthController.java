package github.tonyenergy.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @Author: liutaohua
 * @Date: 2025/4/9
 */
@RestController
public class HealthController {
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    @GetMapping("/ping")
    public String ping() {
        logger.log("📩 Server Received message：ping");
        return "pong";
    }
}

