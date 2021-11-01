package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import static servlet.CreatrConnection.createConnection;

/**
 * @author 连仕杰
 */
@WebServlet(name = "SignUp")
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){

        try {
            Connection con=createConnection();
            Statement stmt=con.createStatement();
            String sql;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        doPost(request,response);
    }
}
