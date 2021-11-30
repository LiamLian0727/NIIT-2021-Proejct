package MySQL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static utils.MySqlUtils.createConnection;

public class Login {

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

    public static void signUp(String name,String password,String email) throws SQLException, ClassNotFoundException {
        Connection con = createConnection();
        Statement stmt = con.createStatement();
        String sql = "insert into niit.user values('" + name + "','"
                                                      + password + "','"
                                                      + email + "')";
        stmt.executeUpdate(sql);
    }
}
