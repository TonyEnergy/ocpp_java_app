package github.tonyenergy.websocket;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.common.MessageTypeEnumCode;
import github.tonyenergy.entity.common.OCPPCallResultEnumCode;
import github.tonyenergy.entity.common.ResetTypeEnumCode;
import github.tonyenergy.entity.req.ChangeConfigurationReq;
import github.tonyenergy.entity.req.GetConfigurationReq;
import github.tonyenergy.entity.req.ResetReq;
import github.tonyenergy.service.ChargerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ChargerService chargerService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String chargerId = (String) session.getAttributes().get("chargerId");
        log.info("🔗 WebSocket Connected: sessionId={}, chargerId={}", session.getId(), chargerId);
        WEB_SOCKET_SESSIONS.add(session);
        // log connect success data
        chargerService.connect(chargerId);
    }


    /**
     * Handle message from charger
     *
     * @param session session
     * @param message message from charger
     * @throws Exception error
     */
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
            MessageTypeEnumCode typeEnum = MessageTypeEnumCode.fromNumber(messageTypeId);
            if (typeEnum != null) {
                CompletableFuture<String> future;
                switch (typeEnum) {
                    case CALL:
                        future = new CompletableFuture<>();
                        log.info("📩 Received message of type {}: {}", messageTypeId, payload);
                        // Check OCPP command, then response.
                        String responseToCharger = handlePayload(payload, messageId, future);
                        session.sendMessage(new TextMessage(responseToCharger));
                        future.complete(payload);
                        break;
                    case CALL_RESULT:
                        future = pendingRequests.remove(messageId);
                        if (future != null) {
                            log.info("📩 Received message of type {}: {}", messageTypeId, payload);
                            pendingRequests.put(messageId, future);
                            future.complete(payload);
                        } else {
                            log.warn("⚠️ No pending request for messageId: {}", messageId);
                        }
                        break;
                    case CALL_ERROR:
                        future = pendingRequests.remove(messageId);
                        if (future != null) {
                            log.warn("⚠️ Received CallError: {}", payload);
                            future.completeExceptionally(new RuntimeException("⚠️ Received CallError: " + payload));
                        } else {
                            log.warn("⚠️ No pending request for messageId: {}", messageId);
                        }
                        break;
                    default:
                        log.error("❌ Error OCPP Command");
                }
            } else {
                log.warn("⚠️ Unknown message type ID: {}", messageTypeId);
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
    public String handlePayload(String payload, String messageId, CompletableFuture<String> future) {
        try {
            Object[] arr = objectMapper.readValue(payload, Object[].class);
            String action = arr[2].toString();
            OCPPCallResultEnumCode command = OCPPCallResultEnumCode.from(action);
            if (command != null) {
                log.info("📤 Response Charger: {}", command.handle(messageId));
                pendingRequests.put(messageId, future);
                return command.handle(messageId);
            } else {
                log.warn("⚠️ Unknown action: {}", action);
                return "{}";
            }
        } catch (JsonProcessingException e) {
            log.error("❌ Error read payload");
        }
        return null;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WEB_SOCKET_SESSIONS.remove(session);
        log.info("❌ WebSocket connection closed: sessionId={}, status={}", session.getId(), status);
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
                    String path = uri.getPath();
                    String currentChargerId = path.substring(path.lastIndexOf('/') + 1);
                    if (chargerId.equals(currentChargerId)) {
                        return session;
                    }
                }
            }
        }
        log.warn("⚠️ No WebSocket session found for chargerId:{}", chargerId);
        return null;
    }

    public void checkSessions() {
        if (WEB_SOCKET_SESSIONS.isEmpty()) {
            log.info("⚠️ There have no sessions");
        } else {
            for (WebSocketSession session : WEB_SOCKET_SESSIONS) {
                log.info("session uri: {}", session.getUri());
                log.info("session id: {}", session.getId());
            }
        }
    }

    /**
     * Sending GetConfiguration request，return CompletableFuture to asynchronous get response
     *
     * @param chargerId charger id
     * @param keys      the key which need to be got
     * @return CompletableFuture
     */
    public CompletableFuture<String> sendGetConfiguration(String chargerId, String[] keys) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String messageId = UUID.randomUUID().toString();
        try {
            WebSocketSession session = findSessionByChargerId(chargerId);
            Object[] ocppMessage = new GetConfigurationReq(keys).getRequest(messageId);
            String jsonMessage = objectMapper.writeValueAsString(ocppMessage);
            pendingRequests.put(messageId, future);
            if (session != null) {
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("📤 Sending GetConfiguration to {}: {}", chargerId, jsonMessage);
            } else {
                log.error("❌ ChargerId: {} is not active or message send failed", chargerId);
                future.complete("❌ ChargerId:" + chargerId + "is not active or message send failed");
            }
        } catch (IOException ioException) {
            log.error("❌ Error sending message to chargerId: {}, messageId: {}", chargerId, messageId, ioException);
            future.complete("❌ Error sending message to chargerId: " + chargerId + "messageId: " + messageId);
        }
        return future;
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
        String messageId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            WebSocketSession session = findSessionByChargerId(chargerId);
            Object[] ocppMessage = new ChangeConfigurationReq(key, value).getRequest(messageId);
            String jsonMessage = objectMapper.writeValueAsString(ocppMessage);
            pendingRequests.put(messageId, future);
            if (session != null) {
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("📤 Sending ChangeConfiguration to {}: {}", chargerId, jsonMessage);
            } else {
                log.error("❌ ChargerId: {} is not active or message send failed", chargerId);
                future.complete("❌ ChargerId:" + chargerId + "is not active or message send failed");
            }
            return future;
        } catch (IOException ioException) {
            log.error("❌ Error sending message to chargerId: {}, messageId: {}", chargerId, messageId, ioException);
            future.complete("❌ Error sending message to chargerId: " + chargerId + "messageId: " + messageId);
        }
        return null;
    }

    /**
     * Reset charger (Soft and Hard)
     *
     * @param chargerId charger id
     * @param type hard and soft
     * @return charger response
     */
    public CompletableFuture<String> sendReset(String chargerId, String type) {
        String messageId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        WebSocketSession session = findSessionByChargerId(chargerId);
        ResetTypeEnumCode resetType = ResetTypeEnumCode.from(type);
        try {
            if (resetType != null) {
                Object[] ocppMessage = new ResetReq(resetType.name()).getRequest(messageId);
                String jsonMessage = objectMapper.writeValueAsString(ocppMessage);
                pendingRequests.put(messageId, future);
                if (session != null) {
                    session.sendMessage(new TextMessage(jsonMessage));
                    log.info("📤 Sending Reset to {}: {}", chargerId, jsonMessage);
                } else {
                    log.error("❌ ChargerId: {} is not active or message send failed", chargerId);
                    future.complete("❌ ChargerId:" + chargerId + "is not active or message send failed");
                }
                return future;
            } else {
                log.warn("⚠️ Reset type is wrong");
            }
        } catch (IOException ioException) {
            log.error("❌ Error sending message to chargerId: {}, messageId: {}", chargerId, messageId, ioException);
            future.complete("❌ Error sending message to chargerId: " + chargerId + "messageId: " + messageId);
        }
        return null;
    }
}
