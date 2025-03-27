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
                            new ErrorResponseDto(ResponseCode.SFTP_FILE_NOT_FOUND, null).toString());
                }
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (msg.contains("no such file") || msg.contains("file not found") || msg.contains("not exist")) {
                    throw new RuntimeException(
                            new ErrorResponseDto(ResponseCode.SFTP_FILE_NOT_FOUND, null).toString());
                }
                throw e;
            }

            logger.info("從 SFTP 讀取的 CSV 內容: {}", csvContent);

            List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);
            if (csvDtos.isEmpty()) {
                throw new RuntimeException(
                        new ErrorResponseDto(ResponseCode.CSV_EMPTY_ERROR, null).toString());
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
            ResponseCode errorCode;
            String errorMessage;
            String exceptionMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            switch (exceptionMessage) {
                case String msg when msg.contains("file not found") -> {
                    errorCode = ResponseCode.SFTP_FILE_NOT_FOUND;
                    errorMessage = ResponseCode.SFTP_FILE_NOT_FOUND.getDefaultMessage();
                }
                case String msg when msg.contains("sftp permission denied") -> {
                    errorCode = ResponseCode.SFTP_PERMISSION_DENIED;
                    errorMessage = ResponseCode.SFTP_PERMISSION_DENIED.getDefaultMessage();
                }
                case String msg when msg.contains("sftp error") && !msg.contains("file not found") -> {
                    errorCode = ResponseCode.SFTP_CONNECTION_ERROR;
                    errorMessage = ResponseCode.SFTP_CONNECTION_ERROR.getDefaultMessage();
                }
                case String msg when msg.contains("csv content is empty") -> {
                    errorCode = ResponseCode.CSV_EMPTY_ERROR;
                    errorMessage = ResponseCode.CSV_EMPTY_ERROR.getDefaultMessage();
                }
                case String msg when msg.contains("parse") -> {
                    errorCode = ResponseCode.CSV_PARSE_ERROR;
                    errorMessage = ResponseCode.CSV_PARSE_ERROR.getDefaultMessage();
                }
                default -> {
                    errorCode = ResponseCode.UNKNOWN_ERROR;
                    errorMessage = ResponseCode.UNKNOWN_ERROR.getDefaultMessage() + ": " + e.getMessage();
                }
            }

            logger.error("將 CSV 傳輸到資料庫時出錯: {}", errorMessage);
            throw new RuntimeException(new ErrorResponseDto(errorCode, errorMessage).toString(), e);
        }
    }
}