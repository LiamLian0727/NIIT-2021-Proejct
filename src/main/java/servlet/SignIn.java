package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

import static utils.MySqlUtils.createConnection;

/**
 * @author 连仕杰
 */
@WebServlet(urlPatterns = "/SignIn")
public class SignIn extends HttpServlet {

    ResultSet rs;
    String urlIndex = "http://localhost:8080/Group4Project/index.html";
    String utlMain = "http://localhost:8080/Group4Project/analyze.html";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("Username");
        System.out.println(name + "--------------------------------------------");
        Connection con = null;
        try {
            con = createConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if ("".equals(name)) {
            response.sendRedirect(urlIndex + "?error=UsernameNull");
        } else {
            String sql = "Select password from niit.user where username = '" + name + "'";
            String password = request.getParameter("Password");
            Statement stmt;
            try {
                assert con != null;
                stmt = con.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String spwd = rs.getString(1);
                    if (spwd.equals(password)) {
                        System.out.println("Success");
                        response.sendRedirect(utlMain);
                    } else {
                        response.sendRedirect(urlIndex + "?error=passwdError");

                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}
