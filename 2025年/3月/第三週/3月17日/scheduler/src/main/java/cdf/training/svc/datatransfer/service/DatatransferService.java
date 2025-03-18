// package cdf.training.svc.datatransfer.service;

// public class DatatransferService {
    
// }
package cdf.training.svc.datatransfer.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Value;

@Service
@EnableScheduling
public class DatatransferService {
    private final WebClient webClient;
    private final String apiUrl;

    public DatatransferService(WebClient webClient, @Value("${spring.webclient.api-url}") String apiUrl) {
        this.webClient = webClient;
        this.apiUrl = apiUrl;
    }

    @Scheduled(cron = "${spring.scheduler.cron}")
    public void callDatatransferApi() {
        String randomCompany = List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
        Map<String, String> requestBody = Map.of(
                "company", randomCompany,
                "excute_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        webClient.post()
                .uri(apiUrl)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(System.out::println);
    }
}