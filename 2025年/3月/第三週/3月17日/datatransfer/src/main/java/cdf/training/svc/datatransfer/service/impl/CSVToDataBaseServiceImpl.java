package cdf.training.svc.datatransfer.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.util.CSVParserUtil;

@Service
public class CSVToDataBaseServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(CSVToDataBaseServiceImpl.class);
    private final SFTPServiceImpl sftpService;
    private final CSVParserUtil csvParserUtil;
    private final DataConverterImpl dataConverter;
    private final JdbcTemplate jdbcTemplate;

    public CSVToDataBaseServiceImpl(SFTPServiceImpl sftpService, CSVParserUtil csvParserUtil,
                                    DataConverterImpl dataConverter, JdbcTemplate jdbcTemplate) {
        this.sftpService = sftpService;
        this.csvParserUtil = csvParserUtil;
        this.dataConverter = dataConverter;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void processCsvToDatabase(CSVToDataBaseRequestDto request) {
        try {
            String csvContent = sftpService.readFileFromSFTP("/upload/employee_data.csv");
            logger.info("從 SFTP 讀取的 CSV 內容: {}", csvContent);

            List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);

            String COMPANY = request.getCOMPANY() != null ? request.getCOMPANY() :
                    List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
            String EXCUTETIMEStr = request.getEXCUTETIME() != null ? request.getEXCUTETIME() :
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime EXCUTETIME = LocalDateTime.parse(EXCUTETIMEStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 增強 DTO 資料
            List<EmployeeDataCSVDto> enrichedDtos = dataConverter.enrichCsvData(csvDtos, COMPANY, EXCUTETIME);

            String sql = "INSERT INTO employee_data (ID, DEPARTMENT, JOB_TITLE, NAME, TEL, EMAIL, COMPANY, EXCUTETIME) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            for (EmployeeDataCSVDto dto : enrichedDtos) {
                logger.info("準備寫入 SQL: ID={}, DEPARTMENT={}, JOB_TITLE={}, NAME={}, TEL={}, EMAIL={}, COMPANY={}, EXCUTETIME={}",
                        dto.getID(), dto.getDEPARTMENT(), dto.getJOB_TITLE(), dto.getNAME(),
                        dto.getTEL(), dto.getEMAIL(), dto.getCOMPANY(), dto.getEXCUTETIME());

                jdbcTemplate.update(sql, dto.getID(), dto.getDEPARTMENT(), dto.getJOB_TITLE(), dto.getNAME(),
                        dto.getTEL(), dto.getEMAIL(), dto.getCOMPANY(), dto.getEXCUTETIME());
            }

            logger.info("成功新增 {} 筆資料到資料庫", enrichedDtos.size());

        } catch (Exception e) {
            String errorMessage = e.getMessage().contains("SFTP") ? "無法連接到 SFTP 伺服器，請檢查配置或網路狀態" :
                    e.getMessage().contains("parse") ? "CSV 檔案解析失敗，請確認檔案格式正確" :
                            e.getMessage().contains("database") ? "資料庫寫入失敗，請檢查資料庫連線或權限" :
                                    "發生未知錯誤：" + e.getMessage();
            logger.error("將 CSV 傳輸到資料庫時出錯: {}", errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }
}