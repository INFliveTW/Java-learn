package cdf.training.svc.datatransfer.service.impl;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cdf.training.svc.datatransfer.config.SFTPConfig;
import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import cdf.training.svc.datatransfer.dto.ErrorResponseDto;

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
        Session session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
        session.setPassword(sftpConfig.getPassword());
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
            InputStream inputStream = getFileInputStream(channel, filePath);
            String content = new String(inputStream.readAllBytes());
            return content;
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            ErrorResponseDto errorDto;
            if (msg.contains("no such file") || msg.contains("file not found") || msg.contains("not exist")) {
                errorDto = new ErrorResponseDto(ResponseCode.SFTP_FILE_NOT_FOUND.getCode(),
                                                ResponseCode.SFTP_FILE_NOT_FOUND.getDefaultMessage(),
                                                null);
            } else if (msg.contains("permission denied")) {
                errorDto = new ErrorResponseDto(ResponseCode.SFTP_PERMISSION_DENIED.getCode(),
                                                ResponseCode.SFTP_PERMISSION_DENIED.getDefaultMessage(),
                                                null);
            } else {
                errorDto = new ErrorResponseDto(ResponseCode.SFTP_CONNECTION_ERROR.getCode(),
                                                ResponseCode.SFTP_CONNECTION_ERROR.getDefaultMessage(),
                                                null);
            }
            throw new RuntimeException(errorDto.toString(), e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}