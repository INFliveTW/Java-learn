package cdf.training.svc.datatransfer.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
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

        EmployeeDataEntity entity = new EmployeeDataEntity();
        entity.setID("1");
        entity.setDEPARTMENT("IT");
        entity.setJOB_TITLE("Engineer");
        entity.setNAME("John");
        entity.setTEL("12345678");
        entity.setEMAIL("john@example.com");
        entity.setCOMPANY("金控");
        entity.setEXCUTETIME(LocalDateTime.parse("2025-03-20 15:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        doReturn(List.of(entity))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
        doNothing().when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        boolean result = service.processCsvToDatabase(request);
        assertTrue(result, "Should return true on success");
        System.out.println("COMPANY與時間戳記寫入SQL測試成功");
    }

    @Test
    void testProcessCsvToDatabase_Success_MultipleEntities() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com\n2,HR,Manager,Jane,87654321,jane@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto1 = new EmployeeDataCSVDto();
        dto1.setID("1");
        dto1.setDEPARTMENT("IT");
        dto1.setJOB_TITLE("Engineer");
        dto1.setNAME("John");
        dto1.setTEL("12345678");
        dto1.setEMAIL("john@example.com");

        EmployeeDataCSVDto dto2 = new EmployeeDataCSVDto();
        dto2.setID("2");
        dto2.setDEPARTMENT("HR");
        dto2.setJOB_TITLE("Manager");
        dto2.setNAME("Jane");
        dto2.setTEL("87654321");
        dto2.setEMAIL("jane@example.com");

        doReturn(List.of(dto1, dto2)).when(csvParserUtil).parseCsv(anyString());

        EmployeeDataEntity entity1 = new EmployeeDataEntity();
        entity1.setID("1");
        entity1.setDEPARTMENT("IT");
        entity1.setJOB_TITLE("Engineer");
        entity1.setNAME("John");
        entity1.setTEL("12345678");
        entity1.setEMAIL("john@example.com");
        entity1.setCOMPANY("金控");
        entity1.setEXCUTETIME(LocalDateTime.parse("2025-03-20 15:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        EmployeeDataEntity entity2 = new EmployeeDataEntity();
        entity2.setID("2");
        entity2.setDEPARTMENT("HR");
        entity2.setJOB_TITLE("Manager");
        entity2.setNAME("Jane");
        entity2.setTEL("87654321");
        entity2.setEMAIL("jane@example.com");
        entity2.setCOMPANY("金控");
        entity2.setEXCUTETIME(LocalDateTime.parse("2025-03-20 15:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        doReturn(List.of(entity1, entity2))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
        doNothing().when(repository).insert(any());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");

        boolean result = service.processCsvToDatabase(request);
        assertTrue(result, "Should return true on success");
        System.out.println("多筆資料寫入SQL測試成功");
    }

    @Test
    void testProcessCsvToDatabase_Success_NullCompanyAndExcutetime_Random0() {
        try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
                (mock, context) -> when(mock.nextInt(3)).thenReturn(0))) { // 模擬 Random 返回 0，對應 "金控"

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

            EmployeeDataEntity entity = new EmployeeDataEntity();
            entity.setID("1");
            entity.setDEPARTMENT("IT");
            entity.setJOB_TITLE("Engineer");
            entity.setNAME("John");
            entity.setTEL("12345678");
            entity.setEMAIL("john@example.com");
            doReturn(List.of(entity))
                    .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
            doNothing().when(repository).insert(any());

            CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
            request.setCOMPANY(null);
            request.setEXCUTETIME(null);

            boolean result = service.processCsvToDatabase(request);
            assertTrue(result, "Should return true on success");
            System.out.println("COMPANY 和 EXCUTETIME 為 null，Random 返回 0 (金控)，測試成功");
        }
    }

    @Test
    void testProcessCsvToDatabase_Success_NullCompanyAndExcutetime_Random1() {
        try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
                (mock, context) -> when(mock.nextInt(3)).thenReturn(1))) { // 模擬 Random 返回 1，對應 "銀行"

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

            EmployeeDataEntity entity = new EmployeeDataEntity();
            entity.setID("1");
            entity.setDEPARTMENT("IT");
            entity.setJOB_TITLE("Engineer");
            entity.setNAME("John");
            entity.setTEL("12345678");
            entity.setEMAIL("john@example.com");
            doReturn(List.of(entity))
                    .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
            doNothing().when(repository).insert(any());

            CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
            request.setCOMPANY(null);
            request.setEXCUTETIME(null);

            boolean result = service.processCsvToDatabase(request);
            assertTrue(result, "Should return true on success");
            System.out.println("COMPANY 和 EXCUTETIME 為 null，Random 返回 1 (銀行)，測試成功");
        }
    }

    @Test
    void testProcessCsvToDatabase_Success_NullCompanyAndExcutetime_Random2() {
        try (MockedConstruction<Random> mocked = mockConstruction(Random.class,
                (mock, context) -> when(mock.nextInt(3)).thenReturn(2))) { // 模擬 Random 返回 2，對應 "證券"

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

            EmployeeDataEntity entity = new EmployeeDataEntity();
            entity.setID("1");
            entity.setDEPARTMENT("IT");
            entity.setJOB_TITLE("Engineer");
            entity.setNAME("John");
            entity.setTEL("12345678");
            entity.setEMAIL("john@example.com");
            doReturn(List.of(entity))
                    .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
            doNothing().when(repository).insert(any());

            CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
            request.setCOMPANY(null);
            request.setEXCUTETIME(null);

            boolean result = service.processCsvToDatabase(request);
            assertTrue(result, "Should return true on success");
            System.out.println("COMPANY 和 EXCUTETIME 為 null，Random 返回 2 (證券)，測試成功");
        }
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
        String errorMessage = "ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)";
        doThrow(new RuntimeException(errorMessage))
                .when(sftpService).readFileFromSFTP(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals(errorMessage, exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，異常訊息以 ErrorResponseDto 開頭，測試成功");
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
        String errorMessage = "ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)";
        doThrow(new RuntimeException(errorMessage))
                .when(sftpService).readFileFromSFTP(anyString());

        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals(errorMessage, exception.getMessage());
        System.out.println("資料夾沒有CSV檔案，測試成功");
    }

    @Test
    void testProcessCsvToDatabase_CSVContentNull() {
        doReturn(null).when(sftpService).readFileFromSFTP(anyString());
        
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("SFTP 讀取的 csvContent 為 null，測試成功");
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

        String errorMessage = "ErrorResponseDto(code=CSV_003, message=CSV檔案，欄位缺少，請確認檔案, triggerTime=null)";
        doThrow(new RuntimeException(errorMessage))
                .when(csvParserUtil).parseCsv(anyString());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals(errorMessage, exception.getMessage());
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
    void testProcessCsvToDatabase_DatabaseConnectionFailure_OnlyRefused() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("refused"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（僅包含 refused），測試成功");
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
    void testProcessCsvToDatabase_DatabaseConnectionFailure_NullCause() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("Some error"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_002, message=資料庫寫入失敗，請檢查資料庫連線或權限, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（cause 為 null），測試成功");
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
    void testProcessCsvToDatabase_DatabaseConnectionFailure_OnlyTimeout() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        doThrow(new RuntimeException("timeout"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（僅包含 timeout），測試成功");
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

        doThrow(new RuntimeException("communications link failure"))
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

        doThrow(new RuntimeException("cannot connect to database"))
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
    //111
    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_OnlyTcpIpConnection() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());
    
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());
    
        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
    
        // 修正異常訊息，確保包含 "tcp/ip connection"
        doThrow(new RuntimeException("error due to tcp/ip connection failure"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（僅包含 tcp/ip connection），測試成功");
    }
    //111
    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_ConnectionRefused() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());
    
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());
    
        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
    
        // 修正異常訊息，確保包含 "connection refused: connect"
        doThrow(new RuntimeException("error due to connection refused: connect issue"))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（僅包含 connection refused: connect），測試成功");
    }
    //111
    @Test
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CauseOnlyTcpIpConnection() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());
    
        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());
    
        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));
    
        // 修正 cause 訊息，確保包含 "tcp/ip connection"
        Exception cause = new Exception("failed due to tcp/ip connection issue in cause");
        doThrow(new RuntimeException("SomeErrorWithoutKeywords", cause))
                .when(repository).insert(any());
    
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("金控");
        request.setEXCUTETIME("2025-03-20 15:30:45");
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.processCsvToDatabase(request);
        });
        assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連線到資料庫（cause 僅包含 tcp/ip connection），測試成功");
    }

    //111
    @Test
void testProcessCsvToDatabase_DatabaseConnectionFailure_SimpleConnection() {
    doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
            .when(sftpService).readFileFromSFTP(anyString());

    EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
    dto.setID("1");
    doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

    doReturn(List.of(new EmployeeDataEntity()))
            .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

    // 設置異常訊息為 "simple connection error"，只觸發 msg.contains("connection")
    doThrow(new RuntimeException("simple connection error"))
            .when(repository).insert(any());

    CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
    request.setCOMPANY("金控");
    request.setEXCUTETIME("2025-03-20 15:30:45");

    Exception exception = assertThrows(RuntimeException.class, () -> {
        service.processCsvToDatabase(request);
    });
    assertEquals("ErrorResponseDto(code=SQL_001, message=無法連線到資料庫，請檢查配置或網路狀態, triggerTime=null)", 
                 exception.getMessage());
    System.out.println("無法連線到資料庫（僅包含 connection），測試成功");
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

        doThrow(new RuntimeException("failed to connect to database"))
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
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CauseConnection() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        Exception cause = new Exception("connection error in cause");
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
    void testProcessCsvToDatabase_DatabaseConnectionFailure_CauseOnlyRefused() {
        doReturn("ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com")
                .when(sftpService).readFileFromSFTP(anyString());

        EmployeeDataCSVDto dto = new EmployeeDataCSVDto();
        dto.setID("1");
        doReturn(List.of(dto)).when(csvParserUtil).parseCsv(anyString());

        doReturn(List.of(new EmployeeDataEntity()))
                .when(dataConverter).convertToEntities(anyList(), anyString(), any(LocalDateTime.class));

        Exception cause = new Exception("refused");
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
        System.out.println("無法連線到資料庫（cause 僅包含 refused），測試成功");
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
void testProcessCsvToDatabase_SFTPConnectionFailure_TcpIpConnection() {
    String errorMessage = "tcp/ip connection error";
    doThrow(new RuntimeException(errorMessage))
            .when(sftpService).readFileFromSFTP(anyString());

    CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

    Exception exception = assertThrows(RuntimeException.class, () -> {
        service.processCsvToDatabase(request);
    });
    assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                 exception.getMessage());
    System.out.println("無法連接到 SFTP 伺服器（異常訊息包含 tcp/ip connection），測試成功");
}

@Test
void testProcessCsvToDatabase_SFTPConnectionFailure_ConnectionRefused() {
    String errorMessage = "connection refused: connect";
    doThrow(new RuntimeException(errorMessage))
            .when(sftpService).readFileFromSFTP(anyString());

    CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

    Exception exception = assertThrows(RuntimeException.class, () -> {
        service.processCsvToDatabase(request);
    });
    assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                 exception.getMessage());
    System.out.println("無法連接到 SFTP 伺服器（異常訊息包含 connection refused: connect），測試成功");
}

@Test
void testProcessCsvToDatabase_SFTPConnectionFailure_CauseTcpIpConnection() {
    Exception cause = new Exception("tcp/ip connection in cause");
    doThrow(new RuntimeException("Some error", cause))
            .when(sftpService).readFileFromSFTP(anyString());

    CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();

    Exception exception = assertThrows(RuntimeException.class, () -> {
        service.processCsvToDatabase(request);
    });
    assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                 exception.getMessage());
    System.out.println("無法連接到 SFTP 伺服器（cause 包含 tcp/ip connection），測試成功");
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