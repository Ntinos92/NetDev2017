package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class subSettings extends mqttSubscriber{

    final MainActivity parent;

    public subSettings(String id, String topic, MainActivity parent) {

        super(id, topic);
        this.parent = parent;
    }

    @Override
    public void set_callback(MqttConnectOptions connOpts, String broker){
        subSettings subscriber = new subSettings(clientId+" WORKER",topic,parent);
        Client.setCallback(subscriber);
        System.out.println("Connecting	to	broker:	"+broker);
        try {
            Client.connect(connOpts);
        }catch(MqttException me){}
        System.out.println("Connected");
    }
}

