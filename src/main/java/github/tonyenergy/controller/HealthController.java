package github.tonyenergy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liutaohua
 * @Date: 2025/4/9
 */
@RestController
public class HealthController {

    @GetMapping("/ping")
    public String ping() {
        System.out.println("Server received ping command");
        return "pong";
    }
}

