package github.tonyenergy.websocket;

/**
 * @Author: liutaohua
 * @Date: 2025/3/20
 */

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WebSocketServer extends TextWebSocketHandler {

    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("🔗 WebSocket Connect Established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("📩 收到消息：" + message.getPayload());

        // 发送消息回客户端
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage("Receive: " + message.getPayload()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("❌ WebSocket 连接关闭：" + session.getId());
    }
}

