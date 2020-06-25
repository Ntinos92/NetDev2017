package main;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static featureSelectionMetricsPackage.calculateEntropy.calculateEntropy;

public class CsvFiles
{
    private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
    private static final String TABLE_REGEX = "\\$\\{table\\}";
    private static final String KEYS_REGEX = "\\$\\{keys\\}";
    private static final String VALUES_REGEX = "\\$\\{values\\}";
    private Connection connection;
    private char seprator;

    public CsvFiles(Connection connection) {
        this.connection = connection;
        //Set default separator
        this.seprator = ',';
    }

    public void loadCSV(String FromCsvFile, String tableName,String CSVname, String name) throws Exception
    {

        CSVReader FromCsvReader = null;
        if(null == this.connection)     //Check if connection is made
        {
            throw new Exception("Connection Failed!");
        }

        //pairnoume apo to table name pou theloume na valoume tis entropies tis onomasies tou
        ResultSet result = ((this.connection).createStatement()).executeQuery("SELECT * FROM "+"entropys");
        ResultSetMetaData resultMetaData = result.getMetaData();
        int numberOfColumns = resultMetaData.getColumnCount();
        ArrayList<String> names = new ArrayList<String>();
        for (int i =2; i<=numberOfColumns; i++) {
            names.add(resultMetaData.getColumnName(i));
        }

        try
        {
            //Reading from csv file
            FromCsvReader = new CSVReader(new FileReader(FromCsvFile), this.seprator);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("Error with CSV file "+ e.getMessage());
        }


        String[] FromheaderRow = FromCsvReader.readNext();


        if (null == FromheaderRow )
        {
            throw new FileNotFoundException("CSV file is empty" +
                    "Or change its format");
        }

        String questionmarks = StringUtils.repeat("?,", 15);
        questionmarks = (String) questionmarks.subSequence(0, questionmarks.length() - 1);

        //Query to insert into database
        String query = SQL_INSERT.replaceFirst(TABLE_REGEX, "entropys");
        query = query.replaceFirst(KEYS_REGEX, StringUtils.join(names, ","));
        query = query.replaceFirst(VALUES_REGEX, questionmarks);



        String[] nextLine;
        Connection con = null;
        PreparedStatement ps = null;

        //listes me oles tis times gia to kathe channel tou csv arxeiou
        List<Double> AF3 =  new ArrayList<Double>();
        List<Double> F7 =  new ArrayList<Double>();
        List<Double> F3 =  new ArrayList<Double>();
        List<Double> FC5 =  new ArrayList<Double>();
        List<Double> T7 =  new ArrayList<Double>();
        List<Double> P7 =  new ArrayList<Double>();
        List<Double> O1 =  new ArrayList<Double>();
        List<Double> O2 =  new ArrayList<Double>();
        List<Double> P8 =  new ArrayList<Double>();
        List<Double> T8 =  new ArrayList<Double>();
        List<Double> FC6 =  new ArrayList<Double>();
        List<Double> F4 =  new ArrayList<Double>();
        List<Double> F8=  new ArrayList<Double>();
        List<Double> AF4 =  new ArrayList<Double>();

        try
        {
            con = this.connection;
            con.setAutoCommit(false);
            ps = con.prepareStatement(query);
            double[] value = new double[1];
            int flag = 0;
            //Reads each line of the file until the end of it
            while ((nextLine = FromCsvReader.readNext()) != null)
            {
                //untill the end of file
                if (null != nextLine)
                {

                    //tsekaroume na paroume tis times
                    // mas endiaferie kai h poiothta syndeseis opote an vroume timh poy einai katw apo 3,4 apokleioume thn sygkekrimenh
                    //grammi me ta channels kai pame sthn epomenh
                    for (int y=29; y>=0; y--)//String string : nextLine)
                    {
                        //an den yparxei kalh syndesh me kapoion aisthitira tha skiparouome aythn thn grammh. Exw valei na pairnei aishithres mono me poiothta syndeshs 3 h 4
                        if (y >= 14 && y<=27 && (Double.parseDouble(nextLine[y]) < 3))
                        {
                            flag++;
                            break;
                        }
                        //analoga to channel to vazoume sthn antistoixi lista
                        if( y == 0)
                        {
                            AF3.add(Double.parseDouble(nextLine[y]));
                        }
                        else if(y == 1)
                            F7.add(Double.parseDouble(nextLine[y]));
                        else if(y == 2)
                            F3.add(Double.parseDouble(nextLine[y]));
                        else if(y == 3)
                            FC5.add(Double.parseDouble(nextLine[y]));
                        else if(y == 4)
                            T7.add(Double.parseDouble(nextLine[y]));
                        else if(y == 5)
                            P7.add(Double.parseDouble(nextLine[y]));
                        else if(y == 6)
                            O1.add(Double.parseDouble(nextLine[y]));
                        else if(y == 7)
                            O2.add(Double.parseDouble(nextLine[y]));
                        else if(y == 8)
                            P8.add(Double.parseDouble(nextLine[y]));
                        else if(y == 9)
                            T8.add(Double.parseDouble(nextLine[y]));
                        else if(y == 10)
                            FC6.add(Double.parseDouble(nextLine[y]));
                        else if(y == 11)
                            F4.add(Double.parseDouble(nextLine[y]));
                        else if(y == 12)
                            F8.add(Double.parseDouble(nextLine[y]));
                        else if(y == 13)
                            AF4.add(Double.parseDouble(nextLine[y]));
                    }
                }
            }

            //Afou exei skiparei aythn thn grammi an den exei kalh syndesh me ton aiisthitira oi listes tha einai kenes. Opote tha empainan sthn vash mas midenikes entropies
            //Opote vazoume mono an einai gemates oi lsites na ypologistoun oi entropies kai na mpin sthn vash. Diwnixoume dhladh midenika row .
            if(!(AF3.isEmpty()) && !(F7.isEmpty())  && !(F3.isEmpty()) && !(FC5.isEmpty()) && !(T7.isEmpty()) && !(P7.isEmpty()) && !(O1.isEmpty()) && !(O2.isEmpty()) && !(T8.isEmpty()) && !(P8.isEmpty()) && !(FC6.isEmpty()) && !(F4.isEmpty()) && !(F8.isEmpty()) && !(AF4.isEmpty()) )
            {

                //sthn apo katw diadikasia gia kathe lista thn metatrepoyme se array
                double[] targetAF3 = new double[AF3.size()];
                double[] targetF7 = new double[F7.size()];
                double[] targetF3 = new double[F3.size()];
                double[] targetFC5 = new double[FC5.size()];
                double[] targetT7 = new double[T7.size()];
                double[] targetP7 = new double[P7.size()];
                double[] targetO1 = new double[O1.size()];
                double[] targetO2 = new double[O2.size()];
                double[] targetP8 = new double[P8.size()];
                double[] targetT8 = new double[T8.size()];
                double[] targetFC6 = new double[FC6.size()];
                double[] targetF4 = new double[AF3.size()];
                double[] targetF8 = new double[F8.size()];
                double[] targetAF4 = new double[AF4.size()];
                targetAF3 = ListToArray(AF3, targetAF3);
                targetF7 = ListToArray(F7, targetF7);
                targetF3 = ListToArray(F3, targetF3);
                targetFC5 = ListToArray(FC5, targetFC5);
                targetT7 = ListToArray(T7, targetT7);
                targetP7 = ListToArray(P7, targetP7);
                targetO1 = ListToArray(O1, targetO1);
                targetO2 = ListToArray(O2, targetO2);
                targetP8 = ListToArray(P8, targetP8);
                targetT8 = ListToArray(T8, targetT8);
                targetFC6 = ListToArray(FC6, targetFC6);
                targetF4 = ListToArray(F4, targetF4);
                targetF8 = ListToArray(F8, targetF8);
                targetAF4 = ListToArray(AF4, targetAF4);

                //kai twra ypologizoume thn entropia gia kathe channel
                double EntropyValueAF3 = calculateEntropy(targetAF3);
                double EntropyValueF7 = calculateEntropy(targetF7);
                double EntropyValueF3 = calculateEntropy(targetF3);
                double EntropyValueFC5 = calculateEntropy(targetFC5);
                double EntropyValueT7 = calculateEntropy(targetT7);
                double EntropyValueP7 = calculateEntropy(targetP7);
                double EntropyValueO1 = calculateEntropy(targetO1);
                double EntropyValueO2 = calculateEntropy(targetO2);
                double EntropyValueP8 = calculateEntropy(targetP8);
                double EntropyValueT8 = calculateEntropy(targetT8);
                double EntropyValueFC6 = calculateEntropy(targetFC6);
                double EntropyValueF4 = calculateEntropy(targetF4);
                double EntropyValueF8 = calculateEntropy(targetF8);
                double EntropyValueAF4 = calculateEntropy(targetAF4);


                int index = 1;
                for (int y = 0; y < 15; y++)//String string : nextLine)
                {
                    //tis etoimazoume gia na anevoune sthn database mas
                    if (y == 0) {
                        ps.setNString(index++, CSVname);
                    } else if (y == 1)
                        ps.setDouble(index++, EntropyValueAF3);
                    else if (y == 2)
                        ps.setDouble(index++, EntropyValueF7);
                    else if (y == 3)
                        ps.setDouble(index++, EntropyValueF3);
                    else if (y == 4)
                        ps.setDouble(index++, EntropyValueFC5);
                    else if (y == 5)
                        ps.setDouble(index++, EntropyValueT7);
                    else if (y == 6)
                        ps.setDouble(index++, EntropyValueP7);
                    else if (y == 7)
                        ps.setDouble(index++, EntropyValueO1);
                    else if (y == 8)
                        ps.setDouble(index++, EntropyValueO2);
                    else if (y == 9)
                        ps.setDouble(index++, EntropyValueP8);
                    else if (y == 10)
                        ps.setDouble(index++, EntropyValueT8);
                    else if (y == 11)
                        ps.setDouble(index++, EntropyValueFC6);
                    else if (y == 12)
                        ps.setDouble(index++, EntropyValueF4);
                    else if (y == 13)
                        ps.setDouble(index++, EntropyValueF8);
                    else if (y == 14)
                        ps.setDouble(index++, EntropyValueAF4);

                }
                //Eisagwgh sthn vash
                ps.addBatch();
                //upload sthn vash
                ps.executeBatch();
                //kane tis allages
                con.commit();
            }
        }
        catch (Exception e)
        {
            con.rollback();
            e.printStackTrace();
            throw new Exception("Error occured while loading data from file to database."+ e.getMessage());
        }
        finally
        {
            if (null != ps)
            {
                ps.close();
            }
            FromCsvReader.close();
        }

    }

    public void setSeprator(char seprator) {
        this.seprator = seprator;
    }

    //metatrepei mia lista double se array double
    public double[] ListToArray(List<Double> list , double [] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = list.get(i);
        }
        return array;
    }

}