package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

import static utils.MySqlUtils.createConnection;

/**
 * @author 连仕杰
 */
@WebServlet(urlPatterns = "/SignUp")
public class SignUp extends HttpServlet {

    String url = "http://localhost:8080/Group4Project/index.html";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            Connection con = createConnection();
            Statement stmt = con.createStatement();
            String sql;
            String name = request.getParameter("UsernameSU");
            boolean equals = request.getParameter("PasswordSU").equals(request.getParameter("ConfirmPassword"));
            sql = "SELECT UserName from user where UserName = '" + name + "'";
            ResultSet resultSet = stmt.executeQuery(sql);
            boolean exit = resultSet.next();
            if (equals && !exit && !"".equals(name)) {
                sql = "insert into niit.user values('" + request.getParameter("UsernameSU") + "','"
                        + request.getParameter("PasswordSU") + "','"
                        + request.getParameter("EmailSU") + "')";
                stmt.executeUpdate(sql);
                response.sendRedirect(url + "?error=noError");
            } else if (!equals) {
                response.sendRedirect(url + "?error=notEqual");
            } else if (exit) {
                response.sendRedirect(url + "?error=hasExit");
            } else {
                response.sendRedirect(url + "?error=UsernameNull");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }
}
