package com.project.dao;

import com.project.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface UserDao {
    /**
     * 增加用户
     * @param user 用户
     * @return 布尔
     */
    @Insert("Insert Into user(name, sex, phone, role, province, city, district, detail_address, state, create_time) " +
            "values(#{name}, #{sex}, #{phone}, #{role}, #{province}, #{city}, #{district}, #{detail_address}, #{state}, NOW())")
    Boolean InsertUser(User user);

    /**
     * 删除用户
     * @param userid 用户id
     * @return 布尔
     */
    @Delete("Delete From user Where userid=#{userid}")
    Boolean DeleteUser(Integer userid);

    /**
     * 通过userid查询用户
     * @param userid 用户id
     * @return user
     */
    @Select("Select * From user Where userid=#{userid}")
    User SelectUserByUserid(Integer userid);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    @Select("Select * From user")
    List<User> SelectUsers();

    /**
     * 多条件查询
     * @param user 用户
     * @return 用户列表
     */
    List<User> SelectUsersByConditions(User user);

    /**
     * 修改用户信息
     * @param user 用户
     * @return 布尔
     */
    Boolean UpdateUser(User user);
}
