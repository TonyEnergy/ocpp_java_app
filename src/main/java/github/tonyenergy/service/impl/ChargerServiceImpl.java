package github.tonyenergy.service.impl;

import github.tonyenergy.service.ChargerService;
import github.tonyenergy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    public void uploadChargerCardToOss(String filename, File file) {
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
    }

    public List<String> listChargerFiles(String prefix, String end) {
        return ossService.listFiles(prefix, end);
    }

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
}
