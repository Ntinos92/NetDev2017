package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */

import java.lang.*;

public class publisher_thread extends my_thread{

    String id;
    String topic;

    public publisher_thread(String name, String id, String topic) {
        super(name);

        this.id = id;
        this.topic = topic;

    }

    @Override
    public void operate() {
        new mqttPublisher(id,"DATA", mqtt_settings.frequency ).publish();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
