package servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static MySQL.Login.isExist;
import static MySQL.Login.signUp;

/**
 * @author 连仕杰
 */
@WebServlet(urlPatterns = "/SignUp")
public class SignUp extends HttpServlet {

    String url = "http://localhost:8080/Group4Project/index.html";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {

            String name = request.getParameter("UsernameSU");
            boolean equals = request.getParameter("PasswordSU").equals(request.getParameter("ConfirmPassword"));
            boolean exit = isExist(name);

            if (equals && !exit && !"".equals(name)) {
                signUp( name,
                        request.getParameter("PasswordSU"),
                        request.getParameter("EmailSU"));
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
