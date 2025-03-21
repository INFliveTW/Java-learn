package cdf.training.svc.datatransfer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import cdf.training.svc.datatransfer.dto.CSVToDataBaseRequestDto;
import cdf.training.svc.datatransfer.dto.EmployeeDataCSVDto;
import cdf.training.svc.datatransfer.entity.EmployeeDataEntity;
import cdf.training.svc.datatransfer.repository.EmployeeDataRepository;
import cdf.training.svc.datatransfer.service.impl.CSVToDataBaseServiceImpl;
import cdf.training.svc.datatransfer.service.impl.DataConverterImpl;
import cdf.training.svc.datatransfer.service.impl.SFTPServiceImpl;
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
        // 初始化 Mockito 模擬物件
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessCsvToDatabase_Success() {
        // Arrange - 準備測試數據
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        request.setCOMPANY("銀行");
        request.setEXCUTETIME("2025-03-20 12:00:00");

        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,123456,john@example.com";
        EmployeeDataCSVDto csvDto = new EmployeeDataCSVDto();
        csvDto.setID("1");
        csvDto.setDEPARTMENT("IT");
        csvDto.setJOB_TITLE("Engineer");
        csvDto.setNAME("John");
        csvDto.setTEL("123456");
        csvDto.setEMAIL("john@example.com");

        EmployeeDataEntity entity = new EmployeeDataEntity();
        entity.setID("1");
        entity.setDEPARTMENT("IT");
        entity.setJOB_TITLE("Engineer");
        entity.setNAME("John");
        entity.setTEL("123456");
        entity.setEMAIL("john@example.com");
        entity.setCOMPANY("銀行");
        entity.setEXCUTETIME(LocalDateTime.parse("2025-03-20 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        when(sftpService.readFileFromSFTP(anyString())).thenReturn(csvContent);
        when(csvParserUtil.parseCsv(csvContent)).thenReturn(List.of(csvDto));
        when(dataConverter.convertToEntities(any(List.class), anyString(), any(LocalDateTime.class))).thenReturn(List.of(entity));

        // Act - 執行測試
        service.processCsvToDatabase(request);

        // Assert - 驗證結果
        verify(repository, times(1)).saveAll(List.of(entity));
    }

    @Test
    void testProcessCsvToDatabase_SFTPError() {
        // Arrange
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto();
        when(sftpService.readFileFromSFTP(anyString())).thenThrow(new RuntimeException("SFTP connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.processCsvToDatabase(request));
        assertEquals("無法連接到 SFTP 伺服器，請檢查配置或網路狀態", exception.getMessage());
    }

    @Test
    void testProcessCsvToDatabase_EmptyRequest() {
        // Arrange
        CSVToDataBaseRequestDto request = new CSVToDataBaseRequestDto(); // COMPANY 和 EXCUTETIME 為 null

        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,123456,john@example.com";
        EmployeeDataCSVDto csvDto = new EmployeeDataCSVDto();
        csvDto.setID("1");

        when(sftpService.readFileFromSFTP(anyString())).thenReturn(csvContent);
        when(csvParserUtil.parseCsv(csvContent)).thenReturn(List.of(csvDto));
        when(dataConverter.convertToEntities(any(List.class), anyString(), any(LocalDateTime.class))).thenReturn(List.of(new EmployeeDataEntity()));

        // Act
        service.processCsvToDatabase(request);

        // Assert
        verify(repository, times(1)).saveAll(any(List.class));
    }
}