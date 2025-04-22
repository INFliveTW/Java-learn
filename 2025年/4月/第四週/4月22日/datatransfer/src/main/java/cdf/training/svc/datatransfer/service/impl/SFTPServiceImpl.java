package cdf.training.svc.datatransfer.service.impl;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cdf.training.svc.datatransfer.config.SFTPConfig;

@Service
public class SFTPServiceImpl {
    private final SFTPConfig sftpConfig;

    public SFTPServiceImpl(SFTPConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    // 提取 JSch 創建方法，方便模擬
    protected JSch createJSch() throws JSchException {
        return new JSch();
    }

    // 提取 Session 創建方法，方便模擬
    protected Session createSession(JSch jsch) throws JSchException {
        Session session = jsch.getSession(sftpConfig.getusername(), sftpConfig.gethost(), sftpConfig.getport());
        session.setPassword(sftpConfig.getpassword());
        session.setConfig("StrictHostKeyChecking", "no");
        return session;
    }

    // 提取 ChannelSftp 創建方法，方便模擬
    protected ChannelSftp createChannel(Session session) throws JSchException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        return channel;
    }

    // 提取檔案讀取方法，方便模擬
    protected InputStream getFileInputStream(ChannelSftp channel, String filePath) throws SftpException {
        return channel.get(filePath);
    }

    public String readFileFromSFTP(String filePath) {
        Session session = null;
        ChannelSftp channel = null;
        try {
            JSch jsch = createJSch();
            session = createSession(jsch);
            session.connect();
    
            channel = createChannel(session);
            channel.connect();
            InputStream inputStream = channel.get(filePath);
            return new String(inputStream.readAllBytes());
        } catch (SftpException e) {
            if (e.id == 2) { // 檔案不存在
                throw new RuntimeException("ErrorResponseDto(code=SFTP_002, message=SFTP 資料夾沒有CSV檔案，請確認SFTP, triggerTime=null)", e);
            } else if (e.id == 3) { // 權限被拒絕
                throw new RuntimeException("ErrorResponseDto(code=SFTP_001, message=SFTP 伺服器拒絕訪問，請檢查權限, triggerTime=null)", e);
            } else { // 其他 SFTP 異常
                throw new RuntimeException("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", e);
            }
        } catch (Exception e) { // 其他異常（如 JSchException）
            throw new RuntimeException("ErrorResponseDto(code=SFTP_003, message=無法連接到 SFTP 伺服器，請檢查配置或網路狀態, triggerTime=null)", e);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}