package cdf.training.bch.scheduler.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration //配置類，定義Bean
//SchedulerApplication掃描相關
public class WebClientConfig {
    private final APIProperties apiProperties;
    //注入apiProperties
    //從application.yml讀取timeout
    
    public WebClientConfig(APIProperties apiProperties) {
        this.apiProperties = apiProperties;
    }
    //@Bean：定義 WebClient 的 Bean

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(apiProperties.getTimeout())); // 從 APIProperties 獲取 timeout
        //獲取timeout: 3秒
        //影響DatatransferService的API呼叫

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        //用來構建 WebClient
    }
}
