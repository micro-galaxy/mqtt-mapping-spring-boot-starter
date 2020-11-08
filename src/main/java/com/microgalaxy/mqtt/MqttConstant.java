package com.microgalaxy.mqtt;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
public class MqttConstant {

    /**
     * MQTT Quality of Service
     */
    public enum MqttQos {
        /**
         * Send up to once
         * <p>
         * The performance of the underlying network since the message is distributed.
         * The receiver does not send a response and the sender does not try again.
         * The receiver receives the message only once, or not even once.
         */
        QOS0(0),
        /**
         * Send at least once
         * <p>
         * The QoS in this case ensures that the message reaches the recipient at least once.
         * The variable packet header of a QoS 1 PUBLISH packet contains the packet unique identification
         * and has a PUBACK packet acknowledgement.
         */
        QOS1(1),
        /**
         * Accurate one-time delivery
         * <p>
         * This is the highest quality of service and is used in cases
         * where lost and duplicate messages are unacceptable.
         * This quality of service increases costs.
         */
        QOS2(2);

        private final int qosLevel;

        MqttQos(int qosLevel) {
            this.qosLevel = qosLevel;
        }

        public int qos() {
            return qosLevel;
        }
    }

}
