# mqtt-mapping-spring-boot-starter

make mqtt message processing as simple as spring processing http request mapping


#### 1,add maven dependency
```` 
<dependency>
  <groupId>github.micro-galaxy.starter</groupId>
  <artifactId>mqtt-mapping-spring-boot-starter</artifactId>
  <version>1.0</version>
</dependency>
````

#### 2,springBoot configuration file
````
spring:
 mqtt:
  host: tcp://localhost:1020
  clientId: SERVER000000000000001
  username: admin
  password: admin
  connectionTimeout: 10
  automaticReconnect: true
  cleanSession: true
  keepAliveInterval: 60
````

#### 3,use in projects
````
    /**
     * @author Microgalaxy
     * @version v1.0.0
     */
    @MqttMassageController
    @MqttMassageMapping("mqtt")
    public class MqttController {
    
        @Autowired
        private MqttTemplate mqttTemplate;
    
        @OnConnectComplete
        public void onConnect() {
            mqttTemplate.subscribe("mqtt/msg", MqttConstant.MqttQos.QOS1);
    
    
            mqttTemplate.publish("mqtt/msg", MqttConstant.MqttQos.QOS1,"hello");
        }
    
        @MqttMassageMapping("/msg")
        public void iotMsg(@RequestTopic String topic, @RequestMassage MqttMessage msg) {
           System.out.println(new String( msg.getPayload()));
        }

        @OnDisconnect
        public void onDisconnect() {
           //do something...
        }


    }
````
