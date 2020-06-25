package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Button;
/*Class which takes  data from MainThreadUI, for mqttSubscriber Camera and Music Management*/
public class HandlingCamMusic {
    private static HandlingCamMusic mInstance= null;

    public Handler handlerMusic;
    public Button musicButton;
    public MediaPlayer buttonSoundOn;

    public Handler  handlerCamera;
    public Button cameraButton;
    public Context cameraContext;

    public Handler handlerConnection;


    protected HandlingCamMusic(){}

    public static synchronized HandlingCamMusic getInstance(){
        if(null == mInstance){
            mInstance = new HandlingCamMusic();
        }
        return mInstance;
    }
}