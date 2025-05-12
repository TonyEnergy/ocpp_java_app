package github.tonyenergy.config;

import github.tonyenergy.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * Web socket config
 *
 * @Author: Tony
 * @Date: 2025/5/5
 */
@Configuration
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketServer webSocketServer;

    @Value("${server.port}")
    private int port;

    @Value("${render.external-hostname}")
    private String externalHostname;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public WebSocketConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String protocol = activeProfile.equals("prod") ? "wss" : "ws"; // 根据环境选择协议
        registry.addHandler(webSocketServer, "/ocpp/ws/{chargerId}")
                .setAllowedOrigins("*"); // allow CORS
        log.info("🚀 {} WebSocket handler registered at: {}://{}:{}/ocpp/ws/{{chargerId}}",
                activeProfile.equals("prod") ? "Production" : "Development", protocol, externalHostname, port);
    }
}
