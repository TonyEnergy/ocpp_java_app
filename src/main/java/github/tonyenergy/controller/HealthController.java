package github.tonyenergy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
/**
 * @Author: liutaohua
 * @Date: 2025/4/9
 */
@RestController
@Slf4j
public class HealthController {
    @GetMapping("/ping")
    public String ping() {
        log.info("📩 Server Received message：ping");
        return "pong";
    }
}

