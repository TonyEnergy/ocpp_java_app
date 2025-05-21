package github.tonyenergy.schedule;

import github.tonyenergy.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Log Scheduler
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LogScheduler {
    private final LogService logService;

    /**
     * Print alive log per 2 minutes
     */
    @Scheduled(fixedRate = 120000)
    public void logAlive() {
        log.info("💡 Server is still alive at {}", LocalDateTime.now());
    }
    
    /**
     * Send log to WeChat every 5 hours at the 1st minute
     */
    @Scheduled(cron = "0 1 0/5 * * ?")
    public void scheduledSendLogToWeChat() {
        log.info("🕒 [{}] Scheduled task: sending logs to wechat...", LocalDateTime.now());
        logService.sendLogsToWechat();
        log.info("🕒 Scheduled task: Sent!");
    }

    /**
     * Upload Log every 1 hour at the 2nd minute
     */
    @Scheduled(cron = "0 2 0/5 * * ?")
    public void scheduleLogUpload() {
        log.info("🕒 [{}] Scheduled task: uploading logs to OSS...", LocalDateTime.now());
        logService.uploadLocalLogsToOss();
        log.info("🕒 Scheduled task: Uploaded!");
    }

    /**
     * Delete Log file every 5 hours at the 3rd minute
     */
    @Scheduled(cron = "0 3 0/5 * * ?")
    public void scheduledDeleteLogs() {
        log.info("🕒 [{}] Scheduled task: Deleting local logs...", LocalDateTime.now());
        logService.deleteLocalLogs();
        log.info("🕒 Scheduled task: Deleted!");
    }
}

