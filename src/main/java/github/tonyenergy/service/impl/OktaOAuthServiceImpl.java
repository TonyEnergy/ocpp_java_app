package github.tonyenergy.service.impl;

import github.tonyenergy.service.OktaOAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

/**
 * Okta OAuth service impl
 *
 * @Author: Tony
 * @Date: 2025/8/6
 */
@Service
public class OktaOAuthServiceImpl implements OktaOAuthService {


    @Value("${okta.client-id}")
    private String clientId;
    @Value("${okta.client-secret}")
    private String clientSecret;
    @Value("${okta.redirect-uri}")
    private String redirectUri;
    @Value("${okta.domain}")
    private String domain;
    @Value("${okta.authServer}")
    private String authServer;


    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Jump to the Okta authorize page, let user input username and password
     *
     * @return https://integrator-xxxxx.okta.com/oauth2/default/v1/authorize
     * ?client_id=012at4231Jx2697
     * &response_type=code&scope=openid%20profile%20email%20offline_access
     * &redirect_uri=http://localhost/oauth/okta/callback&state=random_state
     */
    @Override
    public String getAuthorizationUrl() {
        String authorizationUrl = "https://" + domain + "/oauth2/" + authServer + "/v1/authorize";
        System.out.println(domain);
        System.out.println(authorizationUrl);
        return UriComponentsBuilder.fromHttpUrl(authorizationUrl)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email offline_access")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", "random_state")
                .build().toUriString();
    }

    /**
     * Use Okta code to get Okta token
     *
     * @param code code from Okta
     * @return Token
     */
    @Override
    public HashMap<String, String> exchangeCodeForToken(String code) {
        HashMap<String, String> resultMap = new HashMap<>();
        String tokenUrl = "https://" + domain + "/oauth2/" + authServer + "/v1/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        String body = UriComponentsBuilder.newInstance()
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri)
                .build().getQuery();

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            resultMap.put("access_token", node.get("access_token").asText());
            resultMap.put("token_type", node.get("token_type").asText());
            resultMap.put("expires_in", node.get("expires_in").asText());
            resultMap.put("refresh_token", node.get("refresh_token").asText());
            resultMap.put("scope", node.get("scope").asText());
            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException("Token parse fail: " + e.getMessage());
        }
    }

    /**
     * Get user info
     *
     * @param accessToken accessToken from Okta server
     * @return user info
     */
    @Override
    public String getUserInfo(String accessToken) {
        String userInfoUrl = "https://" + domain + "/oauth2/" + authServer + "/v1/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
