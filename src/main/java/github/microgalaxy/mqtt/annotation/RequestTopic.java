package github.microgalaxy.mqtt.annotation;

import java.lang.annotation.*;

/**
 * Annotation indicating a method parameter should be
 * bound to the mqtt topic of the mqtt massage.
 * <p>
 * The type of the topic class must be {@link String}.
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
public @interface RequestTopic {
}
