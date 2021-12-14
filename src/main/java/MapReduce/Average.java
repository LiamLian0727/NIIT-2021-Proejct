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
import java.util.Iterator;
import java.util.TreeMap;

import static utils.HbaseUtils.*;

/**
 * @author 郑欣然 连仕杰
 */

public class Average {

    /**
     * @param csvSplitBySet
     * 使用分隔符
     * @param columnFamilySet
     * 列族名
     * @param typeKey
     * 分析类别
     * @param countMin
     * 输出结果的最小作品数量
     * @param size
     * 输出个数
     * */

    static String csvSplitBy = null;
    static String[] columnFamily = null;
    static String typeKey = null;
    static int countMin = 0;
    static int size = 100;
    static TreeMap<Float, String[]> tree;


    static final String NULLVALUE = "N/A";

    public static void set(String csvSplitBySet, String[] columnFamilySet, String typeKeySet, int countMinSet, int sizeSet) {
        csvSplitBy = csvSplitBySet;
        columnFamily = columnFamilySet;
        typeKey = typeKeySet;
        countMin = countMinSet;
        size = sizeSet;

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
        protected void setup(Context context) throws IOException, InterruptedException {
            tree = new TreeMap<>();
        }

        @Override
        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {
            float sum = 0;
            int count = 0;
            for (FloatWritable value : values) {
                sum += value.get();
                count++;
            }
            float averageScore = sum / count;
            if (count >= countMin) {
                tree.put(averageScore, new String[]{key.toString(), String.valueOf(count)});
                if (tree.size() > size) {
                    tree.remove(tree.firstKey());
                }
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            String[] value = null;

            float averageScore;

            Iterator iter = tree.entrySet().iterator();

            while (iter.hasNext()) {

                java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
                // 获取key
                averageScore = (Float) entry.getKey();
                value = (String[])entry.getValue();
                format = String.format("%.3f", 10f - averageScore);

                Put put = new Put(Bytes.toBytes(format + value[0] + typeKey));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes(typeKey),
                        Bytes.toBytes(value[0]));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes("averageScore"),
                        Bytes.toBytes(String.format("%.3f", averageScore)
                                         + ":" + value[1]));
                context.write(null, put);
            }


        }
    }


    public static void getAverage() throws IOException, InterruptedException, ClassNotFoundException {
        /**
         * actors
         * or
         * director
         * or
         * production_company
         */

        Average.set(
                ",",
                new String[]{"Info"},
                "actors",
                3, 10);

        HbaseUtils.jobSubmission(
                getConnection(init()).getAdmin(),
                "IMDb",
                "OutAverageScore",
                Average.Map.class,
                Average.Reduce.class,
                Text.class,
                FloatWritable.class);
    }

    public static void main(String[] args) {
        try {
            getAverage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
