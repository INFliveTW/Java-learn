package cdf.training.svc.datatransfer.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;

import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.dto.ErrorResponseDto;

@Component
public class CSVParserUtil {
    private static final Logger logger = LoggerFactory.getLogger(CSVParserUtil.class);

    protected CSVReader createCSVReader(String csvContent) {
        return new CSVReader(new StringReader(csvContent));
    }

    protected List<String> splitCsvContentIntoLines(String csvContent) {
        if (csvContent == null || csvContent.trim().isEmpty()) {
            return List.of();
        }
        return List.of(csvContent.split("\n", -1));
    }

    public List<EmployeeDataCSVDto> parseCsv(String csvContent) {
        try {
            if (csvContent == null || csvContent.trim().isEmpty()) {
                throw new RuntimeException(
                    new ErrorResponseDto(ResponseCode.CSV_EMPTY_ERROR.getCode(),
                                         ResponseCode.CSV_EMPTY_ERROR.getDefaultMessage(),
                                         null).toString());
            }

            if (csvContent.contains(";")) {
                throw new RuntimeException(
                    new ErrorResponseDto(ResponseCode.CSV_PARSE_ERROR.getCode(),
                                         ResponseCode.CSV_PARSE_ERROR.getDefaultMessage() + ": 不合法分隔符 (使用 ; 而非 ,)",
                                         null).toString());
            }

            if (csvContent.startsWith("\uFEFF")) {
                csvContent = csvContent.substring(1);
            }

            List<String> lines = splitCsvContentIntoLines(csvContent);
            if (lines.isEmpty() || lines.get(0).trim().isEmpty()) {
                throw new RuntimeException(
                    new ErrorResponseDto(ResponseCode.CSV_EMPTY_ERROR.getCode(),
                                         ResponseCode.CSV_EMPTY_ERROR.getDefaultMessage(),
                                         null).toString());
            }

            List<EmployeeDataCSVDto> dtos = new ArrayList<>();
            try (CSVReader csvReader = createCSVReader(csvContent)) {
                String[] headers;
                try {
                    headers = csvReader.readNext();
                } catch (Exception e) {
                    throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.CSV_PARSE_ERROR.getCode(),
                                             ResponseCode.CSV_PARSE_ERROR.getDefaultMessage() + ": 無法讀取標頭: " + e.getMessage(),
                                             null).toString());
                }
                if (headers == null || headers.length == 0) {
                    throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.CSV_EMPTY_ERROR.getCode(),
                                             ResponseCode.CSV_EMPTY_ERROR.getDefaultMessage(),
                                             null).toString());
                }

                Map<String, Integer> headerMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    headerMap.put(headers[i].trim().toLowerCase(), i);
                }

                String[] requiredFields = {"ID", "DEPARTMENT", "JOB_TITLE", "NAME", "TEL", "EMAIL"};
                for (String field : requiredFields) {
                    if (!headerMap.containsKey(field.toLowerCase())) {
                        throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.CSV_MISSING_FIELDS.getCode(),
                                                 ResponseCode.CSV_MISSING_FIELDS.getDefaultMessage() + " (缺少欄位: " + field + ")",
                                                 null).toString());
                    }
                }

                int expectedCommaCount = headers.length - 1;

                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.trim().isEmpty()) continue;

                    String[] tempFields = line.split(",", -1);
                    if (tempFields.length <= 1) {
                        continue;
                    }

                    int commaCount = (int) line.chars().filter(ch -> ch == ',').count();
                    if (commaCount < expectedCommaCount) {
                        throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.CSV_PARSE_ERROR.getCode(),
                                                 ResponseCode.CSV_PARSE_ERROR.getDefaultMessage() + " (第 " + i + " 行缺少分隔符號: 預期 " + expectedCommaCount + " 個逗號，實際 " + commaCount + " 個)",
                                                 null).toString());
                    }
                }

                String[] fields;
                int lineNumber = 0;
                while (true) {
                    try {
                        fields = csvReader.readNext();
                    } catch (Exception e) {
                        throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.CSV_PARSE_ERROR.getCode(),
                                                 ResponseCode.CSV_PARSE_ERROR.getDefaultMessage() + ": 無法讀取第 " + (lineNumber + 1) + " 行: " + e.getMessage(),
                                                 null).toString());
                    }
                    if (fields == null) break;

                    if (fields.length <= 1) continue;

                    lineNumber++;

                    if (fields.length > headers.length) {
                        throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.CSV_MISSING_FIELDS.getCode(),
                                                 ResponseCode.CSV_MISSING_FIELDS.getDefaultMessage() + " (第 " + lineNumber + " 行資料欄數多於標頭: " + fields.length + " > " + headers.length + ")",
                                                 null).toString());
                    } else if (fields.length < headers.length) {
                        throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.CSV_PARSE_ERROR.getCode(),
                                                 ResponseCode.CSV_PARSE_ERROR.getDefaultMessage() + " (第 " + lineNumber + " 行欄數不一致: " + fields.length + " < " + headers.length + ")",
                                                 null).toString());
                    }

                    EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
                    dto.setID(getFieldValue(fields, headerMap, "ID"));
                    dto.setDEPARTMENT(getFieldValue(fields, headerMap, "DEPARTMENT"));
                    dto.setJOB_TITLE(getFieldValue(fields, headerMap, "JOB_TITLE"));
                    dto.setNAME(getFieldValue(fields, headerMap, "NAME"));
                    dto.setTEL(getFieldValue(fields, headerMap, "TEL"));
                    dto.setEMAIL(getFieldValue(fields, headerMap, "EMAIL"));

                    StringBuilder missingFields = new StringBuilder();
                    if (dto.getID() == null || dto.getID().trim().isEmpty()) missingFields.append("ID, ");
                    if (dto.getDEPARTMENT() == null || dto.getDEPARTMENT().trim().isEmpty()) missingFields.append("DEPARTMENT, ");
                    if (dto.getJOB_TITLE() == null || dto.getJOB_TITLE().trim().isEmpty()) missingFields.append("JOB_TITLE, ");
                    if (dto.getNAME() == null || dto.getNAME().trim().isEmpty()) missingFields.append("NAME, ");
                    if (dto.getTEL() == null || dto.getTEL().trim().isEmpty()) missingFields.append("TEL, ");
                    if (dto.getEMAIL() == null || dto.getEMAIL().trim().isEmpty()) missingFields.append("EMAIL, ");

                    if (missingFields.length() > 0) {
                        String missingFieldsStr = missingFields.substring(0, missingFields.length() - 2);
                        throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.CSV_MISSING_DATA.getCode(),
                                                 ResponseCode.CSV_MISSING_DATA.getDefaultMessage() + " (第 " + lineNumber + " 行缺少欄位: " + missingFieldsStr + ")",
                                                 null).toString());
                    }

                    logger.info("Parsed CSV data: ID={}, DEPARTMENT={}, JOB_TITLE={}, NAME={}, TEL={}, EMAIL={}",
                            dto.getID(), dto.getDEPARTMENT(), dto.getJOB_TITLE(), dto.getNAME(),
                            dto.getTEL(), dto.getEMAIL());

                    dtos.add(dto);
                }
} catch (Exception e) {
    String message = e.getMessage();
    logger.info("Inner catch: message == null: {}", message == null);
    if (message != null && message.startsWith("ErrorResponseDto(code=")) {
        throw new RuntimeException(message);  // ✅ 直接丟原始訊息
    }
    throw new RuntimeException(
        new ErrorResponseDto(ResponseCode.CSV_PARSE_ERROR.getCode(),
                             ResponseCode.CSV_PARSE_ERROR.getDefaultMessage() + ": " + (message != null ? message : "未知原因"),
                             null).toString());
}

            return dtos;
        } catch (Exception e) {
            String message = e.getMessage();
            //logger.info("Outer catch: message == null: {}", message == null);
            if (message != null && message.startsWith("ErrorResponseDto(code=")) {
                System.out.println("🔥 外層 catch 命中 rethrow 原始 ErrorResponseDto");
                throw new RuntimeException(message, e);
            }
            System.out.println("💀 外層 catch 進入 fallback");
            throw new RuntimeException(
                new ErrorResponseDto(ResponseCode.CSV_PARSE_ERROR.getCode(),
                                     ResponseCode.CSV_PARSE_ERROR.getDefaultMessage() + ": " + (message != null ? message : "未知原因"),
                                     null).toString());
        }
    }

    String getFieldValue(String[] fields, Map<String, Integer> headerMap, String fieldName) {
        Integer index = headerMap.get(fieldName.toLowerCase());
        if (index == null || index >= fields.length) {
            return null;
        }
        return fields[index].trim();
    }
}