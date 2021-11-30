package servlet;

import MySQL.Login;

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
            boolean b = false;
            try {
                b = Login.signIn(name, password);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (b) {
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
