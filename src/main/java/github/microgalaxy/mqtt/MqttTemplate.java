package github.microgalaxy.mqtt;

import github.microgalaxy.mqtt.properties.MqttProperties;
import github.microgalaxy.mqtt.utils.Assert;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
public final class MqttTemplate {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private MqttProperties config;
    private MqttCallback mqttMassageDispatcher;
    private MqttClient mqttClient;

    protected MqttTemplate(MqttProperties config, MqttCallback mqttMassageDispatcher) {
        this.config = config;
        this.mqttMassageDispatcher = mqttMassageDispatcher;
    }


    /**
     * publish a massage to the mqtt broker
     *
     * @param topic mqtt topic
     * @param qos   Quality of Service, {@link MqttConstant.MqttQos}
     * @param msg
     * @return
     * @throws MqttException
     */
    public boolean publish(String topic, MqttConstant.MqttQos qos, String msg) {
        Assert.isBlank(topic, "The topic must not be empty.");
        Assert.isNull(qos, "Quality of service must not be null.");
        Assert.isNull(msg, "The massage must not be null.");

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos.qos());
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        try {
            MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
            token.waitForCompletion();
            if (log.isInfoEnabled()) {
                log.info("Published a message.【topic: {}】【message: {}】", topic, msg);
            }
            return true;
        } catch (MqttException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to publish a message: {}【topic: {}】【qos: {}】【msg: {}】",
                        e.getMessage(), topic, qos.qos(), msg, e);
            }
        }
        return false;
    }


    /**
     * publish a massage to the mqtt broker
     *
     * @param topic   mqtt topic
     * @param qos     Quality of Service, {@link MqttConstant.MqttQos}
     * @param payload
     * @return
     * @throws MqttException
     */
    public boolean publish(String topic, MqttConstant.MqttQos qos, byte[] payload) {
        Assert.isBlank(topic, "The topic must not be empty.");
        Assert.isNull(qos, "Quality of service must not be null.");
        Assert.isNull(payload, "The payload must not be null.");

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos.qos());
        mqttMessage.setPayload(payload);
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        try {
            MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
            token.waitForCompletion();
            if (log.isInfoEnabled()) {
                log.info("Published a message.【topic: {}】【data: {}】", topic, payload);
            }
            return true;
        } catch (MqttException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to publish a message: {}【topic: {}】【qos: {}】【data: {}】",
                        e.getMessage(), topic, qos.qos(), payload, e);
            }
        }
        return false;
    }


    /**
     * subscribe to a topic from mqtt broker
     *
     * @param topic mqtt topic
     * @param qos   Quality of Service, {@link MqttConstant.MqttQos}
     * @return
     */
    public boolean subscribe(String topic, MqttConstant.MqttQos qos) {
        Assert.isBlank(topic, "The topic must not be empty.");
        Assert.isNull(qos, "Quality of service must not be null.");

        try {
            mqttClient.subscribe(topic, qos.qos());
            return true;
        } catch (MqttException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to subscribe to a topic from mqtt broker: {}【topic：{}】【qos：{}】",
                        e.getMessage(), topic, qos.qos(), e);
            }
        }
        return false;
    }

    /**
     * subscribe to  topics from mqtt broker
     *
     * @param topics mqtt topics
     * @param qos    Quality of Service, {@link MqttConstant.MqttQos}
     * @return
     */
    public boolean subscribe(String[] topics, int[] qos) {
        try {
            mqttClient.subscribe(topics, qos);
            return true;
        } catch (MqttException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to subscribe to a topic from mqtt broker: {}【topic：{}】【qos：{}】",
                        e.getMessage(), topics, qos, e);
            }
        }
        return false;
    }


    protected void initMqttClient() throws Exception {
        MqttClient mqttClient = new MqttClient(getHost(config), config.getClientId(), new MemoryPersistence());
        mqttClient.setCallback(mqttMassageDispatcher);
        this.mqttClient = mqttClient;
    }

    private String getHost(MqttProperties config) {
        return "tcp://" + config.getDomain() + ":" + config.getPort();
    }

    protected void connectionMqttBroker(MqttConnectOptions options) throws MqttException {
        mqttClient.connect(options);
    }
}
