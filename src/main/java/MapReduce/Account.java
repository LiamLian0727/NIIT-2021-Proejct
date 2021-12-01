package MapReduce;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import utils.HbaseUtils;

import java.io.IOException;

import static utils.HbaseUtils.*;

/**
 * @author 殷明
 */
public class Account {
    static String csvSplitBy = null;
    static String[] columnFamily = null;
    static String typeKey = null;
    static float percentageMin = 0f;


    static final String NULLVALUE = "N/A";

    public static void set(String csvSplitBySet, String[] columnFamilySet, String typeKeySet, float percentageMinSet) {
        csvSplitBy = csvSplitBySet;
        columnFamily = columnFamilySet;
        typeKey = typeKeySet;
        percentageMin = percentageMinSet;

    }

    public static class Map extends TableMapper<Text, IntWritable> {

        static public final IntWritable ONE = new IntWritable(1);
        static private Text word = new Text("default");
        static String keyValue;

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

            keyValue = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeKey)));
            if (!(NULLVALUE.equals(keyValue))) {
                keyValue = keyValue.replaceAll("\"", "");
                for (String s : keyValue.split(csvSplitBy)) {
                    word.set(s.trim());
                    context.write(word, ONE);
                }
            }
        }

    }

    public static class Reduce extends TableReducer<Text, IntWritable, ImmutableBytesWritable> {

        String format;

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            final float sum = 85585F;
            int count = 0;
            for (IntWritable value : values) {
                count += value.get();
            }
            float percentage = count / sum;
            format = String.format("%.3f", 1f - percentage);
            if (percentage >= percentageMin) {
                Put put = new Put(Bytes.toBytes(format + key + typeKey));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes(typeKey),
                        Bytes.toBytes(String.valueOf(key)));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes("percentage"),
                        Bytes.toBytes(String.format("%.3f", percentage)));
                context.write(null, put);
            }
        }
    }


    public static void getAccount() throws IOException, InterruptedException, ClassNotFoundException {
        /**
         * actors
         * or
         * director
         * or
         * production_company
         */

        Account.set(
                ",",
                 new String[]{"Info"},
                "language",
                0.01f
        );

        HbaseUtils.jobSubmission(
                 getConnection(init()).getAdmin(),
                "IMDb",
                "OutAccount",
                 Map.class,
                 Reduce.class,
                 Text.class,
                 IntWritable.class
        );
    }

    public static void main(String[] args) {
        try {
            getAccount();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
