package github.microgalaxy.mqtt.annotation;

import java.lang.annotation.*;

/**
 * This annotation is used for mqtt client disconnect method handle.
 * <p>
 *
 * For example
 * <code>
    @OnDisconnect
    public void disconnect(Throwable t) {
        do something;
    }
 * </code
 *
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/6 20:20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnDisconnect {
}
