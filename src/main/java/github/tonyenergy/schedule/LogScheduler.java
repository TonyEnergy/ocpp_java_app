package github.tonyenergy.schedule;
import github.tonyenergy.service.LogUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/**
 * @Author: Tony
 * @Date: 2025/4/9
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LogScheduler {
    private final LogUploadService logUploadService;

    /**
     * Exec per hour
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void scheduleLogUpload() {
        log.info("🕒 Scheduled task: uploading logs to OSS...");
        logUploadService.uploadLogsToOSS();
        log.info("🕒 Scheduled task: Finish");
    }
}

