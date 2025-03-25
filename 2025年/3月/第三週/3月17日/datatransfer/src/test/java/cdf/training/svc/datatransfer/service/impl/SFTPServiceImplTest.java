package cdf.training.svc.datatransfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import cdf.training.svc.datatransfer.config.SFTPConfig;

class SFTPServiceImplTest {
    @Mock
    private SFTPConfig sftpConfig;

    @InjectMocks
    private SFTPServiceImpl sftpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReadFileFromSFTP_Success() {
        when(sftpConfig.getHost()).thenReturn("localhost");
        when(sftpConfig.getPort()).thenReturn(2222);
        when(sftpConfig.getUsername()).thenReturn("sa");
        when(sftpConfig.getPassword()).thenReturn("1QAZ2WSX3EDc4@");

        assertDoesNotThrow(() -> sftpService.readFileFromSFTP("/upload/employee_data.csv"));
        System.out.println("測試成功"); // 測試通過時顯示
    }

    @Test
    void testReadFileFromSFTP_ConnectionFailure() {
        when(sftpConfig.getHost()).thenReturn("invalid_host");
        when(sftpConfig.getPort()).thenReturn(2222);
        when(sftpConfig.getUsername()).thenReturn("sa");
        when(sftpConfig.getPassword()).thenReturn("1QAZ2WSX3EDc4@");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpService.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertTrue(exception.getMessage().contains("SFTP error"));
        System.out.println("測試成功"); // 測試通過時顯示
    }

    @Test
    void testReadFileFromSFTP_FileNotFound() {
        when(sftpConfig.getHost()).thenReturn("localhost");
        when(sftpConfig.getPort()).thenReturn(2222);
        when(sftpConfig.getUsername()).thenReturn("sa");
        when(sftpConfig.getPassword()).thenReturn("1QAZ2WSX3EDc4@");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpService.readFileFromSFTP("/upload/non_existent_file.csv");
        });
        assertTrue(exception.getMessage().contains("SFTP error"));
        System.out.println("測試成功"); // 測試通過時顯示
    }
}