package config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 连仕杰
 */
public class SecurityConfig {
    private static List<String> uriList;

    private static String URL = "http://localhost:8080/Group4Project";
    /**
     * 放行的接口：
     * 登录
     * 注册
     * 上传文件
     * */
    static{
        uriList = new ArrayList<>();
        uriList.add(URL + "/index.html");
        uriList.add(URL + "/up.html");
        uriList.add(URL + "/SignUp");
        uriList.add(URL + "/SignIn");
    }

    public static List<String> getUriList() {
        return uriList;
    }
}
