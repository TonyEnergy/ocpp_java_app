package github.tonyenergy.controller;

import github.tonyenergy.entity.ChargerCard;
import github.tonyenergy.service.ChargerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Charger controller
 *
 * @Author: Tony
 * @Date: 2025/5/14
 */

@RestController("/api")
@Slf4j
public class ChargerController {

    @Autowired
    private ChargerService chargerService;

    @PostMapping("/addCharger")
    public ResponseEntity<?> addCharger(@RequestBody ChargerCard chargerCard) throws IOException {
        // Under root dir "data/charger_card" folder
        Path dataDir = Paths.get(System.getProperty("user.dir"), "data", "charger_card");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        String chargerId = chargerCard.getChargerId();
        String filename = "charger_" + chargerId + ".json";
        // Check if we have same chargerId file
        Path filePath = dataDir.resolve(filename);
        if (Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This charger already exist: " + chargerId);
        }
        // Save file to local
        String json = chargerCard.toJson();
        Files.write(filePath, json.getBytes(StandardCharsets.UTF_8));
        log.info("Save path: {}", filePath.toAbsolutePath());
        // Upload local file to OSS
        chargerService.uploadChargerCardToOss(filename, filePath.toFile());
        return ResponseEntity.ok("saved");
    }
}
