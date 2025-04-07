package cdf.training.svc.datatransfer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class EmployeeDataController {

    private final CSVToDataBaseServiceImpl csvToDataBaseService;

    public EmployeeDataController(CSVToDataBaseServiceImpl csvToDataBaseService) {
        this.csvToDataBaseService = csvToDataBaseService;
    }

    @Operation(summary = "處理員工資料", description = "從 SFTP 讀取 CSV 檔案並寫入 SQL，返回處理結果")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功執行或業務錯誤",
            content = @Content(examples = {
                @ExampleObject(name = "資料處理成功", 
                    value = "{\n  \"metadata\": { \"status\": true, \"errorCode\": null, \"errorDesc\": null },\n  \"data\": \"資料處理成功\"\n}"),
                @ExampleObject(name = "SFTP 伺服器拒絕訪問", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"SFTP_001\", \"errorDesc\": \"SFTP 伺服器拒絕訪問，請檢查權限\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "SFTP 資料夾沒有CSV檔案", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"SFTP_002\", \"errorDesc\": \"SFTP 資料夾沒有CSV檔案，請確認SFTP\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "CSV 檔案解析失敗", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"CSV_001\", \"errorDesc\": \"CSV 檔案解析失敗，請確認檔案格式正確\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "CSV 檔案內容沒有任何資料", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"CSV_002\", \"errorDesc\": \"CSV 檔案內容沒有任何資料，請確認文件內容\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "CSV檔案，欄位缺少", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"CSV_003\", \"errorDesc\": \"CSV檔案，欄位缺少，請確認檔案\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "CSV檔案，資料缺少", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"CSV_004\", \"errorDesc\": \"CSV檔案，資料缺少，請確認檔案\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "資料庫寫入失敗", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"SQL_002\", \"errorDesc\": \"資料庫寫入失敗，請檢查資料庫連線或權限\" },\n  \"data\": null\n}")
            })),
        @ApiResponse(responseCode = "500", description = "系統錯誤",
            content = @Content(examples = {
                @ExampleObject(name = "系統發生未知錯誤", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"UNKNOWN_001\", \"errorDesc\": \"發生未知錯誤\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "無法連接到 SFTP 伺服器", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"SFTP_003\", \"errorDesc\": \"無法連接到 SFTP 伺服器，請檢查配置或網路狀態\" },\n  \"data\": null\n}"),
                @ExampleObject(name = "無法連線到資料庫", 
                    value = "{\n  \"metadata\": { \"status\": false, \"errorCode\": \"SQL_001\", \"errorDesc\": \"無法連線到資料庫，請檢查配置或網路狀態\" },\n  \"data\": null\n}")
            }))
    })
    @PostMapping("/employee-data")
    public ResponseEntity<BaseResponse> processEmployeeData(@RequestBody CSVToDataBaseRequestDto request) {
        try {
            boolean isValid = csvToDataBaseService.processCsvToDatabase(request);
            if (isValid) {
                return ResponseEntity.ok(new BaseResponse("資料處理成功"));
            } else {
                return ResponseEntity.ok(new BaseResponse(ResponseCode.UNKNOWN_ERROR.getCode(), ResponseCode.UNKNOWN_ERROR.getDefaultMessage()));
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.startsWith("ErrorResponseDto(code=")) {
                try {
                    String[] parts = errorMessage.substring("ErrorResponseDto(code=".length(), errorMessage.length() - 1).split(", ");
                    String code = parts[0].trim();
                    String message = parts[1].replace("message=", "").trim();
                    return ResponseEntity.ok(new BaseResponse(code, message));
                } catch (Exception parseEx) {
                    return ResponseEntity.status(500).body(new BaseResponse(ResponseCode.UNKNOWN_ERROR.getCode(), "解析錯誤: " + parseEx.getMessage()));
                }
            } else {
                return ResponseEntity.status(500).body(new BaseResponse(ResponseCode.UNKNOWN_ERROR.getCode(), "發生未知錯誤: " + (errorMessage != null ? errorMessage : "未知原因")));
            }
        }
    }
}