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

@WebServlet(urlPatterns = "/AccountEcharts")
public class AccountEcharts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Configuration init = utils.HbaseUtils.init();
        Connection conn = getConnection(init);
        Admin admin = conn.getAdmin();

        Account.set(
                ",",
                new String[]{"Info"},
                "language",
                0.01f
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

            setJSON(conn, "OutAccount", 10, request, "Acc");
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
