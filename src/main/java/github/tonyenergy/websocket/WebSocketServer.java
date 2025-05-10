package github.tonyenergy.websocket;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.common.MessageTypeEnumCode;
import github.tonyenergy.entity.common.OCPPCommandEnumCode;
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
            ObjectMapper mapper = new ObjectMapper();
            Object[] arr = mapper.readValue(payload, Object[].class);
            String action = arr[2].toString();
            OCPPCommandEnumCode command = OCPPCommandEnumCode.from(action);
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

    public void checkSessions() {
        if (WEB_SOCKET_SESSIONS.isEmpty()) {
            log.info("There have no sessions");
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
        try {
            WebSocketSession session = findSessionByChargerId(chargerId);
            String messageId = UUID.randomUUID().toString();
            Map<String, Object> payload = new HashMap<>();
            if (keys != null && keys.length > 0) {
                payload.put("key", keys);
            }
            Object[] ocppMessage = new Object[]{2, messageId, "GetConfiguration", payload};
            String jsonMessage = objectMapper.writeValueAsString(ocppMessage);
            pendingRequests.put(messageId, future);
            session.sendMessage(new TextMessage(jsonMessage));
            log.info("📤 Sending GetConfiguration to {}: {}", chargerId, jsonMessage);
        } catch (Exception e) {
            log.error("❌ ChargerId: {} is not active or message send failed", chargerId);
            future.complete("❌ ChargerId is not active or failed to send message: " + chargerId);
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
