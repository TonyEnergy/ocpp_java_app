package github.tonyenergy.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * http util
 *
 * @Author: Tony
 * @Date: 2025/4/11
 */
public class HttpUtil {
    public static void post(String url, String json) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Request sent successfully");
        } else {
            System.err.println("Failed to send request");
        }
    }
}

