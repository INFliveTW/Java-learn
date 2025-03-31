package cdf.training.svc.datatransfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        when(sftpConfig.getHost()).thenReturn("localhost");
        when(sftpConfig.getPort()).thenReturn(2222);
        when(sftpConfig.getUsername()).thenReturn("sa");
        when(sftpConfig.getPassword()).thenReturn("1QAZ2WSX3EDc4@");
    }

    @Test
    void testReadFileFromSFTP_Success() {
        // 由於無法模擬 JSch 連線，這裡假設連線成功並返回模擬數據
        // 實際測試可能需要使用 Testcontainers 或模擬 SFTP 伺服器
        // 這裡僅驗證方法不拋出異常
        assertDoesNotThrow(() -> sftpService.readFileFromSFTP("/upload/employee_data.csv"));
        System.out.println("連接SFTP，測試成功");
    }

    @Test
    void testReadFileFromSFTP_ConnectionFailure() {
        // 模擬無效的主機名，導致連線失敗
        when(sftpConfig.getHost()).thenReturn("invalid-host");
        when(sftpConfig.getPort()).thenReturn(2222);
        when(sftpConfig.getUsername()).thenReturn("sa");
        when(sftpConfig.getPassword()).thenReturn("1QAZ2WSX3EDc4@");
    
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpService.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，測試成功");
    }

    @Test
    void testReadFileFromSFTP_FileNotFound() {
        // 模擬檔案不存在
        // 由於 JSch 會拋出異常，這裡直接測試異常處理邏輯
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpService.readFileFromSFTP("/upload/non_existent_file.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("找不到SFTP檔案，測試成功");
    }
}