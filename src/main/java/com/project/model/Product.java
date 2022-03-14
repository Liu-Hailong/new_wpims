package com.project.model;

import lombok.Data;

@Data
public class Product {
    private String productid;
    private String product_name;
    private String other_info;
    private String in_time;
    private Integer in_userid;
    private String in_name;
    private String out_time;
    private Integer out_userid;
    private String out_name;
    private Integer out_object_userid;
    private String out_object_name;
    private String install_time;
    private String install_province;
    private String install_city;
    private String install_district;
    private String install_detail_address;
    private String install_picture;
    private Integer install_agent_userid;
    private String install_agent_name;
    private String customer_name;
    private String customer_phone;
    private String is_complete_install_fee;
    private String fee_time;

    public Boolean hasStorage(){
        return this.in_time != null && !this.in_time.equals("");
    }

    public Boolean hasDelivery(){
        return this.out_time != null && !this.out_time.equals("");
    }

    public Boolean hasInstall(){
        return this.install_time != null && !this.install_time.equals("");
    }
}
