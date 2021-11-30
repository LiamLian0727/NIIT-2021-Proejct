package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.io.IOException;

/**
 * @author 连仕杰
 */
public class HbaseUtils {

    public static Configuration init() throws IOException {
        Configuration con = new Configuration();
        con.set("hbase.zookeeper.quorum", "niit");
        System.setProperty("HADOOP_USER_NAME", "root");
        return con;
    }

    public static Connection getConnection(Configuration con) throws IOException {
        Connection conn = ConnectionFactory.createConnection(con);
        return conn;
    }

    public static void createTable(String tbName, String[] columnFamily, Admin admin) throws IOException {
        TableDescriptorBuilder tdb = TableDescriptorBuilder.newBuilder(TableName.valueOf(tbName));
        for (String s : columnFamily) {
            tdb.setColumnFamily(ColumnFamilyDescriptorBuilder.of(s)).build();
        }
        admin.createTable(tdb.build());
    }

    public static boolean isExists(Admin admin, String tableName) throws IOException {

        return admin.tableExists(TableName.valueOf(tableName));
    }

    public static void dropTable(Admin admin, String tableName) throws IOException {

        if (isExists(admin, tableName)) {

            admin.disableTable(TableName.valueOf(tableName));

            admin.deleteTable(TableName.valueOf(tableName));
            System.out.println("表" + tableName + "删除成功!");
        } else {
            System.out.println("表" + tableName + "不存在!");
        }
    }

    public static boolean jobSubmission(Admin admin, String sourceTable, String targetTable,
                                        Class<?> Map, Class<?> Reduce, Class<?> outputKey, Class<?> outputValue) throws IOException, ClassNotFoundException, InterruptedException {

        BasicConfigurator.configure();
        if (isExists(admin, sourceTable)) {
            if (isExists(admin, targetTable)) {
                dropTable(admin, targetTable);
            }
            createTable(targetTable, new String[]{"Per_Info"}, admin);

            Job job = Job.getInstance(init());
            job.setJarByClass(HbaseUtils.class);
            Scan scan = new Scan();
            scan.setCaching(100);
            scan.setCacheBlocks(false);
            TableMapReduceUtil.initTableMapperJob(
                    sourceTable,
                    scan,
                    (Class<? extends TableMapper>) Map,
                    (Class<? extends Writable>) outputKey,
                    (Class<? extends Writable>) outputValue,
                    job);
            TableMapReduceUtil.initTableReducerJob(
                    targetTable,
                    (Class<? extends TableReducer>) Reduce,
                    job);
            boolean b = job.waitForCompletion(true);
            return b;
        } else {
            return false;
        }
    }

}
