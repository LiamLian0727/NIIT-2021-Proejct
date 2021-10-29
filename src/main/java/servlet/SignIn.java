package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * @author 连仕杰
 */
@WebServlet(name = "SignIn")
public class SignIn extends HttpServlet {

    ResultSet rs;
    public Connection createConnection() {
        Connection con=null;
        try {
            java.lang.Class.forName("com.mysql.cj.jdbc.Driver");
            con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/niit?serverTimezone=Asia/Shanghai&characterEncoding=utf-8",
                    "root","niit1234");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        String name=request.getParameter("uname");
        Connection con=createConnection();
        String sql="Select UserName from niit.user where UserName = '" + name + "'";
        String password=request.getParameter("upass");
        Statement stmt;
        try {
            stmt = con.createStatement();
            rs=stmt.executeQuery(sql);
            while (rs.next()) {
                String spwd = rs.getString(1);
                if (spwd.equals(password)) {
                    response.sendRedirect("http://localhost:8080/Group4Project/analyze.html");
                }else {
                    response.sendRedirect("http://localhost:8080/Group4Project/SignIn.html");
                }
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

        @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request,response);
    }
}
