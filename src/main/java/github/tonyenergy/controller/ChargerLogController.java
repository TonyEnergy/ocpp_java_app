package github.tonyenergy.controller;

import github.tonyenergy.entity.common.ChargerLog;
import github.tonyenergy.service.ChargerLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api")
@Slf4j
public class ChargerLogController {

    @Autowired
    private ChargerLogService chargerLogService;

    /**
     * check charger logs
     * @param chargerId charger id
     * @return return charger log list
     */
    @PostMapping("/checkChargerLogs")
    public List<ChargerLog> checkChargerLogs(@RequestBody String chargerId) {
        return chargerLogService.checkChargerLogs(chargerId);
    }
}
