package com.example.moneychangeapi.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.moneychangeapi.service.TestService;

import reactor.core.publisher.Mono;

@RestController
public class TestController {

    private final TestService service;

    public TestController(TestService service) {
        this.service = service;
    }

            // {
//     @GetMapping("/exchange-rate")
//     public Mono<String> getExchangeRate( ) {//來源貨幣FROMCURRENCYCODE、目標貨幣TOCURRENCYCODE、轉換金額(來源>目標)AMOUNT
                
//     }
// }
    @GetMapping("/testapi")
    public Mono<String> cedf (@RequestParam (name = "FROM") String from) {
        
        return service.getBaseResponse("https://api.currencyfreaks.com/v2.0/convert/latest?apikey=4870617b331c498c8ad43b78ce86c1ab&from=USD&to=PKR&amount=500");
        //return service.getBaseResponse("https://api.currencyfreaks.com/v2.0/currency-symbols");
    }
//https://api.currencyfreaks.com/v2.0/convert/latest
}
//https://api.currencyfreaks.com/v2.0/convert/latest?apikey=4870617b331c498c8ad43b78ce86c1ab&from=USD&to=PKR&amount=500
//http://localhost:8080/exchange-rate?FROM=jpy&TO=twd&AMOUNT=1000
//@Controller 和 @ResponseBody，表示類處理 REST 請求並返回資料
