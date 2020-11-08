package com.microgalaxy.mqtt.annotation;

import java.lang.annotation.*;

/**
 * This annotation is used for mqtt client connected method handle.
 * <p>
 * For example
 * <code>
    @OnConnectComplete
    public void connectComplete() {
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
public @interface OnConnectComplete {
}
