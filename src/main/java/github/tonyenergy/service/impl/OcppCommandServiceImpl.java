package github.tonyenergy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.tonyenergy.entity.common.OCPPServerCommandsEnumCode;
import github.tonyenergy.entity.common.OcppCommand;
import github.tonyenergy.mapper.OcppCommandMapper;
import github.tonyenergy.service.OcppService;
import github.tonyenergy.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class OcppCommandServiceImpl extends ServiceImpl<OcppCommandMapper, OcppCommand> implements OcppService {

    @Autowired
    private WebSocketServer webSocketServer;
    /**
     * get all ocpp commands
     *
     * @return ocpp commands list
     */
    @Override
    public List<OcppCommand> getAllOcppCommands() {
        return baseMapper.selectList(null);
    }

    /**
     * execute ocpp action
     * @param chargerId charger id
     * @param action action
     * @param payload payload
     * @return return response from charger
     */
    @Override
    public String executeOcppAction(String chargerId, String action, HashMap<String,Object> payload) {
        if (OCPPServerCommandsEnumCode.from(action) == OCPPServerCommandsEnumCode.GetConfiguration) {
            Object key = payload.get("key");
            CompletableFuture<String> future = webSocketServer.sendGetConfiguration(chargerId, (String[]) key);
            if (future != null) {
                try {
                    // Wait for the response with a timeout of 10 seconds
                    return future.get(10, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    log.error("❌ Response Timeout for chargerId: {}", chargerId);
                    return "Response Timeout!!!";
                } catch (InterruptedException | ExecutionException e) {
                    log.error("❌ Error while getting response for chargerId: {}", chargerId);
                    return "Error while getting response.";
                }
            } else {
                log.warn("⚠️ ChargerId: {} not active or failed to send message", chargerId);
                return "Charger not active or failed to send message.";
            }
        } else if (OCPPServerCommandsEnumCode.from(action) == OCPPServerCommandsEnumCode.ChangeConfiguration) {
            String key = (String) payload.get("key");
            String value = (String) payload.get("value");
            CompletableFuture<String> future = webSocketServer.sendChangeConfiguration(chargerId, key, value);
            if (future == null) {
                log.warn("⚠️ ChargerId: {} not active or failed to send message", chargerId);
                return "Charger not active or failed to send message.";
            } else {
                try {
                    // Wait for the response with a timeout of 10 seconds
                    String ocppCallResult = future.get(10, TimeUnit.SECONDS);
                    log.info("📩 Received CALL message from server {}", ocppCallResult);
                    return ocppCallResult;
                } catch (TimeoutException e) {
                    log.error("❌ Response Timeout for chargerId: {}", chargerId);
                    return "Response Timeout!!!";
                } catch (InterruptedException | ExecutionException e) {
                    log.error("❌ Error while getting response for chargerId: {}", chargerId);
                    return "Error while getting response.";
                }
            }
        } else {
            return "❌ Command is not standard!";
        }
    }
}
