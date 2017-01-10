package com.zongmu.service.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class FtpProperties {

    @Value("${ftp.url}")
    private String ftpServiceUrl;

    @Value("${ftp.userName}")
    private String userName;

    @Value("${ftp.password}")
    private String password;

    public String getFtpServiceUrl() {
        return ftpServiceUrl;
    }

    public void setFtpServiceUrl(String ftpServiceUrl) {
        this.ftpServiceUrl = ftpServiceUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
