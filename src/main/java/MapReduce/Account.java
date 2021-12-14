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
import java.util.Iterator;
import java.util.TreeMap;

import static utils.HbaseUtils.*;

/**
 * @author 殷明，刘宣兑
 */
public class Account {

    /**
     * @param csvSplitBySet
     * 使用分隔符
     * @param columnFamilySet
     * 列族名
     * @param typeKeySet
     * 分析类别
     * @param percentageMinSet
     * 输出结果的最小占比
     * @param size
     * 输出个数(默认加1 因为有other = 1 - sum（size1，size2 ……）)
     * */

    static String csvSplitBy = null;
    static String[] columnFamily = null;
    static String typeKey = null;
    static float percentageMin = 0f;
    static TreeMap<Float,String> tree;
    static int size = 10;
    static final String NULLVALUE = "N/A";

    public static void set(String csvSplitBySet, String[] columnFamilySet, String typeKeySet, float percentageMinSet, int sizeSet) {
        csvSplitBy = csvSplitBySet;
        columnFamily = columnFamilySet;
        typeKey = typeKeySet;
        percentageMin = percentageMinSet;
        size = sizeSet;

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
        protected void setup(Context context) throws IOException, InterruptedException {
            tree = new TreeMap<>();
        }

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            final float sum = 85585F;
            int count = 0;
            for (IntWritable value : values) {
                count += value.get();
            }
            float percentage = count / sum;
            tree.put(percentage,key.toString());
            if (tree.size() > size){
                tree.remove(tree.firstKey());
            }

        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

            String value;

            float key;

            float other = 1f;
            Iterator iter = tree.entrySet().iterator();

            while (iter.hasNext()) {

                java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
                // 获取key
                key = (Float) entry.getKey();
                other -= key;
                value = (String) entry.getValue();
                format = String.format("%.3f", 1f - key);
                if (key >= percentageMin) {
                    Put put = new Put(Bytes.toBytes(format + value + typeKey));
                    put.addColumn(Bytes.toBytes("Per_Info"),
                            Bytes.toBytes(typeKey),
                            Bytes.toBytes(value));
                    put.addColumn(Bytes.toBytes("Per_Info"),
                            Bytes.toBytes("percentage"),
                            Bytes.toBytes(String.format("%.3f", key)));
                    context.write(null, put);
                }

            }
            Put put = new Put(Bytes.toBytes(String.format("%.3f", 1f - other) + "other" + typeKey));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes(typeKey),
                    Bytes.toBytes("other"));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes("percentage"),
                    Bytes.toBytes(String.format("%.3f", other)));
            context.write(null, put);


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
                0.01f,
                10
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
