package github.tonyenergy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.tonyenergy.entity.ChargerCard;
import github.tonyenergy.entity.common.OCPPServerCommandsEnumCode;
import github.tonyenergy.entity.vo.ChargerCardVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Charger service
 *
 * @author Tony
 * @date 2025/5/14
 */
public interface ChargerService extends IService<ChargerCard> {

    /**
     * add a new charger card
     *
     * @param chargerCardVo charger card vo
     * @return success or fail
     */
    ResponseEntity<?> addCharger(@RequestBody ChargerCardVo chargerCardVo);

    /**
     * list all charger card
     *
     * @return charger card list
     */
    List<ChargerCard> listAllChargerCard();


    /**
     * Get charger card entity through charger id
     *
     * @param chargerId charger id
     * @return charger card entity
     */
    ChargerCard getChargerCardByChargerId(String chargerId);

    /**
     * Delete charger card
     *
     * @param chargerId charger id
     * @return if delete successful, return true
     */
    Boolean deleteChargerByChargerId(String chargerId);

    /**
     * if charger connect successful, print log
     *
     * @param chargerId charger id
     */
    void connect(String chargerId);

    /**
     * Check if charger id exist in database
     *
     * @param chargerId charger id
     * @return if it existed, return true, if it doesn't exist, return false
     */
    boolean checkChargerId(String chargerId);

    /**
     * get all ocpp commands
     *
     * @return ocpp commands list
     */
    List<OCPPServerCommandsEnumCode> getAllOcppCommands();
}
