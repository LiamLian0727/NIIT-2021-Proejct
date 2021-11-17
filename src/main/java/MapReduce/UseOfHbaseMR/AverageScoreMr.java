package MapReduce.UseOfHbaseMR;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;

import java.io.IOException;

import static utils.HbaseUtils.*;

/**
 * @author 连仕杰
 */
public class AverageScoreMr {

    static String sourceTable = "IMDb";
    static String targetTable = "OutAverageScore";
    static String csvSplitBy = ",";
    static String[] columnFamily = new String[]{"Info", "Data"};

    public static class Map extends TableMapper<Text, FloatWritable> {

        public FloatWritable avgVote = new FloatWritable(0);
        private Text word = new Text("default");
        String vote, director;
        float voteFloat;

        @Override
        public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {

            director = new String(value.getValue(Bytes.toBytes(columnFamily[0]),
                    Bytes.toBytes("director")));
            vote =  new String(value.getValue(Bytes.toBytes(columnFamily[1]),
                    Bytes.toBytes("avg_vote")));
            if (director != null && vote != null) {
                director = director.replaceAll("\"", "");
                voteFloat = Float.parseFloat(vote);
                avgVote.set(voteFloat);
                for (String s : director.split(csvSplitBy)) {
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
            format = String.format("%.3f", averageScore);
            Put put = new Put(Bytes.toBytes(format + key));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes("director"),
                    Bytes.toBytes(String.valueOf(key)));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes("averageScore"),
                    Bytes.toBytes(format));
            put.addColumn(Bytes.toBytes("Per_Info"),
                    Bytes.toBytes("count"),
                    Bytes.toBytes(count));
            context.write(null, put);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration con = init();
        Connection conn = getConnection(con);
        Admin admin = conn.getAdmin();
        if (!admin.tableExists(TableName.valueOf(targetTable))) {
            createTable(targetTable, new String[]{"Per_Info"}, admin);
        }
        Job job = Job.getInstance(con);
        job.setJarByClass(AverageScoreMr.class);

        Scan scan = new Scan();
        scan.setCaching(100);
        scan.setCacheBlocks(false);

        /** 测试用
        scan.setStartRow(Bytes.toBytes("tt0000009"));
        scan.setStopRow(Bytes.toBytes("tt0021492"));
         */

        TableMapReduceUtil.initTableMapperJob(
                sourceTable,
                scan,
                Map.class,
                Text.class,
                FloatWritable.class,
                job);

        TableMapReduceUtil.initTableReducerJob(
                targetTable,
                Reduce.class,
                job);

        conn.close();
        admin.close();
        System.exit(job.waitForCompletion(true) ? 0 : 1);


    }
}
