package utils;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static utils.HbaseUtils.*;

/**
 *@author 连仕杰
 */


public class DataFromCsv {

    /**
     * <p>
     *
     * @param title:
     * 0: imdb_title_id      1:  title                2:  original_title             3 : year
     * 4:  date_published    5:  genre                6:  duration                   7 : country
     * 8:  language          9:  director             10: writer                     11: production_company
     * 12: actors            13: description          14: avg_vote                   15: votes
     * 16: budget            17: usa_gross_income     18: worlwide_gross_income      19: metascore
     * 20: reviews_from_users                         21: reviews_from_critics
     */

    private static String[] title;
    private static Table table;
    private static Connection conn;
    private static int count = 0;
    private static int split = 14;
    private static List<Put> list = new ArrayList<Put>();
    private static String tbName = "IMDb";
    private static Admin admin;
    private static Configuration con;
    private static String[] columnFamily = new String[]{"Info"};
    private static String csvSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String NULLVALUE = "N/A";
    private static final String TMP = "src/main/java/utils/tmp/";

    public static void begin() throws IOException {
        con = init();
        conn = getConnection(con);
        admin = conn.getAdmin();
        if (isExists(admin, tbName)) {
            dropTable(admin, tbName);
        }
        createTable(tbName, columnFamily, admin);
        table = conn.getTable(TableName.valueOf(tbName));
    }

    public static void dataFromCsvToHbase(String path) throws IOException, URISyntaxException, InterruptedException {
        begin();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        DfsUtil.getFs().open(
                                new Path(path)
                        )
                )
        );
        String line;
        line = reader.readLine();
        title = line.split(csvSplitBy);
        while ((line = reader.readLine()) != null) {
            count++;
            String[] item = line.split(csvSplitBy);

            Put p = new Put(Bytes.toBytes(item[0]));

            for (int i = 1; i < item.length; i++) {
                if ("".equals(item[i])) {
                    p.addColumn(Bytes.toBytes(columnFamily[0]),
                            Bytes.toBytes(title[i]),
                            Bytes.toBytes(NULLVALUE));
                } else {
                    p.addColumn(Bytes.toBytes(columnFamily[0]),
                            Bytes.toBytes(title[i]),
                            Bytes.toBytes(item[i]));
                }
            }
            if (item.length < title.length) {
                for (int i = item.length; i < title.length; i++) {
                    p.addColumn(Bytes.toBytes(columnFamily[0]),
                            Bytes.toBytes(title[i]),
                            Bytes.toBytes(NULLVALUE));
                }
            }

            list.add(p);
            if (count % 10000 == 0) {
                System.out.println("========>[" + count + "/85855]");
            }
        }
        System.out.println("Load successful, entering data now");
        table.put(list);
        destroy();
    }

    public static void destroy() throws IOException {
        table.close();
        conn.close();
        admin.close();
    }

    public static void main(String[] args) {
        try {
            dataFromCsvToHbase("hdfs://niit:9000/IMDb_movies.csv");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
