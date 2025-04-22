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
            try {
                csvContent = sftpService.readFileFromSFTP("/upload/employee_data.csv");
                if (csvContent == null) {
                    throw new RuntimeException(new ErrorResponseDto(ResponseCode.SFTP_FILE_NOT_FOUND.getCode(),
                            ResponseCode.SFTP_FILE_NOT_FOUND.getDefaultMessage(), null).toString());
                }
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg != null && msg.startsWith("ErrorResponseDto(code=")) {
                    throw new RuntimeException(msg);
                }
                throw new RuntimeException(new ErrorResponseDto(ResponseCode.SFTP_CONNECTION_ERROR.getCode(),
                        ResponseCode.SFTP_CONNECTION_ERROR.getDefaultMessage(), null).toString());
            }

            logger.info("從 SFTP 讀取的 CSV 內容: {}", csvContent);

            List<EmployeeDataCSVDto> csvDtos = csvParserUtil.parseCsv(csvContent);
            if (csvDtos.isEmpty()) {
                throw new RuntimeException(new ErrorResponseDto(ResponseCode.CSV_EMPTY_ERROR.getCode(),
                        ResponseCode.CSV_EMPTY_ERROR.getDefaultMessage(), null).toString());
            }

            String COMPANY = request.getCOMPANY() != null ? request.getCOMPANY() :
                    List.of("金控", "銀行", "證券").get(new Random().nextInt(3));
            String EXCUTETIMEStr = request.getEXCUTETIME() != null ? request.getEXCUTETIME() :
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String EXCUTETIME = LocalDateTime.parse(EXCUTETIMEStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            List<EmployeeDataEntity> entities = dataConverter.convertToEntities(csvDtos, COMPANY, EXCUTETIME);

            for (EmployeeDataEntity entity : entities) {
                logger.info("準備寫入 SQL: ID={}, DEPARTMENT={}, JOB_TITLE={}, NAME={}, TEL={}, EMAIL={}, COMPANY={}, EXCUTETIME={}",
                        entity.getID(), entity.getDEPARTMENT(), entity.getJOB_TITLE(), entity.getNAME(),
                        entity.getTEL(), entity.getEMAIL(), entity.getCOMPANY(), entity.getEXCUTETIME());
                try {
                    repository.insert(entity);
                } catch (Exception sqlEx) {
                    logger.error("資料庫操作失敗，異常訊息: {}", sqlEx.getMessage(), sqlEx);
                    String msg = sqlEx.getMessage() != null ? sqlEx.getMessage().toLowerCase() : "";
                    String causeMsg = sqlEx.getCause() != null && sqlEx.getCause().getMessage() != null
                            ? sqlEx.getCause().getMessage().toLowerCase() : "";
                    if (msg.contains("tcp/ip connection") || msg.contains("connection refused") || causeMsg.contains("refused")) {
                        throw new RuntimeException(new ErrorResponseDto(ResponseCode.SQL_CONNECTION_ERROR.getCode(),
                                ResponseCode.SQL_CONNECTION_ERROR.getDefaultMessage(), null).toString());
                    }
                    throw new RuntimeException(new ErrorResponseDto(ResponseCode.SQL_WRITE_ERROR.getCode(),
                            ResponseCode.SQL_WRITE_ERROR.getDefaultMessage(), null).toString());
                }
            }

            logger.info("成功新增 {} 筆資料到資料庫", entities.size());
            return true;
        } catch (Exception e) {
            logger.error("將 CSV 傳輸到資料庫時出錯: {}", e.getMessage(), e);
            throw new RuntimeException(new ErrorResponseDto(ResponseCode.UNKNOWN_ERROR.getCode(),
                    ResponseCode.UNKNOWN_ERROR.getDefaultMessage() + ": " +
                            (e.getMessage() != null ? e.getMessage() : "未知原因"), null).toString());
        }
    }
}
