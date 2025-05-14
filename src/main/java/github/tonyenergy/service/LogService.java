package github.tonyenergy.service;

import java.io.*;

/**
 * Log Service
 *
 * @Author: Tony
 * @Date: 2025/4/9
 */
public interface LogService {

    /**
     * upload local logs to oss
     */
    void uploadLocalLogsToOss();

    /**
     * delete local logs
     */
    void deleteLocalLogs();


    /**
     * get Recent 5 hours logs
     *
     * @return recent logs
     */
    String getRecentLogs();

    /**
     * Thorough server chan to send local log to WeChat
     */
    void sendLogsToWechat();


    /**
     * check logDir exist and check logFiles see if it's null
     *
     * @param logDir   log directory
     * @param logFiles log files
     * @return if directory exist and it has files, return true
     */
    boolean checkLogFiles(File logDir, File[] logFiles);

    /**
     * Merge ocpp logs
     *
     * @return merged logs file
     */
    File mergeLogs();

    /**
     * Send logfile to server chan
     *
     * @param formattedContent Formatted 5hours log file
     */
    void sendLogsToServerChan(String formattedContent);

    /**
     * Format log content
     *
     * @param mergedLogFile mergedLogFile
     * @return log after format
     */
    String formatLogContent(File mergedLogFile);

    /**
     * Sort ocpp log files by timestamp
     *
     * @param logFiles ocpp-xxx
     */
    void sortLogFilesByTimestamp(File[] logFiles);


    /**
     * when shutdown server gracefully, execute this func
     */
    void onShutdown();
}
