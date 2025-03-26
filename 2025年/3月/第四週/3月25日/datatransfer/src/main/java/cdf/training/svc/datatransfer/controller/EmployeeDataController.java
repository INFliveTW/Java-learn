package cdf.training.svc.datatransfer.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
public class EmployeeDataController {
    private final CSVToDataBaseServiceImpl csvToDataBaseService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public EmployeeDataController(CSVToDataBaseServiceImpl csvToDataBaseService) {
        this.csvToDataBaseService = csvToDataBaseService;
    }

    @Operation(summary = "處理員工資料", description = "從 SFTP 讀取 CSV 檔案並寫入SQL，無須輸入任何參數(因為COMPANY為隨機寫入)，直接Execute即可")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "0", description = "【範例(200)】資料處理成功\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"message\": \"資料處理成功\", \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "1", description = "【範例(500)】SFTP 伺服器拒絕訪問，請檢查權限\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"SFTP_PERMISSION_DENIED\", \"message\": \"SFTP 伺服器拒絕訪問，請檢查權限\", \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "2", description = "【範例(500)】SFTP 資料夾沒有CSV檔案，請確認SFTP\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"SFTP_FILE_NOT_FOUND\", \"message\": \"SFTP 資料夾沒有CSV檔案，請確認SFTP\", \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "3", description = "【範例(500)】CSV 檔案解析失敗，請確認檔案格式正確\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"CSV_PARSE_ERROR\", \"message\": \"CSV 檔案解析失敗，請確認檔案格式正確\", \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "4", description = "【範例(500)】CSV 檔案內容沒有任何資料，請確認文件內容\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"CSV_EMPTY_ERROR\", \"message\": \"CSV 檔案內容沒有任何資料，請確認文件內容\", \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "5", description = "【範例(500)】發生未知錯誤\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"UNKNOWN_ERROR\", \"message\": \"發生未知錯誤\", \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "6", description = "【範例(500)】無法連接到 SFTP 伺服器，請檢查配置或網路狀態\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"SFTP_CONNECTION_ERROR\", \"message\": \"無法連接到 SFTP 伺服器，請檢查配置或網路狀態\", \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "7", description = "【範例(500)】資料庫寫入失敗，請檢查資料庫連線或權限\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"DATABASE_ERROR\", \"message\": \"資料庫寫入失敗，請檢查資料庫連線或權限\", \"triggerTime\": \"2025/03/25 14:30:00\"}")))
    })
    
    @PostMapping("/employee-data")
    public ResponseEntity<?> processEmployeeData(@RequestBody CSVToDataBaseRequestDto request) {
        Instant startTime = Instant.now();
        String formattedStartTime = formatter.format(startTime.atZone(java.time.ZoneId.systemDefault()));
        System.out.println("呼叫 API 前時間: " + formattedStartTime);

        try {
            csvToDataBaseService.processCsvToDatabase(request);
            Instant endTime = Instant.now();
            String formattedEndTime = formatter.format(endTime.atZone(java.time.ZoneId.systemDefault()));
            System.out.println("呼叫 API 後時間: " + formattedEndTime);
            Duration duration = Duration.between(startTime, endTime);
            double seconds = duration.toMillis() / 1000.0;
            System.out.println("API 呼叫耗時: " + seconds + " 秒");

            CSVToDataBaseResponseDto response = new CSVToDataBaseResponseDto("資料處理成功");
            response.setTriggerTime(formattedStartTime); // 設置觸發時間
            return ResponseEntity.ok(response);
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
            errorResponse.setTriggerTime(formattedStartTime); // 設置觸發時間
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}