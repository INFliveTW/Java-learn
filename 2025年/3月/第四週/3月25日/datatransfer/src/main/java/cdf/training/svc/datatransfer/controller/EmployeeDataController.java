package cdf.training.svc.datatransfer.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cdf.training.svc.datatransfer.dto.BaseResponse;
import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.CSVToDataBaseResponseDto;
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
            @ApiResponse(responseCode = "200", description = "【範例(200)】資料處理成功\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"code\": \"200\", \"message\": \"資料處理成功\", \"data\": {}, \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "403", description = "【範例(403)】SFTP 伺服器拒絕訪問，請檢查權限\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"code\": \"403\", \"message\": \"SFTP 伺服器拒絕訪問，請檢查權限\", \"data\": null, \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "404", description = "【範例(404)】SFTP 資料夾沒有CSV檔案，請確認SFTP\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"code\": \"404\", \"message\": \"SFTP 資料夾沒有CSV檔案，請確認SFTP\", \"data\": null, \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "400", description = "【範例(400)】CSV 檔案解析失敗，請確認檔案格式正確\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"code\": \"400\", \"message\": \"CSV 檔案解析失敗，請確認檔案格式正確\", \"data\": null, \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "204", description = "【範例(204)】CSV 檔案內容沒有任何資料，請確認文件內容\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"code\": \"204\", \"message\": \"CSV 檔案內容沒有任何資料，請確認文件內容\", \"data\": null, \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "500", description = "【範例(500)】發生未知錯誤\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"code\": \"500\", \"message\": \"發生未知錯誤\", \"data\": null, \"triggerTime\": \"2025/03/25 14:30:00\"}"))),
            @ApiResponse(responseCode = "503", description = "【範例(503)】無法連接到 SFTP 伺服器，請檢查配置或網路狀態\n時間：YYYY/MM/DD HH:MM:SS",
                    content = @Content(examples = @ExampleObject(value = "{\"code\": \"503\", \"message\": \"無法連接到 SFTP 伺服器，請檢查配置或網路狀態\", \"data\": null, \"triggerTime\": \"2025/03/25 14:30:00\"}")))
    })
    @PostMapping("/employee-data")
    public ResponseEntity<BaseResponse<?>> processEmployeeData(@RequestBody CSVToDataBaseRequestDto request) {
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

            CSVToDataBaseResponseDto responseDto = new CSVToDataBaseResponseDto("資料處理成功");
            return ResponseEntity.ok(new BaseResponse<>("資料處理成功", responseDto, formattedStartTime));
        } catch (RuntimeException e) {
            ResponseCode errorCode;
            String errorMessage;
            String triggerTime = formattedStartTime;
            try {
                // 解析 ErrorResponseDto 的 toString() 格式
                String[] parts = e.getMessage().split(", ");
                String codePart = parts[0].replace("ErrorResponseDto(code=", "").trim();
                String messagePart = parts[1].replace("message=", "").trim();
                String triggerTimePart = parts.length > 2 ? parts[2].replace("triggerTime=", "").replace(")", "").trim() : null;

                errorCode = ResponseCode.valueOf(codePart);
                errorMessage = messagePart;
                if (triggerTimePart != null && !triggerTimePart.equals("null")) {
                    triggerTime = triggerTimePart; // 若異常中帶有 triggerTime，則使用
                }
            } catch (Exception parseEx) {
                errorCode = ResponseCode.UNKNOWN_ERROR;
                errorMessage = "發生未知錯誤：" + e.getMessage();
            }
            return ResponseEntity.status(500)
                    .body(new BaseResponse<>(errorCode, errorMessage, triggerTime));
        }
    }
}