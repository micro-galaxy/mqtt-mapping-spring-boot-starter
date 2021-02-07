package github.microgalaxy.mqtt.annotation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Types that carry this annotation are treated as controllers where
 * {@link MqttMassageMapping} methods assume
 *
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 * @see MqttMassageMapping,RequestTopic,RequestMassage
 * @see OnConnectComplete,OnDisconnect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Order(0)
public @interface MqttMassageController {
}
