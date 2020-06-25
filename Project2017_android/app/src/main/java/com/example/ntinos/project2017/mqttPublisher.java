package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class mqttPublisher {

    String topic;
    String	clientId;
    String content;

    public mqttPublisher( String id, String topic,String content){
        this.topic = topic;
        clientId = id;
        this.content = content;
    }

    public void publish() {
        int qos =	2;//Quality of service " = 2" message is always delivered exactly once.

        String	broker = "tcp://"+mqtt_settings.ipaddress+":"+mqtt_settings.port;
        MemoryPersistence persistence = new	MemoryPersistence();
        try	{
//Connect	to	MQTT	Broker
            MqttClient Client =	new MqttClient(broker,	clientId,	persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting	to	broker:	"+broker);

            Client.connect(connOpts);
            System.out.println("Connected");
//Publish	message	to	MQTT	Broker
            System.out.println("Publishing	message:	"+content);
            MqttMessage message	=	new	MqttMessage(content.getBytes());
            message.setQos(qos);
            Client.publish(topic,	message);
            System.out.println("Message	published");
            Client.disconnect();
            System.out.println("Disconnected");
            // System.exit(0);  // It executes once with a publish and a subscribe message
        }	catch(MqttException me)	{
            System.out.println("reason	"	+	me.getReasonCode());
            System.out.println("msg "	+	me.getMessage());
            System.out.println("loc "	+	me.getLocalizedMessage());
            System.out.println("cause	"	+	me.getCause());
            System.out.println("excep "	+	me);
            me.printStackTrace();
        }
    }

}
