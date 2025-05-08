package github.tonyenergy.websocket;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.common.MessageTypeEnumCode;
import github.tonyenergy.entity.conf.BootNotificationConf;
import github.tonyenergy.entity.conf.HeartbeatConf;
import github.tonyenergy.entity.conf.StatusNotificationConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;


/**
 * Web socket server
 *
 * @Author: Tony
 * @Date: 2025/5/5
 */

@Component
@Slf4j
public class WebSocketServer extends TextWebSocketHandler {

    private static final Set<WebSocketSession> WEB_SOCKET_SESSIONS = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Store messageId and CompletableFuture Map, use to asynchronous response
     */
    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // /ocpp/ws/XGJ20241014
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String chargerId = path.substring(path.lastIndexOf('/') + 1);
        log.info("🔗 WebSocket Connected: sessionId={}, chargerId={}", session.getId(), chargerId);
        WEB_SOCKET_SESSIONS.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // read payload as an object map
        JsonNode jsonNode = objectMapper.readTree(payload);
        if (jsonNode.isArray() && jsonNode.size() >= 3) {
            // get message type id
            int messageTypeId = jsonNode.get(0).asInt();
            // get message id
            String messageId = jsonNode.get(1).asText();
            // Call from client (charger)
            if (messageTypeId == MessageTypeEnumCode.CALL.getMessageTypeNumber()) {
                CompletableFuture<String> future = new CompletableFuture<>();
                log.info("📩 Received message of type {}: {}", messageTypeId, payload);
                // Check OCPP command, then response.
                String responseToCharger = dealWithPayload(payload, messageId, future);
                session.sendMessage(new TextMessage(responseToCharger));
                future.complete(payload);
            }
            // Response from client (charger)
            else if (messageTypeId == MessageTypeEnumCode.CALL_RESULT.getMessageTypeNumber()) {
                CompletableFuture<String> future = pendingRequests.remove(messageId);
                if (future != null) {
                    log.info("📩 Received message of type {}: {}", messageTypeId, payload);
                    pendingRequests.put(messageId, future);
                    future.complete(payload);
                } else {
                    log.warn("⚠️ No pending request for messageId: {}", messageId);
                }
            }
            // Call Error
            else if (messageTypeId == MessageTypeEnumCode.CALL_ERROR.getMessageTypeNumber()) {
                CompletableFuture<String> future = pendingRequests.remove(messageId);
                if (future != null) {
                    log.warn("⚠️ Received CallError: {}", payload);
                    future.completeExceptionally(new RuntimeException("⚠️ Received CallError: " + payload));
                } else {
                    log.warn("⚠️ No pending request for messageId: {}", messageId);
                }
            } else {
                log.info("📩 Received message of type {}: {}", messageTypeId, payload);
            }
        } else {
            log.warn("⚠️ Invalid OCPP message format: {}", payload);
        }
    }

    /**
     * Check payload OCPP command type, then deal with it, return response then response charger
     *
     * @param payload   Charger request
     * @param messageId unique id
     * @param future    completable future
     * @return response
     */
    public String dealWithPayload(String payload, String messageId, CompletableFuture<String> future) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object[] arr = mapper.readValue(payload, Object[].class);
            String action = arr[2].toString();
            log.info("🔼 Action is: {}", action);
            switch (action) {
                case "BootNotification":
                    BootNotificationConf bootNotificationConf = new BootNotificationConf("Accepted", new Date(), 3600);
                    log.info("📤 Response Charger :{}", bootNotificationConf.getResponse(messageId));
                    pendingRequests.put(messageId, future);
                    return bootNotificationConf.getResponse(messageId);
                case "StatusNotification":
                    StatusNotificationConf statusNotificationConf = new StatusNotificationConf();
                    log.info("📤 Response Charger :{}", statusNotificationConf.getResponse(messageId));
                    pendingRequests.put(messageId, future);
                    return statusNotificationConf.getResponse(messageId);
                case "Heartbeat":
                    HeartbeatConf heartbeatConf = new HeartbeatConf(new Date());
                    log.info("📤 Response Charger :{}", heartbeatConf.getResponse(messageId));
                    return heartbeatConf.getResponse(messageId);
                default:
                    return "{}";
            }
        } catch (JsonProcessingException e) {
            log.error("❌ Error read action");
            return null;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WEB_SOCKET_SESSIONS.remove(session);
        log.info("❌ WebSocket connection closed: {}", session.getId());
    }

    /**
     * Finding Websocket session through chargerId
     *
     * @param chargerId charger id
     * @return session
     */
    public WebSocketSession findSessionByChargerId(String chargerId) {
        for (WebSocketSession session : WEB_SOCKET_SESSIONS) {
            if (session.isOpen()) {
                URI uri = session.getUri();
                if (uri != null) {
                    // /ocpp/ws/ChargerSN
                    String path = uri.getPath();
                    String currentChargerId = path.substring(path.lastIndexOf('/') + 1);
                    if (chargerId.equals(currentChargerId)) {
                        return session;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sending GetConfiguration request，return CompletableFuture to asynchronous get response
     *
     * @param chargerId charger id
     * @param keys the key which need to be got
     * @return CompletableFuture
     */
    public CompletableFuture<String> sendGetConfiguration(String chargerId, String[] keys) {
        try {
            WebSocketSession session = findSessionByChargerId(chargerId);
            String messageId = UUID.randomUUID().toString();
            Map<String, Object> payload = new HashMap<>();
            if (keys != null) {
                payload.put("key", keys);
            }
            Object[] ocppMessage = new Object[]{2, messageId, "GetConfiguration", payload};
            String jsonMessage = objectMapper.writeValueAsString(ocppMessage);
            log.info("📤 Sending GetConfiguration to {}: {}", chargerId, jsonMessage);
            CompletableFuture<String> future = new CompletableFuture<>();
            pendingRequests.put(messageId, future);
            session.sendMessage(new TextMessage(jsonMessage));
            return future;
        } catch (IOException e) {
            log.info("ChargerId: {} is not active", chargerId);
        }
        return null;
    }

    /**
     * Sending ChangeConfiguration request，return CompletableFuture to asynchronous get response
     *
     * @param chargerId charger id
     * @param key the key which need to be changed
     * @param value new value
     * @return charger response
     */
    public CompletableFuture<String> sendChangeConfiguration(String chargerId, String key, String value) {
        try {
            WebSocketSession session = findSessionByChargerId(chargerId);
            String messageId = UUID.randomUUID().toString();
            Map<String, Object> payload = new HashMap<>();
            if (key != null && value != null) {
                payload.put("key", key);
                payload.put("value", value);
            }
            Object[] ocppMessage = new Object[]{2, messageId, "ChangeConfiguration", payload};
            String jsonMessage = objectMapper.writeValueAsString(ocppMessage);
            log.info("📤 Sending ChangeConfiguration to {}: {}", chargerId, jsonMessage);
            CompletableFuture<String> future = new CompletableFuture<>();
            pendingRequests.put(messageId, future);
            session.sendMessage(new TextMessage(jsonMessage));
            return future;
        } catch (IOException e) {
            log.info("ChargerId: {} is not active", chargerId);
        }
        return null;
    }
}
