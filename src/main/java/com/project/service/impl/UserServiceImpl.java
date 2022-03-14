package com.project.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.dao.AccountDao;
import com.project.dao.UserDao;
import com.project.model.Account;
import com.project.model.User;
import com.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountDao accountDao;

    @Override
    public Boolean isLogin(String OpenId, String SessionId) {
        Account account = accountDao.SelectAccountByOpenId(OpenId);
        return (account != null && account.getSessionId().equals(SessionId));
    }

    @Override
    public Boolean UpdateLoginState(String OpenId, String SessionId) {
        return accountDao.UpdateAccountSessionId(OpenId, SessionId);
    }

    @Override
    public User validUserInfo(String OpenId, String SessionId, String password, String state, String role) {
        Account currentAccount = accountDao.SelectAccountByOpenId(OpenId);
        if (currentAccount == null) return null;
        User currentUser = userDao.SelectUserByUserid(currentAccount.getUserid());
        if (currentUser == null) return null;
        if ((SessionId == null || currentAccount.getSessionId().equals(SessionId)) &&
                (password == null || currentAccount.getPassword().equals(password)) &&
                (state == null || currentUser.getState().equals(state)) &&
                (role == null || (!role.equals("") && currentUser.getRole().contains(role)))
        ){
            return currentUser;
        }
        return null;
    }

    @Override
    public Boolean AddUser(User user, Account account) {
        // 需要提供openid
        // 添加用户 现在user表内添加用户，然后在account表内添加账户
        // 验证格式
        // 注册时手机号码必须唯一
        if (userDao.SelectUsersByConditions(new User(user.getPhone(), null)).size() > 0) return false;
        System.out.println(user.toString());
        if (user.valid()){
            if (!userDao.InsertUser(user)) return false;
            List<User> users = userDao.SelectUsersByConditions(user);
            if (users.size() != 1){
                System.out.println("添加用户时添加用户表后查询结果不唯一");
            } else{
                account.setUserid(users.get(0).getUserid());
                if (account.valid()){
                    if(accountDao.InsertAccount(account)){
                        System.out.println("添加用户成功");
                        System.out.println(users.get(0).toString());
                        return true;
                    }
                    System.out.println("账户插入失败");
                }
            }
            if (!userDao.DeleteUser(users.get(0).getUserid())) System.out.println("添加用户时，插入账户失败时，用户删除失败");
        }
        System.out.println("账户验证失败");
        return false;
    }

    @Override
    public Boolean DeleteUserByUserid(Integer userid) {
        accountDao.DeleteAccountByUserid(userid);
        return userDao.DeleteUser(userid);
    }

    @Override
    public Map<String, List<Integer>> DeleteUsers(List<Integer> userid) {
        Map<String, List<Integer>> map = new HashMap<>();
        List<Integer> success = new ArrayList<>();
        List<Integer> fail = new ArrayList<>();
        userid.forEach((Integer item)->{
            if(userDao.DeleteUser(item)){
                success.add(item);
            } else{
                fail.add(item);
            }
        });
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    @Override
    public User SelectUserByUserid(Integer userid) {
        return userDao.SelectUserByUserid(userid);
    }

    @Override
    public Account SelectAccountByOpenId(String OpenId) {
        return accountDao.SelectAccountByOpenId(OpenId);
    }

    @Override
    public Account SelectAccountByUserId(Integer UserId) {
        return accountDao.SelectAccountByUserid(UserId);
    }

    @Override
    public List<Integer> SelectAccountUserIdsByInvitedUserId(Integer InvitedUserId) {
        return accountDao.SelectAccountUserIdByInvitedUserId(InvitedUserId);
    }

    @Override
    public List<Map<String, Object>> SelectAgentDealer() {
        List<Map<String, Object>> res = new ArrayList<>();
        userDao.SelectUsersByConditions(new User(null, "代理商")).forEach(item->{
            Map<String,Object> map = new HashMap<>();
            map.put("userid", item.getUserid());
            map.put("name", item.getName());
            map.put("role", item.getRole());
            res.add(map);
        });
        userDao.SelectUsersByConditions(new User(null, "经销商")).forEach(item->{
            Map<String,Object> map = new HashMap<>();
            map.put("userid", item.getUserid());
            map.put("name", item.getName());
            map.put("role", item.getRole());
            res.add(map);
        });
        return res;
    }

    @Override
    public List<User> SelectUsersByConditions(User user) {
        return userDao.SelectUsersByConditions(user);
    }

    @Override
    public Boolean UpdateUser(User user) {
        if (user.valid()){
            return userDao.UpdateUser(user);
        }
        return false;
    }

    @Override
    public Map<String, List<Integer>> UpdateUsersState(List<Integer> userids, String state) {
        List<Integer> success = new ArrayList<>();
        List<Integer> fail = new ArrayList<>();
        if (User.states.contains(state)){
            userids.forEach(item->{
                User user = new User();
                user.setState(state);
                user.setUserid(item);
                if (userDao.UpdateUser(user)){
                    success.add(item);
                } else {
                    fail.add(item);
                }
            });
            Map<String, List<Integer>> map = new HashMap<>();
            map.put("success", success);
            map.put("fail", fail);
            return map;
        }
        return null;
    }
}
