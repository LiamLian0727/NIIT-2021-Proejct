package servlet;

import model.User;

import java.sql.SQLException;

/**
 * @author 连仕杰
 */
public interface IUserService {

    /**
     * saveUser（） ： 注册
     * <p/>
     * <b>参数 :<b/>
     * <p/>
     * @param user 用户类
     */
    public void saveUser(User user) throws SQLException, ClassNotFoundException;

    /**
     * login（） ： 登录
     * <p/>
     * <b>参数 :<b/>
     * <p/>
     * @param username 用户名
     * @param password 用户密码
     * @return 返回User对象
     */
    public User login(String username, String password) throws SQLException, ClassNotFoundException;

    /**
     * userIsExist() :  检查用户是否存在
     * <p/>
     * <b>参数 :<b/>
     * <p/>
     * @param username 用户名
     * @return 是否存在该用户
     */
    public boolean userIsExist(String username) throws SQLException, ClassNotFoundException;

}
