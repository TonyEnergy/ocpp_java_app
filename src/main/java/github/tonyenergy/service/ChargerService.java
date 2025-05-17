package github.tonyenergy.service;

import github.tonyenergy.entity.ChargerCard;
import github.tonyenergy.entity.vo.ChargerCardVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

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
     * add new charger, store in the local file and upload to oss
     *
     * @param chargerCardVo charger card vo
     * @return success or fail
     */
    ResponseEntity<?> addCharger(@RequestBody ChargerCardVo chargerCardVo);

    /**
     * Lists all charger card files under a specified prefix in OSS, optionally filtered by suffix.
     *
     * @param prefix The prefix to filter the files (e.g., "/data/charger_card/").
     * @param end    The suffix to filter the files (e.g., ".json", ".log").
     * @return A list of filenames that match the prefix and suffix criteria.
     */
    List<String> listOssChargerFileNames(String prefix, String end);

    /**
     * list all local charger id
     *
     * @return charger id list
     */
    List<String> listLocalChargerId();

    /**
     * Downloads charger card files from OSS to a local directory.
     *
     * @param dataDir  The local directory where the charger card files will be saved.
     * @param prefix   The prefix used to filter the charger card files in OSS.
     * @param fileName The name of the file to be downloaded.
     */
    void downloadChargerCardFiles(Path dataDir, String prefix, String fileName);

    /**
     * Get charger card entity through charger id
     *
     * @param chargerId charger id
     * @return charger card entity
     */
    ChargerCard getChargerCardByChargerId(String chargerId);

    /**
     * Delete charger card oss file and local file through charger id
     *
     * @param chargerId charger id
     * @return if delete successful, return true
     */
    Boolean deleteChargerByChargerId(String chargerId);

    /**
     * get all local charger cards
     *
     * @return charger card list
     */
    List<ChargerCard> getAllLocalChargerCards();

    /**
     * if charger connect successful, print log
     *
     * @param chargerId charger id
     */
    void connect(String chargerId);
}
