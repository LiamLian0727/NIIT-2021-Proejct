package servlet;

import MySQL.Login;
import model.User;
import servlet.impl.UserServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * @author 连仕杰
 */
@WebServlet(urlPatterns = "/SignIn")
public class SignIn extends HttpServlet {


    String urlIndex = "http://localhost:8080/Group4Project/index.html";
    String utlMain = "http://localhost:8080/Group4Project/analyze/index.html";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("Username");
        if ("".equals(name)) {
            response.sendRedirect(urlIndex + "?error=UsernameNull");
        } else {

            String password = request.getParameter("Password");
            IUserService userService = new UserServiceImpl();
            User user = null;
            try {
                user = userService.login(name, password);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (user != null) {
                request.getSession().setAttribute("user", user);
                response.sendRedirect(utlMain);
            } else {
                response.sendRedirect(urlIndex + "?error=passwdError");

            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}
