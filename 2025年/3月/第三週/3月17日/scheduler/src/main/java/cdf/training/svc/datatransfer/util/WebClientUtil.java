package cdf.training.svc.datatransfer.util;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientUtil {
    
    @Autowired
    private WebClient webClient;
    
    @Value("${spring.scheduler.api.timeout}")
    private String timeout;
    
    @Value("${spring.scheduler.api.timeout-message}")
    private String timeoutMessage;
    
    public ResponseEntity<String> postRequest(String url, Object requestBody) {
        try {
            String response = webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(Long.parseLong(timeout.replace("s", ""))))
                .block();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(timeoutMessage);
        }
    }
}