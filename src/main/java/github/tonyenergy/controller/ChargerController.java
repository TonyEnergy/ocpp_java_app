package github.tonyenergy.controller;

import github.tonyenergy.entity.vo.ChargerCardVo;
import github.tonyenergy.entity.ChargerCard;
import github.tonyenergy.service.ChargerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * Create a charger
     *
     * @param chargerCardVo charger card vo
     * @return message
     */
    @PostMapping("/addCharger")
    public ResponseEntity<?> addCharger(@RequestBody ChargerCardVo chargerCardVo) {
        return chargerService.addCharger(chargerCardVo);
    }

    /**
     * List all charger file name from oss, use for restore data to local
     *
     * @return file name list
     */
    @PostMapping("/listOssChargerFileNames")
    public List<String> listOssChargerFileNames() {
        String prefix = "data/charger_card/";
        String end = ".json";
        return chargerService.listOssChargerFileNames(prefix, end);
    }

    /**
     * Get charger card by charger id
     *
     * @param chargerId charger id
     * @return charger card json string
     */
    @PostMapping("/getChargerCardByChargerId")
    public ChargerCard getChargerCardByChargerId(String chargerId) {
        return chargerService.getChargerCardByChargerId(chargerId);
    }

    /**
     * Delete charger by charger id
     *
     * @param chargerId charger id
     * @return if delete oss and local file successful, return true
     */
    @DeleteMapping("/charger/{chargerId}")
    public ResponseEntity<Boolean> deleteChargerByChargerId(@PathVariable String chargerId) {
        boolean success = chargerService.deleteChargerByChargerId(chargerId);
        if (success) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * Get all local charger cards
     *
     * @return return all local charger cards
     */
    @PostMapping("/getAllLocalChargerCards")
    public List<ChargerCard> getAllLocalChargerCards() {
        return chargerService.getAllLocalChargerCards();
    }
}
