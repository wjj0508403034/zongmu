package com.zongmu.service.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class ProxyProperties {

    @Value("${proxy.host}")
    private String host;

    @Value("${proxy.port}")
    private Integer port;

    @Value("${proxy.username}")
    private String userName;

    @Value("${proxy.password}")
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public boolean isEnabled() {
        if (StringUtils.isEmpty(this.getHost()) || this.getPort() == null || this.getPort() == 0) {
            return false;
        }

        return true;
    }

}
