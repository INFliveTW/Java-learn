package cdf.training.svc.datatransfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import cdf.training.svc.datatransfer.config.SFTPConfig;

class SFTPServiceImplTest {

    @Mock
    private SFTPConfig sftpConfig;

    @Mock  // 直接模擬，而不是使用 @Spy
    private SFTPServiceImpl sftpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadFileFromSFTP_Success() {
        when(sftpService.readFileFromSFTP("/upload/employee_data.csv")).thenReturn("mocked CSV content");

        assertDoesNotThrow(() -> sftpService.readFileFromSFTP("/upload/employee_data.csv"));
        System.out.println("連接SFTP，測試成功");
    }

    @Test
    void testReadFileFromSFTP_ConnectionFailure() {
        when(sftpService.readFileFromSFTP("/upload/employee_data.csv"))
            .thenThrow(new RuntimeException("SFTP error: Connection failed"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpService.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertTrue(exception.getMessage().contains("SFTP error"));
        System.out.println("無法連接到 SFTP 伺服器，測試成功");
    }

    @Test
    void testReadFileFromSFTP_FileNotFound() {
        when(sftpService.readFileFromSFTP("/upload/non_existent_file.csv"))
            .thenThrow(new RuntimeException("SFTP error: No such file"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpService.readFileFromSFTP("/upload/non_existent_file.csv");
        });
        assertTrue(exception.getMessage().contains("SFTP error"));
        System.out.println("找不到SFTP，測試成功");
    }
}