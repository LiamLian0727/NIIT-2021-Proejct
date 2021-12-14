package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author 连仕杰
 */
public class PutDataToHdfs {

    public static FileSystem fs;

    static {
        try {
            fs = FileSystem.get(
                    new URI("hdfs://niit:9000"),
                    new Configuration(),
                    "root");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * Put上传文件
     *
     * @param pathHDFS
     * hdfs路径
     * @param pathLocal
     * 本地文件路径
     * */
    public static void Put(String pathLocal, String pathHDFS) throws IOException {
        /**
         * 参数一：表示删除原数据；
         * 参数二：是否允许覆盖；
         * 参数三：原数据路径；
         * 参数四：目的地路径
         * */
        fs.copyFromLocalFile(false, true,
                new Path(pathLocal),
                new Path(pathHDFS));
    }

    /**
     * 检查某一指定路径是否存在
     * @param pathHDFS
     * hdfs路径
     * @return
     * 该路径是否存在*/
    public static boolean isExistHDFS(String pathHDFS) throws IOException {
        return fs.exists(new Path(pathHDFS));
    }




}
