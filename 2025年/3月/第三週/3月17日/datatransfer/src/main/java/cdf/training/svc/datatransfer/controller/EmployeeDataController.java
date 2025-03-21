package cdf.training.svc.datatransfer.controller;

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
        csvToDataBaseService.processCsvToDatabase(request); //調用服務處理
        return ResponseEntity.ok(new CSVToDataBaseResponseDto("Data processed successfully"));
    }
}
//步驟5：接收請求