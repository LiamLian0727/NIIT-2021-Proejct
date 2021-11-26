package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.HbaseUtils.*;

/**
 * @author 连仕杰
 */
public class Filters {
    static Configuration con;
    static Connection conn;
    static Admin admin;
    static String targetTable = "OutAverageScore";
    static Table table;
    static String[] returnData = new String[]{"","",""};
    static String[] column = new String[]{"director","avg_vote","count"};

    static {
        try {
            con = init();
            conn = getConnection(con);
            admin = conn.getAdmin();
            if (!admin.tableExists(TableName.valueOf(targetTable))) {
                createTable(targetTable, new String[]{"Per_Info"}, admin);
            }
            table = conn.getTable(TableName.valueOf(targetTable));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void destory() throws IOException {
        table.close();
        conn.close();
        admin.close();
    }

    public static String[] filterScan(String type) throws IOException {
        Filter dependentColumnFilter = new DependentColumnFilter(
                Bytes.toBytes("Per_Info"),
                Bytes.toBytes("count"),
                false,
                CompareOperator.EQUAL,
                new RegexStringComparator("[^1-3]$")
        );
        Filter rowFilter = new RowFilter(
                CompareOperator.EQUAL,
                new SubstringComparator(type));

        List<Filter> filters= new ArrayList<Filter>();
        filters.add(dependentColumnFilter);
        filters.add(rowFilter);
        FilterList filterList = new FilterList(filters);
        Scan scan = new Scan();
        scan.setLimit(10);
        scan.setFilter(filterList);
        ResultScanner results = table.getScanner(scan);
        for (Result result : results) {
            returnData[0] += ",'" + Bytes.toString(
                    result.getValue(
                            Bytes.toBytes("Per_Info"),
                            Bytes.toBytes(type))) + "'" ;
            for (int i = 1; i < returnData.length ; i++) {
                returnData[i] += ",'" + Bytes.toString(
                                        result.getValue(
                                        Bytes.toBytes("Per_Info"),
                                        Bytes.toBytes(column[i]))) + "'";
            }
        }
        for (int i = 0; i < returnData.length ; i++) {
            returnData[i] = returnData[i].substring(1);
        }
        destory();
        return returnData;
    }
    /**
    public static void main(String[] args) throws IOException {

         //director or actors
        for (String director : new Filters().filterScan("actors")) {
            System.out.println(director);
        }
    }
    */


}
