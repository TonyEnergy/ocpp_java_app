package github.tonyenergy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.tonyenergy.entity.common.ChargerLog;
import github.tonyenergy.entity.common.OCPPCall;
import github.tonyenergy.entity.common.OCPPCallResult;
import github.tonyenergy.mapper.ChargerLogMapper;
import github.tonyenergy.service.ChargerLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ChargerLogServiceImpl extends ServiceImpl<ChargerLogMapper, ChargerLog> implements ChargerLogService {

    @Override
    public void saveChargerLog(String chargerId, OCPPCall ocppCall, OCPPCallResult ocppCallResult) {
        ChargerLog chargerLog = new ChargerLog();
        chargerLog.setChargerId(chargerId);
        chargerLog.setAction(ocppCall.getAction());
        chargerLog.setActionTime(LocalDateTime.now());
        chargerLog.setOcppCallPayload(ocppCall.getPayload().toString());
        chargerLog.setOcppCallResultPayload(ocppCallResult.getPayload().toString());
        baseMapper.insert(chargerLog);
        ResponseEntity.ok("✅ saved");
    }

    @Override
    public List<ChargerLog> checkChargerLogs(String chargerId) {
        LambdaQueryWrapper<ChargerLog> chargerLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chargerLogLambdaQueryWrapper.eq(ChargerLog::getChargerId, chargerId);
        return baseMapper.selectList(chargerLogLambdaQueryWrapper);
    }
}
