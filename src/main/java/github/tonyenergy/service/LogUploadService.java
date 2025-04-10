package github.tonyenergy.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @Author: Tony
 * @Date: 2025/4/9
 */
@Service
@Slf4j
public class LogUploadService {

    private OSS ossClient;
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
    private final String localLogPath = "logs";

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }


    public void uploadLogsToOSS() {
        File logDir = new File(localLogPath);
        if (!logDir.exists() || !logDir.isDirectory()) {
            log.warn("Log directory not found: {}", localLogPath);
            return;
        }

        File[] logFiles = logDir.listFiles();
        if (logFiles == null || logFiles.length == 0) {
            log.info("No log files to upload.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

        for (File logFile : logFiles) {
            if (logFile.isFile()) {
                String timestamp = sdf.format(new Date());
                String objectName = "logs/" + logFile.getName().replace(".log", "") + "-" + timestamp + ".log";

                try {
                    // upload log file to oss
                    ossClient.putObject(bucketName, objectName, logFile);
                    log.info("✅ Log file upload successful！Path: {}", objectName);

                    // try to delete local log file
                    boolean deleted = logFile.delete();
                    if (deleted) {
                        log.info("🧹 Local log file deleted: {}", logFile.getName());
                    } else {
                        log.warn("⚠️ Failed to delete local log file: {}", logFile.getName());
                    }
                } catch (Exception e) {
                    log.error("❌ Failed to upload log file: {}", logFile.getName(), e);
                }
            }
        }
    }



    @PreDestroy
    public void onShutdown() {
        log.info("🛑 Application shutting down. Uploading logs to OSS...");
        uploadLogsToOSS();
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
