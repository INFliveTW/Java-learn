package cdf.training.svc.datatransfer.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.dto.ErrorResponseDto;
import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;
import cdf.training.svc.datatransfer.repository.EmployeeDataRepository;
import cdf.training.svc.datatransfer.util.CSVParserUtil;

@Service
public class CSVToDataBaseServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(CSVToDataBaseServiceImpl.class);
    private final SFTPServiceImpl sftpService;
    private final CSVParserUtil csvParserUtil;
    private final DataConverterImpl dataConverter;
    private final EmployeeDataRepository repository;

    public CSVToDataBaseServiceImpl(SFTPServiceImpl sftpService, CSVParserUtil csvParserUtil,
                                    DataConverterImpl dataConverter, EmployeeDataRepository repository) {
        this.sftpService = sftpService;
        this.csvParserUtil = csvParserUtil;
        this.dataConverter = dataConverter;
        this.repository = repository;
    }

    public boolean processCsvToDatabase(CSVToDataBaseRequestDto request) {
        try {
            String csvContent;
            try { //讀取sftp檔案 /upload/employee_data.csv、測試無權限讀取 /upload/test/employee_data.csv
                csvContent = sftpService.readFileFromSFTP("/upload/employee_data.csv");
                if (csvContent == null || csvContent.trim().isEmpty()) {
                    throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.SFTP_FILE_NOT_FOUND.getCode(),
                                             ResponseCode.SFTP_FILE_NOT_FOUND.getDefaultMessage(),
                                             null).toString());
                }
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("ErrorResponseDto(code=")) {
                    throw new RuntimeException(msg);
                }
                throw new RuntimeException(
                    new ErrorResponseDto(ResponseCode.SFTP_CONNECTION_ERROR.getCode(),
                                         ResponseCode.SFTP_CONNECTION_ERROR.getDefaultMessage(),
                                         null).toString());
            }

            logger.info("從 SFTP 讀取的 CSV 內容: {}", csvContent);

            List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);
            if (csvDtos.isEmpty()) {
                throw new RuntimeException(
                    new ErrorResponseDto(ResponseCode.CSV_EMPTY_ERROR.getCode(),
                                         ResponseCode.CSV_EMPTY_ERROR.getDefaultMessage(),
                                         null).toString());
            }

            String COMPANY = request.getCOMPANY() != null ? request.getCOMPANY() :
                    List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
            String EXCUTETIMEStr = request.getEXCUTETIME() != null ? request.getEXCUTETIME() :
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime EXCUTETIME = LocalDateTime.parse(EXCUTETIMEStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            List<EmployeeDataEntity> entities = dataConverter.convertToEntities(csvDtos, COMPANY, EXCUTETIME);

            for (EmployeeDataEntity entity : entities) {
                logger.info("準備寫入 SQL: ID={}, DEPARTMENT={}, JOB_TITLE={}, NAME={}, TEL={}, EMAIL={}, COMPANY={}, EXCUTETIME={}",
                        entity.getID(), entity.getDEPARTMENT(), entity.getJOB_TITLE(), entity.getNAME(),
                        entity.getTEL(), entity.getEMAIL(), entity.getCOMPANY(), entity.getEXCUTETIME());
                try {
                    repository.insert(entity);
                } catch (Exception sqlEx) {
                    // 記錄異常訊息以便排查
                    logger.error("資料庫操作失敗，異常訊息: {}", sqlEx.getMessage(), sqlEx);

                    // 檢查異常訊息
                    String msg = sqlEx.getMessage() != null ? sqlEx.getMessage().toLowerCase() : "";
                    // 檢查底層原因（如果異常被包裝）
                    String causeMsg = sqlEx.getCause() != null && sqlEx.getCause().getMessage() != null 
                        ? sqlEx.getCause().getMessage().toLowerCase() : "";

                    // 擴展連線失敗的關鍵字檢查，針對 MSSQL
                    if (msg.contains("tcp/ip connection") || // 先檢查更具體的條件
                    msg.contains("connection refused: connect") || // 先檢查更具體的條件
                    causeMsg.contains("tcp/ip connection") || // 先檢查更具體的條件
                    msg.contains("connection") || 
                    msg.contains("cannot connect") || 
                    msg.contains("communications link failure") || 
                    msg.contains("refused") || 
                    msg.contains("timeout") || 
                    msg.contains("failed to connect") || 
                    causeMsg.contains("connection") || 
                    causeMsg.contains("refused")) {
                    throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.SQL_CONNECTION_ERROR.getCode(),
                                             ResponseCode.SQL_CONNECTION_ERROR.getDefaultMessage(),
                                             null).toString());
                }
                    // 其他情況假設為寫入失敗
                    throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.SQL_WRITE_ERROR.getCode(),
                                             ResponseCode.SQL_WRITE_ERROR.getDefaultMessage(),
                                             null).toString());
                }
            }

            logger.info("成功新增 {} 筆資料到資料庫", entities.size());
            return true;
        } catch (Exception e) {
            String exceptionMessage = e.getMessage();
            if (exceptionMessage != null && exceptionMessage.startsWith("ErrorResponseDto(code=")) {
                throw new RuntimeException(exceptionMessage);
            }
            logger.error("將 CSV 傳輸到資料庫時出錯: {}", e.getMessage(), e);
            throw new RuntimeException(
                new ErrorResponseDto(ResponseCode.UNKNOWN_ERROR.getCode(),
                                     ResponseCode.UNKNOWN_ERROR.getDefaultMessage() + ": " +
                                     (e.getMessage() != null ? e.getMessage() : "未知原因"),
                                     null).toString());
        }
    }
}