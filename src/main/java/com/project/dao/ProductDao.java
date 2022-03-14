package com.project.dao;

import com.project.model.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductDao {
    /**
     * 添加产品
     * @param productid 产品编号（型号开头）
     * @param userid 发货员id
     * @param name 发货员姓名
     * @return 布尔
     */
    @Insert("Insert into product(productid, product_name, other_info, in_time, in_userid, in_name, is_complete_install_fee) values(#{productid}, #{product_name}, #{other_info}, NOW(), #{userid}, #{name}, 'No')")
    Boolean InsertProduct(String productid, String product_name, Integer userid, String name, String other_info);

    /**
     * 删除产品
     * @param productid 产品编号
     * @return 布尔
     */
    @Delete("Delete from product where productid=#{productid}")
    Boolean DeleteProduct(String productid);

    /**
     * 通过产品编号查询产品
     * @param productid 产品编号
     * @return 布尔
     */
    @Select("Select * from product where productid=#{productid}")
    Product SelectProductByProductid(String productid);

    /**
     * 多条件查询产品
     * @param product 产品编号
     * @return 产品列表
     */
    List<Product> SelectProductsByConditions(Product product, String timeType, String startTime, String endTime);

    /**
     * 更新产品信息
     * @param product 产品
     * @return 布尔
     */
    Boolean UpdateProduct(Product product);
}
