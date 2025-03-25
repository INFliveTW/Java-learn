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
            String csvContent = sftpService.readFileFromSFTP("/upload/employee_data.csv");
            if (csvContent == null || csvContent.trim().isEmpty()) {
                throw new RuntimeException("SFTP 資料夾沒有CSV檔案，請確認SFTP");
            }
            logger.info("從 SFTP 讀取的 CSV 內容: {}", csvContent);

            List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);
            if (csvDtos.isEmpty()) {
                throw new RuntimeException("CSV 內容為空，請確認文件內容");
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

            if (e.getMessage().contains("SFTP error") && !e.getMessage().contains("file not found") && !e.getMessage().contains("CSV content is empty")) {
                errorCode = "SFTP_CONNECTION_ERROR";
                errorMessage = "無法連接到 SFTP 伺服器，請檢查配置或網路狀態";
            } else if (e.getMessage().contains("file not found")) {
                errorCode = "SFTP_FILE_NOT_FOUND";
                errorMessage = "SFTP 資料夾沒有CSV檔案，請確認SFTP";
            } else if (e.getMessage().contains("CSV content is empty") || e.getMessage().contains("CSV content has no data")) {
                errorCode = "CSV_EMPTY_ERROR";
                errorMessage = "CSV 檔案內容沒有任何資料，請確認文件內容";
            } else if (e.getMessage().contains("parse")) {
                errorCode = "CSV_PARSE_ERROR";
                errorMessage = "CSV 檔案解析失敗，請確認檔案格式正確";
            } else if (e.getMessage().contains("database")) {
                errorCode = "DATABASE_ERROR";
                errorMessage = "資料庫寫入失敗，請檢查資料庫連線或權限";
            } else {
                errorCode = "UNKNOWN_ERROR";
                errorMessage = "發生未知錯誤：" + e.getMessage();
            }

            logger.error("將 CSV 傳輸到資料庫時出錯: {}", errorMessage);
            throw new RuntimeException(new cdf.training.svc.datatransfer.dto.ErrorResponseDto(errorCode, errorMessage).toString(), e);
        }
    }
}
//         try {
//             String csvContent = sftpService.readFileFromSFTP("/upload/employee_data.csv");
//             logger.info("從 SFTP 讀取的 CSV 內容: {}", csvContent);

//             List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);

//             String COMPANY = request.getCOMPANY() != null ? request.getCOMPANY() :
//                     List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
//             String EXCUTETIMEStr = request.getEXCUTETIME() != null ? request.getEXCUTETIME() :
//                     LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//             LocalDateTime EXCUTETIME = LocalDateTime.parse(EXCUTETIMEStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

//             List<EmployeeDataEntity> entities = dataConverter.convertToEntities(csvDtos, COMPANY, EXCUTETIME);

//             for (EmployeeDataEntity entity : entities) {
//                 logger.info("準備寫入 SQL: ID={}, DEPARTMENT={}, JOB_TITLE={}, NAME={}, TEL={}, EMAIL={}, COMPANY={}, EXCUTETIME={}",
//                         entity.getID(), entity.getDEPARTMENT(), entity.getJOB_TITLE(), entity.getNAME(),
//                         entity.getTEL(), entity.getEMAIL(), entity.getCOMPANY(), entity.getEXCUTETIME());
//                 repository.insert(entity); // 使用 MyBatis 插入
//             }

//             logger.info("成功新增 {} 筆資料到資料庫", entities.size());

//         } catch (Exception e) {
//             System.out.println("message : " + e.getMessage());
//             String errorMessage = e.getMessage().contains("SFTP") ? "無法連接到 SFTP 伺服器，請檢查配置或網路狀態" :
//                     e.getMessage().contains("parse") ? "CSV 檔案解析失敗，請確認檔案格式正確" :
//                             e.getMessage().contains("database") ? "資料庫寫入失敗，請檢查資料庫連線或權限" :
//                                     "發生未知錯誤：" + e.getMessage();
//             logger.error("將 CSV 傳輸到資料庫時出錯: {}", errorMessage);
//             throw new RuntimeException(errorMessage, e);
//         }
//     }
// }
//步驟6：SFTP讀取