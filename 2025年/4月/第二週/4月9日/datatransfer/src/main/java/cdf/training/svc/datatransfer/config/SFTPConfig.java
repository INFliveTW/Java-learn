package cdf.training.svc.datatransfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "sftp")
public class SFTPConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String remoteDir;
}