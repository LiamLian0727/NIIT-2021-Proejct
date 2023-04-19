package MapReduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import utils.HbaseUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import static MapReduce.AverageC.getC;
import static config.Config.NULLVALUE;
import static utils.HbaseUtils.getConnection;
import static utils.HbaseUtils.init;

/**
 * @author 连仕杰
 */

public class Top250 {
    /**
     *@param  r
     * average for the movie (mean) = (Rating)
     * (是用普通的方法计算出的平均分)
     *@param v
     * number of votes for the movie = (votes)
     * （投票人数，需要注意的是，只有经常投票者才会被计算在内，这个下面详细解释）
     *@param m
     * minimum votes required to be listed in the top 250 (currently 1250)
     * (进入imdb top 250需要的最小票数，只有三两个人投票的电影就算得满分也没用的）
     *@param C
     * the mean vote across the whole report (currently 5.9)
     * （目前所有电影的平均得分）/
     * weighted rank (WR) = (v ÷ (v + m)) × r + (m ÷ ( v + m)) × C
     * <br/>
     * @param csvSplitBySet
     * 使用分隔符
     * @param columnFamilySet
     * 列族名
     * @param size
     * 输出个数(默认加1 因为有other = 1 - sum（size1，size2 ……）)
     * */

    static String csvSplitBy = null;
    static String[] columnFamily = null;
    static int size = 250;
    static int m = 0;
    static float r, v;
    static float C;
    static float weightedRank;
    static TreeMap<Float,Text> tree;

    static final String[] typeKey = {"original_title", "avg_vote", "votes"};

    public static void set(String csvSplitBySet, String[] columnFamilySet, float meanVote, int maxMinSet, int sizeSet) {
        csvSplitBy = csvSplitBySet;
        columnFamily = columnFamilySet;
        C = meanVote;
        m = maxMinSet;
        size = sizeSet;

    }

    public static class Map extends TableMapper<Text, FloatWritable> {

        static private FloatWritable weight = new FloatWritable(0f);
        static String name, rValue, vValue;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            tree = new TreeMap<>();
        }

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {

            name = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeKey[0])));
            rValue = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeKey[1])));
            vValue = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes(typeKey[2])));
            if (!(NULLVALUE.equals(name) || NULLVALUE.equals(rValue) || NULLVALUE.equals(vValue))) {
                v = Integer.parseInt(vValue);
                if (v > m) {
                    r = Float.parseFloat(rValue);
                    weightedRank = (v / (v + m)) * r + (m / (v + m)) * C;
                    tree.put(weightedRank,new Text(name.trim()));
                    if (tree.size() > size){
                        tree.remove(tree.firstKey());
                    }
                }

            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

            Text value = null;

            Float key = null;

            Iterator iter = tree.entrySet().iterator();

            while(iter.hasNext()) {

                java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
                // 获取key
                key = (Float) entry.getKey();
                weight.set(key);
                // 获取value
                value = (Text) entry.getValue();
                context.write(value, weight);
            }
        }
    }

    public static class Reduce extends TableReducer<Text, FloatWritable, ImmutableBytesWritable> {

        String format;

        @Override
        public void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {

            float weight = 0f;
            for (FloatWritable value : values) {
                weight = value.get();
            }
            format = String.format("%.3f", 10f - weight);
            Put put = new Put(Bytes.toBytes(format + key));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes(typeKey[0]),
                    Bytes.toBytes(String.valueOf(key)));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes(typeKey[1]),
                    Bytes.toBytes(String.valueOf(weight)));
            context.write(null, put);

        }
    }


    public static void getTop() throws IOException, InterruptedException, ClassNotFoundException {

        Configuration init = init();
        Connection conn = getConnection(init);
        Admin admin = conn.getAdmin();

        getC(
                 conn,
                 admin,
                "IMDb",
                "C"
        );

        C=getC(conn,admin,"IMDb","C");

        Top250.set(
                ",",
                 new String[]{"Info"},
                 C,
                5000,
                10
        );

        HbaseUtils.jobSubmission(
                 admin,
                "IMDb",
                "TopN",
                 Map.class,
                 Reduce.class,
                 Text.class,
                 FloatWritable.class
        );
    }

    public static void main(String[] args) {
        try {
            getTop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
