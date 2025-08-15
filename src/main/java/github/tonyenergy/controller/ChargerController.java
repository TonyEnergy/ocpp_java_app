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
     * add a charger card
     *
     * @param chargerCardVo charger card vo
     * @return message
     */
    @PostMapping("/addCharger")
    public ResponseEntity<?> addCharger(@RequestBody ChargerCardVo chargerCardVo) {
        return chargerService.addCharger(chargerCardVo);
    }

    /**
     * List all charger card
     *
     * @return file name list
     */
    @PostMapping("/listAllChargerCard")
    public List<ChargerCard> listAllChargerCard() {
        return chargerService.listAllChargerCard();
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
     * @return if delete successful, return true
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
}
