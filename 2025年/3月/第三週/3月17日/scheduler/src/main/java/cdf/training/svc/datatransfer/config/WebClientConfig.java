package cdf.training.svc.datatransfer.config;

import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import ch.qos.logback.core.util.Duration;
@Configuration
public class WebClientConfig {
    @Value("${spring.webclient.timeout}")
    private Duration timeout;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(timeout)))
                .build();
    }
}

// package cdf.training.svc.datatransfer.config;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.reactive.function.client.WebClient;

// @Configuration
// public class WebClientConfig {
    
//     @Value("${spring.scheduler.api.timeout}")
//     private String timeout;

//     @Bean
//     public WebClient webClient() {
//         return WebClient.builder()
//             .defaultHeader("Content-Type", "application/json")
//             .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
//             .build();
//     }
// }