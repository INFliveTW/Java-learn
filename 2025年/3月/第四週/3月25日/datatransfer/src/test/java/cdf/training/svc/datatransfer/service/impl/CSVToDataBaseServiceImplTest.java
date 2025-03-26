package cdf.training.svc.datatransfer.service.impl;

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
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("ID,DEPARTMENT\n1,IT");
        when(csvParserUtil.parseCsv(anyString())).thenReturn(List.of(new EmployeeDataCSVDto()));
        when(dataConverter.convertToEntities(anyList(), anyString(), any())).thenReturn(List.of(new EmployeeDataEntity()));
        doNothing().when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        assertDoesNotThrow(() -> service.processCsvToDatabase(request));
        System.out.println("COMPANY與時間戳記寫入SQL測試成功"); // 測試通過時顯示
    }

    @Test
    void testProcessCsvToDatabase_SFTPConnectionFailure() {
        when(sftpService.readFileFromSFTP(anyString())).thenThrow(new RuntimeException("SFTP error"));
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(errorCode=SFTP_CONNECTION_ERROR, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，測試成功"); // 測試通過時顯示
    }

    @Test
    void testProcessCsvToDatabase_SFTPFileNotFound() {
        when(sftpService.readFileFromSFTP(anyString())).thenThrow(new RuntimeException("SFTP error: file not found"));
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(errorCode=SFTP_FILE_NOT_FOUND, message=SFTP 資料夾沒有CSV檔案，請確認SFTP)", 
                     exception.getMessage());
        System.out.println("資料夾沒有CSV檔案，測試成功"); // 測試通過時顯示
    }

    @Test
    void testProcessCsvToDatabase_CSVEmpty() {
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("");
        
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(errorCode=UNKNOWN_ERROR, message=發生未知錯誤：SFTP 資料夾沒有CSV檔案，請確認SFTP)", 
                     exception.getMessage());
        System.out.println("發生未知錯誤，測試成功"); // 測試通過時顯示
    }

    @Test
    void testProcessCsvToDatabase_CSVInvalidFormat() {
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("invalid content");
        when(csvParserUtil.parseCsv(anyString())).thenThrow(new IllegalArgumentException("CSV parse error"));
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(errorCode=CSV_PARSE_ERROR, message=CSV 檔案解析失敗，請確認檔案格式正確)", 
                     exception.getMessage());
        System.out.println("檔案解析失敗，測試成功"); // 測試通過時顯示
    }

    @Test
    void testProcessCsvToDatabase_DatabaseError() {
        when(sftpService.readFileFromSFTP(anyString())).thenReturn("ID,DEPARTMENT\n1,IT");
        when(csvParserUtil.parseCsv(anyString())).thenReturn(List.of(new EmployeeDataCSVDto()));
        when(dataConverter.convertToEntities(anyList(), anyString(), any())).thenReturn(List.of(new EmployeeDataEntity()));
        doThrow(new RuntimeException("database error")).when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(errorCode=DATABASE_ERROR, message=資料庫寫入失敗，請檢查資料庫連線或權限)", 
                     exception.getMessage());
        System.out.println("資料庫寫入失敗，測試成功"); // 測試通過時顯示
    }
}