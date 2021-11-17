package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.HbaseUtils.*;


/**
 * * @author 连仕杰
 *
 * title:
 * 0: imdb_title_id      1:  title                2:  original_title             3 : year
 * 4:  date_published    5:  genre                6:  duration                   7 : country
 * 8:  language          9:  director             10: writer                     11: production_company
 * 12: actors            13: description          14: avg_vote                   15: votes
 * 16: budget            17: usa_gross_income     18: worlwide_gross_income      19: metascore
 * 20: reviews_from_users                         21: reviews_from_critics
*/
public class DataFromCsv{

    String[] title;                             Table table;
    Connection conn;                            int count = 0;
    static int split = 14;                      List<Put> list = new ArrayList<Put>();
    static String tbName = "IMDb";              Admin admin;
    Configuration con;
    static String[] columnFamily = new String[]{"Info","Data"};
    static String csvSplitBy =",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public void begin() throws IOException {
        con = init();
        conn = getConnection(con);
        admin = conn.getAdmin();
        if(!admin.tableExists(TableName.valueOf(tbName))){
            createTable(tbName,columnFamily,admin);
        }
        table = conn.getTable(TableName.valueOf(tbName));
    }

    public void dataFromCsvToHbase(String path) throws IOException {

        BufferedReader reader = new BufferedReader(
                new FileReader(path));
        String line;
        line = reader.readLine();
        title = line.split(csvSplitBy);
        while ((line = reader.readLine()) != null) {
            count++;
            String[] item = line.split(csvSplitBy);
            Put p = new Put(Bytes.toBytes(item[0]));
            for (int i = 1 ; i < split ; i++){
                if(!("".equals(item[i]))) {
                    p.addColumn(Bytes.toBytes(columnFamily[0]),
                                Bytes.toBytes(title[i]),
                                Bytes.toBytes(item[i]));
                }
            }
            for (int i = split ; i < item.length ; i++){
                if(!("".equals(item[i]))) {
                    p.addColumn(Bytes.toBytes(columnFamily[1]),
                                Bytes.toBytes(title[i]),
                                Bytes.toBytes(item[i]));
                }
            }
            list.add(p);
            if(count % 10000 == 0 ){
                System.out.println("========>["+count+"/85855]");
            }
        }
        System.out.println("Load successful, entering data now");
        table.put(list);
    }

    public void destroy() throws IOException {
        table.close();
        conn.close();
        admin.close();
    }

    public static void main(String[] args) {
        try {
            DataFromCsv dataFromCsv = new DataFromCsv();
            dataFromCsv.begin();
            dataFromCsv.dataFromCsvToHbase("src/main/dataset/IMDb_movies.csv");
            dataFromCsv.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
