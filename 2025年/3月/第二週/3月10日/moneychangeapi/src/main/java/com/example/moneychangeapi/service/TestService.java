package com.example.moneychangeapi.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class  TestService {

  /**
   * 使用WebClient發送post請求，並返回BaseResponse
   *
   * @param url     URL
   * @param request 請求物件
   * @return Mono<BaseResponse>
   */
//   @Override 繼承
//https://api.currencyfreaks.com/v2.0/convert/latest?apikey=4870617b331c498c8ad43b78ce86c1ab&from=USD&to=PKR&amount=500
//https://api.currencyfreaks.com/v2.0/rates/latest?apikey=4870617b331c498c8ad43b78ce86c1abv&from=jpy&to=twd&amount=1000.0
  public Mono<String> getBaseResponse(String url) {
    return WebClient.create().get()
        .uri(url)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .retrieve()
        .bodyToMono(String.class);
  }
}