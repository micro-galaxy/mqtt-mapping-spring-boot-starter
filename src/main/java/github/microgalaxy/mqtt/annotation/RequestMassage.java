package github.microgalaxy.mqtt.annotation;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.annotation.*;

/**
 * Annotation indicating a method parameter should be
 * bound to the mqtt massage of the mqtt massage.
 * <p>
 * The type of the massage class must be {@link MqttMessage}.
 *
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 * @see MqttMassageController
 * @see MqttMassageMapping
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMassage {
}
