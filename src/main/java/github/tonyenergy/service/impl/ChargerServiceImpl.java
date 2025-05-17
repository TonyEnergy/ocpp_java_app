package github.tonyenergy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import github.tonyenergy.entity.ChargerCard;
import github.tonyenergy.entity.vo.ChargerCardVo;
import github.tonyenergy.service.ChargerService;
import github.tonyenergy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Charger service impl
 *
 * @author Tony
 * @date 2025/5/14
 */
@Service
@Slf4j
public class ChargerServiceImpl implements ChargerService {

    @Autowired
    public OssService ossService;

    /**
     * create a charger, set status and firmware version and offline enable as default
     *
     * @param chargerCardVo charger card vo which send from front server
     * @return
     */
    @Override
    public ResponseEntity<?> addCharger(ChargerCardVo chargerCardVo) {
        try {
            // Under root dir "data/charger_card" folder
            Path dataDir = Paths.get(System.getProperty("user.dir"), "data", "charger_card");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            String chargerId = chargerCardVo.getChargerId();
            String filename = "charger_" + chargerId + ".json";
            // Check if we have same chargerId file
            Path filePath = dataDir.resolve(filename);
            if (Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This charger already exist: " + chargerId);
            }
            // Change charger card vo to charger card
            ChargerCard chargerCard = BeanUtil.copyProperties(chargerCardVo, ChargerCard.class);
            chargerCard.setStatus("offline");
            chargerCard.setFirmwareVersion("");
            chargerCard.setOfflineEnable(false);
            // Save file to local
            String json = chargerCard.toJson();
            Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
            log.info("✅ Save path: {}", filePath.toAbsolutePath());
            log.info("✅ Charger Info: {}", json);
            // Upload local file to OSS
            File file = filePath.toFile();
            log.info("✅ Uploading charger info to OSS...");
            String objectName = "data/charger_card/" + filename;
            try {
                InputStream inputStream = Files.newInputStream(file.toPath());
                String contentType = Files.probeContentType(file.toPath());
                String fileUrl = ossService.uploadFile(objectName, inputStream, contentType != null ? contentType : "application/octet-stream");
                log.info("✅ Charger file uploaded successfully! OSS URL: {}", fileUrl);
            } catch (Exception e) {
                log.error("❌ Failed to upload charger file: {}", file.getName(), e);
            }
            return ResponseEntity.ok("✅ saved");
        } catch (IOException e) {
            log.error("❌ saved error");
            return null;
        }
    }

    /**
     * list oss charger file names
     *
     * @param prefix The prefix to filter the files (e.g., "/data/charger_card/").
     * @param end    The suffix to filter the files (e.g., ".json", ".log").
     * @return file name list
     */
    public List<String> listOssChargerFileNames(String prefix, String end) {
        return ossService.listFiles(prefix, end);
    }


    /**
     * list local charger id
     *
     * @return charger id list
     */
    @Override
    public List<String> listLocalChargerId() {
        List<String> chargerIds = new ArrayList<>();
        File folder = new File("data/charger_card");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.startsWith("charger_") && name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    // get charger id part
                    String chargerId = fileName.substring("charger_".length(), fileName.length() - ".json".length());
                    chargerIds.add(chargerId);
                }
            }
        }
        return chargerIds;
    }


    /**
     * download charger card file from oss
     *
     * @param dataDir  The local directory where the charger card files will be saved.
     * @param prefix   The prefix used to filter the charger card files in OSS.
     * @param fileName The name of the file to be downloaded.
     */
    public void downloadChargerCardFiles(Path dataDir, String prefix, String fileName) {
        try {
            InputStream input = ossService.downloadFile(prefix + fileName);
            Path targetFile = dataDir.resolve(fileName);
            Files.copy(input, targetFile, StandardCopyOption.REPLACE_EXISTING);
            input.close();
        } catch (IOException e) {
            log.error("❌ Failed to download charger file: {}", fileName);
        }
    }

    /**
     * Get charger card entity through charger id
     *
     * @param chargerId charger id
     * @return charger card entity
     */
    @Override
    public ChargerCard getChargerCardByChargerId(String chargerId) {
        String fileName = "charger_" + chargerId + ".json";
        Path dataDir = Paths.get(System.getProperty("user.dir"), "data", "charger_card");
        Path filePath = dataDir.resolve(fileName);
        // check if file is exist
        if (!FileUtil.exist(filePath.toFile())) {
            throw new RuntimeException("Charger file not found: " + filePath);
        }
        // read as json string
        String jsonStr = FileUtil.readUtf8String(filePath.toFile());
        // parse object as charger card entity
        return JSON.parseObject(jsonStr, ChargerCard.class);
    }

    /**
     * delete charger by charger id
     *
     * @param chargerId charger id
     * @return if delete oss and local file successful, return true
     */
    @Override
    public Boolean deleteChargerByChargerId(String chargerId) {
        String fileName = "charger_" + chargerId + ".json";
        // delete local file first
        Path localPath = Paths.get(System.getProperty("user.dir"), "data", "charger_card", fileName);
        boolean localDeleted = false;
        if (FileUtil.exist(localPath.toFile())) {
            localDeleted = FileUtil.del(localPath.toFile());
        }
        if (localDeleted) {
            log.info("✅ Charger card delete successful (Local)");
        } else {
            log.error("❌ Charger card delete failed (Local)");
        }
        // delete oss file
        String ossPath = "data/charger_card/" + fileName;
        boolean ossDeleted = ossService.deleteFile(ossPath);
        if (ossDeleted) {
            log.info("✅ Charger card delete successful (OSS)");
        } else {
            log.error("❌ Charger card delete failed (OSS)");
        }
        // if oss file and local file delete success, return true
        return localDeleted && ossDeleted;
    }


    /**
     * get all local charger cards
     *
     * @return charger card list
     */
    @Override
    public List<ChargerCard> getAllLocalChargerCards() {
        List<ChargerCard> list = new ArrayList<>();
        Path folderPath = Paths.get(System.getProperty("user.dir"), "data", "charger_card");

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath, "charger_*.json");
            for (Path filePath : stream) {
                String content = new String(Files.readAllBytes(filePath));
                ChargerCard card = JSON.parseObject(content, ChargerCard.class);
                list.add(card);
            }
        } catch (IOException | DirectoryIteratorException e) {
            log.error("Get all local charger cards error...");
        }
        log.info("User get charger list, {}", list);
        return list;
    }

    /**
     * if charger connect successful, print log
     *
     * @param chargerId charger id
     */
    @Override
    public void connect(String chargerId) {
        log.info("Charger: {} connect to the server", chargerId);
    }
}
