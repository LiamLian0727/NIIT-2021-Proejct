package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static config.Config.*;

/**
 * @author 连仕杰
 */
public class DfsUtil {
    private static Configuration con = null;
    private static FileSystem fs = null;

    static {
        con = new Configuration();
    }

    public static String getPATH() {
        return PATH;
    }

    public static FileSystem getFs() throws URISyntaxException, IOException, InterruptedException {
        fs = FileSystem.get(new URI(URI_LINE), con, USER);
        return fs;
    }

    public static boolean putFilesInToHDFSUtil(InputStream fin, String fileName) {

        try {
            FileSystem fs = getFs();
            // we need to give the file address in local
            FSDataOutputStream fos = DfsUtil.fs.create(new Path(PATH + fileName));
            IOUtils.copyBytes(fin, fos, con);
            IOUtils.closeStream(fos);
            IOUtils.closeStream(fin);
            System.out.println("File has transferred successfully");
            return true;
        } catch (IOException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
