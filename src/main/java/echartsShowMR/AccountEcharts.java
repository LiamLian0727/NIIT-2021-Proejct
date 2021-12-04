package echartsShowMR;

import MapReduce.Account;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import utils.HbaseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static utils.HbaseUtils.getConnection;
import static utils.HbaseUtils.*;

/**
 * @author 殷明，刘宣兑
 */
@WebServlet(urlPatterns = "/AccountEcharts")
public class AccountEcharts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Configuration init = utils.HbaseUtils.init();
        Connection conn = getConnection(init);
        Admin admin = conn.getAdmin();
        String stauts = "error";

        String type = request.getParameter("type");
        float min = Float.parseFloat(request.getParameter("min"));
        int num = Integer.parseInt(request.getParameter("num"));

        Account.set(
                ",",
                new String[]{"Info"},
                type,
                min,
                10
        );

        try {
            HbaseUtils.jobSubmission(
                    admin,
                    "IMDb",
                    "OutAccount",
                    Account.Map.class,
                    Account.Reduce.class,
                    Text.class,
                    IntWritable.class
            );

            setJSON(conn, "OutAccount", request, "Acc");

            stauts = "success";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            response.sendRedirect("http://localhost:8080/Group4Project/analyze/account.html?status="+stauts);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
