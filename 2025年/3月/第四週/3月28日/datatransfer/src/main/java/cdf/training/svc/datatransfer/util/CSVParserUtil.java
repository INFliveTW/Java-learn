package cdf.training.svc.datatransfer.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.dto.ErrorResponseDto;

@Component
public class CSVParserUtil {
    private static final Logger logger = LoggerFactory.getLogger(CSVParserUtil.class);
    
    public List<EmployeeDataCSVDto> parseCsv(String csvContent) {
        List<String> lines = Arrays.asList(csvContent.split("\n"));
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV 內容為空");
        }

        // 解析標題
        String[] headers = lines.get(0).split(",");
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim().toLowerCase(), i); // 忽略大小寫並去除空白
        }

        // 檢查必要欄位
        String[] requiredFields = {"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"};
        for (String field : requiredFields) {
            if (!headerMap.containsKey(field.toLowerCase())) {
                throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.CSV_MISSING_FIELDS.getCode(),
                                             ResponseCode.CSV_MISSING_FIELDS.getDefaultMessage(),
                                             null).toString());
            }
        }

        // 解析資料行並檢查資料是否缺失
        List<EmployeeDataCSVDto> dtos = lines.stream()
                .skip(1) // 跳過標頭
                .filter(line -> !line.trim().isEmpty()) // 過濾空行
                .map(line -> {
                    String[] fields = line.split(",");
                    EmployeeDataCSVDto dto = new EmployeeDataCSVDto();

                    // 動態映射字段
                    dto.setID(getFieldValue(fields, headerMap, "ID"));
                    dto.setDEPARTMENT(getFieldValue(fields, headerMap, "DEPARTMENT"));
                    dto.setJOB_TITLE(getFieldValue(fields, headerMap, "JOB_TITLE"));
                    dto.setNAME(getFieldValue(fields, headerMap, "NAME"));
                    dto.setTEL(getFieldValue(fields, headerMap, "TEL"));
                    dto.setEMAIL(getFieldValue(fields, headerMap, "EMAIL"));

                    // 檢查必要資料是否缺失
                    if (dto.getID() == null || dto.getID().trim().isEmpty() ||
                        dto.getDEPARTMENT() == null || dto.getDEPARTMENT().trim().isEmpty() ||
                        dto.getJOB_TITLE() == null || dto.getJOB_TITLE().trim().isEmpty() ||
                        dto.getNAME() == null || dto.getNAME().trim().isEmpty() ||
                        dto.getTEL() == null || dto.getTEL().trim().isEmpty() ||
                        dto.getEMAIL() == null || dto.getEMAIL().trim().isEmpty()) {
                        throw new RuntimeException(
                                new ErrorResponseDto(ResponseCode.CSV_MISSING_DATA.getCode(),
                                                     ResponseCode.CSV_MISSING_DATA.getDefaultMessage(),
                                                     null).toString());
                    }

                    logger.info("Parsed CSV data: ID={}, DEPARTMENT={}, JOB_TITLE={}, NAME={}, TEL={}, EMAIL={}",
                            dto.getID(), dto.getDEPARTMENT(), dto.getJOB_TITLE(), dto.getNAME(),
                            dto.getTEL(), dto.getEMAIL());
                            
                    return dto;
                })
                .collect(Collectors.toList());

        return dtos;
    }

    private String getFieldValue(String[] fields, Map<String, Integer> headerMap, String fieldName) {
        Integer index = headerMap.get(fieldName.toLowerCase());
        return (index != null && index < fields.length) ? fields[index].trim() : null;
    }
}
//步驟8：CSV解析