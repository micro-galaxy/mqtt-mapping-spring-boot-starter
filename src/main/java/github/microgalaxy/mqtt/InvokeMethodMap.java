package github.microgalaxy.mqtt;

import java.lang.reflect.Method;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
class InvokeMethodMap {

    private final Object targetClass;

    private final Method method;

    private final Object[] argsSpace;

    InvokeMethodMap(Object targetClass, Method method,
                    Object[] argsSpace) {
        this.targetClass = targetClass;
        this.method = method;
        this.argsSpace = argsSpace;
    }

    Object getTargetClass() {
        return targetClass;
    }

    Method getMethod() {
        return method;
    }

    Object[] getArgsSpace() {
        return argsSpace;
    }
}
