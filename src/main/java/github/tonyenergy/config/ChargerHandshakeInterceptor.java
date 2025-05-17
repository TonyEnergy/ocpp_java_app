package github.tonyenergy.config;

import github.tonyenergy.service.ChargerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Charger Handshake Interceptor
 *
 * @Author: Tony
 * @Date: 2025/5/14
 */

@Slf4j
@RequiredArgsConstructor
public class ChargerHandshakeInterceptor implements HandshakeInterceptor {

    private final ChargerService chargerService;

    /**
     * before handshake, need to check local charger card list, if new connect charger not in the charger card list, refuse connect
     *
     * @param request    request
     * @param response   response
     * @param wsHandler  websocket handler
     * @param attributes attributes
     * @return if charger id exist in the local file, return true, else return false
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String path = request.getURI().getPath();
        String chargerId = path.substring(path.lastIndexOf('/') + 1);
        log.info("🤝 Trying handshake for chargerId: {}", chargerId);
        List<String> chargerIdList = chargerService.listLocalChargerId();
        if (chargerIdList.contains(chargerId)) {
            log.info("✅ Charger exists. Allow WebSocket handshake for {}", chargerId);
            attributes.put("chargerId", chargerId);
            return true;
        } else {
            log.warn("❌ Rejecting WebSocket handshake. Charger not found: {}", chargerId);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {

    }
}
