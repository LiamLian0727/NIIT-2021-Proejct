package utils;

import com.alibaba.fastjson.JSONObject;
import model.MostPopularVo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.BasicConfigurator;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static config.Config.*;

/**
 * @author 连仕杰
 */
public class HbaseUtils {

    public static Configuration init() {
        Configuration con = new Configuration();
        con.set("hbase.zookeeper.quorum", ZOOKEEPER_QUOEUM);
        System.setProperty("HADOOP_USER_NAME", USER);
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

    public static ResultScanner getAllRows(Connection conn, String tableName, String keyName, String valueName) throws IOException {

        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("Per_Info"), Bytes.toBytes(keyName));
        scan.addColumn(Bytes.toBytes("Per_Info"), Bytes.toBytes(valueName));
        return table.getScanner(scan);
    }

    public static List<MostPopularVo> getAllRows(Connection conn, String targetTable, String keyName, String valueName, HbaseUtils hbaseUtils) throws IOException {
        List<MostPopularVo> mostPopulars = new ArrayList<>();
        ResultScanner results = getAllRows(conn, targetTable, keyName, valueName);
        /** 返回rk下边的所有单元格*/
        for (Result result : results) {

            MostPopularVo mostPopular = new MostPopularVo();
            byte[] k = result.getValue(Bytes.toBytes("Per_Info"), Bytes.toBytes(keyName));
            byte[] v = result.getValue(Bytes.toBytes("Per_Info"), Bytes.toBytes(valueName));
            mostPopular.setKey(Bytes.toString(k));
            mostPopular.setValue(Bytes.toString(v));
            mostPopulars.add(mostPopular);

        }
        System.out.println(mostPopulars);
        return mostPopulars;
    }

    public static void setJSON(Connection conn, String tableName, HttpServletRequest request, String name, String keyName, String valueName) throws IOException {
        List<MostPopularVo> mostPopularVos =
                getAllRows(
                        conn,
                        tableName,
                        keyName,
                        valueName,
                        null);
        JSONObject targetJson = new JSONObject();
        targetJson.put("rows", mostPopularVos);
        request.getSession().setAttribute(name, targetJson);

        /**
         * page
         * request
         * session
         * context*/
    }


}
