package github.tonyenergy.controller;

import github.tonyenergy.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Log Controller
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@RestController
@RequestMapping("/logs")
@Slf4j
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * upload
     */
    @GetMapping("/uploadLog")
    public void uploadLocalLogsToOSS() {
        logService.uploadLocalLogsToOSS();
    }

    /**
     * delete local logs
     */
    @DeleteMapping("/deleteLog")
    public void deleteLocalLogs() {
        logService.deleteLocalLogs();
    }

    /**
     * get log file through local logs then send to server chan
     */
    @GetMapping("/sendLog")
    public void mergeAndSendLogs() {
        logService.mergeAndSendLogs();
    }
}
