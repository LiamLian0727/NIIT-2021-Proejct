package echartsShowMR;

import MapReduce.SumGrossIncome;
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

        SumGrossIncome.set(",",
                new String[]{"Info"},
                "worlwide_gross_income",
                "original_title",
                false
        );

        try {
            HbaseUtils.jobSubmission(
                    admin,
                    "IMDb",
                    "OutSumGrossIncome",
                    SumGrossIncome.Map.class,
                    SumGrossIncome.Reduce.class,
                    Text.class,
                    LongWritable.class);

            setJSON(conn, "OutSumGrossIncome", 10, request, "Sum");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
