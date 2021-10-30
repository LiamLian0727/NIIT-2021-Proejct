package servlet;

import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @author 连仕杰
 */
@WebServlet(name = "SignUp")
public class SignUp extends HttpServlet {

    public Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection con=null;
        Class.forName("com.mysql.jdbc.Driver");
        con= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/niit?serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false",
                "root","niit1234");
        return con;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        try {
            Connection con=createConnection();
            Statement stmt=con.createStatement();
            String sql;
            PrintWriter pw=response.getWriter();

            boolean equals = request.getParameter("Password").equals(request.getParameter("PasswordAgain"));
            sql = "SELECT UserName from user where UserName = '" + request.getParameter("UserName") +"'";
            ResultSet resultSet = stmt.executeQuery(sql);
            boolean exit = resultSet.next();
            if( equals && !exit){
                sql = "insert into niit.user values('" + request.getParameter("UserName") + "','"
                                                       + request.getParameter("Password") + "','"
                                                       + request.getParameter("EmailID")  + "','"
                                                       + request.getParameter("MobileNo") + "','"
                                                       + request.getParameter("Majors")   + "','"
                                                       + request.getParameter("Country")  + "')";
                stmt.executeUpdate(sql);
                response.sendRedirect("http://localhost:8080/Group4Project/SignIn.jsp");
            }
            else if(!equals){
                response.sendRedirect("http://localhost:8080/Group4Project/SignUp.jsp?error=notEqual");
            }else {
                response.sendRedirect("http://localhost:8080/Group4Project/SignUp.jsp?error=hasExit");
            }
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
