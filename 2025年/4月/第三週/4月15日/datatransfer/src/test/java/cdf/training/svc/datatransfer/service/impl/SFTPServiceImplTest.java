package cdf.training.svc.datatransfer.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cdf.training.svc.datatransfer.config.SFTPConfig;

public class SFTPServiceImplTest {

    @Mock
    private SFTPConfig sftpConfig;

    @Mock
    private JSch jsch;

    @Mock
    private Session session;

    @Mock
    private ChannelSftp channel;

    @InjectMocks
    private SFTPServiceImpl sftpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 配置模擬的 SFTPConfig
        when(sftpConfig.getHost()).thenReturn("localhost");
        when(sftpConfig.getPort()).thenReturn(22);
        when(sftpConfig.getUsername()).thenReturn("sa");
        when(sftpConfig.getPassword()).thenReturn("1QAZ2WSX3EDc4@");
        // 每次測試前重置模擬物件，避免狀態干擾
        Mockito.reset(jsch, session, channel);
    }

    @Test
    void testCreateJSch_Success() throws Exception {
        JSch result = sftpService.createJSch();
        assertNotNull(result);
        System.out.println("成功創建 JSch，測試成功");
    }

    @Test
    void testReadFileFromSFTP_Success() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        JSch mockJSch = mock(JSch.class);
        doReturn(mockJSch).when(sftpServiceSpy).createJSch();
        Session mockSession = mock(Session.class);
        doReturn(mockSession).when(mockJSch).getSession("sa", "localhost", 22);
        doReturn(mockSession).when(sftpServiceSpy).createSession(mockJSch);
        ChannelSftp mockChannel = mock(ChannelSftp.class);
        doReturn(mockChannel).when(sftpServiceSpy).createChannel(mockSession);
        doNothing().when(mockSession).connect();
        doNothing().when(mockChannel).connect();
        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        doReturn(inputStream).when(mockChannel).get("/upload/employee_data.csv");
        doNothing().when(mockChannel).disconnect();
        doNothing().when(mockSession).disconnect();
        String result = sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        assertEquals(csvContent, result);
        System.out.println("成功讀取SFTP檔案，測試成功");
    }

    @Test
    void testReadFileFromSFTP_ConnectionFailure_SessionConnect() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        JSch realJSch = sftpServiceSpy.createJSch();
        Session mockSession = mock(Session.class);
        doReturn(mockSession).when(sftpServiceSpy).createSession(realJSch);
        doThrow(new JSchException("Connection failed")).when(mockSession).connect();
        doNothing().when(mockSession).disconnect();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器（session.connect），測試成功");
    }

    @Test
    void testReadFileFromSFTP_ConnectionFailure_ChannelConnect() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        JSch realJSch = sftpServiceSpy.createJSch();
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);
        doReturn(mockSession).when(sftpServiceSpy).createSession(realJSch);
        doReturn(mockChannel).when(sftpServiceSpy).createChannel(mockSession);
        doNothing().when(mockSession).connect();
        doThrow(new JSchException("Channel connection failed")).when(mockChannel).connect();
        doNothing().when(mockChannel).disconnect();
        doNothing().when(mockSession).disconnect();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器（channel.connect），測試成功");
    }

    @Test
    void testReadFileFromSFTP_FileNotFound_NoSuchFile() throws Exception {
        // 模擬 JSch、Session 和 ChannelSftp
        JSch mockJSch = mock(JSch.class);
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);
    
        // 模擬 sftpServiceSpy 的內部方法
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(mockJSch).when(sftpServiceSpy).createJSch();
        doReturn(mockSession).when(sftpServiceSpy).createSession(mockJSch);
        doReturn(mockChannel).when(sftpServiceSpy).createChannel(mockSession);
    
        // 模擬 connect 方法
        doNothing().when(mockSession).connect();
        doNothing().when(mockChannel).connect();
    
        // 模擬 channel.get 拋出 SftpException(id=2)
        SftpException sftpException = new SftpException(2, "no such file");
        doThrow(sftpException).when(mockChannel).get("/upload/non_existent_file.csv");
    
        // 模擬 disconnect 方法
        doNothing().when(mockChannel).disconnect();
        doNothing().when(mockSession).disconnect();
    
        // 執行測試
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/non_existent_file.csv");
        });
    
        // 驗證異常訊息
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("找不到SFTP檔案（no such file），測試成功");
    }

    @Test
    void testCreateChannel_JSchException() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        Session mockSession = mock(Session.class);
        doThrow(new JSchException("Failed to open channel")).when(mockSession).openChannel("sftp");
    
        Exception exception = assertThrows(JSchException.class, () -> {
            sftpServiceSpy.createChannel(mockSession);
        });
        assertEquals("Failed to open channel", exception.getMessage());
        System.out.println("createChannel 方法拋出 JSchException，測試成功");
    }

    @Test
    void testGetFileInputStream_Success() throws Exception {
        ChannelSftp mockChannel = mock(ChannelSftp.class);
        InputStream mockInputStream = new ByteArrayInputStream("test content".getBytes());
        when(mockChannel.get("/upload/employee_data.csv")).thenReturn(mockInputStream);
    
        InputStream result = sftpService.getFileInputStream(mockChannel, "/upload/employee_data.csv");
        assertNotNull(result);
        assertEquals("test content", new String(result.readAllBytes()));
        System.out.println("getFileInputStream 方法成功返回 InputStream，測試成功");
    }

    @Test
    void testReadFileFromSFTP_PermissionDenied() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        JSch mockJSch = mock(JSch.class);
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);
    
        doReturn(mockJSch).when(sftpServiceSpy).createJSch();
        doReturn(mockSession).when(sftpServiceSpy).createSession(mockJSch);
        doReturn(mockChannel).when(sftpServiceSpy).createChannel(mockSession);
        doNothing().when(mockSession).connect();
        doNothing().when(mockChannel).connect();
        doThrow(new SftpException(3, "Permission denied")).when(mockChannel).get(anyString());
        doNothing().when(mockChannel).disconnect();
        doNothing().when(mockSession).disconnect();
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertTrue(exception.getMessage().contains("SFTP_001")); // 修正為檢查 SFTP_001
        System.out.println("權限被拒絕 (id=3)，測試成功");
    }

    @Test
    void testReadFileFromSFTP_OtherSftpException() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        JSch mockJSch = mock(JSch.class);
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);
    
        doReturn(mockJSch).when(sftpServiceSpy).createJSch();
        doReturn(mockSession).when(sftpServiceSpy).createSession(mockJSch);
        doReturn(mockChannel).when(sftpServiceSpy).createChannel(mockSession);
        doThrow(new SftpException(4, "Other error")).when(mockChannel).get(anyString());
    
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertTrue(exception.getMessage().contains("SFTP_003"));
        System.out.println("SftpException 的其他 id，測試成功");
    }

    @Test
    void testReadFileFromSFTP_Success_DisconnectCalled() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        JSch mockJSch = mock(JSch.class);
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);
        InputStream mockInputStream = new ByteArrayInputStream("test content".getBytes());
    
        // 模擬 createJSch, createSession, createChannel
        doReturn(mockJSch).when(sftpServiceSpy).createJSch();
        doReturn(mockSession).when(sftpServiceSpy).createSession(mockJSch);
        doReturn(mockChannel).when(sftpServiceSpy).createChannel(mockSession);
    
        // 模擬 connect 和 get 方法
        doNothing().when(mockSession).connect();
        doNothing().when(mockChannel).connect();
        doReturn(mockInputStream).when(mockChannel).get(anyString());
    
        // 模擬 disconnect 方法
        doNothing().when(mockChannel).disconnect();
        doNothing().when(mockSession).disconnect();
    
        // 執行 readFileFromSFTP
        String result = sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        assertEquals("test content", result);
    
        // 驗證 session.disconnect() 被調用
        verify(mockSession, times(1)).disconnect();
        System.out.println("session.disconnect() 被調用，測試成功");
    }

    @Test
    void testCreateChannel_Success() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        Session mockSession = mock(Session.class);
        ChannelSftp mockChannel = mock(ChannelSftp.class);

    // 模擬 session.openChannel 成功返回 ChannelSftp
        doReturn(mockChannel).when(mockSession).openChannel("sftp");

    // 執行 createChannel 方法
        ChannelSftp result = sftpServiceSpy.createChannel(mockSession);

    // 驗證結果
        assertNotNull(result);
        assertEquals(mockChannel, result);
        System.out.println("createChannel 方法成功創建 ChannelSftp，測試成功");
    }

    @Test
    void testReadFileFromSFTP_SessionCreationFailure() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        JSch mockJSch = mock(JSch.class);
    
        // 模擬 createJSch 返回 mockJSch
        doReturn(mockJSch).when(sftpServiceSpy).createJSch();
    
        // 模擬 createSession 拋出 JSchException
        doThrow(new JSchException("Failed to create session")).when(sftpServiceSpy).createSession(mockJSch);
    
        // 執行 readFileFromSFTP，應拋出異常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
    
        // 驗證異常訊息
        assertTrue(exception.getMessage().contains("SFTP_003"));
        System.out.println("session 創建失敗，session == null，測試成功");
    }
}