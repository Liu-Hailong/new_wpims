package com.project.service.impl;

import com.project.Utils.Result;
import com.project.dao.ProductDao;
import com.project.model.Product;
import com.project.model.User;
import com.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;


    @Override
    public Boolean ProductStorage(User user, String productid, String product_name, String other_info) {
        // 产品入库
        return productDao.InsertProduct(productid, product_name, user.getUserid(), user.getName(), other_info);
    }

    @Override
    public Boolean ProductDelivery(User user, Product product, User object_agent_user) {
        // 产品出库
        Product currentProduct = productDao.SelectProductByProductid(product.getProductid()); // 查阅当前产品信息
        // 判断当前产品未入库、已出库、已经安装完毕
        if (!currentProduct.hasStorage() || currentProduct.hasDelivery() || currentProduct.hasInstall()) return false;
        // 当前产品有条件出库
        Product new_product = new Product();
        new_product.setProductid(product.getProductid());
        new_product.setOut_name(user.getName());
        new_product.setOut_time("*"); // 设置产品出库时间
        new_product.setOut_userid(user.getUserid());
        new_product.setOut_object_name(object_agent_user.getName());
        new_product.setOut_object_userid(object_agent_user.getUserid());
        // 更新产品信息
        return productDao.UpdateProduct(new_product);
    }

    @Override
    public Boolean ProductInstall(User user, Product product, String product_picture, String province, String city, String district, String detail_address, String customer_name, String customer_phone) {
        // 产品安装
        Product currentProduct = productDao.SelectProductByProductid(product.getProductid()); // 查阅当前产品信息
        // 判断当前产品是否具备安装条件
        if (!currentProduct.hasStorage() || !currentProduct.hasDelivery() || currentProduct.hasInstall()) return false;
        // 当前产品有条件安装
        Product new_product = new Product();
        new_product.setProductid(product.getProductid());
        new_product.setInstall_agent_name(user.getName());
        new_product.setInstall_agent_userid(user.getUserid());
        new_product.setInstall_time("*"); // 设置产品安装时间
        new_product.setInstall_province(province);
        new_product.setInstall_city(city);
        new_product.setInstall_district(district);
        new_product.setInstall_detail_address(detail_address);
        new_product.setInstall_picture(product_picture);
        new_product.setCustomer_name(customer_name);
        new_product.setCustomer_phone(customer_phone);
        // 更新产品信息
        return productDao.UpdateProduct(new_product);
    }

    @Override
    public List<Product> SelectProductInfo(Product product, String timeType, String startTime, String endTime) {
        // 查询产品信息
        return productDao.SelectProductsByConditions(product, timeType, startTime, endTime);
    }

    @Override
    public Product SelectProductByProductid(String productid) {
        return productDao.SelectProductByProductid(productid);
    }

    @Override
    public Map<String, List<String>> DeleteProduct(List<String> productIds) {
        // 批量删除产品
        Map<String, List<String>> map = new HashMap<>();
        List<String> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();
        productIds.forEach(item->{
            if (productDao.DeleteProduct(item)) success.add(item);
            else fail.add(item);
        });
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    @Override
    public Map<String, List<String>> UpdateProductInstallFee(List<String> productIds, String installFee) {
        // 批量结算费用
        Map<String, List<String>> map = new HashMap<>();
        List<String> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();
        Product product = new Product();
        product.setIs_complete_install_fee(installFee);
        productIds.forEach(item->{
            product.setProductid(item);
            System.out.println(product);
            if (productDao.UpdateProduct(product)) success.add(item);
            else fail.add(item);
        });
        map.put("success", success);
        map.put("fail", fail);
        return map;
    }

    @Override
    public Boolean UpdateProductInfo(Product product) {
        // 更新产品信息
        return productDao.UpdateProduct(product);
    }
}
