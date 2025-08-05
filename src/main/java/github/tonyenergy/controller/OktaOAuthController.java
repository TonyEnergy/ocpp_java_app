package github.tonyenergy.controller;

import github.tonyenergy.service.OktaOAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Okta OAuth Controller
 *
 * @Author: Tony
 * @Date: 2025/8/6
 */

@RestController
@RequestMapping("/oauth/okta")
public class OktaOAuthController {

    @Autowired
    private OktaOAuthService oktaOAuthService;

    /**
     * Cobbled authorization url and redirect to the authorization url, guide user input username and password
     */
    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        String url = oktaOAuthService.getAuthorizationUrl();
        response.sendRedirect(url);
    }

    /**
     * After login finished, Okta server will bring a code call back
     *
     * @param code code
     * @return accessToken
     */
    @GetMapping("/callback")
    public HashMap<String, String> callback(@RequestParam String code) {
        return oktaOAuthService.exchangeCodeForToken(code);
    }

    /**
     * Get user info
     *
     * @param accessToken access token from Okta server
     * @return user info
     */
    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getUserInfo(String accessToken) {
        String userInfo = oktaOAuthService.getUserInfo(accessToken);
        return ResponseEntity.ok(userInfo);
    }
}
