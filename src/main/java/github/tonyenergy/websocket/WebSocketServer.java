package github.tonyenergy.websocket;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import github.tonyenergy.entity.common.*;
import github.tonyenergy.entity.common.enums.MessageTypeEnum;
import github.tonyenergy.entity.common.enums.OCPPActionEnum;
import github.tonyenergy.entity.common.enums.ResetTypeEnum;
import github.tonyenergy.entity.conf.BootNotificationConf;
import github.tonyenergy.entity.conf.HeartbeatConf;
import github.tonyenergy.entity.req.ChangeConfigurationReq;
import github.tonyenergy.entity.req.GetConfigurationReq;
import github.tonyenergy.entity.req.ResetReq;
import github.tonyenergy.service.ChargerLogService;
import github.tonyenergy.service.ChargerService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class WebSocketServer extends TextWebSocketHandler {

    private static final Set<WebSocketSession> WEB_SOCKET_SESSIONS = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper;

    /**
     * Store messageId and CompletableFuture Map, use to asynchronous response
     */
    private final ConcurrentHashMap<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    @Autowired
    public ChargerService chargerService;

    @Autowired
    public ChargerLogService chargerLogService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String chargerId = (String) session.getAttributes().get("chargerId");
        log.info("🔗 WebSocket Connected: sessionId={}, chargerId={}", session.getId(), chargerId);
        WEB_SOCKET_SESSIONS.add(session);
        // log connect success data
        chargerService.connect(chargerId);
    }


    /**
     * Handle call message from charger
     *
     * @param session session
     * @param message message from charger
     * @throws Exception error
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // get chargerId
        String chargerId = (String) session.getAttributes().get("chargerId");
        String ocppMessage = message.getPayload();
        // Read payload as a json node
        JsonNode jsonNode = objectMapper.readTree(ocppMessage);
        CompletableFuture<String> future;
        // Check message is OCPP CALL message or OCPP CALL_RESULT message or invalid message
        if (jsonNode.isArray() && jsonNode.size() == 4) {
            OCPPCall ocppCall = new OCPPCall();
            // get and set message type id
            ocppCall.setMessageType(jsonNode.get(0).asInt());
            // get and set message id
            ocppCall.setMessageId(jsonNode.get(1).asText());
            // get and set action
            ocppCall.setAction(jsonNode.get(2).asText());
            // get and set payload
            ocppCall.setPayload(objectMapper.convertValue(jsonNode.get(3), Object.class));
            // get message type
            MessageTypeEnum messageType = MessageTypeEnum.fromNumber(ocppCall.getMessageType());
            // If message type is CALL, means charger proactively call server
            if (messageType == MessageTypeEnum.CALL) {
                future = new CompletableFuture<>();
                log.info("📩 Received CALL message from charger {}", ocppCall.getCallJson());
                // Check OCPP command, then create a response
                OCPPCallResult ocppCallResult = handleOcppCall(chargerId, ocppCall, future);
                // send response to charger
                log.info("📤 Response Charger: {}", ocppCallResult.getCallResultJson());
                session.sendMessage(new TextMessage(ocppCallResult.getCallResultJson()));
                future.complete(ocppMessage);
            } else {
                log.error("❌ Ocpp call is not standard! Json Node length is 4, message type is not CALL!");
            }
        } else if (jsonNode.isArray() && jsonNode.size() == 3) {
            OCPPCallResult ocppCallResult = new OCPPCallResult();
            // get and set message type id
            ocppCallResult.setMessageType(jsonNode.get(0).asInt());
            // get and set message id
            ocppCallResult.setMessageId(jsonNode.get(1).asText());
            // get and set payload
            ocppCallResult.setPayload(objectMapper.convertValue(jsonNode.get(2), Object.class));
            MessageTypeEnum messageType = MessageTypeEnum.fromNumber(ocppCallResult.getMessageType());
            if (messageType == MessageTypeEnum.CALL_RESULT) {
                future = pendingRequests.remove(ocppCallResult.getMessageId());
                if (future != null) {
                    log.info("📩 Received message of type {}", ocppMessage);
                    future.complete(ocppMessage);
                } else {
                    log.warn("⚠️ No pending request for messageId: {}", ocppCallResult.getMessageId());
                }
            } else {
                log.warn("⚠️ Unknown message type ID: {}", messageType);
            }
        } else {
            // TODO: return CALL ERROR
            log.error("❌ Ocpp call is not standard! Return CALL ERROR!");
            String messageId = jsonNode.get(1).asText();
            future = pendingRequests.remove(messageId);
            if (future != null) {
                log.warn("⚠️ Received CallError: {}", ocppMessage);
                future.completeExceptionally(new RuntimeException("⚠️ Received CallError: " + ocppMessage));
            } else {
                log.warn("⚠️ No pending request for messageId: {}", messageId);
            }
        }
    }

    /**
     * handle ocpp call
     * @param chargerId chargerId
     * @param ocppCall ocpp call, call from charger
     * @param future    completable future
     * @return ocpp call result
     */
    public OCPPCallResult handleOcppCall(String chargerId, OCPPCall ocppCall, CompletableFuture<String> future) {
        OCPPActionEnum action = OCPPActionEnum.from(ocppCall.getAction());
        // if command is from OCPP protocol, handle command
        OCPPCallResult ocppCallResult = new OCPPCallResult();
        if (action != null) {
            pendingRequests.put(ocppCall.getMessageId(), future);
            if (action == OCPPActionEnum.BootNotification){
                // TODO: need to finish boot notification logic, return default value as temp
                BootNotificationConf bootNotificationConf = new BootNotificationConf();
                bootNotificationConf.setInterval(3600);
                bootNotificationConf.setTimestamp(new Date());
                bootNotificationConf.setStatus("Accepted");
                // build ocpp call result, then response charger
                ocppCallResult.setMessageType(MessageTypeEnum.CALL_RESULT.getCode());
                ocppCallResult.setMessageId(ocppCall.getMessageId());
                ocppCallResult.setPayload(bootNotificationConf);
            } else if (action == OCPPActionEnum.StatusNotification){
                // TODO: need to finish status notification logic, if Status notification is standard, return "{}"
                // build ocpp call result, then response charger
                ocppCallResult.setMessageType(MessageTypeEnum.CALL_RESULT.getCode());
                ocppCallResult.setMessageId(ocppCall.getMessageId());
                ocppCallResult.setPayload("{}");
            } else if (action == OCPPActionEnum.Heartbeat){
                // TODO: need to finish status notification logic, if Status notification is standard, return now
                HeartbeatConf heartbeatConf = new HeartbeatConf(new Date());
                // build ocpp call result, then response charger
                ocppCallResult.setMessageType(MessageTypeEnum.CALL_RESULT.getCode());
                ocppCallResult.setMessageId(ocppCall.getMessageId());
                ocppCallResult.setPayload(heartbeatConf);
            } else {
                ocppCallResult.setPayload("{other action}");
                log.info("{other action}");
            }
            log.info("Saving charger Log...");
            chargerLogService.saveChargerLog(chargerId, ocppCall, ocppCallResult);
            return ocppCallResult;
        } else {
            log.warn("⚠️ Action is null!");
            return null;
        }
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
        return future;
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
        ResetTypeEnum resetType = ResetTypeEnum.from(type);
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
        return future;
    }
}
