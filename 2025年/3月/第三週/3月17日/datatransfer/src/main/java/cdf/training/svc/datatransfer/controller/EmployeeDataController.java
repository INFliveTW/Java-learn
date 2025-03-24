package cdf.training.svc.datatransfer.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.CSVToDataBaseResponseDto;
import cdf.training.svc.datatransfer.service.impl.CSVToDataBaseServiceImpl;

@RestController //標記為 REST 控制器
@RequestMapping("/api")
public class EmployeeDataController {
    private final CSVToDataBaseServiceImpl csvToDataBaseService;

    public EmployeeDataController(CSVToDataBaseServiceImpl csvToDataBaseService) {
        this.csvToDataBaseService = csvToDataBaseService;
    }

    @PostMapping("/employee-data") //處理 POST請求
    public ResponseEntity<CSVToDataBaseResponseDto> processEmployeeData(@RequestBody CSVToDataBaseRequestDto request) {
        // 記錄 API 呼叫前的時間
        Instant startTime = Instant.now();
        System.out.println("呼叫 API 前時間: " + startTime);

        csvToDataBaseService.processCsvToDatabase(request); //調用服務處理

        // 記錄 API 呼叫後的時間
        Instant endTime = Instant.now();
        System.out.println("呼叫 API 後時間: " + endTime);

        // 計算時間差 (以秒為單位)
        Duration duration = Duration.between(startTime, endTime);
        double seconds = duration.toMillis() / 1000.0;
        System.out.println("API 呼叫耗時: " + seconds + " 秒");
        
        return ResponseEntity.ok(new CSVToDataBaseResponseDto("資料處理成功"));
    }
}
//步驟5：接收請求