package config;

import java.util.ArrayList;
import java.util.List;

public class SecurityConfig {
    private static List<String> uriList;
    public static List<String> getUriList() {
        return uriList;
    }
    private static String urlIndex = "http://localhost:8080/Group4Project/index.html";
    /**
     * 放行的接口
     * */
    static{
        uriList = new ArrayList<>();
        uriList.add(urlIndex);
    }
}
