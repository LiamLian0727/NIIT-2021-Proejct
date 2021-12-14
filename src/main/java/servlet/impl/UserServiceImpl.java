package servlet.impl;

import dao.UserDao;
import model.User;
import servlet.IUserService;



/**
 * @author 连仕杰
 */
public class UserServiceImpl implements IUserService {
    @Override
    public void saveUser(User user){
        UserDao.saveUser(user);
    }

    @Override
    public User login(String username, String password){
        return UserDao.login(username,password);
    }

    @Override
    public boolean userIsExist(String username){
        return UserDao.userIsExist(username);
    }
}
