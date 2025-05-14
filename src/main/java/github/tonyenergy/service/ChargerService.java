package github.tonyenergy.service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Charger service
 *
 * @author Tony
 * @date 2025/5/14
 */
public interface ChargerService {

    /**
     * Uploads a charger card file to OSS.
     *
     * @param filename The name of the file to be uploaded (including the file extension).
     * @param file     The file object representing the charger card data to be uploaded.
     */
    void uploadChargerCardToOss(String filename, File file);

    /**
     * Lists all charger card files under a specified prefix in OSS, optionally filtered by suffix.
     *
     * @param prefix The prefix to filter the files (e.g., "/data/chargerCard/").
     * @param end    The suffix to filter the files (e.g., ".json", ".log").
     * @return A list of filenames that match the prefix and suffix criteria.
     */
    List<String> listChargerFiles(String prefix, String end);

    /**
     * Downloads charger card files from OSS to a local directory.
     *
     * @param dataDir  The local directory where the charger card files will be saved.
     * @param prefix   The prefix used to filter the charger card files in OSS.
     * @param fileName The name of the file to be downloaded.
     */
    void downloadChargerCardFiles(Path dataDir, String prefix, String fileName);
}
