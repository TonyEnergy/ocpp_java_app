package github.tonyenergy.controller;

import github.tonyenergy.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
    public String ocppGetConfiguration(@RequestParam String chargerId, @RequestParam(required = false) String[] keys) {
        CompletableFuture<String> future = webSocketServer.sendGetConfiguration(chargerId, keys);
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
    }

    @GetMapping("/changeConfiguration")
    public String ocppChangeConfiguration(@RequestParam String chargerId, @RequestParam String key, @RequestParam String value) {
        CompletableFuture<String> future = webSocketServer.sendChangeConfiguration(chargerId, key, value);
        if (future == null) {
            log.warn("⚠️ ChargerId: {} not active or failed to send message", chargerId);
            return "Charger not active or failed to send message.";
        } else {
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
        }
    }

    @GetMapping("/reset")
    public String ocppReset(@RequestParam String chargerId, @RequestParam String type) {
        CompletableFuture<String> future = webSocketServer.sendReset(chargerId, type);
        if (future == null) {
            log.warn("⚠️ ChargerId: {} not active or failed to send message", chargerId);
            return "Charger not active or failed to send message.";
        } else {
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
        }
    }

    @GetMapping("/checkSessions")
    public void checkSessions() {
        webSocketServer.checkSessions();
    }
}
