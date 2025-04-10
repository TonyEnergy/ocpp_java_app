package github.tonyenergy.controller;  // 你可以根据你的实际包路径修改这里的包名

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);  // 创建 logger 实例

    @GetMapping
    public ResponseEntity<String> getLogContent() {
        logger.info("请求日志内容");  // 用日志记录请求
        try {
            String content = new String(Files.readAllBytes(Paths.get("logs/ocpp.log")));
            return ResponseEntity.ok().body(content);
        } catch (IOException e) {
            logger.error("读取日志失败: {}", e.getMessage(), e);  // 用日志记录异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("读取日志失败: " + e.getMessage());
        }
    }
}

