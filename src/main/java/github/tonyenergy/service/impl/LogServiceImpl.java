package github.tonyenergy.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import github.tonyenergy.service.LogService;
import github.tonyenergy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Log Service
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Autowired
    public OssService ossService;

    @Value("${serverchan.token}")
    private String serverChanToken;

    private final String localLogPath = "logs";


    /**
     * Upload Merged Logs to Oss, need to merge first, if system shutdown in some case, we can update the logs.
     */
    public void uploadLocalLogsToOss() {
        log.info("✅ Uploading log file to OSS...");
        File mergedLogFile = mergeLogs();
        if (mergedLogFile == null) {
            log.warn("⚠️ No logs to upload.");
            return;
        }
        String dateFolder = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String objectName = "logs/" + dateFolder + "/" + mergedLogFile.getName();

        try {
            InputStream inputStream = Files.newInputStream(mergedLogFile.toPath());
            String contentType = Files.probeContentType(mergedLogFile.toPath());
            String fileUrl = ossService.uploadFile(objectName, inputStream, contentType != null ? contentType : "application/octet-stream");
            log.info("✅ Log file upload successful! OSS URL: {}", fileUrl);
        } catch (Exception e) {
            log.error("❌ Failed to upload log file: {}", mergedLogFile.getName(), e);
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
                        log.info("✅ Successfully deleted log file: {}", fileName);
                    } else {
                        log.warn("⚠️ Failed to delete log file: {}", fileName);
                    }
                }
            }
        }
    }


    /**
     * get Recent 5 hours logs
     *
     * @return recent logs
     */
    public String getRecentLogs() {
        File mergedLogFile = mergeLogs();
        if (mergedLogFile == null) {
            return "⚠️ No logs available.";
        }
        try {
            log.info("✅ User getting Recent logs...");
            return new String(FileCopyUtils.copyToByteArray(mergedLogFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Thorough server chan to send local log to WeChat
     */
    public void sendLogsToWechat() {
        File mergedLogFile = mergeLogs();
        if (mergedLogFile != null) {
            String formattedContent = formatLogContent(mergedLogFile);
            sendLogsToServerChan(formattedContent);
        }
    }


    /**
     * check logDir exist and check logFiles see if it's null
     *
     * @param logDir   log directory
     * @param logFiles log files
     * @return if directory exist and it has files, return true
     */
    public boolean checkLogFiles(File logDir, File[] logFiles) {
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
                sortLogFilesByTimestamp(logFiles);
                log.info("✅ Done!");
                // Get today's date
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String mergedLogFileName = "merged-log-" + timestamp + ".txt";
                File mergedLogFile = new File(localLogPath, mergedLogFileName);
                // Merge log files
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(mergedLogFile))) {
                    for (File logFile : logFiles) {
                        log.info("✅ Merging log file: {}", logFile.getName());
                        byte[] fileContent = FileCopyUtils.copyToByteArray(logFile);
                        writer.write(new String(fileContent, StandardCharsets.UTF_8));
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
    public void sendLogsToServerChan(String formattedContent) {
        try {
            log.info("✅ Sending to server chan...");
            // Send to server chan app
            JSONObject payload = new JSONObject();
            payload.put("text", "Server Log");
            payload.put("desp", formattedContent);
            String url = "https://sctapi.ftqq.com/" + serverChanToken + ".send";
            HttpUtil.post(url, payload.toString());
            log.info("✅ ServerChan push completed.");
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
    public String formatLogContent(File mergedLogFile) {
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
     * Sort ocpp log files by timestamp
     *
     * @param logFiles ocpp-xxx
     */
    public void sortLogFilesByTimestamp(File[] logFiles) {
        log.info("✅ Sorting...");
        Arrays.sort(logFiles, Comparator.comparing(file -> {
            String name = file.getName();
            try {
                // If log file name is ocpp.log, means this is the latest log file, so need to set is to max timestamp
                if ("ocpp.log".equals(name)) {
                    // Sort this file at last
                    return new Date(Long.MAX_VALUE);
                }
                int prefixIndex = name.indexOf("ocpp-");
                int suffixIndex = name.indexOf(".log");
                if (prefixIndex == -1 || suffixIndex == -1 || prefixIndex + 5 >= suffixIndex) {
                    throw new IllegalArgumentException("⚠️ File name format is invalid: " + name);
                }
                String timePart = name.substring(prefixIndex + 5, suffixIndex);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH");
                return sdf.parse(timePart);
            } catch (Exception e) {
                log.warn("⚠️ Failed to parse timestamp from file name: {}", name);
                return new Date(Long.MAX_VALUE);
            }
        }));
        Arrays.stream(logFiles).forEach(f -> log.info("📄 {}", f.getName()));
    }


    /**
     * when shutdown server gracefully, execute this func
     */
    @PreDestroy
    public void onShutdown() {
        log.error("🛑 Application shutting down. Uploading logs to OSS...");
        uploadLocalLogsToOss();
        log.error("🛑 Upload finished!");
    }
}
