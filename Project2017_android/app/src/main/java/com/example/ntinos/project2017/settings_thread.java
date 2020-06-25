package com.example.ntinos.project2017;


import android.content.Context;
import android.os.Handler;
import android.widget.Button;

/**
 * Created by Ntinos on 28-Nov-17.
 */


public class settings_thread extends my_thread {

    subSettings subscriber;
    String id;
    String topic;
    MainActivity parent;
    Context cameraContext;
    Handler handler;
    Button cameraButton;

    public settings_thread(String name, String id, String topic,MainActivity parent) {
        super(name);

        this.id = id;
        this.topic = topic;
        this.parent = parent;
        subscriber = new subSettings(id,topic,parent);
    }
    public settings_thread(String name, String id, String topic, MainActivity parent, Context cameraContext, Handler handler, Button cameraButton){
        super(name);
        this.id = id;
        this.topic = topic;
        this.parent = parent;
        this.cameraContext = cameraContext;
        subscriber = new subSettings(id,topic,parent);
    }

    @Override
    public void operate(){

        String TempMSG = null;
        subscriber.subscribe();//Subscribe to mqtt broker
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        subscriber.disconnect();
        subscriber = null;//the old subscriber will be deleted by garbage collector
    }

    @Override
    public void resume(){
        subscriber = new subSettings(id,topic,parent);
        run();
    }

}

