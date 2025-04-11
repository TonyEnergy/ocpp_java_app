package github.tonyenergy.websocket;

/**
 * WebSocketServer
 *
 * @Author: Tony
 * @Date: 2025/3/20
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
public class WebSocketServer extends TextWebSocketHandler {

    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("🔗 WebSocket Connect Established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("📩 Received message：" + message.getPayload());

        // Send message back to client
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage("Receive: " + message.getPayload()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("❌ WebSocket connection closed：" + session.getId());
    }
}

