package echartsShowMR;

import MapReduce.Sum;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import utils.HbaseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static utils.HbaseUtils.*;
import static utils.HbaseUtils.init;

/**
 * @author 连仕杰
 */
@WebServlet(urlPatterns = "/SumIncomeEcharts")
public class SumIncomeEcharts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Configuration init = utils.HbaseUtils.init();
        Connection conn = getConnection(init);
        Admin admin = conn.getAdmin();
        String status = "error";

        String type1 = request.getParameter("type1");
        String type2 = request.getParameter("type2");
        int num = Integer.parseInt(request.getParameter("num"));

//      String type1 = "worlwide_gross_income";
//      String type2 = "original_title";
        System.out.println("------------------"+type1 + ":" + type2+"-----------------------------");

        Sum.set(
                ",",
                 new String[]{"Info"},
                 type1,
                 type2,
                 num
        );

        try {
            HbaseUtils.jobSubmission(
                     admin,
                    "IMDb",
                    "OutSumGrossIncome",
                     Sum.Map.class,
                     Sum.Reduce.class,
                     Text.class,
                     Text.class);

            setJSON(conn, "OutSumGrossIncome", request, "Sum", type2, "sumIncome");

            status = "success";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            response.sendRedirect("http://localhost:8080/Group4Project/analyze/sum.jsp?status=" + status);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
