package github.tonyenergy.controller;

import github.tonyenergy.entity.vo.ChargerCardVo;
import github.tonyenergy.service.ChargerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
     * create a charger
     *
     * @param chargerCardVo charger card vo
     * @return message
     */
    @PostMapping("/addCharger")
    public ResponseEntity<?> addCharger(@RequestBody ChargerCardVo chargerCardVo) {
        return chargerService.addCharger(chargerCardVo);
    }

    /**
     * List all charger file name from oss
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
     * get charger card by charger id
     *
     * @param chargerId charger id
     * @return charger card json string
     */
    @PostMapping("/getChargerCardByChargerId")
    public String getChargerCardByChargerId(String chargerId) {
        return chargerService.getChargerCardByChargerId(chargerId).toJson();
    }

    /**
     * delete charger by charger id
     *
     * @param chargerId charger id
     * @return if delete oss and local file successful, return true
     */
    @DeleteMapping("/deleteChargerByChargerId")
    public Boolean deleteChargerByChargerId(String chargerId) {
        return chargerService.deleteChargerByChargerId(chargerId);
    }
}
