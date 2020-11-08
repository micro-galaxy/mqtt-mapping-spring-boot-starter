package com.microgalaxy.mqtt;

import com.microgalaxy.mqtt.properties.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
public class MqttTemplateAutoConfiguration {
    private final Logger log = LoggerFactory.getLogger(MqttTemplateAutoConfiguration.class);

    @Autowired
    MqttProperties config;

    private MqttMassageDispatcher dispatcher;

    @PostConstruct
    @ConditionalOnMissingBean(MqttTemplate.class)
    public void initDispatcher() {
        dispatcher = new MqttMassageDispatcher(config);
        dispatcher.initMqttHandleMap();
    }

    @PostConstruct
    @ConditionalOnMissingBean
    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(config.getUsername());
        options.setPassword(config.getPassword().toCharArray());
        //default：30
        options.setConnectionTimeout(config.getConnectionTimeout());
        //default：false
        options.setAutomaticReconnect(config.getAutomaticReconnect());
        //default：true
        options.setCleanSession(config.getCleanSession());
        //default：60
        options.setKeepAliveInterval(config.getKeepAliveInterval());
        return options;
    }

    @ConditionalOnMissingBean
    @Bean
    public MqttTemplate mqttTemplate() {
        MqttTemplate mqttTemplate = new MqttTemplate(config, dispatcher);
        try {
            mqttTemplate.initMqttClient();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Create mqtt client failed: {}", e.getMessage(), e);
            }
            System.exit(1);
        }
        return mqttTemplate;
    }



    @ConditionalOnBean(MqttTemplate.class)
    @Bean
    public void connectionMqttBroker() {

        if(log.isInfoEnabled()) {
            log.info("Connecting to mqtt broker ...【host: {}】【clientId: {}】【username: {}】",
                    config.getHost(), config.getClientId(), config.getUsername());
        }

        MqttConnectOptions options = config.getApplicationContext().getBean(MqttConnectOptions.class);
        MqttTemplate mqttTemplate = config.getApplicationContext().getBean(MqttTemplate.class);
        try {
            mqttTemplate.connectionMqttBroker(options);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("【Connection to mqtt broker failed: {}】", e.getMessage(), e);
            }
            System.exit(1);
        }
    }


}
