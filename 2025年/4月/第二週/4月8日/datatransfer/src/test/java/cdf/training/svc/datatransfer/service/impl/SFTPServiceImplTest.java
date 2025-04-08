package cdf.training.svc.datatransfer.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
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
        when(sftpConfig.getHost()).thenReturn("localhost");
        when(sftpConfig.getPort()).thenReturn(22);
        when(sftpConfig.getUsername()).thenReturn("sa");
        when(sftpConfig.getPassword()).thenReturn("1QAZ2WSX3EDc4@");
    }

    @Test
    void testReadFileFromSFTP_Success() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doReturn(channel).when(sftpServiceSpy).createChannel(session);

        doNothing().when(session).connect();
        doNothing().when(channel).connect();

        String csvContent = "ID,DEPARTMENT,JOB_TITLE,NAME,TEL,EMAIL\n1,IT,Engineer,John,12345678,john@example.com";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        doReturn(inputStream).when(sftpServiceSpy).getFileInputStream(channel, "/upload/employee_data.csv");

        // 模擬 disconnect
        doNothing().when(channel).disconnect();
        doNothing().when(session).disconnect();

        String result = sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        assertEquals(csvContent, result);
        System.out.println("成功讀取SFTP檔案，測試成功");
    }

    @Test
    void testReadFileFromSFTP_ConnectionFailure_SessionConnect() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);

        doThrow(new JSchException("Connection failed")).when(session).connect();

        // 模擬 disconnect
        doNothing().when(session).disconnect();

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
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doReturn(channel).when(sftpServiceSpy).createChannel(session);

        doNothing().when(session).connect();
        doThrow(new JSchException("Channel connection failed")).when(channel).connect();

        // 模擬 disconnect
        doNothing().when(channel).disconnect();
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器（channel.connect），測試成功");
    }

    @Test
    void testReadFileFromSFTP_ConnectionFailure_NullMessage() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);

        doThrow(new JSchException((String) null)).when(session).connect();

        // 模擬 disconnect
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("無法連接到 SFTP 伺服器，異常訊息為 null，測試成功");
    }

    @Test
    void testReadFileFromSFTP_FileNotFound_NoSuchFile() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doReturn(channel).when(sftpServiceSpy).createChannel(session);

        doNothing().when(session).connect();
        doNothing().when(channel).connect();

        doThrow(new SftpException(2, "no such file"))
                .when(sftpServiceSpy).getFileInputStream(channel, "/upload/non_existent_file.csv");

        // 模擬 disconnect
        doNothing().when(channel).disconnect();
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/non_existent_file.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("找不到SFTP檔案（no such file），測試成功");
    }

    @Test
    void testReadFileFromSFTP_FileNotFound_FileNotFound() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doReturn(channel).when(sftpServiceSpy).createChannel(session);

        doNothing().when(session).connect();
        doNothing().when(channel).connect();

        doThrow(new SftpException(2, "file not found"))
                .when(sftpServiceSpy).getFileInputStream(channel, "/upload/non_existent_file.csv");

        // 模擬 disconnect
        doNothing().when(channel).disconnect();
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/non_existent_file.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("找不到SFTP檔案（file not found），測試成功");
    }

    @Test
    void testReadFileFromSFTP_FileNotFound_NotExist() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doReturn(channel).when(sftpServiceSpy).createChannel(session);

        doNothing().when(session).connect();
        doNothing().when(channel).connect();

        doThrow(new SftpException(2, "not exist"))
                .when(sftpServiceSpy).getFileInputStream(channel, "/upload/non_existent_file.csv");

        // 模擬 disconnect
        doNothing().when(channel).disconnect();
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/non_existent_file.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("找不到SFTP檔案（not exist），測試成功");
    }

    @Test
    void testReadFileFromSFTP_PermissionDenied() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doReturn(channel).when(sftpServiceSpy).createChannel(session);

        doNothing().when(session).connect();
        doNothing().when(channel).connect();

        doThrow(new SftpException(3, "permission denied"))
                .when(sftpServiceSpy).getFileInputStream(channel, "/upload/test/employee_data.csv");

        // 模擬 disconnect
        doNothing().when(channel).disconnect();
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/test/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_001, message=SFTP 伺服器拒絕訪問，請檢查權限, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("SFTP 權限被拒絕，測試成功");
    }

    // 新增測試案例：createJSch 拋出異常
    @Test
    void testReadFileFromSFTP_CreateJSchException() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doThrow(new JSchException("Failed to create JSch")).when(sftpServiceSpy).createJSch();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("創建 JSch 失敗，測試成功");
    }

    // 新增測試案例：createSession 拋出異常
    @Test
    void testReadFileFromSFTP_CreateSessionException() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doThrow(new JSchException("Failed to create session")).when(sftpServiceSpy).createSession(jsch);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("創建 Session 失敗，測試成功");
    }

    // 新增測試案例：createChannel 拋出異常
    @Test
    void testReadFileFromSFTP_CreateChannelException() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doNothing().when(session).connect();
        doThrow(new JSchException("Failed to create channel")).when(sftpServiceSpy).createChannel(session);

        // 模擬 disconnect
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("創建 Channel 失敗，測試成功");
    }

    // 新增測試案例：inputStream.readAllBytes 拋出 IOException
    @Test
    void testReadFileFromSFTP_IOException() throws Exception {
        SFTPServiceImpl sftpServiceSpy = spy(sftpService);
        doReturn(jsch).when(sftpServiceSpy).createJSch();
        doReturn(session).when(sftpServiceSpy).createSession(jsch);
        doReturn(channel).when(sftpServiceSpy).createChannel(session);

        doNothing().when(session).connect();
        doNothing().when(channel).connect();

        InputStream inputStream = spy(new ByteArrayInputStream("test content".getBytes()));
        doReturn(inputStream).when(sftpServiceSpy).getFileInputStream(channel, "/upload/employee_data.csv");
        doThrow(new IOException("Read error")).when(inputStream).readAllBytes();

        // 模擬 disconnect
        doNothing().when(channel).disconnect();
        doNothing().when(session).disconnect();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sftpServiceSpy.readFileFromSFTP("/upload/employee_data.csv");
        });
        assertEquals("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", 
                     exception.getMessage());
        System.out.println("讀取檔案時發生 IOException，測試成功");
    }
}