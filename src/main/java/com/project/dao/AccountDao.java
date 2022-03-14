package com.project.dao;

import com.project.model.Account;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AccountDao {
    /**
     * 增加账户
     * @param account 账户
     * @return 布尔
     */
    @Insert("Insert Into account(OpenId, SessionId, userid, invited_userid, password) " +
            "values(#{OpenId}, #{SessionId}, #{userid}, #{invited_userid}, #{password})")
    Boolean InsertAccount(Account account);

    /**
     * 删除account
     * @param userid 用户id
     * @return 布尔
     */
    @Delete("Delete from account where userid=#{userid}")
    Boolean DeleteAccountByUserid(Integer userid);

    /**
     * 通过微信openid查找账户
     * @param OpenId 微信openid
     * @return account
     */
    @Select("Select * from account where OpenId=#{OpenId}")
    Account SelectAccountByOpenId(String OpenId);

    /**
     * 通过userid查找账户
     * @param userid 用户id
     * @return account
     */
    @Select("Select * from account where userid=#{userid}")
    Account SelectAccountByUserid(Integer userid);

    @Select("Select userid from account where invited_userid=#{InvitedUserId}")
    List<Integer> SelectAccountUserIdByInvitedUserId(Integer InvitedUserId);

    /**
     * 查询所有账户
     * @return account list
     */
    @Select("Select * from account")
    List<Account> SelectAccounts();

    /**
     * 更改用户会话id
     * @param OpenId 微信openid
     * @param SessionId 会话id
     * @return 布尔
     */
    @Update("Update account set SessionId=#{SessionId} where OpenId=#{OpenId}")
    Boolean UpdateAccountSessionId(String OpenId, String SessionId);

    /**
     * 更改用户密码
     * @param userid 用户id
     * @param password 用户密码
     * @return 布尔
     */
    @Update("Update account set password=#{password} where userid=#{userid}")
    Boolean UpdateAccountPassword(Integer userid, String password);

}
