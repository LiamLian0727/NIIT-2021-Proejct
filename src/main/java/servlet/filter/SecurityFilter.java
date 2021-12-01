package servlet.filter;

import config.SecurityConfig;
import model.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *  登录拦截器
 * @author 连仕杰
 * */

public class SecurityFilter implements Filter {

    String urlIndex = "http://localhost:8080/Group4Project/index.html";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取登录的用户信息
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String currentURL = request.getRequestURI();
        List<String> uriList = SecurityConfig.getUriList();
        /* 放行接口 */
        for (String uri : uriList) {
            if(uri.equals(currentURL)){
                chain.doFilter(request, response);
                return;
            }
        }
        /* 未放行接口，需要检查是否登录才可访问页面 */
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            chain.doFilter(request, response);
        } else {
            PrintWriter pw = response.getWriter();
            pw.println("<h1>请登录~</h1>");
            request.getRequestDispatcher(urlIndex).include(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("容器销毁了===");
    }
}
