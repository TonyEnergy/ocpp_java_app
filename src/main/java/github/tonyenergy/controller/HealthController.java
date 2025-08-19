package github.tonyenergy.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
/**
 * Use for uptimeRobot, wake up render application
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@RestController
@Slf4j
public class HealthController {
    @GetMapping("/ping")
    @Operation(summary = "Keep render server alive", description = "uptime robot will invoke this function per 5 minutes, keep render backend alive")
    public String ping() {
//        log.info("📩 Server Received message：ping");
        return "pong";
    }
}

