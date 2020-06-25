package com.example.ntinos.project2017;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.lang.*;

/**
 * Created by MANOS on 14/2/2018.
 */

public class connection_mythread extends my_thread {
    Context context;
    Connection_State cs;
    public connection_mythread(String name,Context c) {
        super(name);
        this.context=c;
        cs=new Connection_State(c);
    }

    @Override
    public void operate() {
        Bundle bundle= new Bundle();
        String message;
        if(cs.isConnectedWifi()){
            message="Internet Connection is Available and specifically Wifi Data is Open";
            bundle.putString("messageConnection", message);
            Message messageConnection = HandlingCamMusic.getInstance().handlerConnection.obtainMessage();
            messageConnection.setData(bundle);
            HandlingCamMusic.getInstance().handlerConnection.sendMessage(messageConnection);
        }
        else{
            message="Internet Connection is not Available";
            bundle.putString("messageConnection", message);
            Message messageConnection = HandlingCamMusic.getInstance().handlerConnection.obtainMessage();
            messageConnection.setData(bundle);
            HandlingCamMusic.getInstance().handlerConnection.sendMessage(messageConnection);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
