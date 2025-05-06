package github.tonyenergy.controller;

import github.tonyenergy.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * OCPP controller
 *
 * @Author: Tony
 * @Date: 2025/5/5
 */

@RestController
@RequestMapping("/ocpp")
@Slf4j
public class OCPPController {

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("/getConfiguration")
    public String ocppGetConfiguration(@RequestParam String chargerId) {
        CompletableFuture<String> future = webSocketServer.sendGetConfiguration(chargerId, null);
        if (future == null) {
            return null;
        } else {
            try {
                return future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.info("Response Timeout!!!");
                return "Response Timeout!!!";
            }
        }
    }

    @GetMapping("/changeConfiguration")
    public String ocppChangeConfiguration(@RequestParam String chargerId, @RequestParam String key, @RequestParam String value) {
        CompletableFuture<String> future = webSocketServer.sendChangeConfiguration(chargerId, key, value);
        if (future == null) {
            return null;
        } else {
            try {
                return future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.info("Response Timeout!!!");
                return "Response Timeout!!!";
            }
        }
    }
}
