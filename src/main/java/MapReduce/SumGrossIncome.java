package MapReduce;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import utils.HbaseUtils;

import java.io.IOException;

import static utils.HbaseUtils.*;

/**
 * @author 连仕杰 杜秋予
 */
public class SumGrossIncome {
    static String csvSplitBy = null;
    static String[] columnFamily = null;
    static String typeIncome = null;
    static String typeKey = null;
    static boolean earn = false;
    //ture ：纯利润 false ： 票房


    static final String NULLVALUE = "N/A";

    public static void set(String csvSplitBySet, String[] columnFamilySet, String typeIncomeSet, String typeKeySet,boolean earnSet) {
        csvSplitBy = csvSplitBySet;
        columnFamily = columnFamilySet;
        typeIncome = typeIncomeSet;
        typeKey = typeKeySet;
        earn = earnSet;


    }

    public static class Map extends TableMapper<Text, LongWritable> {

        static public LongWritable sumIn = new LongWritable(0);
        static private Text word = new Text("default");
        static String income, keyValue,budget;
        static long voteLong;

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            keyValue = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeKey)));
            income = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeIncome)));
            if (earn){
                budget = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                        Bytes.toBytes("budget")));
                if (!(NULLVALUE.equals(keyValue) || NULLVALUE.equals(income) || NULLVALUE.equals(budget))) {
                    keyValue = keyValue.replaceAll("\"", "");
                    voteLong = Long.parseLong(income.split(" ")[1]);
                    voteLong -= Long.parseLong(budget.split(" ")[1]);
                    sumIn.set(voteLong);
                    for (String s : keyValue.split(csvSplitBy)) {
                        word.set(s.trim());
                        context.write(word, sumIn);
                    }
                }
            }else {
                if (!(NULLVALUE.equals(keyValue) || NULLVALUE.equals(income))) {
                    keyValue = keyValue.replaceAll("\"", "");
                    voteLong = Long.parseLong(income.split(" ")[1]);
                    sumIn.set(voteLong);
                    for (String s : keyValue.split(csvSplitBy)) {
                        word.set(s.trim());
                        context.write(word, sumIn);
                    }
                }
            }

        }

    }

    public static class Reduce extends TableReducer<Text, LongWritable, ImmutableBytesWritable> {

        String format;

        @Override
        public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long sum = 0;
            for (LongWritable value : values) {
                sum += value.get();
            }
            double sumDivW = sum;
            format = String.format("%12.0f", 1e12 - sumDivW);

            Put put = new Put(Bytes.toBytes(format + key + typeKey));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes(typeKey),
                    Bytes.toBytes(String.valueOf(key)));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes("sumIncome"),
                    Bytes.toBytes(String.valueOf(sumDivW)));

            context.write(null, put);

        }
    }


    public static void getSum() throws IOException, InterruptedException, ClassNotFoundException {
        /**
         *
         * usa_gross_income   or   worlwide_gross_income
         */


        SumGrossIncome.set(",",
                            new String[]{"Info"},
                            "worlwide_gross_income",
                            "original_title",
                            false
        );

        HbaseUtils.jobSubmission(getConnection(init()).getAdmin(),
                                "IMDb",
                                "OutSumGrossIncome",
                                 SumGrossIncome.Map.class,
                                 SumGrossIncome.Reduce.class,
                                 Text.class,
                                 LongWritable.class);
    }

    public static void main(String[] args) {
        try {
            getSum();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
