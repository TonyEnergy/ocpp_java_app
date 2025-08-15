package github.tonyenergy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.tonyenergy.entity.ChargerCard;
import github.tonyenergy.entity.vo.ChargerCardVo;
import github.tonyenergy.mapper.ChargerMapper;
import github.tonyenergy.service.ChargerService;
import github.tonyenergy.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
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
        log.info("Charger: {} connect to the server", chargerId);
    }
}
