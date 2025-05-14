package github.tonyenergy.config;

import github.tonyenergy.service.ChargerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Restore charger card data when restart render server
 *
 * @author Tony
 * @date 2025/5/14
 */
@Component
@RequiredArgsConstructor
public class ChargerInitializer implements ApplicationRunner {

    private final ChargerService chargerService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String prefix = "data/charger_card/";
        String end = ".json";
        Path dataDir = Paths.get(System.getProperty("user.dir"), "data/charger_card");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        List<String> chargerFiles = chargerService.listChargerFiles(prefix, end);
        for (String filename : chargerFiles) {
            chargerService.downloadChargerCardFiles(dataDir, prefix, filename);
        }
        System.out.println("✅ ChargerCard data restored from OSS.");
    }
}

