package github.tonyenergy.config;

import github.tonyenergy.service.ChargerService;
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

    private final ChargerService chargerService;

    public WebConfig(ChargerService chargerService, WebSocketServer webSocketServer) {
        this.chargerService = chargerService;
        this.webSocketServer = webSocketServer;
    }

    /**
     * Web socket CORS config
     *
     * @param registry registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String protocol = "prod".equals(activeProfile) ? "wss" : "ws";
        registry.addHandler(webSocketServer, "/ocpp/ws/{chargerId}")
                .addInterceptors(new ChargerHandshakeInterceptor(chargerService))
                .setAllowedOrigins("*");
        log.info("🚀 {} WebSocket handler registered at: {}://{}:{}/ocpp/ws/{{chargerId}}",
                "prod".equals(activeProfile) ? "Production" : "Development", protocol, externalHostname, port);
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
