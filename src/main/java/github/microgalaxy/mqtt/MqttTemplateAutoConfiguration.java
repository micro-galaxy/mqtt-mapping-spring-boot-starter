package github.microgalaxy.mqtt;

import github.microgalaxy.mqtt.properties.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
@Configuration
@EnableConfigurationProperties(MqttProperties.class)
class MqttTemplateAutoConfiguration {
    private final Logger log = LoggerFactory.getLogger(MqttTemplateAutoConfiguration.class);

    @Autowired
    private MqttProperties config;

    private MqttMassageDispatcher dispatcher;

    @ConditionalOnMissingBean
    @Bean
    protected MqttMassageDispatcher mqttMassageDispatcher() {
        dispatcher = new MqttMassageDispatcher(config);
        dispatcher.initMqttHandleMap();
        return dispatcher;
    }

    @ConditionalOnMissingBean
    @Bean
    protected MqttConnectOptions mqttConnectOptions() {
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
    protected MqttTemplate mqttTemplate() {
        MqttTemplate mqttTemplate = new MqttTemplate(config);
        try {
            mqttTemplate.initMqttClient();
        } catch (Exception e) {
            log.error("==> Create mqtt client failed: {}", e.getMessage(), e);
            System.exit(1);
        }
        return mqttTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connectionMqttBroker() {
        log.info("==> Connecting to mqtt broker ...【host: {}】【clientId: {}】【username: {}】",
                "tcp://" + config.getDomain() + ":" + config.getPort(), config.getClientId(), config.getUsername());

        MqttConnectOptions options = mqttConnectOptions();
        MqttTemplate mqttTemplate = mqttTemplate();
        try {
            mqttTemplate.connectionMqttBroker(options, dispatcher);
        } catch (Exception e) {
            log.error("==> Connection to mqtt broker failed: {}", e.getMessage(), e);
        }
    }
}
