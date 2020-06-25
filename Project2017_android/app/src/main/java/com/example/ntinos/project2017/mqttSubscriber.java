package com.example.ntinos.project2017;
/**
 * Created by Ntinos on 28-Nov-17.
 */

        import android.annotation.TargetApi;
        import android.content.Context;
        import android.hardware.camera2.CameraAccessException;
        import android.hardware.camera2.CameraManager;
        import android.media.MediaPlayer;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Message;
        import android.view.View;

        import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
        import org.eclipse.paho.client.mqttv3.MqttCallback;
        import org.eclipse.paho.client.mqttv3.MqttClient;
        import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
        import org.eclipse.paho.client.mqttv3.MqttException;
        import org.eclipse.paho.client.mqttv3.MqttMessage;
        import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

        import static java.lang.Thread.sleep;

public class mqttSubscriber implements MqttCallback {

    String	clientId;
    String topic;
    MqttClient Client;


    protected String minimaMusic;
    protected String minimaCamera;
    protected boolean flagOnMusic = false;
    protected boolean flagOnFlash = false;
    protected boolean flash = false;

    public mqttSubscriber(String id,String topic) {
        clientId = id;
        this.topic = topic;

        System.out.println("new subscriber");
    }

    public void subscribe() {
        int qos =	2;

        String	broker	=	"tcp://"+ mqtt_settings.ipaddress+":"+mqtt_settings.port;
        MemoryPersistence persistence	=	new	MemoryPersistence();
        try	{
//Connect	client	to	MQTT	Broker (10.0.0.2/emulator)
            Client =	new	MqttClient(broker,	clientId,	persistence);
            MqttConnectOptions connOpts =	new	MqttConnectOptions();
            connOpts.setCleanSession(true);
//Set	callback
            set_callback(connOpts,broker);
//Subscribe	to	a	topic
            System.out.println("Subscribing	to	topic	\""+topic+"\"	qos "+qos);
            Client.subscribe(topic,	qos);
        }	catch(MqttException me)	{
            System.out.println("reason	"	+	me.getReasonCode());
            System.out.println("msg "	+	me.getMessage());
            System.out.println("loc "	+	me.getLocalizedMessage());
            System.out.println("cause	"	+	me.getCause());
            System.out.println("excep "	+	me);
            me.printStackTrace();
        }
    }

    public void set_callback(MqttConnectOptions connOpts,String broker){
        mqttSubscriber subscriber = new mqttSubscriber(clientId+" WORKER",topic);
        Client.setCallback(subscriber);
        System.out.println("Connecting	to	broker:	"+broker);
        try {
            Client.connect(connOpts);
        }catch(MqttException me){}
        System.out.println("Connected");
    }

    @Override
    public void connectionLost(Throwable throwable) {
    }

    @Override
    public void messageArrived(String topic, final MqttMessage message) throws Exception {
        int y =0;
        try {
            System.out.println("Message arrived: \"" + message.toString()
                    + "\" on topic \"" + topic.toString() + "\"");

            synchronized(Shared_string.get_message_arrived()){
                Shared_string.change(message.toString());
                //-------------------------------------------------------------//
                if(message.toString().contains("SOUND"))
                {
                    System.out.println("Thread-1 Send: " + message.toString() + " ------ FlagON: " + flagOnMusic);

                    /*The Function for the button. In each incoming command the button must be suitable to deactivate this command
                    So if command equals to ON for the music part it will be deactivated. If we press it again when the on's time wasnt ready to end
                    it wont do anyhing, because it counts only one command each time it get pressed. So with the same way works the OFF command. */
                    HandlingCamMusic.getInstance().musicButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            if(!flagOnMusic)
                            {
                                if (message.toString().equals("ON SOUND")) {
                                    if (HandlingCamMusic.getInstance().buttonSoundOn.isPlaying()) {
                                        HandlingCamMusic.getInstance().buttonSoundOn.pause();
                                    }
                                    minimaMusic = "It's OFF button Music";
                                    Bundle bundle = new Bundle();
                                    bundle.putString("messageMusic", minimaMusic);
                                    Message message = HandlingCamMusic.getInstance().handlerMusic.obtainMessage();
                                    message.setData(bundle);
                                    HandlingCamMusic.getInstance().handlerMusic.sendMessage(message);

                                    flagOnMusic = true;
                                    System.out.println(" flagON: " + flagOnMusic);
                                } else if (message.toString().equals("OFF SOUND")) {
                                    if (!(HandlingCamMusic.getInstance().buttonSoundOn.isPlaying())) {
                                        HandlingCamMusic.getInstance().buttonSoundOn.start();
                                    }
                                    minimaMusic = "It's ON button Music";
                                    flagOnMusic = true;
                                    System.out.println(" flagOFF: " + flagOnMusic);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("messageMusic", minimaMusic);
                                    Message message = HandlingCamMusic.getInstance().handlerMusic.obtainMessage();
                                    message.setData(bundle);
                                    HandlingCamMusic.getInstance().handlerMusic.sendMessage(message);
                                }
                            }
                        }
                    });

                    /*if the user doesnt push the button then accordingly to the command it took the sound plays or it doesnt */
                    if(!flagOnMusic)
                    {
                        if (message.toString().equals("ON SOUND"))
                        {
                            HandlingCamMusic.getInstance().buttonSoundOn.start();
                            minimaMusic = "Music is ON";
                        }
                        else if (message.toString().equals("OFF SOUND"))
                        {
                            if (HandlingCamMusic.getInstance().buttonSoundOn.isPlaying()) {
                                HandlingCamMusic.getInstance().buttonSoundOn.pause();
                            }
                            minimaMusic = "Music is OFF";
                        }
                    }

                    /*we send a message to the main thread, so that the main screen will be refreshed and we will have the appropriate changes */
                    System.out.println("minima: " + minimaMusic);
                    Bundle bundle = new Bundle();
                    bundle.putString("messageMusic", minimaMusic);
                    Message messageMusic = HandlingCamMusic.getInstance().handlerMusic.obtainMessage();
                    messageMusic.setData(bundle);
                    HandlingCamMusic.getInstance().handlerMusic.sendMessage(messageMusic);
                    //there is a slim delay when the user needs to push the button

                }
                /* we follow the same guidance as the Music part , but with the difference that we use the flash variable for the opening and closing of the flash*/
                else if(message.toString().contains("FLASH"))
                {

                    System.out.println("Thread-2 Send: " + message.toString());
                    HandlingCamMusic.getInstance().cameraButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {

                            if(!flagOnFlash)
                            {
                                if (message.toString().equals("ON FLASH")) {
                                    if (flash) {
                                        try {
                                            flash = flashOff();
                                        } catch (CameraAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    minimaCamera = "It's OFF button Flash";
                                    Bundle bundle = new Bundle();
                                    bundle.putString("messageCamera", minimaCamera);
                                    Message message = HandlingCamMusic.getInstance().handlerCamera.obtainMessage();
                                    message.setData(bundle);
                                    HandlingCamMusic.getInstance().handlerCamera.sendMessage(message);
                                    flagOnFlash = true;
                                    System.out.println(" flagON: " + flagOnFlash);

                                } else if (message.toString().equals("OFF FLASH")) {
                                    if (!flash) {
                                        try {
                                            flash = flashOn();
                                        } catch (CameraAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    minimaCamera = "It's ON button Flash ";
                                    Bundle bundle = new Bundle();
                                    bundle.putString("messageCamera", minimaCamera);
                                    Message message = HandlingCamMusic.getInstance().handlerCamera.obtainMessage();
                                    message.setData(bundle);
                                    HandlingCamMusic.getInstance().handlerCamera.sendMessage(message);
                                    flagOnFlash = true;
                                    System.out.println(" flagON: " + flagOnFlash);
                                }
                            }
                        }
                    });

                    if (!flagOnFlash)
                    {
                        if (message.toString().equals("ON FLASH")) {
                            try {
                                flash = flashOn();
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                            minimaCamera = "Camera It's ON";
                        } else if (message.toString().equals("OFF FLASH")) {
                            try {
                                flash = flashOff();
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }

                            minimaCamera = "Camera It's OFF";
                        }
                    }
                    Bundle bundle = new Bundle();
                    String msg = minimaCamera;
                    bundle.putString("messageCamera", msg);
                    Message messageFlash = HandlingCamMusic.getInstance().handlerCamera.obtainMessage();
                    messageFlash.setData(bundle);
                    HandlingCamMusic.getInstance().handlerCamera.sendMessage(messageFlash);
                }
                try
                {
                    sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }


                //---------------------------------------------------------------//
            }
//            System.out.println("TEMP MSG!!!!!!!" + Shared_string.get_message_arrived());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public void disconnect(){
        try {
            Client.disconnect();
        }catch (Exception e){}
    }


/*Function for utilizing the camera manager for opening  flash of the front camera */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean flashOn() throws CameraAccessException {

        CameraManager camManager = (CameraManager) HandlingCamMusic.getInstance().cameraContext.getSystemService(Context.CAMERA_SERVICE);
        String cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
        camManager.setTorchMode(cameraId, true);
        return true;

    }
    /*Function for utilizing the camera manager for closing  flash of the front camera */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean  flashOff() throws CameraAccessException {

        CameraManager camManager = (CameraManager) HandlingCamMusic.getInstance().cameraContext.getSystemService(Context.CAMERA_SERVICE);
        String cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
        camManager.setTorchMode(cameraId, false);
        return false;

    }

}
