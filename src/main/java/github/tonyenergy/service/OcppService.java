package github.tonyenergy.service;

import github.tonyenergy.entity.common.OcppCommand;

import java.util.List;

public interface OcppService {
    List<OcppCommand> getAllOcppCommands();
}
