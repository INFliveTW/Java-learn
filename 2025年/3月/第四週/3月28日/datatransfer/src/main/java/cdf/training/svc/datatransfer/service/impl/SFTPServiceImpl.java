package cdf.training.svc.datatransfer.service.impl;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import cdf.training.svc.datatransfer.config.SFTPConfig;
import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import cdf.training.svc.datatransfer.dto.ErrorResponseDto;

@Service
public class SFTPServiceImpl {
    private final SFTPConfig sftpConfig;

    public SFTPServiceImpl(SFTPConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    public String readFileFromSFTP(String filePath) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
            session.setPassword(sftpConfig.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            InputStream inputStream = channel.get(filePath);
            String content = new String(inputStream.readAllBytes());
            channel.disconnect();
            session.disconnect();
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
        }
    }
}