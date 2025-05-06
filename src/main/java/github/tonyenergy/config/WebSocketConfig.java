package github.tonyenergy.config;

import github.tonyenergy.websocket.WebSocketServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketServer webSocketServer;

    public WebSocketConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Map path /ws/{chargerId}, support charger visit
        registry.addHandler(webSocketServer, "/ocpp/ws/{chargerId}")
                .setAllowedOrigins("*"); // allow CORS
    }
}
