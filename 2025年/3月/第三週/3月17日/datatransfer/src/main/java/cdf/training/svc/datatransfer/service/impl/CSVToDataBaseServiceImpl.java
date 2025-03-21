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

@Service //標記服務類型
public class CSVToDataBaseServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(CSVToDataBaseServiceImpl.class);
    private final SFTPServiceImpl sftpService;
    private final CSVParserUtil csvParserUtil;
    private final DataConverterImpl dataConverter;
    private final EmployeeDataRepository repository;
    //private final 注入依賴

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
            logger.info("從SFTP讀取的CSV內容: {}", csvContent);
            //讀取SFTP
            List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);
            //解析CSV
            String COMPANY = request.getCOMPANY() != null ? request.getCOMPANY() :
                    List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
            String EXCUTETIMEStr = request.getEXCUTETIME() != null ? request.getEXCUTETIME() :
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime EXCUTETIME = LocalDateTime.parse(EXCUTETIMEStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //轉換時間
            List<EmployeeDataEntity> entities = dataConverter.convertToEntities(csvDtos, COMPANY, EXCUTETIME);
            
            for (EmployeeDataEntity entity : entities) {
                logger.info("準備寫入SQL: ID={}, DEPARTMENT={}, JOB_TITLE={}, NAME={}, TEL={}, EMAIL={}, COMPANY={}, EXCUTETIME={}",
                        entity.getID(), entity.getDEPARTMENT(), entity.getJOB_TITLE(), entity.getNAME(),
                        entity.getTEL(), entity.getEMAIL(), entity.getCOMPANY(), entity.getEXCUTETIME());
            }
            
            repository.saveAll(entities); //寫入資料庫
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
//步驟6：SFTP讀取