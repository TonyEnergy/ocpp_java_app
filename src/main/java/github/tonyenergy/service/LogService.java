package github.tonyenergy.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import github.tonyenergy.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log Service
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@Service
@Slf4j
public class LogService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    private OSS ossClient;
    @Value("${serverchan.token}")
    private String serverChanToken;

    private final String localLogPath = "logs";

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }


    /**
     * Upload Local Logs to Oss
     */
    public void uploadLocalLogsToOSS() {
        File logDir = new File(localLogPath);
        File[] logFiles = logDir.listFiles((dir, name) -> name.contains("ocpp-") && name.endsWith(".log") && !name.equals("ocpp.log"));
        if (checkLogFiles(logDir, logFiles)) {
            for (File logFile : logFiles) {
                String dateFolder = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                String objectName = "logs/" + dateFolder + "/" + logFile.getName();
                try {
                    // upload log file to oss
                    ossClient.putObject(bucketName, objectName, logFile);
                    log.info("✅ Log file upload successful! Path: {}", objectName);
                } catch (Exception e) {
                    log.error("❌ Failed to upload log file: {}", logFile.getName(), e);
                }
            }
        }
    }

    /**
     * delete local logs
     */
    public void deleteLocalLogs() {
        File logDir = new File(localLogPath);
        File[] logFiles = logDir.listFiles((dir, name) -> name.contains("ocpp-") || name.contains("merge-") && !name.equals("ocpp.log"));
        if (checkLogFiles(logDir, logFiles)) {
            for (File logFile : logFiles) {
                String fileName = logFile.getName();
                boolean deleted = logFile.delete();
                if (deleted) {
                    log.info("🧹 Successfully deleted log file: {}", fileName);
                } else {
                    log.warn("⚠️ Failed to delete log file: {}", fileName);
                }
            }
        }
    }

    /**
     * check logDir exist and check logFiles see if it's null
     */
    private boolean checkLogFiles(File logDir, File[] logFiles) {
        if (!logDir.exists() || !logDir.isDirectory()) {
            log.warn("⚠️ Log directory not found: {}", localLogPath);
            return false;
        }
        if (logFiles == null || logFiles.length == 0) {
            log.info("⚠️ No log files to delete.");
            return false;
        }
        return true;
    }

    public void mergeAndSendLogs() {
        try {
            File logDir = new File(localLogPath);
            File[] logFiles = logDir.listFiles((dir, name) -> name.contains("ocpp-") && name.endsWith(".log") && !name.equals("ocpp.log"));
            if (checkLogFiles(logDir, logFiles)) {
                // 获取今天的日期
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String mergedLogFileName = "merged-log-" + timestamp + ".txt";
                File mergedLogFile = new File(localLogPath, mergedLogFileName);
                // 合并日志文件
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(mergedLogFile))) {
                    for (File logFile : logFiles) {
                        log.info("✅ Merging log file: {}", logFile.getName());
                        byte[] fileContent = FileCopyUtils.copyToByteArray(logFile);
                        writer.write(new String(fileContent));
                        writer.newLine();
                    }
                }
                // 将合并后的日志发送到Server酱
                log.info("✅ Sending log file to wechat: {}", mergedLogFile.getName());
                sendLogsToServerChan(mergedLogFile);
                log.info("✅ Sent!");
            }
        } catch (Exception e) {
            log.error("❌ Error merging and sending logs: ", e);
        }
    }

    /**
     * send logfile to server chan
     *
     * @param mergedLogFile 5hours log file
     * @throws IOException error
     */
    private void sendLogsToServerChan(File mergedLogFile) throws IOException {
        String content = new String(FileCopyUtils.copyToByteArray(mergedLogFile));
        String formattedContent = content.replaceAll("\r\n", "  \n")
                .replaceAll("\n", "  \n");
        JSONObject payload = new JSONObject();
        payload.put("text", "Today's Log");
        payload.put("desp", formattedContent);
        String url = "https://sctapi.ftqq.com/" + serverChanToken + ".send";
        HttpUtil.post(url, payload.toJSONString());
    }


    /**
     * when shutdown server gracefully, execute this func
     */
    @PreDestroy
    public void onShutdown() {
        log.info("🛑 Application shutting down. Uploading logs to OSS...");
        uploadLocalLogsToOSS();
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
