package cdf.training.svc.datatransfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "sftp")
public class SFTPConfig {
    private String host;
    public String gethost() {
        return host;
    }
    public void sethost(String host) {
        this.host = host;
    }    
    private int port;
    public int getport() {
        return port;
    }
    public void setport(int port) {
        this.port = port;
    }    
    private String username;
    public String getusername() {
        return username;
    }
    public void setusername(String username) {
        this.username = username;
    }    
    private String password;
    public String getpassword() {
        return password;
    }
    public void setpassword(String password) {
        this.password = password;
    }    
    private String remoteDir;
    public String getremoteDir() {
        return remoteDir;
    }
    public void setremoteDir(String remoteDir) {
        this.remoteDir = remoteDir;
    }    
}