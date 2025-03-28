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
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service //標記服務類型
@EnableScheduling //啟用定時任務(5秒)
//@Value //步驟1的初始化
public class DatatransferService {
    WebClient webClient;
    APIProperties apiProperties;


    public DatatransferService(WebClient webClient, APIProperties apiProperties) {
        this.webClient = webClient;
        this.apiProperties = apiProperties;
    }
    
    @Scheduled(fixedRateString = "${spring.scheduler.interval}000") //每五秒執行(interval在application.yml)
    public void callDatatransferApi() {
        String randomCompany = List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
        //隨機選擇任一公司
        Map<String, String> requestBody = Map.of(
                "COMPANY", randomCompany,
                "EXCUTE_TIME", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))
        );
        log.info("🔍 API URL: {}", apiProperties.getUrl());
        
        webClient.post()
                .uri(apiProperties.getUrl()) //目標URL
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class) //獲取回應
                .onErrorResume(throwable -> { //錯誤處理
                    log.error("🚨 API 呼叫失敗：" + throwable.getMessage());
                    log.info("🔍 API URL: {}", apiProperties.getUrl());

                    // return Mono.just(apiProperties.getTimeoutMessage());
                
                    if(throwable instanceof java.util.concurrent.TimeoutException) {
                        return  Mono.just(apiProperties.getTimeoutMessage());
                    }
                    return Mono.just(apiProperties.getTimeoutMessage());
                })
                .subscribe(result -> System.out.println("API 回應："+ result));
    }
}
