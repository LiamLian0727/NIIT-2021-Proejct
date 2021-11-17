package MapReduce.UseOfHadoopMR;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.HbaseUtils.*;

/**
 * @author 连仕杰
 *
 * 本类仅供测试参考
 */
public class AverageScoreDriver {
    static String csvSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)" ;

    static String targetTable = "OutAverageScore";
    static String tmpPath = "src/main/java/MapReduce/UseOfHadoopMR/tmp/part-r-00000";
    static List<Put> list = new ArrayList<Put>();
    static String[] columnName = new String[]{"avg_vote","director","count"};

    public static class Map extends Mapper<LongWritable, Text, Text, FloatWritable> {
        String[] director;
        Float avgVote;

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            if (key.get() != 0) {
                String line = value.toString();
                String[] split = line.split(csvSplitBy);
                if (!("".equals(split[9]) || "".equals(split[14]))) {
                    director = split[9].replaceAll("\"", "").split(",");
                    avgVote = Float.valueOf(split[14]);
                }
                for (String s : director) {
                    context.write(new Text(s.trim()), new FloatWritable(avgVote));
                }
            }

        }
    }

    public static class Reduce extends Reducer<Text, FloatWritable, Text, Text> {

        @Override
        protected void reduce(Text key, Iterable<FloatWritable> values, Context context) throws IOException, InterruptedException {

            int count = 0;
            float sum = 0;
            for (FloatWritable value : values) {
                sum += value.get();
                count++;
            }
            String OutV = key.toString() +  "\t" + count;
            context.write(new Text(String.format("%.3f", sum / count)), new Text(OutV));
        }
    }

    public static void toHbase(String path) throws IOException {
        Configuration con = init();
        Connection conn = getConnection(con);
        Admin admin = conn.getAdmin();
        if (!admin.tableExists(TableName.valueOf(targetTable))) {
            createTable(targetTable, new String[]{"Per_Info"}, admin);
        }
        Table table = conn.getTable(TableName.valueOf(targetTable));
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] item = line.split("\t");
            Put p = new Put(Bytes.toBytes(item[0]+item[1]));
            for (int i = 0; i < columnName.length; i++) {
                p.addColumn(Bytes.toBytes("Per_Info"),
                        Bytes.toBytes(columnName[i]),
                        Bytes.toBytes(item[i]));
            }
            list.add(p);
        }
        table.put(list);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        args = new String[]{"src\\main\\dataset\\IMDb_movies.csv",
                "src\\main\\java\\MapReduce\\UseOfHadoopMR\\tmp"};

        Configuration conf = new Configuration();
        conf.set("mapred.textoutputformat.separator","\t");
        Job job = Job.getInstance(conf);

        job.setJarByClass(AverageScoreDriver.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FloatWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean result = job.waitForCompletion(true);

        if (new File(tmpPath).exists()) {
            toHbase(tmpPath);
        }

        System.exit(result ? 0 : 1);
    }

}
