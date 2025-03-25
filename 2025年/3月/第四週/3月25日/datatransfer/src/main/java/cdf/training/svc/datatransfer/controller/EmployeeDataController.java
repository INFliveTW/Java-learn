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
import cdf.training.svc.datatransfer.dto.ErrorResponseDto;
import cdf.training.svc.datatransfer.service.impl.CSVToDataBaseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
public class EmployeeDataController {
    private final CSVToDataBaseServiceImpl csvToDataBaseService;

    public EmployeeDataController(CSVToDataBaseServiceImpl csvToDataBaseService) {
        this.csvToDataBaseService = csvToDataBaseService;
    }

    @Operation(summary = "處理員工資料", description = "從 SFTP 讀取 CSV 檔案並寫入資料庫")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "資料處理成功",
                    content = @Content(schema = @Schema(implementation = CSVToDataBaseResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "伺服器錯誤",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/employee-data")
    public ResponseEntity<?> processEmployeeData(@RequestBody CSVToDataBaseRequestDto request) {
        Instant startTime = Instant.now();
        System.out.println("呼叫 API 前時間: " + startTime);

        try {
            csvToDataBaseService.processCsvToDatabase(request);
            Instant endTime = Instant.now();
            System.out.println("呼叫 API 後時間: " + endTime);
            Duration duration = Duration.between(startTime, endTime);
            double seconds = duration.toMillis() / 1000.0;
            System.out.println("API 呼叫耗時: " + seconds + " 秒");

            return ResponseEntity.ok(new CSVToDataBaseResponseDto("資料處理成功"));
        } catch (RuntimeException e) {
            ErrorResponseDto errorResponse;
            try {
                errorResponse = new ErrorResponseDto(
                        e.getMessage().split(",")[0].split("=")[1],
                        e.getMessage().split(",")[1].split("=")[1].replace("}", "")
                );
            } catch (Exception parseEx) {
                errorResponse = new ErrorResponseDto("UNKNOWN_ERROR", "發生未知錯誤：" + e.getMessage());
            }
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
// @RestController //標記為 REST 控制器
// @RequestMapping("/api")
// public class EmployeeDataController {
//     private final CSVToDataBaseServiceImpl csvToDataBaseService;

//     public EmployeeDataController(CSVToDataBaseServiceImpl csvToDataBaseService) {
//         this.csvToDataBaseService = csvToDataBaseService;
//     }
    
//     @PostMapping("/employee-data") //處理 POST請求
//     public ResponseEntity<CSVToDataBaseResponseDto> processEmployeeData(@RequestBody CSVToDataBaseRequestDto request) {
//         // 記錄 API 呼叫前的時間
//         Instant startTime = Instant.now();
//         System.out.println("呼叫 API 前時間: " + startTime);

//         csvToDataBaseService.processCsvToDatabase(request); //調用服務處理

//         // 記錄 API 呼叫後的時間
//         Instant endTime = Instant.now();
//         System.out.println("呼叫 API 後時間: " + endTime);

//         // 計算時間差 (以秒為單位)
//         Duration duration = Duration.between(startTime, endTime);
//         double seconds = duration.toMillis() / 1000.0;
//         System.out.println("API 呼叫耗時: " + seconds + " 秒");
        
//         return ResponseEntity.ok(new CSVToDataBaseResponseDto("資料處理成功"));
//     }
// }
//步驟5：接收請求