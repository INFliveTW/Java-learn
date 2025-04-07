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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        // 模擬 CSVParserUtil 解析 CSV 內容
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
        doNothing().when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        // 測試資料寫入成功
        assertDoesNotThrow(() -> service.processCsvToDatabase(request));
        System.out.println("COMPANY與時間戳記寫入SQL測試成功");
    }

    @Test
    void testProcessCsvToDatabase_SFTPConnectionFailure() {
        // 模擬 SFTP 連線失敗
        doThrow(new RuntimeException("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)"))
                .when(sftpService).readFileFromSFTP(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // 測試無法連接到 SFTP 伺服器
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_SFTPFileNotFound() {
        // 模擬 SFTP 檔案不存在
        doThrow(new RuntimeException("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)"))
                .when(sftpService).readFileFromSFTP(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // 測試 SFTP 資料夾沒有CSV檔案
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("資料夾沒有CSV檔案，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVEmpty() {
        // 模擬 SFTP 返回空內容
        doReturn("").when(sftpService).readFileFromSFTP(anyString());
        
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // 測試 SFTP 資料夾沒有CSV檔案
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("SFTP 資料夾沒有CSV檔案，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVInvalidFormat() {
        // 模擬 SFTP 返回無效的 CSV 內容
        doReturn("invalid content").when(sftpService).readFileFromSFTP(anyString());

        // 模擬 CSVParserUtil 拋出 CSV 格式錯誤
        doThrow(new RuntimeException("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案, triggerTime=null)"))
                .when(csvParserUtil).parseCsv(anyString());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        // 測試 CSV 檔案解析失敗
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
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        // 模擬 CSVParserUtil 解析成功
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

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
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        // 模擬 CSVParserUtil 解析成功
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

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