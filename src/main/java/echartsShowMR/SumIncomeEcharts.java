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

        Sum.set(",",
                new String[]{"Info"},
                "worlwide_gross_income",
                "original_title",
                false,
                10
        );

        try {
            HbaseUtils.jobSubmission(
                    admin,
                    "IMDb",
                    "OutSumGrossIncome",
                    Sum.Map.class,
                    Sum.Reduce.class,
                    Text.class,
                    LongWritable.class);

            setJSON(conn, "OutSumGrossIncome", request, "Sum");

            status="success";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            response.sendRedirect("http://localhost:8080/Group4Project/analyze/country.html?status="+status);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
