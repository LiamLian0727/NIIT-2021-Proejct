package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author 连仕杰
 */
public class MySqlUtils {

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        Connection con;
        Class.forName("com.mysql.jdbc.Driver");
        con= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/niit?serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false",
                "root","niit1234");
        return con;
    }
}
