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

/**
 * @author 连仕杰
 */
public class HbaseUtils {

    public static Configuration init(){
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

    public static ResultScanner getAllRows(Connection conn,String tableName) throws IOException {

        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        return table.getScanner(scan);
    }

    public static List<MostPopularVo> getAllRows(Connection conn,String targetTable,HbaseUtils hbaseUtils) throws IOException {
        List<MostPopularVo> mostPopulars = new ArrayList<>();
        ResultScanner results = getAllRows(conn,targetTable);
        /** 返回rk下边的所有单元格*/
        for(Result result : results){
            Cell[] cells = result.rawCells();
            for(Cell cell : cells){
                MostPopularVo mostPopular = new MostPopularVo();
                mostPopular.setRowkey(Bytes.toString(CellUtil.cloneRow(cell)));
                mostPopular.setFamily(Bytes.toString(CellUtil.cloneFamily(cell)));
                mostPopular.setColumn(Bytes.toString(CellUtil.cloneQualifier(cell)));
                mostPopular.setValue(Bytes.toString(CellUtil.cloneValue(cell)));
                mostPopulars.add(mostPopular);
            }
        }
        System.out.println(mostPopulars);
        return mostPopulars;
    }

    public static void setJSON(Connection conn, String tableName, HttpServletRequest request, String name) throws IOException {
        List<MostPopularVo>  mostPopularVos=
                getAllRows(
                        conn,
                        tableName,
                        null);
        JSONObject targetJson = new JSONObject();
        targetJson.put("rows", targetJson);
        request.setAttribute(name,targetJson);
    }



}
