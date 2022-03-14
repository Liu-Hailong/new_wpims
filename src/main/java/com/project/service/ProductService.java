package com.project.service;

import com.project.Utils.Result;
import com.project.model.Product;
import com.project.model.User;

import java.util.List;
import java.util.Map;

public interface ProductService {
    /**
     * 产品入库
     * @param user 发货员
     * @param productid 产品编号
     * @param product_name 产品名称
     * @return 序列化结果
     */
    // 入库 ProductStorage
    Boolean ProductStorage(User user, String productid, String product_name, String other_info);

    /**
     * 产品出库
     * @param user 发货员
     * @param product 产品
     * @param object_agent_user 目标代理商
     * @return 序列化结果
     */
    // 出库 ProductDelivery
    Boolean ProductDelivery(User user, Product product, User object_agent_user);

    /**
     * 安装submit
     * @param user 代理商、经销商
     * @param product 产品
     * @param product_picture 安装图片本地地址
     * @param province 安装省
     * @param city 安装市
     * @param district 安装县
     * @param detail_address 安装详细地址
     * @param customer_name 客户姓名
     * @param customer_phone 客户联系方式
     * @return 序列化结果
     */
    // 安装 ProductInstall
    Boolean ProductInstall(User user, Product product, String product_picture, String province, String city, String district, String detail_address, String customer_name, String customer_phone);

    /**
     * 查询产品信息
     * @param product 产品
     * @return 序列化结果
     */
    // 查询产品信息
    List<Product> SelectProductInfo(Product product, String timeType, String startTime, String endTime);

    Product SelectProductByProductid(String productid);

    /**
     * 批量删除产品
     * @param productIds 产品编号列表
     * @return 序列化结果
     */
    // 批量删除产品信息
    Map<String, List<String>> DeleteProduct(List<String> productIds);

    /**
     * 更新安装费标志
     * @param productIds 产品编号列表
     * @param installFee 安装费标志
     * @return 序列化结果
     */
    // 批量标志安装费
    Map<String, List<String>> UpdateProductInstallFee(List<String> productIds, String installFee);

    /**
     * 更新产品信息
     * @param product 产品
     * @return 序列化结果
     */
    // 更新产品信息
    Boolean UpdateProductInfo(Product product);
}
