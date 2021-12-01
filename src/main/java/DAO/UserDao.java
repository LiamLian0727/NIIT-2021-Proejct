package DAO;

import model.User;

import java.sql.SQLException;

import static MySQL.Login.*;

/**
 * @author 连仕杰
 */
public class UserDao{

    public static void saveUser(User user){
        String name = user.getName();
        String password = user.getPassword();
        String email = user.getEmail();
        try {
            signUp(name,password,email);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static User login(String username, String password){
        try {
            if(signIn(username,password)){
                User user = new User();
                user.setName(username);
                user.setPassword(password);
                String email = getEmail(username,password);
                user.setEmail(email);
                return user;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean userIsExist(String username){
        try {
            return isExist(username);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
