package cdf.training.svc.datatransfer.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cdf.training.svc.datatransfer.dto.BaseResponse;
import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
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

    @Operation(summary = "處理員工資料", description = "從 SFTP 讀取 CSV 檔案並寫入SQL，返回處理結果")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功執行：\n" +
                    "\n**透過選項查看範例**", // 新增自訂文字
                    content = @Content(examples = {
                            @ExampleObject(name = "資料處理成功", value = "{\"code\": \"200\", \"message\": \"資料處理成功\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "SFTP 伺服器拒絕訪問", value = "{\"code\": \"200\", \"message\": \"SFTP 伺服器拒絕訪問，請檢查權限\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "SFTP 資料夾沒有CSV檔案", value = "{\"code\": \"200\", \"message\": \"SFTP 資料夾沒有CSV檔案，請確認SFTP\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "CSV 檔案解析失敗", value = "{\"code\": \"200\", \"message\": \"CSV 檔案解析失敗，請確認檔案格式正確\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "CSV 檔案內容沒有任何資料", value = "{\"code\": \"200\", \"message\": \"CSV 檔案內容沒有任何資料，請確認文件內容\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "CSV檔案，欄位缺少", value = "{\"code\": \"200\", \"message\": \"CSV檔案，欄位缺少，請確認檔案\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "CSV檔案，資料缺少", value = "{\"code\": \"200\", \"message\": \"CSV檔案，資料缺少，請確認檔案\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "資料庫寫入失敗", value = "{\"code\": \"200\", \"message\": \"資料庫寫入失敗，請檢查資料庫連線或權限\", \"triggerTime\": \"2025/03/25 14:30:00\"}")
                        })
                        ),//資料庫寫入失敗，請檢查資料庫連線或權限
            @ApiResponse(responseCode = "500", description = "錯誤：\n" +
                    "\n**透過選項查看範例**", // 新增自訂文字
                    content = @Content(examples = {
                            @ExampleObject(name = "發生未知錯誤", value = "{\"code\": \"500\", \"message\": \"發生未知錯誤\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "無法連接到 SFTP 伺服器", value = "{\"code\": \"500\", \"message\": \"無法連接到 SFTP 伺服器，請檢查配置或網路狀態\", \"triggerTime\": \"2025/03/25 14:30:00\"}"),
                            @ExampleObject(name = "無法連線到資料庫", value = "{\"code\": \"500\", \"message\": \"無法連線到資料庫，請檢查配置或網路狀態\", \"triggerTime\": \"2025/03/25 14:30:00\"}")
                        })
                        )
                        
                    //@ApiResponse(responseCode = "500", description = "發生未知錯誤\n時間：YYYY/MM/DD HH:MM:SS",
                    //content = @Content(examples = @ExampleObject(value = "{\"code\": \"500\", \"message\": \"發生未知錯誤\", \"triggerTime\": \"2025/03/25 14:30:00\"}")))
    })
    @PostMapping("/employee-data")
    public ResponseEntity<BaseResponse> processEmployeeData(@RequestBody CSVToDataBaseRequestDto request) {
        Instant startTime = Instant.now();
        String formattedStartTime = formatter.format(startTime.atZone(ZoneId.systemDefault()));
        System.out.println("呼叫 API 前時間: " + formattedStartTime);

        try {
            csvToDataBaseService.processCsvToDatabase(request);
            Instant endTime = Instant.now();
            String formattedEndTime = formatter.format(endTime.atZone(ZoneId.systemDefault()));
            System.out.println("呼叫 API 後時間: " + formattedEndTime);
            Duration duration = Duration.between(startTime, endTime);
            double seconds = duration.toMillis() / 1000.0;
            System.out.println("API 呼叫耗時: " + seconds + " 秒");

            return ResponseEntity.ok(new BaseResponse("資料處理成功", formattedStartTime));
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            System.out.println("捕獲到的異常訊息: " + errorMessage); // 調試輸出
            if (errorMessage != null && errorMessage.startsWith("ErrorResponseDto(code=")) {
                try {
                    String[] parts = errorMessage.substring("ErrorResponseDto(code=".length(), errorMessage.length() - 1).split(", ");
                    if (parts.length < 2) {
                        throw new IllegalArgumentException("ErrorResponseDto 格式無效");
                    }
                    String code = parts[0].trim();
                    String message = parts[1].replace("message=", "").trim();
                    String triggerTime = parts.length > 2 ? parts[2].replace("triggerTime=", "").trim() : formattedStartTime;

                    System.out.println("解析結果: code=" + code + ", message=" + message + ", triggerTime=" + triggerTime); // 調試輸出
                    if (code.equals(ResponseCode.UNKNOWN_ERROR.getCode())) {
                        return ResponseEntity.status(500)
                                .body(new BaseResponse(ResponseCode.UNKNOWN_ERROR, message, triggerTime));
                    } else {
                        return ResponseEntity.ok(new BaseResponse(ResponseCode.SUCCESS, message, triggerTime));
                    }
                } catch (Exception parseEx) {
                    System.out.println("解析異常: " + parseEx.getMessage()); // 調試輸出
                    return ResponseEntity.status(500)
                            .body(new BaseResponse(ResponseCode.UNKNOWN_ERROR, "解析錯誤: " + parseEx.getMessage(), formattedStartTime));
                }
            } else {
                System.out.println("未匹配 ErrorResponseDto 格式，直接返回 500: " + errorMessage); // 調試輸出
                return ResponseEntity.status(500)
                        .body(new BaseResponse(ResponseCode.UNKNOWN_ERROR, "發生未知錯誤: " + (errorMessage != null ? errorMessage : "未知原因"), formattedStartTime));
            }
        }
    }
}