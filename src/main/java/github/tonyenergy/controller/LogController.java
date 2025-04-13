package github.tonyenergy.controller;

import github.tonyenergy.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Upload Local Logs to Oss", description = "Merge ocpp logs then upload merged log file to oss")
    public void uploadLocalLogsToOss() {
        logService.uploadLocalLogsToOss();
    }

    /**
     * delete local logs
     */
    @DeleteMapping("/deleteLog")
    @Operation(summary = "Delete local logs", description = "Delete local logs, including ocpp-xxx logs and merged-xxx logs, will keep ocpp.log which is be always writing")
    public void deleteLocalLogs() {
        logService.deleteLocalLogs();
    }

    /**
     * get log file through local logs then send to server chan
     */
    @GetMapping("/sendLogToWechat")
    @Operation(summary = "Send log to wechat", description = "Merge ocpp logs then  send merged log file to WeChat")
    public void sendLogToWeChat() {
        logService.sendLogsToWechat();
    }

    /**
     * get Recent 5 hours logs
     *
     * @return recent logs
     */
    @GetMapping("/getRecentLogs")
    @Operation(summary = "Get recent 5 hours logs", description = "will merge ocpp-xxx logs then return")
    public String getRecentLogs() {
        return logService.getRecentLogs();
    }
}
