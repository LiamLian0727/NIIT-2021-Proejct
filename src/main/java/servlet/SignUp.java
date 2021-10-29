package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author 连仕杰
 */
@WebServlet(name = "SignUp")
public class SignUp extends HttpServlet {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection con=createConnection();
            Statement stmt=con.createStatement();
            String sql = null;
            if(request.getParameter("password").equals(request.getParameter("PasswordAgain"))){
                sql = "insert into MyTrip.Users values('" + request.getParameter("UserName") + "','"
                                                          + request.getParameter("password") + "','"
                                                          + request.getParameter("EmailID") + "','"
                                                          + request.getParameter("MobileNo") + "','"
                                                          + request.getParameter("Majors") + "','"
                                                          + request.getParameter("Country") + "')";
                response.sendRedirect("http://localhost:8080/Group4Project/SignIn.html");}
            else {

            }
            stmt.executeUpdate(sql);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
