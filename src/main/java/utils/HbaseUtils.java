package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;

/**
 * @author 连仕杰
 */
public class HbaseUtils {

    public static Configuration init() throws IOException {
        Configuration con = new Configuration();
        con.set("hbase.zookeeper.quorum","192.168.186.100:2181");
        System.setProperty("HADOOP_USER_NAME","hadoop");
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


}
