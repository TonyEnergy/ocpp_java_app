package github.tonyenergy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.tonyenergy.entity.ChargerCard;
import github.tonyenergy.entity.common.OCPPServerCommandsEnumCode;
import github.tonyenergy.entity.vo.ChargerCardVo;
import github.tonyenergy.mapper.ChargerMapper;
import github.tonyenergy.service.ChargerService;
import github.tonyenergy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Charger service impl
 *
 * @author Tony
 * @date 2025/5/14
 */
@Service
@Slf4j
public class ChargerServiceImpl extends ServiceImpl<ChargerMapper, ChargerCard> implements ChargerService {

    @Autowired
    public OssService ossService;

    /**
     * add a chargerCard, set status to unavailable as default
     *
     * @param chargerCardVo charger card vo which send from front server
     * @return ok
     */
    @Override
    public ResponseEntity<?> addCharger(ChargerCardVo chargerCardVo) {
        ChargerCard chargerCard = new ChargerCard();
        BeanUtils.copyProperties(chargerCardVo, chargerCard);
        chargerCard.setStatus("unavailable");
        baseMapper.insert(chargerCard);
        return ResponseEntity.ok("✅ saved");
    }

    /**
     * list all charger cards
     *
     * @return charger cards list
     */
    public List<ChargerCard> listAllChargerCard() {
        return baseMapper.selectList(null);
    }

    /**
     * Get charger card through charger id
     *
     * @param chargerId charger id
     * @return charger card entity
     */
    @Override
    public ChargerCard getChargerCardByChargerId(String chargerId) {
        LambdaQueryWrapper<ChargerCard> chargerCardLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chargerCardLambdaQueryWrapper.eq(ChargerCard::getChargerId, chargerId);
        return baseMapper.selectOne(chargerCardLambdaQueryWrapper);
    }

    /**
     * delete charger by charger id
     *
     * @param chargerId charger id
     * @return if delete successful, return true
     */
    @Override
    public Boolean deleteChargerByChargerId(String chargerId) {
        LambdaUpdateWrapper<ChargerCard> chargerCardLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        chargerCardLambdaUpdateWrapper.eq(ChargerCard::getChargerId, chargerId);
        int delete = baseMapper.delete(chargerCardLambdaUpdateWrapper);
        return delete == 1;
    }


    /**
     * if charger connect successful, print log
     *
     * @param chargerId charger id
     */
    @Override
    public void connect(String chargerId) {
        log.info("🔋 Charger: {} connect to the server", chargerId);
    }

    @Override
    public boolean checkChargerId(String chargerId) {
        LambdaQueryWrapper<ChargerCard> chargerCardLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chargerCardLambdaQueryWrapper.eq(ChargerCard::getChargerId, chargerId);
        ChargerCard chargerCard = baseMapper.selectOne(chargerCardLambdaQueryWrapper);
        return chargerCard != null;
    }

    /**
     * get all ocpp commands
     *
     * @return ocpp commands list
     */
    @Override
    public List<OCPPServerCommandsEnumCode> getAllOcppCommands() {
        return Arrays.asList(OCPPServerCommandsEnumCode.values());
    }
}
