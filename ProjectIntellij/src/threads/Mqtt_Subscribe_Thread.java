package threads;
import main.MqttSubscriber;
public class Mqtt_Subscribe_Thread implements Runnable{
    private String threadname;
    private Thread thread;
    MqttSubscriber subscriber = new MqttSubscriber();

    public Mqtt_Subscribe_Thread(String name) { //constructor
        threadname = name;
    }


    @Override
    public void run() {
        subscriber.Subscribe();
    }


    public void start(){
        System.out.println("Starting " +  threadname );
        if(thread == null){
            thread = new Thread(this,threadname);
            thread.start();//Thread start calls run()
        }
    }

    public void stop(){

        subscriber.disconect();
        thread.interrupt();
    }

}
