package main;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
public class MqttSubscriber implements MqttCallback {
    MqttClient client;
    //public static String message_arrived = null;

    public MqttSubscriber() {}

    public void Subscribe() {
        String	topic  = "DATA";
        String  topic2 ="EXIT";
        int qos =	2;
        String	broker	= "tcp://"+Main.mqqtsettings;
        String	clientId =	"FlashLight";
        MemoryPersistence persistence	=	new	MemoryPersistence();
        try	{
//Connect	client	to	MQTT	Broker
            MqttClient sampleClient =	new	MqttClient(broker,	clientId,	persistence);
            MqttConnectOptions connOpts =	new	MqttConnectOptions();
            connOpts.setCleanSession(true);
//Set	callback
            MqttSubscriber p = new MqttSubscriber();
            sampleClient.setCallback(p);
            System.out.println("Connecting	to	broker:	"+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
//Subscribe	to	a	topic
            System.out.println("Subscribing	to	topic	\""+topic+"\"	qos "+qos);
            sampleClient.subscribe(topic,	qos);
            System.out.println("Subscribing	to	topic	\""+topic2+"\"	qos "+qos);
            sampleClient.subscribe(topic2, qos);
        }	catch(MqttException me)	{
            System.out.println("reason	"	+	me.getReasonCode());
            System.out.println("msg "	+	me.getMessage());
            System.out.println("loc "	+	me.getLocalizedMessage());
            System.out.println("cause	"	+	me.getCause());
            System.out.println("excep "	+	me);
            me.printStackTrace();
        }
    }

    public void disconect(){
        try{
            client.disconnect();
            System.out.println("Disconnected");
        }catch(MqttException me){}
    }


    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            System.out.println("Message arrived: \"" + message.toString()
                    + "\" on topic \"" + topic.toString() + "\"");

            shared_string.change(message.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}


