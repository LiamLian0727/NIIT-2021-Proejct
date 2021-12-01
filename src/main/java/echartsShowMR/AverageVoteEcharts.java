package echartsShowMR;

import MapReduce.AverageScoreMr;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import utils.HbaseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static utils.HbaseUtils.*;

/**
 * @author 连仕杰
 */
@WebServlet(urlPatterns = "/AverageVoteEcharts")
public class AverageVoteEcharts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Configuration init = utils.HbaseUtils.init();
        Connection conn = getConnection(init);
        Admin admin = conn.getAdmin();

        AverageScoreMr.set(
                ",",
                new String[]{"Info"},
                "actors",
                3);

        try {
            HbaseUtils.jobSubmission(
                    admin,
                    "IMDb",
                    "OutAverageScore",
                    AverageScoreMr.Map.class,
                    AverageScoreMr.Reduce.class,
                    Text.class,
                    FloatWritable.class);

            setJSON(conn, "OutAverageScore", 10, request, "Ave");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
