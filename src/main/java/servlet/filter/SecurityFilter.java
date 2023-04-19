package servlet.filter;

import config.SecurityConfig;
import model.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static config.Config.WEB_URL_BEGIN;

/**
 * 登录拦截器
 *
 * @author 连仕杰
 */


public class SecurityFilter implements Filter {

    String URL_INDEX = WEB_URL_BEGIN + "index.html";
    String URL_UP = WEB_URL_BEGIN + "up.html";
    ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        // 获取登录的用户信息
        servletResponse.setContentType("text/html");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String currentURL = request.getRequestURI();

        /**
         *  放行接口
         *
         */
        System.out.println(currentURL);
        List<String> uriList = SecurityConfig.getUriList();
        for (String uri : uriList) {
            if (uri.equals(currentURL)) {
                chain.doFilter(request, response);
                return;
            }
        }

        /**
         *  未放行接口，
         *  需要检查是否登录才可访问页面
         *  检验对象： USER and UpToHDFS
         */
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if ("success".equals(session.getAttribute("up"))) {
                chain.doFilter(request, response);
            } else {
                response.sendRedirect(URL_UP + "?error=notUp");
            }
        } else {
            response.sendRedirect(URL_INDEX + "?error=notLog");
        }
    }

    @Override
    public void destroy() {
        System.out.println("容器销毁了===");
    }
}
