package cdf.training.svc.datatransfer.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public void processCsvToDatabase(CSVToDataBaseRequestDto request) {
        try {
            // 嘗試讀取檔案
            String csvContent;
            try {
                csvContent = sftpService.readFileFromSFTP("/upload/employee_data.csv");
                if (csvContent == null || csvContent.trim().isEmpty()) {
                    throw new RuntimeException("file not found: SFTP 資料夾沒有CSV檔案，請確認SFTP");
                }
            } catch (Exception e) {
                // 若異常訊息表示檔案不存在，則拋出明確的 file not found 錯誤
                String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (msg.contains("no such file") || msg.contains("file not found") || msg.contains("not exist")) {
                    throw new RuntimeException("file not found: SFTP 資料夾沒有CSV檔案，請確認SFTP");
                }
                throw e; // 其他異常繼續拋出，例如連接錯誤
            }

            logger.info("從 SFTP 讀取的 CSV 內容: {}", csvContent);

            List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);
            if (csvDtos.isEmpty()) {
                throw new RuntimeException("CSV content is empty: CSV 內容為空，請確認文件內容");
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
                repository.insert(entity);
            }

            logger.info("成功新增 {} 筆資料到資料庫", entities.size());

        } catch (Exception e) {
            String errorCode;
            String errorMessage;
            String exceptionMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            switch (exceptionMessage) {
                case String msg when msg.contains("file not found") -> { // 檔案不存在
                    errorCode = "SFTP_FILE_NOT_FOUND";
                    errorMessage = "SFTP 資料夾沒有CSV檔案，請確認SFTP";
                }
                case String msg when msg.contains("sftp permission denied") -> { // 權限問題
                    errorCode = "SFTP_PERMISSION_DENIED";
                    errorMessage = "SFTP 伺服器拒絕訪問，請檢查權限";
                }
                case String msg when msg.contains("sftp error") && !msg.contains("file not found") -> { // 連接錯誤
                    errorCode = "SFTP_CONNECTION_ERROR";
                    errorMessage = "無法連接到 SFTP 伺服器，請檢查配置或網路狀態";
                }
                case String msg when msg.contains("csv content is empty") -> { // CSV 內容為空
                    errorCode = "CSV_EMPTY_ERROR";
                    errorMessage = "CSV 檔案內容沒有任何資料，請確認文件內容";
                }
                case String msg when msg.contains("parse") -> { // 解析錯誤
                    errorCode = "CSV_PARSE_ERROR";
                    errorMessage = "CSV 檔案解析失敗，請確認檔案格式正確";
                }
                case String msg when msg.contains("database") -> { // 資料庫錯誤
                    errorCode = "DATABASE_ERROR";
                    errorMessage = "資料庫寫入失敗，請檢查資料庫連線或權限";
                }
                default -> {
                    errorCode = "UNKNOWN_ERROR";
                    errorMessage = "發生未知錯誤：" + e.getMessage();
                }
            }

            logger.error("將 CSV 傳輸到資料庫時出錯: {}", errorMessage);
            throw new RuntimeException(new ErrorResponseDto(errorCode, errorMessage).toString(), e);
        }
    }
}