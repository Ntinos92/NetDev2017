package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ToggleButton;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.media.MediaPlayer;



public class MainActivity extends AppCompatActivity  {

    @RequiresApi(api = Build.VERSION_CODES.M)
    settings_thread mqttsubscriberSettings = null;
    publisher_thread publisherData = null;
    connection_mythread cm = null;
    String address = null;
    ToggleButton toggle;
    ImageButton popButton;
    ImageButton backArrow;
    protected String RecieveMessageMusic;
    protected String RecieveMessageCamera;
    protected String RecieveConnectionState;
    // handler for the Music Button
    protected Handler handlerMusic = new Handler()
    {
        @Override
        public void handleMessage(Message message)
        {
            Bundle bundle = message.getData();
            RecieveMessageMusic = bundle.getString("messageMusic");
            //kanei ananewsei to view
            TextView textViewMusic = (TextView) findViewById(R.id.MusicStatus);
            textViewMusic.setText(RecieveMessageMusic);
            System.out.println("Music Message recieved --- " + RecieveMessageMusic );
        }
    };

    // handler for the Camera Button
    protected Handler  handlerCamera = new Handler ()
    {
        @Override
        public void  handleMessage (Message message)
        {
            Bundle bundle = message.getData();
            RecieveMessageCamera = bundle.getString("messageCamera");
            //kanei ananewsei to view
            TextView textViewCamera = (TextView) findViewById(R.id.CameraStatus);
            textViewCamera.setText(RecieveMessageCamera);
            System.out.println("Camera Message recieved --- " + RecieveMessageCamera );
        }
    };
    // handler for the Connection Button
    protected Handler handlerConnection = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           Bundle bundle = msg.getData();
           RecieveConnectionState = bundle.getString("messageConnection");
           //
           TextView textViewConnection = (TextView) findViewById(R.id.Connection_Status);
           textViewConnection.setText(RecieveConnectionState);
           System.out.println("Connection State received ---" + RecieveConnectionState);
        }
    };








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer buttonSoundOn = MediaPlayer.create(this,R.raw.motor);
        Button musicButton =  findViewById(R.id.musiButton);
        Button cameraButton = findViewById(R.id.cameraButton);
        popButton =  findViewById(R.id.PopButton);
        backArrow = findViewById(R.id.backarrow);
        toggle =  findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    //Do something when Switch button is on/checked
                    ON();

                } else {
                    //Do something when Switch is off/unchecked
                    OFF();
                }
            }
        });

// Instances that holds the button for Music , the button for Camera and the Context of MainActivity and the handler for each of them
        HandlingCamMusic.getInstance().buttonSoundOn = buttonSoundOn;
        HandlingCamMusic.getInstance().handlerMusic = handlerMusic;
        HandlingCamMusic.getInstance().musicButton = musicButton;
        HandlingCamMusic.getInstance().cameraButton = cameraButton;
        HandlingCamMusic.getInstance().cameraContext = this;
        HandlingCamMusic.getInstance().handlerCamera =  handlerCamera;
        HandlingCamMusic.getInstance().handlerConnection = handlerConnection;

        System.out.println("Main Thread open UI" );
// Pop ups a message that shows Copyright of the Application
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popupview);
        TextView txt = dialog.findViewById(R.id.textbox1);
        txt.setText("Welcome to iFeels ....\n\nCopyrights: three dudes");
        dialog.show();

            if(cm==null){
                connection_mythread cm = new connection_mythread("connection_mythread",MainActivity.this);
                cm.start();
            } else if (cm!=null)
            {
               cm.resume();
            }

        View.OnClickListener click_listener_2 = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        };

        backArrow.setOnClickListener(click_listener_2);

        View.OnClickListener click_listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, popButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mqtt_settings:
                                startActivity(new Intent(getApplicationContext(), mqtt_settings.class));
                                return true;
                            case R.id.exit:
                                finish();
                                System.exit(0);
                                return true;


                            default:
                                return false;
                        }
                    }
                });

                popup.show(); //showing popup menu
            }
        };
        popButton.setOnClickListener(click_listener);
    }


// The OnBack Arrow that popups a message if you want to exit
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        stopALLthreads();
                        System.exit(0);
                    }
                }).create().show();

    }



    public  void ON(){ //toggle buttom Online

        address = "MACadress";
        if(mqtt_settings.port.equals("none") && mqtt_settings.ipaddress.equals("none") && mqtt_settings.frequency.equals("none")){

            final Toast toast = Toast.makeText(this, "Adjust Mqtt Settings first!!", Toast.LENGTH_SHORT);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 2000);

            toggle.setChecked(false);
            return;
        }

        invalidateOptionsMenu();//hide android settings from menu

        if(mqttsubscriberSettings == null && publisherData == null) {

            mqttsubscriberSettings = new settings_thread("settings_thread", "Subscriber_for_settings " + address, "SETTINGS", MainActivity.this);
            mqttsubscriberSettings.start(); // start subscriber thread


            publisherData = new publisher_thread("publisher_thread",address, "DATA");
            publisherData.start(); //start publisher thread

        }
        else if (mqttsubscriberSettings!= null && publisherData != null){
            publisherData.resume(); // resume publisher thread
            mqttsubscriberSettings.resume(); // resume subscriber thread

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M) //toggle button offline
    public void OFF() {

        invalidateOptionsMenu();//reappear android settings on menu

        if (mqttsubscriberSettings != null && publisherData != null && cm!=null) {
            publisherData.pause(); // pause publisher thread
            mqttsubscriberSettings.pause(); // pause subscriber thread
            cm.pause();
        }



    }

    @Override
     protected void onDestroy() // When we close the app
    {
        stopALLthreads(); // all the threads stop
        super.onDestroy(); // close the window of the app
        System.out.println("Destroy Destroy Destroy Destroy Destroy");
    }

    public void stopALLthreads(){
        if( mqttsubscriberSettings != null && publisherData != null && cm!=null) {
            mqttsubscriberSettings.stop();
            publisherData.stop();
            cm.stop();
        }
    }


}


