package com.example.ntinos.project2017;

/**
 * Created by Ntinos on 28-Nov-17.
 */

public class Shared_string {
    private static String message_arrived = "ON1";

    public static  void change(String replacment){
        message_arrived = replacment;
    }

    public static  String get_message_arrived(){
        return message_arrived;
    }

}

