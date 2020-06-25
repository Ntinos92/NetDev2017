package main;

import threads.Algorithm_Thread;
import threads.Classification;
import threads.MessageBuffer;
import threads.Mqtt_Subscribe_Thread;
import java.io.File;
import java.sql.*;
import java.util.concurrent.Semaphore;

public class Main {
 //   public static String mqqtsettings = "192.168.88.107";
    public static String mqqtsettings = "iot.eclipse.org";
    private static final String JDBC_CONNECTION_URL = "jdbc:mysql://localhost:3306/anaptixh2017";


    private static Connection getCon()
    {
        Connection connection = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            //to onoma tis vasei kai oi kwdikoi
            //password Nwnta = "ted2017"
            connection = DriverManager.getConnection(JDBC_CONNECTION_URL,"root","manos1234");
            if (connection == null)
            {
                System.out.println("connection is null");
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return connection;
    }




    public static void main(String[] args) throws Exception {

        System.out.println("Running Application");
        try
        {
            CsvFiles loader = new CsvFiles(getCon());
            loader.setSeprator(',');
            //pairnoume to onoma tou fakelou pou exoyme ta csv gia na xekinhsei na ta pairnei
           // File folder = new File("C:\\Users\\EPAMEINONDAS\\Desktop\\Anaptyxi2017Teliko-master-5a2c8a5388d4c6dde8f438edc21423e1f1992835\\ProjectIntellij\\Data Final for Software Development");
             File folder = new File("C:\\Users\\MANOS\\IdeaProjects\\ProjectIntellij\\Data Final for Software Development");
            File[] files = folder.listFiles();
            String tableName = null;
            //gia kathe arxeio csv pou einai sto directory mas
            for (File file : files) {

                //pairnoume to onoma tou kai analoga an einai eyesClosed h eyesOpened vazoume to antistoixo tablename
                if (file.getName().toLowerCase().contains("EyesClosed".toLowerCase()))
                {
                //    tableName = "entropy_eyes_closed";
                    tableName = "EyesClosed";
                }
                else if(file.getName().toLowerCase().contains("EyesOpened".toLowerCase()))
                {
                //    tableName = "entropy_eyes_opened";
                    tableName = "EyesOpened";
                }
                // File folder = new File("C:\\Users\\EPAMEINONDAS\\Desktop\\Anaptyxi2017Teliko-master-5a2c8a5388d4c6dde8f438edc21423e1f1992835\\ProjectIntellij\\Data Final for Software Development");
                loader.loadCSV("C:\\Users\\MANOS\\IdeaProjects\\ProjectIntellij\\Data Final for Software Development\\" +file.getName(), "entropys",tableName,file.getName());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //kanoume ena query sthn vash mas gia paroume oles tis entropies pou exoume ypologisei gia kathe ena apo ta csv arxeia
        ResultSet result = ((getCon()).createStatement()).executeQuery("SELECT * FROM "+"entropys");
        ResultSetMetaData resultMetaData = result.getMetaData();
        int numberOfColumns = resultMetaData.getColumnCount(); ///exoume kai ton arithmo twn columns

        while (mqqtsettings == null){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Xekinane ta thread mas
        Mqtt_Subscribe_Thread mt = new Mqtt_Subscribe_Thread("Mqttsubscriber");

        //Semaphores pou tha xrhsimopoihsoume gia na syngxronisoume to classification thread kai to Algorithm_Thread
        Semaphore sem = new Semaphore(1);
        Semaphore sem2 = new Semaphore(1);
        Algorithm_Thread at = new Algorithm_Thread("AlgorithmThread",sem,sem2);
        //Enarxh classification thread pou pairnei ta result tou query kai to number of columns kai ginei h diadikasia ths katigoriopoihshs
        Classification classification = new Classification("Classification",result,numberOfColumns,sem,sem2);
        mt.start();
        //O buffer mas pou pernane mesa ta apotelesmata tou classification pou einai ousiastika kai to ena kai entolh. dhladh to teliko label pou tha parei tha einai kai entolh prow to android
        MessageBuffer messageBuffer = new MessageBuffer();
        Thread classificationThread = new Thread(classification);
        classificationThread.start();
        at.start();
        System.out.println("--------------------END OF MAIN -----------------");
    }

}
