package MapReduce;


import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import utils.HbaseUtils;

import java.io.IOException;

import static utils.HbaseUtils.getConnection;
import static utils.HbaseUtils.init;

/**
 * @author 连仕杰
 * 用来计算所又电影平均分 C
 */
public class AverageVote {
    static String csvSplitBy = ",";
    static String[] columnFamily = new String[]{"Info"};

    static final String NULLVALUE = "N/A";

    public static class Map extends TableMapper<Text, FloatWritable> {

        static public FloatWritable avgVote = new FloatWritable(0);
        static final private Text WORD = new Text("C");
        static String vote;
        static float voteFloat;

        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            vote = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes("avg_vote")));
            if (!NULLVALUE.equals(vote)) {
                voteFloat = Float.parseFloat(vote);
                avgVote.set(voteFloat);
                context.write(WORD, avgVote);
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
            Put put = new Put(Bytes.toBytes("C"));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes("averageVoteC"),
                    Bytes.toBytes(String.format("%.5f", averageScore)));
            context.write(null, put);

        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        HbaseUtils.jobSubmission(
                getConnection(init()).getAdmin(),
                "IMDb",
                "C",
                AverageVote.Map.class,
                AverageVote.Reduce.class,
                Text.class,
                FloatWritable.class);
    }
}
