package MapReduce;


import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import utils.HbaseUtils;

import java.io.IOException;

import static utils.HbaseUtils.*;

/**
 * @author 连仕杰 杜秋予
 */
public class AverageScoreMr {
    static String csvSplitBy = null;
    static String[] columnFamily = null;
    static String typeKey = null;
    static int countMin = 0;


    static final String NULLVALUE = "N/A";

    public static void set(String csvSplitBySet, String[] columnFamilySet, String typeKeySet, int countMinSet) {
        csvSplitBy = csvSplitBySet;
        columnFamily = columnFamilySet;
        typeKey = typeKeySet;
        countMin = countMinSet;

    }

    public static class Map extends TableMapper<Text, FloatWritable> {

        static public FloatWritable avgVote = new FloatWritable(0);
        static private Text word = new Text("default");
        static String vote, keyValue;
        static float voteFloat;

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            keyValue = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeKey)));
            vote = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes("avg_vote")));
            if (!(NULLVALUE.equals(keyValue) || NULLVALUE.equals(vote))) {
                keyValue = keyValue.replaceAll("\"", "");
                voteFloat = Float.parseFloat(vote);
                avgVote.set(voteFloat);
                for (String s : keyValue.split(csvSplitBy)) {
                    word.set(s.trim());
                    context.write(word, avgVote);
                }
            }
        }

    }

    public static class Reduce extends TableReducer<Text, FloatWritable, ImmutableBytesWritable> {

        String format;

        @Override
        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float sum = 0, count = 0;
            for (FloatWritable value : values) {
                sum += value.get();
                count++;
            }
            float averageScore = sum / count;
            format = String.format("%.3f", 10f - averageScore);
            if (count >= countMin) {
                Put put = new Put(Bytes.toBytes(format + key + typeKey));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes(typeKey),
                        Bytes.toBytes(String.valueOf(key)));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes("averageScore"),
                        Bytes.toBytes(String.format("%.3f", averageScore)));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes("count"),
                        Bytes.toBytes(String.valueOf(count)));
                context.write(null, put);
            }
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        /**
         * actors
         * or
         * director
         * or
         * production_company
         */

        AverageScoreMr.set(
                ",",
                new String[]{"Info"},
                "actors",
                3);

        HbaseUtils.jobSubmission(
                getConnection(init()).getAdmin(),
                "IMDb",
                "OutAverageScore",
                AverageScoreMr.Map.class,
                AverageScoreMr.Reduce.class,
                Text.class,
                FloatWritable.class);
    }
}
