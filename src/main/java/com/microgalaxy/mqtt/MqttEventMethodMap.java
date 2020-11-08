package com.microgalaxy.mqtt;

/**
 * @author Microgalaxy
 * @version v1.0.0
 * @date 2020/11/06 20:20
 */
class MqttEventMethodMap {
    private final InvokeMethodMap[] disconnectEvent;
    private final InvokeMethodMap[] connectCompleteEvent;

    public MqttEventMethodMap(InvokeMethodMap[] disconnectEvent,
                              InvokeMethodMap[] connectCompleteEvent) {
        this.disconnectEvent = disconnectEvent;
        this.connectCompleteEvent = connectCompleteEvent;
    }

    public InvokeMethodMap[] getDisconnectEvent() {
        return disconnectEvent;
    }

    public InvokeMethodMap[] getConnectCompleteEvent() {
        return connectCompleteEvent;
    }
}
