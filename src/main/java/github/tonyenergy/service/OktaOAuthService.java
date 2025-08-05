package github.tonyenergy.service;

import java.util.HashMap;

/**
 * Okta OAuth Service
 *
 * @Author: Tony
 * @Date: 2025/8/6
 */
public interface OktaOAuthService {

    /**
     * Jump to the Okta authorize page, let user input username and password
     *
     * @return https://integrator-1842405.okta.com/oauth2/default/v1/authorize
     * ?client_id=0oatwakgonrJt1Jx2697
     * &response_type=code&scope=openid%20profile%20email%20offline_access
     * &redirect_uri=http://localhost/oauth/okta/callback&state=random_state
     */
    String getAuthorizationUrl();


    /**
     * Provide code to Okta server, then get access token
     *
     * @param code code generate from Okta server
     * @return necessary info
     */
    HashMap<String, String> exchangeCodeForToken(String code);

    /**
     * Get User Info
     *
     * @param accessToken accessToken from Okta server
     * @return user info
     */
    String getUserInfo(String accessToken);
}


