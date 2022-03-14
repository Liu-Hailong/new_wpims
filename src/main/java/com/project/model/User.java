package com.project.model;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Data
public class User {
    private Integer userid;
    private String name;
    private String sex;
    private String phone;
    private String role;
    private String province;
    private String city;
    private String district;
    private String detail_address;
    private String state;
    private String create_time;
    public static List<String> roles = Arrays.asList("管理员", "发货员", "经销商", "代理商");
    public static List<String> sexs = Arrays.asList("男", "女");
    public static List<String> states = Arrays.asList("未审核", "未启用", "已启用");

    public User(){}

    public User(String phone, String role){
        this.phone = phone;
    }

    public Boolean valid(){
        return this.name != null
                && !this.name.equals("")
                && User.sexs.contains(this.sex)
                && this.phone != null
                && !this.phone.isEmpty()
                && Pattern.matches("^1[3-9]\\d{9}$", this.phone)
                && User.roles.contains(this.role)
                && User.states.contains(this.state);
    }

    public Boolean isOpen(){
        return Objects.equals(this.state, "已启用");
    }
}
