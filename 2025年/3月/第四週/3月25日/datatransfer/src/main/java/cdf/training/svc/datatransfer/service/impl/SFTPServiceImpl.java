package cdf.training.svc.datatransfer.service.impl;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import cdf.training.svc.datatransfer.config.SFTPConfig;

@Service
public class SFTPServiceImpl {
    private final SFTPConfig sftpConfig;
    //注入SFTP
    public SFTPServiceImpl(SFTPConfig sftpConfig) {
        this.sftpConfig = sftpConfig;
    }

    public String readFileFromSFTP(String filePath) {
        //讀取SFTP
        try {
            JSch jsch = new JSch();
            //初始化 JSch
            Session session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
            //建立SFTP (使用 SFTPConfig配置)
            session.setPassword(sftpConfig.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            //連線到SFTP
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(); //開啟SFTP通道
            InputStream inputStream = channel.get(filePath);
            String content = new String(inputStream.readAllBytes());
            channel.disconnect();
            session.disconnect(); //關閉連線
            return content;
        } catch (Exception e) {
            throw new RuntimeException("SFTP error: " + e.getMessage(), e);
        }
    }
}
//步驟7：SFTP處理