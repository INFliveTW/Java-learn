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

    public void processCsvToDatabase(CSVToDataBaseRequestDto request) {
        try {
            String csvContent;
            try {
                csvContent = sftpService.readFileFromSFTP("/upload/employee_data.csv");
                if (csvContent == null || csvContent.trim().isEmpty()) {
                    throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.SFTP_FILE_NOT_FOUND.getCode(),
                                                 ResponseCode.SFTP_FILE_NOT_FOUND.getDefaultMessage(),
                                                 null).toString());
                }
            } catch (Exception e) {
                // 如果 SFTPServiceImpl 已拋出 ErrorResponseDto，直接傳遞
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("ErrorResponseDto(code=")) {
                    throw new RuntimeException(msg);
                }
                // 否則轉換為未知錯誤
                throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.UNKNOWN_ERROR.getCode(),
                                             ResponseCode.UNKNOWN_ERROR.getDefaultMessage() + ": " + e.getMessage(),
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
                repository.insert(entity);
            }

            logger.info("成功新增 {} 筆資料到資料庫", entities.size());

        } catch (Exception e) {
            String exceptionMessage = e.getMessage();
            if (exceptionMessage != null && exceptionMessage.startsWith("ErrorResponseDto(code=")) {
                throw new RuntimeException(exceptionMessage); // 直接傳遞已格式化的異常
            }
            // 其他未預期的異常轉為 UNKNOWN_ERROR
            logger.error("將 CSV 傳輸到資料庫時出錯: {}", e.getMessage());
            throw new RuntimeException(
                    new ErrorResponseDto(ResponseCode.UNKNOWN_ERROR.getCode(),
                                         ResponseCode.UNKNOWN_ERROR.getDefaultMessage() + ": " + (e.getMessage() != null ? e.getMessage() : "未知原因"),
                                         null).toString());
        }
    }
}