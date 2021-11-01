package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import static servlet.CreatrConnection.createConnection;

/**
 * @author 连仕杰
 */
@WebServlet(name = "SignIn")
public class SignIn extends HttpServlet {

    ResultSet rs;


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        String name=request.getParameter("UserName");
        Connection con= null;
        try {
            con = createConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String sql="Select password from niit.user where username = '" + name + "'";
        String password=request.getParameter("Password");
        Statement stmt;
        try {
            assert con != null;
            stmt = con.createStatement();
            rs=stmt.executeQuery(sql);
            while (rs.next()) {
                String spwd = rs.getString(1);
                if (spwd.equals(password)) {
                    System.out.println("Success");
                    response.sendRedirect("http://localhost:8080/Group4Project/analyze.html");
                }else {
                    response.sendRedirect("http://localhost:8080/Group4Project/SignIn.jsp?error=yes");

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
