package dao;

import model.User;

import java.sql.SQLException;

import static utils.MySqlUtils.*;

/**
 * @author 连仕杰
 *
 * 实现对用户的增(saveUser)、改(login)、查(userIsExist)
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
