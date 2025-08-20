package github.tonyenergy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import github.tonyenergy.entity.common.ChargerLog;
import github.tonyenergy.entity.common.OCPPCall;
import github.tonyenergy.entity.common.OCPPCallResult;

import java.util.List;

public interface ChargerLogService extends IService<ChargerLog> {


    /**
     * save charger log
     *
     * @param ocppCall       ocpp call
     * @param ocppCallResult ocpp call result
     * @param chargerId      charger id
     */
    void saveChargerLog(String chargerId, OCPPCall ocppCall, OCPPCallResult ocppCallResult);


    /**
     * check charger log by charger id
     *
     * @param chargerId charger id
     * @return charger log list
     */
    List<ChargerLog> checkChargerLogs(String chargerId);
}
