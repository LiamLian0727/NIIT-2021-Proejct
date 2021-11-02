package MapReduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author 连仕杰
 *
 *   title:
 *   0: imdb_title_id      1:  title                2:  original_title             3 : year
 *   4:  date_published    5:  genre                6:  duration                   7 : country
 *   8:  language          9:  director             10: writer                     11: production_company
 *   12: actors            13: description          14: avg_vote                   15: votes
 *   16: budget            17: usa_gross_income     18: worlwide_gross_income      19: metascore
 *   20: reviews_from_users                         21: reviews_from_critics
 *
 */
public class DataMap extends Mapper<LongWritable, Text, IntWritable, Text> {

    private String line;
    private String[] words;
    private IntWritable outK;
    private Text outV;
    static int indexIncome =18,indexTitle=1;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        line = value.toString();
        words = line.split(",");
        if("".equals(words[indexIncome])){
            return;
        }
        outK.set(Integer.parseInt(words[indexIncome]));
        outV.set(words[indexTitle]);

    }

}
