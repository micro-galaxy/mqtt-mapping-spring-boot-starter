package com.microgalaxy.mqtt.properties;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
@ConditionalOnProperty(prefix = "spring.mqtt")
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperties implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    private String host;
    private String clientId;
    private String username;
    private String password;
    private Integer connectionTimeout;
    private Boolean automaticReconnect;
    private Boolean cleanSession;
    private Integer keepAliveInterval;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Boolean getAutomaticReconnect() {
        return automaticReconnect;
    }

    public void setAutomaticReconnect(Boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

    public Boolean getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MqttProperties.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
