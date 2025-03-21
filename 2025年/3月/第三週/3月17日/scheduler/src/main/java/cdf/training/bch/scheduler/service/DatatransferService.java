package cdf.training.bch.scheduler.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import cdf.training.bch.scheduler.config.APIProperties;
import lombok.Value;
import reactor.core.publisher.Mono;

@Service //標記服務類型
@EnableScheduling //啟用定時任務(5秒)
@Value //步驟1的初始化
public class DatatransferService {
    WebClient webClient;
    APIProperties apiProperties;

    @Scheduled(fixedRateString = "${spring.scheduler.interval}000") //每五秒執行(interval在application.yml)
    public void callDatatransferApi() {
        String randomCompany = List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
        //隨機選擇任一公司
        Map<String, String> requestBody = Map.of(
                "COMPANY", randomCompany,
                "EXCUTE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
        );

        webClient.post()
                .uri(apiProperties.getApiUrl()) //目標URL
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class) //獲取回應
                .onErrorResume(throwable -> { //超時返回timeoutMessage
                    if(throwable instanceof java.util.concurrent.TimeoutException) {
                        return  Mono.just(apiProperties.getTimeoutMessage());
                    }
                    return Mono.just(apiProperties.getTimeoutMessage());
                })
                .subscribe(result -> System.out.println("超時："+ result));
    }
}
