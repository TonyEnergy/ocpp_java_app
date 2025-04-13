package github.tonyenergy.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
     * Upload Merged Logs to Oss, need to merge first, if system shutdown in some case, we can update the logs.
     */
    public void uploadLocalLogsToOss() {
        File mergedLogs = mergeLogs();
        String dateFolder = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String objectName = "logs/" + dateFolder + "/" + mergedLogs.getName();
        try {
            // upload log file to oss
            ossClient.putObject(bucketName, objectName, mergedLogs);
            log.info("✅ Log file upload successful! Path: {}", objectName);
        } catch (Exception e) {
            log.error("❌ Failed to upload log file: {}", mergedLogs.getName(), e);
        }
    }

    /**
     * delete local logs
     */
    public void deleteLocalLogs() {
        File logDir = new File(localLogPath);
        // delete ocpp file and merged file
        String[] prefixes = {"ocpp-", "merged-"};
        for (String prefix : prefixes) {
            File[] logFiles = logDir.listFiles((dir, name) -> name.contains(prefix) && !"ocpp.log".equals(name));
            if (checkLogFiles(logDir, logFiles)) {
                for (File logFile : logFiles) {
                    String fileName = logFile.getName();
                    if (logFile.delete()) {
                        log.info("🧹 Successfully deleted log file: {}", fileName);
                    } else {
                        log.warn("⚠️ Failed to delete log file: {}", fileName);
                    }
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

    /**
     * Thorough server chan to send local log to WeChat
     */
    public void sendLogsToWechat() {
        File mergedLogs = mergeLogs();
        String formattedContent = formatLogContent(mergedLogs);
        sendLogsToServerChan(formattedContent);
    }

    /**
     * Merge ocpp logs
     *
     * @return merged logs file
     */
    public File mergeLogs() {
        try {
            File logDir = new File(localLogPath);
            // Merge ocpp logs
            File[] logFiles = logDir.listFiles((dir, name) -> (name.contains("ocpp")));
            if (checkLogFiles(logDir, logFiles)) {
                // Get today's date
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String mergedLogFileName = "merged-log-" + timestamp + ".txt";
                File mergedLogFile = new File(localLogPath, mergedLogFileName);
                // Merge log files
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(mergedLogFile))) {
                    for (File logFile : logFiles) {
                        log.info("✅ Merging log file: {}", logFile.getName());
                        byte[] fileContent = FileCopyUtils.copyToByteArray(logFile);
                        writer.write(new String(fileContent));
                        writer.newLine();
                    }
                }
                log.info("✅ Done! Merged file name: {}", mergedLogFileName);
                return mergedLogFile;
            }
        } catch (Exception e) {
            log.error("❌ Error merging and sending logs: ", e);
        }
        return null;
    }

    /**
     * Send logfile to server chan
     *
     * @param formattedContent Formatted 5hours log file
     */
    private void sendLogsToServerChan(String formattedContent) {
        try {
            log.info("✅ Sending to server chan...");
            // Send to server chan app
            JSONObject payload = new JSONObject();
            payload.put("text", "Server Log");
            payload.put("desp", formattedContent);
            String url = "https://sctapi.ftqq.com/" + serverChanToken + ".send";
            HttpUtil.post(url, payload.toString());
            log.info("✅ Done!");
        } catch (Exception e) {
            log.error("❌ Failed to send WeChat notification", e);
        }
    }

    /**
     * Format log content
     *
     * @param mergedLogFile mergedLogFile
     * @return log after format
     */
    private String formatLogContent(File mergedLogFile) {
        log.info("✅ Formatting...");
        // Read file content
        String content;
        try {
            content = new String(FileCopyUtils.copyToByteArray(mergedLogFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("❌ Error formatting...");
            throw new RuntimeException(e);
        }
        // Format content, add newline for each logs
        StringBuilder sb = new StringBuilder();
        sb.append("#### Today's Log  \n  \n");
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.contains("INFO")) {
                sb.append("**INFO**: ").append(line).append("  \n");
            } else if (line.contains("WARN")) {
                sb.append("**WARN**: ").append(line).append("  \n");
            } else if (line.contains("ERROR")) {
                sb.append("**ERROR**: ").append(line).append("  \n");
            } else {
                sb.append(line).append("  \n");
            }
        }
        sb.append("  \n---");
        return sb.toString();
    }


    /**
     * when shutdown server gracefully, execute this func
     */
    @PreDestroy
    public void onShutdown() {
        log.error("🛑 Application shutting down. Uploading logs to OSS...");
        uploadLocalLogsToOss();
        log.error("🛑 Upload finished!");
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
