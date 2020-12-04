package com.microgalaxy.mqtt;

import com.google.gson.Gson;
import com.microgalaxy.mqtt.annotation.*;
import com.microgalaxy.mqtt.properties.MqttProperties;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
class MqttMassageDispatcher implements MqttCallbackExtended {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Gson gson = new Gson();

    private MqttProperties config;
    private Map<String, InvokeMethodMap> mqttMsgHandleMap;
    private MqttEventMethodMap mqttEventMethodMap;

    MqttMassageDispatcher(MqttProperties config) {
        this.config = config;
    }

    protected void initMqttHandleMap() {
        mqttMsgHandleMap = initMqttMsgHandleMap();
        mqttEventMethodMap = initMqttEventMethodMap();
    }


    @Override
    public void connectionLost(Throwable t) {
        if (log.isErrorEnabled()) {
            log.error("Mqtt client disconnected: {}", t.getMessage(), t);
        }
        try {
            for (InvokeMethodMap invokeMethodMap : mqttEventMethodMap.getDisconnectEvent()) {
                invokeMethodMap.getMethod().invoke(invokeMethodMap.getTargetClass(), t);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Call disconnect event error when mqtt client disconnects: {}",
                        e.getMessage(), e);
            }
        }

    }


    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (log.isInfoEnabled()) {
            if (reconnect) {
                log.info("The mqtt client has been reconnected to mqtt broker, serverURI: {}", serverURI);
            } else {
                log.info("The mqtt client is connected to the mqtt broker, serverURI: {}", serverURI);
            }
        }

        try {
            for (InvokeMethodMap invokeMethodMap : mqttEventMethodMap.getConnectCompleteEvent()) {
                invokeMethodMap.getMethod().invoke(invokeMethodMap.getTargetClass());
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Call connect complete event error when mqtt client connect complete: {}",
                        e.getMessage(), e);
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        if (log.isInfoEnabled()) {
            log.info("Receive a message.【topic: {}】【message: {}】【detail: {}】",
                    topic, new String(mqttMessage.getPayload()), gson.toJson(mqttMessage));
        }

        InvokeMethodMap invokeMethodMap = mqttMsgHandleMap.get(topic);
        if (invokeMethodMap == null) {
            if (log.isErrorEnabled()) {
                log.error("No found MqttMassageMapping.【topic:{}】", topic);
            }
            return;
        }
        Object[] args = getInvokeArgs(invokeMethodMap, topic, mqttMessage);
        try {
            invokeMethodMap.getMethod().invoke(invokeMethodMap.getTargetClass(), args);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Call message arrived event error when mqtt message arrived: {}",
                        e.getMessage(), e);
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }


    private Map<String, InvokeMethodMap> initMqttMsgHandleMap() {
        mqttMsgHandleMap = new HashMap<>();
        Map<String, Object> handles = config.getApplicationContext()
                .getBeansWithAnnotation(MqttMassageController.class);

        handles.forEach((k, v) -> {
            Class<?> handleClass = v.getClass();
            MqttMassageMapping rootMapping = AnnotationUtils.findAnnotation(handleClass, MqttMassageMapping.class);
            Method[] methods = handleClass.getDeclaredMethods();

            IntStream.range(0, methods.length).forEach(i -> {
                MqttMassageMapping methodMapping = AnnotationUtils.findAnnotation(methods[i], MqttMassageMapping.class);
                if (ObjectUtils.isEmpty(methodMapping)) {
                    return;
                }

                String topic = getTopic(rootMapping, methodMapping);
                if (mqttMsgHandleMap.containsKey(topic)) {
                    if (log.isErrorEnabled()) {
                        log.error("Duplicate keys in MqttMassageMapping.【signature :{}{}()】",
                                handleClass.getName(), methods[i].getName());
                    }
                    System.exit(1);
                }

                Class<?>[] parameterTypes = methods[i].getParameterTypes();
                Annotation[][] parameterAnnotations = methods[i].getParameterAnnotations();

                Object[] argsSpace = getArgsSpace(parameterTypes, parameterAnnotations,
                        new ArrayList<Class<? extends Annotation>>() {
                            {
                                add(RequestTopic.class);
                                add(RequestMassage.class);
                            }
                        });

                InvokeMethodMap invokeMethodMap = new InvokeMethodMap(v, methods[i], argsSpace);
                mqttMsgHandleMap.put(topic, invokeMethodMap);
            });
        });
        return mqttMsgHandleMap;
    }

    private MqttEventMethodMap initMqttEventMethodMap() {
        Map<String, Object> handles = config.getApplicationContext()
                .getBeansWithAnnotation(MqttMassageController.class);

        List<InvokeMethodMap> connectCompleteEvent = new ArrayList<>();
        List<InvokeMethodMap> disconnectEvent = new ArrayList<>();
        handles.forEach((k, v) -> {
            Class<?> handleClass = v.getClass();
            Method[] methods = handleClass.getDeclaredMethods();

            IntStream.range(0, methods.length).forEach(i -> {
                OnConnectComplete connectCompleteMapping = AnnotationUtils.findAnnotation(methods[i], OnConnectComplete.class);
                OnDisconnect disconnectMapping = AnnotationUtils.findAnnotation(methods[i], OnDisconnect.class);
                if (!ObjectUtils.isEmpty(connectCompleteMapping)) {
                    InvokeMethodMap invokeMethodMap = new InvokeMethodMap(v, methods[i], null);
                    connectCompleteEvent.add(invokeMethodMap);
                }
                if (!ObjectUtils.isEmpty(disconnectMapping)) {
                    InvokeMethodMap invokeMethodMap = new InvokeMethodMap(v, methods[i], null);
                    disconnectEvent.add(invokeMethodMap);
                }

            });
        });

        return new MqttEventMethodMap(disconnectEvent.toArray(new InvokeMethodMap[0]),
                connectCompleteEvent.toArray(new InvokeMethodMap[0]));
    }


    private Object[] getArgsSpace(Class<?>[] parameterTypes, Annotation[][] parameterAnnotations,
                                  ArrayList<Class<? extends Annotation>> clazz) {
        Object[] argsSpace = new Object[parameterTypes.length];
        IntStream.range(0, parameterTypes.length).forEach(i -> {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for (Annotation annotation : parameterAnnotation) {
                Class<? extends Annotation> aClass = annotation.annotationType();
                if (clazz.contains(aClass)) {
                    argsSpace[i] = aClass;
                    return;
                }
            }
        });
        return argsSpace;
    }

    private String getTopic(MqttMassageMapping rootMapping, MqttMassageMapping methodMapping) {
        String topic = (rootMapping == null ? "" : rootMapping.value()) +
                methodMapping.value();
        return topic;
    }

    private Object[] getInvokeArgs(InvokeMethodMap invokeMethodMap, String topic, MqttMessage mqttMessage) {
        Object[] argsSpace = invokeMethodMap.getArgsSpace();
        Object[] args = new Object[argsSpace.length];
        IntStream.range(0, argsSpace.length).forEach(i -> {
            if (RequestTopic.class.equals(argsSpace[i])) {
                args[i] = topic;
                return;
            }
            if (RequestMassage.class.equals(argsSpace[i])) {
                args[i] = mqttMessage;
            }
        });
        return args;
    }
}
