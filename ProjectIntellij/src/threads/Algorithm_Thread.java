package threads;

import main.MqttPublisher;
import main.shared_string;

import java.util.concurrent.Semaphore;

public class Algorithm_Thread extends my_thread {
    Semaphore sem;
    Semaphore sem2;
    int Synck = 0;
    int status=1;
    int test;
    public Algorithm_Thread(String name,Semaphore sem,Semaphore sem2) {
        super(name);
        this.sem = sem;
        this.sem2 = sem2;
    }


    @Override
    public void operate() throws Exception {

        // acquiring the lock
        sem2.acquire();
        if(shared_string.get_message_arrived() !=null && shared_string.get_message_arrived()=="0"){
            test = Integer.parseInt(shared_string.get_message_arrived());
            System.out.println(shared_string.get_message_arrived()+"\n");
            if(test==0){
                System.out.println(test+"\n");
                status=0;
                System.exit(0);
            }
        }
        while(shared_string.get_message_arrived() != null && status==1) {
            if (MessageBuffer.Entoles2 != null && !(MessageBuffer.Entoles2.isEmpty())) {
                System.out.println("MessageBuffer FinalLabel: " + MessageBuffer.Entoles2.get(0));
                {
                    System.out.println("MessageBuffer PreviousLabel: " + MessageBuffer.Entoles2.get(1));
                    System.out.println("Synk: " + Synck);
                    Synck++;
                    if (MessageBuffer.Entoles2.get(0).toLowerCase().contains("EyesOpened".toLowerCase())) {


                        System.out.print("------------------FrequencyON: " + Double.parseDouble(shared_string.get_message_arrived())+"\n");
                        double frequency = Double.parseDouble(shared_string.get_message_arrived());
                        try {
                            Thread.sleep((long)frequency * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    new MqttPublisher("Java_Server_Publisher", "SETTINGS", "ON FLASH").publish();


                        new MqttPublisher("Java_Server_Publisher", "SETTINGS", "ON SOUND").publish();

                    } else {

                        System.out.println("shared_string.get_message_arrived(): {" + shared_string.get_message_arrived() +"}");
                        System.out.println("------------------FrequencyOFF:  {" + Double.parseDouble(shared_string.get_message_arrived()) +"}");

                        double frequency = Double.parseDouble(shared_string.get_message_arrived());
                        try {
                            Thread.sleep((long)frequency * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        new MqttPublisher("Java_Server_Publisher", "SETTINGS", "OFF FLASH").publish();


                        new MqttPublisher("Java_Server_Publisher", "SETTINGS", "OFF SOUND").publish();

                    }
                    //adoiazoume ton buffer tiw prwtes 2 entoles diotis tis diivazoume ana 2
                    MessageBuffer.Entoles2.remove(0);
                    MessageBuffer.Entoles2.remove(0);

                    System.out.println("-------------------------------------------------------");
                    if(MessageBuffer.Entoles2.isEmpty())
                    {
                        System.out.println("KNN Algorithm had  "+ MessageBuffer.statistics*100+"% success");
                        System.exit(1);
                    }

                }

            }
        }
        // Release the permit.
        sem.release();
    }

}