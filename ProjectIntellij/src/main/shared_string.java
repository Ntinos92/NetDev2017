package main;
public class shared_string {

    private static String message_arrived = null;


    public static synchronized void change(String replacment){
        message_arrived = replacment;
    }

    public static synchronized String get_message_arrived(){
        return message_arrived;
    }

}
