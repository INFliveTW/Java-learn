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

public class CSVToDataBaseServiceImplTest {

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
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        dto.setDEPARTMENT("IT");
        dto.setJOB_TITLE("Engineer");
        dto.setNAME("John");
        dto.setTEL("12345678");
        dto.setEMAIL("john@example.com");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
        doNothing().when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        assertDoesNotThrow(() -> service.processCsvToDatabase(request));
        System.out.println("COMPANY與時間戳記寫入SQL測試成功");
    }

    @Test
    void testProcessCsvToDatabase_Success_NullCompanyAndExcutetime() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        dto.setDEPARTMENT("IT");
        dto.setJOB_TITLE("Engineer");
        dto.setNAME("John");
        dto.setTEL("12345678");
        dto.setEMAIL("john@example.com");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
        doNothing().when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY(null);
        request.setEXCUTETIME(null);

        assertDoesNotThrow(() -> service.processCsvToDatabase(request));
        System.out.println("COMPANY 和 EXCUTETIME 為 null，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_InvalidExcutetimeFormat() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=UNKNOWN_001, message=發生未知錯誤: Text '2025-03-20 15:30' could not be parsed at index 16, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("EXCUTETIME 格式錯誤，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_SFTPConnectionFailure() {
        doThrow(new RuntimeException("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)"))
                .when(sftpService).readFileFromSFTP(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_SFTPConnectionFailure_NullMessage() {
        doThrow(new RuntimeException((String) null))
                .when(sftpService).readFileFromSFTP(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，異常訊息為 null，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_SFTPFileNotFound() {
        doThrow(new RuntimeException("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)"))
                .when(sftpService).readFileFromSFTP(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("資料夾沒有CSV檔案，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVEmpty() {
        doReturn("").when(sftpService).readFileFromSFTP(anyString());
        
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("SFTP 資料夾沒有CSV檔案，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVInvalidFormat() {
        doReturn("invalid content").when(sftpService).readFileFromSFTP(anyString());

        doThrow(new RuntimeException("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案, triggerTime=null)"))
                .when(csvParserUtil).parseCsv(anyString());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("檔案解析失敗，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVEmptyContent() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n").when(sftpService).readFileFromSFTP(anyString());
        doReturn(List.of()).when(csvParserUtil).parseCsv(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=CSV_002, message=CSV 檔案內容沒有任何資料，請確認文件內容, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("CSV 內容為空，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_ConnectionRefused() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        dto.setDEPARTMENT("IT");
        dto.setJOB_TITLE("Engineer");
        dto.setNAME("John");
        dto.setTEL("12345678");
        dto.setEMAIL("john@example.com");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("Connection refused: connect"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（connection refused），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_NullMessage() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException((String) null))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_002, message=資料庫寫入失敗，請檢查資料庫連線或權限, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（異常訊息為 null），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_NullCauseMessage() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        Exception cause = new Exception((String) null);
        doThrow(new RuntimeException("Some error", cause))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_002, message=資料庫寫入失敗，請檢查資料庫連線或權限, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（cause 訊息為 null），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_Timeout() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("Connection timeout"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（timeout），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CommunicationsLinkFailure() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("Communications link failure"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（communications link failure），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CannotConnect() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("Cannot connect to database"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（cannot connect），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_FailedToConnect() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("Failed to connect to database"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（failed to connect），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_TcpIpConnection() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("TCP/IP connection failed"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（tcp/ip connection），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CauseConnection() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        Exception cause = new Exception("Connection error in cause");
        doThrow(new RuntimeException("Some error", cause))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（cause 包含 connection），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CauseRefused() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        Exception cause = new Exception("Connection refused in cause");
        doThrow(new RuntimeException("Some error", cause))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（cause 包含 refused），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CauseTcpIpConnection() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        Exception cause = new Exception("TCP/IP connection failed in cause");
        doThrow(new RuntimeException("Some error", cause))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（cause 包含 tcp/ip connection），測試成功");
    }

    @Test
    void testProcessCsvToDatabase_DatabaseWriteFailure() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("Some other database error"))
                .when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_002, message=資料庫寫入失敗，請檢查資料庫連線或權限, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("資料庫寫入失敗，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_UnknownException() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        doThrow(new RuntimeException("Unknown error"))
                .when(csvParserUtil).parseCsv(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=UNKNOWN_001, message=發生未知錯誤: Unknown error, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("未知異常，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_UnknownException_NullMessage() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        doThrow(new RuntimeException((String) null))
                .when(csvParserUtil).parseCsv(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=UNKNOWN_001, message=發生未知錯誤: 未知原因, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("未知異常（異常訊息為 null），測試成功");
    }
}