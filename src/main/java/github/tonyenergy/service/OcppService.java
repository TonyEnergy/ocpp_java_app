package github.tonyenergy.service;

import github.tonyenergy.entity.common.OcppCommand;

import java.util.HashMap;
import java.util.List;

public interface OcppService {
    List<OcppCommand> getAllOcppCommands();

    String executeOcppAction(String chargerId, String action, HashMap<String,Object> payload);
}
