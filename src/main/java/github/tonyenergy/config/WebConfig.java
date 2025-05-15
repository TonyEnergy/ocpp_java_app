package github.tonyenergy.config;

import github.tonyenergy.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
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
public class WebConfig implements WebSocketConfigurer, WebMvcConfigurer {

    private final WebSocketServer webSocketServer;

    @Value("${server.port}")
    private int port;

    @Value("${render.external-hostname}")
    private String externalHostname;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public WebConfig(WebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    /**
     * Web socket CORS config
     *
     * @param registry registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String protocol = activeProfile.equals("prod") ? "wss" : "ws";
        registry.addHandler(webSocketServer, "/ocpp/ws/{chargerId}")
                .setAllowedOrigins("*");
        log.info("🚀 {} WebSocket handler registered at: {}://{}:{}/ocpp/ws/{{chargerId}}",
                activeProfile.equals("prod") ? "Production" : "Development", protocol, externalHostname, port);
    }

    /**
     * HTTP CORS config
     *
     * @param registry registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
