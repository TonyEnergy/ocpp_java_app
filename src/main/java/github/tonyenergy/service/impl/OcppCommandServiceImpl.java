package github.tonyenergy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.tonyenergy.entity.common.OCPPServerCommandsEnumCode;
import github.tonyenergy.entity.common.OcppCommand;
import github.tonyenergy.mapper.OcppCommandMapper;
import github.tonyenergy.service.OcppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OcppCommandServiceImpl extends ServiceImpl<OcppCommandMapper, OcppCommand> implements OcppService {
    /**
     * get all ocpp commands
     *
     * @return ocpp commands list
     */
    @Override
    public List<OcppCommand> getAllOcppCommands() {
        return baseMapper.selectList(null);
    }

}
