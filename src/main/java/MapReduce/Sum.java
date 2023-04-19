package MapReduce;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import utils.HbaseUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import static config.Config.NULLVALUE;
import static utils.HbaseUtils.*;

/**
 * @author 连仕杰
 */
public class Sum {

    /**
     * @param csvSplitBySet
     * 使用分隔符
     * @param columnFamilySet
     * 列族名
     * @param typeIncome
     * 分析的收入类别 美国票房还是全球票房
     * @param typeKey
     * 分析的类别
     * @param size
     * 输出个数
     * */

    static String csvSplitBy = null;
    static String[] columnFamily = null;
    static String typeIncome = null;
    static String typeKey = null;
    static int size = 10;
    static TreeMap<Long, String> tree;


    public static void set(String csvSplitBySet, String[] columnFamilySet, String typeIncomeSet, String typeKeySet,  int sizeSet) {
        csvSplitBy = csvSplitBySet;
        columnFamily = columnFamilySet;
        typeIncome = typeIncomeSet;
        typeKey = typeKeySet;
        size = sizeSet;

    }

    public static class Map extends TableMapper<Text, Text> {

        static public Text sumIn = new Text("default");
        static private Text word = new Text("default");
        static String income, keyValue, budget;

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            keyValue = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeKey)));
            income = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeIncome)));

            budget = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes("budget")));

            if (!(NULLVALUE.equals(keyValue) || NULLVALUE.equals(income) || NULLVALUE.equals(budget))) {
                keyValue = keyValue.replaceAll("\"", "");

                sumIn.set(income + ":" + budget);
                for (String s : keyValue.split(csvSplitBy)) {
                    word.set(s.trim());
                    context.write(word, sumIn);
                }
            }


        }

    }

    public static class Reduce extends TableReducer<Text, Text, ImmutableBytesWritable> {

        String format;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            tree = new TreeMap<>();
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            long sumIn = 0,sumBu=0;
            for (Text value : values) {
                String[] split = value.toString().split(":");
                sumIn += Long.parseLong(split[0].split(" ")[1]);
                sumBu += Long.parseLong(split[1].split(" ")[1]);
            }
            tree.put(sumIn, key.toString() + "@" +sumBu);
            if (tree.size() > size) {
                tree.remove(tree.firstKey());
            }

        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

            String value;
            Long key;
            int max = tree.size();
            int count = 1;
            Iterator iter = tree.entrySet().iterator();

            while (iter.hasNext()) {

                java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
                // 获取key
                key = (Long) entry.getKey();
                value = (String) entry.getValue();
                String[] split = value.split("@");
                Put put = new Put(Bytes.toBytes((max - count) + typeKey));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes(typeKey),
                        Bytes.toBytes(split[0]));
                put.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes("sumIncome"),
                        Bytes.toBytes(String.valueOf(key) +":"+split[1]));

                context.write(null, put);
                count++;
            }


        }
    }


    public static void getSum() throws IOException, InterruptedException, ClassNotFoundException {
        /**
         *
         * usa_gross_income   or   worlwide_gross_income
         */


        Sum.set(",",
                new String[]{"Info"},
                "worlwide_gross_income",
                "original_title",
                10
        );

        HbaseUtils.jobSubmission(getConnection(init()).getAdmin(),
                "IMDb",
                "OutSumGrossIncome",
                Sum.Map.class,
                Sum.Reduce.class,
                Text.class,
                Text.class);
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
