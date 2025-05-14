package github.tonyenergy.service;

import java.io.InputStream;
import java.util.List;

/**
 * Interface for OSS operations.
 *
 * @author Tony
 * @date 2025/5/14
 */
public interface OssService {

    /**
     * Uploads a file to OSS.
     *
     * @param objectName  The object name (path) in OSS, e.g., "data/chargerCard/xxx.json".
     * @param inputStream The file's input stream.
     * @param contentType The content type (e.g., "application/json", "image/png").
     * @return The URL to access the uploaded file (if accessible).
     */
    String uploadFile(String objectName, InputStream inputStream, String contentType);

    /**
     * Deletes a specified object from OSS.
     *
     * @param objectName The object name in OSS.
     * @return Returns true if the deletion is successful, otherwise returns false.
     */
    boolean deleteFile(String objectName);

    /**
     * Retrieves the URL of an object stored in OSS.
     *
     * @param objectName The object name in OSS.
     * @return The URL to access the object.
     */
    String getFileUrl(String objectName);

    /**
     * Checks if a specified file exists in OSS.
     *
     * @param objectName The object name in OSS.
     * @return Returns true if the file exists, otherwise false.
     */
    boolean doesFileExist(String objectName);

    /**
     * Downloads a file from OSS.
     *
     * @param objectName The object name in OSS.
     * @return The input stream of the downloaded file.
     */
    InputStream downloadFile(String objectName);

    /**
     * Lists all files under a specified prefix in OSS, optionally filtered by suffix.
     *
     * @param prefix The prefix to filter the files.
     * @param end    The suffix to filter the files (e.g., ".json", ".log").
     * @return A list of object names that match the prefix and suffix.
     */
    List<String> listFiles(String prefix, String end);
}

