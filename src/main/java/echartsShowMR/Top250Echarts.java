package echartsShowMR;

import MapReduce.Top250;
import com.alibaba.fastjson.JSONObject;
import model.MostPopularVo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import utils.HbaseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static MapReduce.AverageVote.getC;
import static utils.HbaseUtils.*;

/**
 * @author 连仕杰
 */
@WebServlet(urlPatterns = "/Top250Echarts")
public class
Top250Echarts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Configuration init = utils.HbaseUtils.init();
        Connection conn = getConnection(init);
        Admin admin = conn.getAdmin();
        float C = 5.9f;

        try {
            C = getC(
                    conn,
                    admin,
                    "IMDb",
                    "C"
            );


            Top250.set(
                    ",",
                    new String[]{"Info"},
                    C,
                    5000
            );


            HbaseUtils.jobSubmission(
                    admin,
                    "IMDb",
                    "TopN",
                    Top250.Map.class,
                    Top250.Reduce.class,
                    Text.class,
                    FloatWritable.class
            );

            setJSON(conn, "topNTarget", 10, request, "topN");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            getServletContext().getRequestDispatcher("http://localhost:8080/Group4Project/analyze/top.html").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static void main(String[] args) {

    }
}
