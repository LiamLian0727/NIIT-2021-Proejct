package utils;

import java.net.URL;
import java.sql.*;

/**
 * @author 连仕杰
 */
public class MySqlUtils {

    /**
     * @param JDBC
     * 使用JDBC Class Name反射
     * @param URL
     * MySQL url 保护数据库名 时区 字符集 是否使用SSL
     * @param USER
     * user name
     * @param PASSWORD
     * 数据库密码
     * */
    private static final String JDBC = "com.mysql.jdbc.Driver";
    private static final String URL =
            "jdbc:mysql://localhost:3306/niit?serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "niit1234";

    public static Connection createConnection() throws SQLException, ClassNotFoundException {

        Connection con;
        Class.forName(JDBC);
        con= DriverManager.getConnection(URL, USER,PASSWORD);
        return con;
    }

    public static boolean signIn(String name, String password) throws SQLException, ClassNotFoundException {

        Connection con = createConnection();
        Statement stmt = con.createStatement();

        String sql = "Select password from niit.user where username = '" + name + "'";
        ResultSet rs;
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String spwd = rs.getString(1);
            if (spwd.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExist(String name) throws SQLException, ClassNotFoundException {
        Connection con = createConnection();
        Statement stmt = con.createStatement();

        String sql = "SELECT UserName from user where UserName = '" + name + "'";
        ResultSet resultSet = stmt.executeQuery(sql);
        return resultSet.next();
    }

    public static void signUp(String name, String password, String email) throws SQLException, ClassNotFoundException {
        Connection con = createConnection();
        Statement stmt = con.createStatement();
        String sql = "insert into niit.user values('" + name + "','"
                + password + "','"
                + email + "')";
        stmt.executeUpdate(sql);
    }

    public static String getEmail(String username, String password) throws SQLException, ClassNotFoundException {
        Connection con = createConnection();

        String sql = "select * from tb_user where username = ? and password = ?";
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("EmailID");
        }
        return "NULL";

    }
}
