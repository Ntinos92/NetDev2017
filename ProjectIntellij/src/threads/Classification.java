package threads;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Classification extends my_thread{// implements Runnable {

    ResultSet result = null;
    int numberOfColumns = 0;
    Semaphore sem;
    Semaphore sem2;
    int synk = 0;
    int temp=0;
    public Classification(String name,ResultSet result, int numberOfColumns,Semaphore sem,Semaphore sem2) {
        super(name);
        this.result = result;
        this.sem=sem;
        this.sem2 = sem2;
        this.numberOfColumns = numberOfColumns;
    }

    @Override
    public void operate() throws InterruptedException {

        List<Double> EntropysSensors = new ArrayList<>();
        String EntropyLabel = null;
        boolean next = true;
        // acquiring the lock
        sem.acquire();
        //pairnei to kathe row apo to result pou htan to apotelesma tou query sthn vash mas sto table entropys
        try {
            if (this.result.next())
            {
                next = true;
            } else {
                next = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //koitazei an yparxei akomh kai an den yparxei teleiwsan ta dedomena opote teleiwnei kai to programma
        if(next == true) {

            EntropyLabel = null;
            EntropysSensors.clear();
            CSVReader TrainningCsvReader = null;
            //diavazoume apo to Training Set.csv
            try {
                //Reading from csv file
                // File folder = new File("C:\\Users\\EPAMEINONDAS\\Desktop\\Anaptyxi2017Teliko-master-5a2c8a5388d4c6dde8f438edc21423e1f1992835\\ProjectIntellij\\Training Set.csv");
                TrainningCsvReader = new CSVReader(new FileReader("C:\\Users\\MANOS\\IdeaProjects\\ProjectIntellij\\Training Set for Software Development\\Training Set.csv"), ',');
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    throw new Exception("Error with CSV file " + e.getMessage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            String[] TrainningheaderRow = new String[0];
            try {
                TrainningheaderRow = TrainningCsvReader.readNext();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null == TrainningheaderRow) {
                try {
                    throw new FileNotFoundException("CSV file is empty" +
                            "Or change its format");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 2; i <= this.numberOfColumns; i++) {

                if (i == 2) {
                    try {
                        EntropyLabel = this.result.getString(i);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {
                        EntropysSensors.add(this.result.getDouble(i));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }

            System.out.println("EntropysSensors: " + EntropysSensors);
            String FinalLabel = null;
            try {
                FinalLabel = Classification(TrainningCsvReader, EntropyLabel, EntropysSensors);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TrainningCsvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            temp++;
            System.out.println("TEMP: " + temp);


            {

                System.out.println("Previous LABEL: " + EntropyLabel);
                System.out.println("----> FINAL LABEL: " + FinalLabel);
                System.out.println("ClassSynk: " + synk);
                synk++;
                MessageBuffer.Entoles2.add(FinalLabel);
                MessageBuffer.Entoles2.add(EntropyLabel);


                if (EntropyLabel.equals(FinalLabel)) {
                    MessageBuffer.statsSuccess++;
                }

                MessageBuffer.statsCounter++;

            }

        }
        else{
            MessageBuffer.end = 0;
            MessageBuffer.statistics = MessageBuffer.statsSuccess/MessageBuffer.statsCounter ;
            MessageBuffer.statistics = round(MessageBuffer.statistics,2);

        }
        sem2.release();
        // Release the permit.
    }

    public static String Classification(CSVReader TrainningCsvReader, String EntropyLabel, List<Double> EntropysSensors) throws Exception {

        String[] nextLine;
        List<Double> EuclideanDistance =  new ArrayList<Double>();
        List<String> Labels = new ArrayList<String>();
        String Label = null;
        double euclidian = 0.0;
        double DistEntropyValueAF3 = 0.0;
        double DistEntropyValueF7 = 0.0;
        double DistEntropyValueF3 = 0.0;
        double DistEntropyValueFC5 = 0.0;
        double DistEntropyValueT7 = 0.0;
        double DistEntropyValueP7 = 0.0;
        double DistEntropyValueO1 = 0.0;
        double DistEntropyValueO2 = 0.0;
        double DistEntropyValueP8 = 0.0;
        double DistEntropyValueT8 = 0.0;
        double DistEntropyValueFC6 = 0.0;
        double DistEntropyValueF4 = 0.0;
        double DistEntropyValueF8 = 0.0;
        double DistEntropyValueAF4 = 0.0;
        /*
         ***********************************
         * Ypologisimos Euclidian distance *
         ***********************************
         */
     //   System.out.println("EntropysSensors.size(): " + EntropysSensors.size());
        while ((nextLine = TrainningCsvReader.readNext()) != null)
        {
            //pairnoume thn prwth sthlh tou tranning set ppou einai to label tou
            if( nextLine[0].toLowerCase().contains("EyesClosed".toLowerCase()))
            {
                Label = "EyesClosed";
            }
            else
            {
                Label = "EyesOpened";
            }
            //Gia kathe mia apo tis tis stiles to trainning set xvris to label
            for(int i=1; i< 15; i++)
            {
                //kai gia kathe entropia pou einai sthn lista vreikoume thn apostash gia kathe shmeio (AF3, F7, ktlp)  gia na synexisoume na ypologisouem thn eukleidia
                for (int e = 0; e <  EntropysSensors.size(); e++)
                {
                    if (e == 0 && i == 1)
                        DistEntropyValueAF3 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 1 && i == 2)
                        DistEntropyValueF7 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 2 && i == 3)
                        DistEntropyValueF3 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 3 && i == 4)
                        DistEntropyValueFC5 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 4 && i == 5)
                        DistEntropyValueT7 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 5 && i == 6)
                        DistEntropyValueP7 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 6 && i == 7)
                        DistEntropyValueO1 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 7 && i == 8)
                        DistEntropyValueO2 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 8 && i == 9)
                        DistEntropyValueP8 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 9 && i == 10)
                        DistEntropyValueT8 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 10 && i == 11)
                        DistEntropyValueFC6 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 11 && i == 12)
                        DistEntropyValueF4 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 12 && i == 13)
                        DistEntropyValueF8 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                    else if (e == 13 && i == 14)
                        DistEntropyValueAF4 = Math.abs((Double.parseDouble(nextLine[i])) - EntropysSensors.get(e));
                }
            }
            euclidian = Math.sqrt( (DistEntropyValueAF3*DistEntropyValueAF3) +  (DistEntropyValueF7*DistEntropyValueF7) + (DistEntropyValueF3*DistEntropyValueF3) + (DistEntropyValueFC5*DistEntropyValueFC5) + (DistEntropyValueT7*DistEntropyValueT7) + (DistEntropyValueP7*DistEntropyValueP7) + (DistEntropyValueO1*DistEntropyValueO1) + (DistEntropyValueO2*DistEntropyValueO2) + (DistEntropyValueP8*DistEntropyValueP8) + (DistEntropyValueT8*DistEntropyValueT8) + (DistEntropyValueFC6*DistEntropyValueFC6) + (DistEntropyValueF4*DistEntropyValueF4) + (DistEntropyValueF8*DistEntropyValueF8) + (DistEntropyValueAF4*DistEntropyValueAF4));
            Labels.add(Label);
            EuclideanDistance.add(euclidian);
        }


        /*
         *****************************************
         * Ypologisimos synolou I kai counter Yi *
         *****************************************
         */
        int k = 11;
        List<Double> Synolo_I =  new ArrayList<>();
        List<String> Synolo_I_Labels = new ArrayList<>();
        int counterEyesClosed = 0;
        int counterEyesOpened = 0;
        //vazoume sto synolo I tis k mirkoteres times apo thn lista me thn euklidia apostash
        for (int y=0; y<k; y++)
        {
            int indexof = EuclideanDistance.indexOf(Collections.min(EuclideanDistance));
            Synolo_I.add(EuclideanDistance.get(indexof));
            /*
            Vazoume tautgxrona me thn apo panw lista kai mia lista me ta Labels gia tis antistoixes ka mikres times dhladh thn lista Synolo_I_Labels
            . Kanoume loipon epishs kai mai sygkrish sthn Lista Labels label me to indexof pou vrikame kai metrame
            poia einai eyesclosed kai poia eyesopened
             */
            Synolo_I_Labels.add(Labels.get(indexof));
            if(Labels.get(indexof).toLowerCase().contains("EyesClosed".toLowerCase()))
            {
                counterEyesClosed++;
            }
            else
            {
                counterEyesOpened++;
            }

            Labels.remove(indexof);
            EuclideanDistance.remove(indexof);
        }

        /*
         ********************************
         * Ypologisimos Metrikhs varous *
         ********************************
         */

        double w_EyesClosed = 0.0;
        double w_EyesOpened = 0.0;
        int eyesclosed = 0;
        int eyesopened = 0;
        for (int y=0; y<k; y++)
        {
            /*
            Exoume apothkeusei apo prin 2 listes pou h mia exei tis k mikroteres times kai oi allh ta k Labels gia aytes tis k mikroteres times.
            Opote epeidh einai sthn idia seira vlepoyme gia to antistoixo Label na mpoume kai an ypologisoume to varos
             */
            if(Synolo_I_Labels.get(y).toLowerCase().contains("EyesClosed".toLowerCase()))
            {
                w_EyesClosed = w_EyesClosed + (1/Synolo_I.get(y));
                eyesclosed++;
            }
            else
            {
                w_EyesOpened = w_EyesOpened + (1/Synolo_I.get(y));
                eyesopened++;
            }
        }

        double FinalEyesOpened = w_EyesOpened * counterEyesOpened;
        double FinalEyesClosed = w_EyesClosed * counterEyesClosed;

        //Sygkrish gia thn emfanish tou telikou apotelesmatos.
        if ( FinalEyesOpened > FinalEyesClosed )
        {
            Label = "EyesOpened";
        }
        else
        {
            Label = "EyesClosed";
        }
        return Label;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }



}
