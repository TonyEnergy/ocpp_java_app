package github.tonyenergy.websocket;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.BootNotificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
public class WebSocketServer extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Store messageId and CompletableFuture Map, use to asynchronous response
    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = Objects.requireNonNull(session.getUri()).getPath(); // /ocpp/ws/XGJ20241014
        String chargerId = path.substring(path.lastIndexOf('/') + 1);
        log.info("🔗 WebSocket Connected: sessionId={}, chargerId={}", session.getId(), chargerId);
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
//        log.info("📩 Received message from {}: {}", session.getId(), payload);
        JsonNode jsonNode = objectMapper.readTree(payload);
        if (jsonNode.isArray() && jsonNode.size() >= 3) {
            int messageTypeId = jsonNode.get(0).asInt();
            String messageId = jsonNode.get(1).asText();
            // CallResult
            if (messageTypeId == 2) {
                CompletableFuture<String> future = new CompletableFuture<>();
                log.info("ℹ️ Received message of type {}: {}", messageTypeId, payload);
                BootNotificationResponse response = new BootNotificationResponse("Accepted", new Date(), 3600);
                log.info("📤 Response Charger :{}", response.getAll(messageId));
                pendingRequests.put(messageId, future);
                session.sendMessage(new TextMessage(response.getAll(messageId)));
                future.complete(payload);
            } else if (messageTypeId == 3) {
                CompletableFuture<String> future = pendingRequests.remove(messageId);
                if (future != null) {
                    log.info("ℹ️ Received message of type {}: {}", messageTypeId, payload);
                    pendingRequests.put(messageId, future);
                    future.complete(payload);
                } else {
                    log.warn("⚠️ No pending request for messageId: {}", messageId);
                }
            } else if (messageTypeId == 4) { // CallError
                CompletableFuture<String> future = pendingRequests.remove(messageId);
                if (future != null) {
                    log.warn("Received CallError: {}", payload);
                    future.completeExceptionally(new RuntimeException("Received CallError: " + payload));
                } else {
                    log.warn("⚠️ No pending request for messageId: {}", messageId);
                }
            } else {
                log.info("ℹ️ Received message of type {}: {}", messageTypeId, payload);
            }
        } else {
            log.warn("⚠️ Invalid OCPP message format: {}", payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("❌ WebSocket connection closed: {}", session.getId());
    }

    // Finding Websocket session through chargerId
    public WebSocketSession findSessionByChargerId(String chargerId) {
        for (WebSocketSession session : sessions) {
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

    // Sending GetConfiguration request，return CompletableFuture to asynchronous get response
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

    // Sending ChangeConfiguration request，return CompletableFuture to asynchronous get response
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
