package github.microgalaxy.mqtt.annotation;


import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation for mapping mqtt broker massage onto specific handler methods.
 *
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 * @see RequestTopic,RequestMassage
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMassageMapping {

    /**
     * mqtt topic binding
     * <p>
     *
     * For example
     * <pre>{@code
     *      @MqttMassageMapping("mqtt/msg")
     *      public void onMsg(@RequestTopic String topic, @RequestMassage MqttMessage msg){
     *
     *      }
     * }</pre>
     * {@link MqttMessage}
     *
     * @return
     */
    @AliasFor("value")
    String topic() default "";

    @AliasFor("topic")
    String value() default "";
}
