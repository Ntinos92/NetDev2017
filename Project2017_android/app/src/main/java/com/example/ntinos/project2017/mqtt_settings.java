package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class mqtt_settings extends AppCompatActivity implements View.OnClickListener{

    static String ipaddress = "none";
    static String port = "none";
    static String frequency= "none";
    Button btn;
    EditText given_ipaddress;
    EditText given_port;
    EditText given_frequency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt_settings);

        btn = (Button) findViewById(R.id.mqtt_button) ;
        btn.setOnClickListener(this);

        given_ipaddress = (EditText) findViewById(R.id.ipaddress);
        given_port = (EditText) findViewById(R.id.port);
        given_frequency = (EditText) findViewById(R.id.frequency);
    }

    @Override
    public void onClick(View v) {


        ipaddress = given_ipaddress.getText().toString();
        port = given_port.getText().toString();
        frequency = given_frequency.getText().toString();

        final Toast toast = Toast.makeText(this, "IP ADDRESS : "+ipaddress+"\n"+"PORT : "+port+"\n"+"FREQUENCY IN SECONDS : "+frequency, Toast.LENGTH_LONG);
        toast.show();
        System.out.println("IP ADDRESS : "+ipaddress+"\n"+"PORT : "+port+"\n"+"FREQUENCY IN SECONDS : "+frequency);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 10000);
        finish();
    }
}

