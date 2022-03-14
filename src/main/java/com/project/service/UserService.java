package com.project.service;

import com.project.Utils.Result;
import com.project.model.Account;
import com.project.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 检测用户登录状态
     * @param OpenId openid
     * @param SessionId 会话id
     * @return 布尔
     */
    Boolean isLogin(String OpenId, String SessionId);

    /**
     * 更新用户登录状态
     * @param OpenId openid
     * @param SessionId sessionid
     * @return 布尔
     */
    Boolean UpdateLoginState(String OpenId, String SessionId);

    User validUserInfo(String OpenId, String SessionId, String password, String state, String role);

    /**
     * 添加用户
     * @param user 用户
     * @return 序列化结果
     */
    Boolean AddUser(User user, Account account);

    /**
     * 单个删除用户
     * @param userid 用户id
     * @return 序列化结果
     */
    Boolean DeleteUserByUserid(Integer userid);

    /**
     * 批量删除用户
     * @param userid 用户id列表
     * @return 序列化结果
     */
    Map<String, List<Integer>> DeleteUsers(List<Integer> userid);

    /**
     * 通过用户id查找用户信息
     * @param userid 用户id
     * @return 序列化结果
     */
    User SelectUserByUserid(Integer userid);

    /**
     * 通过openid查找账户
     * @param OpenId openid
     * @return account
     */
    Account SelectAccountByOpenId(String OpenId);

    Account SelectAccountByUserId(Integer UserId);

    List<Integer> SelectAccountUserIdsByInvitedUserId(Integer InvitedUserId);

    /**
     * 获取代理商经销商姓名以及编号
     * @return 序列化结果
     */
    List<Map<String, Object>> SelectAgentDealer();

    /**
     * 多条件查询用户信息
     * @param user 用户
     * @return 序列化结果
     */
    List<User> SelectUsersByConditions(User user);

    /**
     * 更新用户信息
     * @param user 用户
     * @return 序列化结果
     */
    Boolean UpdateUser(User user);

    /**
     * 批量更新用户状态
     * @param userids 用户id
     * @param state 用户状态
     * @return 序列化结果
     */
    Map<String, List<Integer>> UpdateUsersState(List<Integer> userids, String state);
}
