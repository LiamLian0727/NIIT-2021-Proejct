package config;

public class Config {
    public static final String ZOOKEEPER_QUOEUM = "niit";

    public static final String NULLVALUE = "N/A";

    public static final String TMP = "src/main/java/utils/tmp/";
    public static final String URI_LINE = "hdfs://niit:8020";

    public static final String USER = "root";
    public static final String PASSWORD = "123456";

    public static final String PATH = "/";

    public static final String JDBC = "com.mysql.jdbc.Driver";
    public static final String DATABASE_NAME = "niit";
    public static final String URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME +
            "?serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false";

    public static final String WEB_URL_BEGIN = "http://localhost:8080/Group4Project/";
    public static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    public static final int MAX_FILE_SIZE = 1024 * 1024 * 50;
    public static final int MAX_REQUEST_SIZE = 1024 * 1024 * 60;


}
