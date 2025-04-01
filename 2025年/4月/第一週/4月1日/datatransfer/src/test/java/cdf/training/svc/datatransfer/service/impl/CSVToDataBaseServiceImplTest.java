package cdf.training.svc.datatransfer.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;
import cdf.training.svc.datatransfer.repository.EmployeeDataRepository;
import cdf.training.svc.datatransfer.util.CSVParserUtil;

class CSVToDataBaseServiceImplTest {
    @Mock
    private SFTPServiceImpl sftpService;

    @Mock
    private CSVParserUtil csvParserUtil;

    @Mock
    private DataConverterImpl dataConverter;

    @Mock
    private EmployeeDataRepository repository;

    @InjectMocks
    private CSVToDataBaseServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessCsvToDatabase_Success() {
        // Arrange
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com");
        when(csvParserUtil.parseCsv(anyString())).thenReturn(List.of(new EmployeeDataCSVDto()));
        when(dataConverter.convertToEntities(anyList(), anyString(), any())).thenReturn(List.of(new EmployeeDataEntity()));
        doNothing().when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        // Act & Assert
        assertDoesNotThrow(() -> service.processCsvToDatabase(request));
        System.out.println("COMPANY與時間戳記寫入SQL測試成功");
    }

    @Test
    void testProcessCsvToDatabase_SFTPConnectionFailure() {
        // Arrange
        when(sftpService.readFileFromSFTP(anyString()))
                .thenThrow(new RuntimeException("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)"));

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_SFTPFileNotFound() {
        // Arrange
        when(sftpService.readFileFromSFTP(anyString()))
                .thenThrow(new RuntimeException("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)"));

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("資料夾沒有CSV檔案，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVEmpty() {
        // Arrange
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("");
        
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("SFTP 資料夾沒有CSV檔案，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVInvalidFormat() {
        // Arrange
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("invalid content");
        when(csvParserUtil.parseCsv(anyString()))
                .thenThrow(new RuntimeException("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案, triggerTime=null)"));
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("檔案解析失敗，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure() {
        // Arrange
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com");
        when(csvParserUtil.parseCsv(anyString())).thenReturn(List.of(new EmployeeDataCSVDto()));
        when(dataConverter.convertToEntities(anyList(), anyString(), any())).thenReturn(List.of(new EmployeeDataEntity()));
        // 模擬資料庫連線失敗，拋出包含 "connection refused" 的異常訊息
        doThrow(new RuntimeException("Connection refused: connect"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseWriteFailure() {
        // Arrange
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com");
        when(csvParserUtil.parseCsv(anyString())).thenReturn(List.of(new EmployeeDataCSVDto()));
        when(dataConverter.convertToEntities(anyList(), anyString(), any())).thenReturn(List.of(new EmployeeDataEntity()));
        doThrow(new RuntimeException("ErrorResponseDto(code=SQL_002, message=資料庫寫入失敗，請檢查資料庫連線或權限, triggerTime=null)"))
                .when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_002, message=資料庫寫入失敗，請檢查資料庫連線或權限, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("資料庫寫入失敗，測試成功");
    }
}