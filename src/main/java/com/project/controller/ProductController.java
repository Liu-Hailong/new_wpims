package com.project.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.Utils.Result;
import com.project.model.Account;
import com.project.model.Product;
import com.project.model.User;
import com.project.service.ProductService;
import com.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/product")
public class ProductController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // 相对于项目根路径的上传路径
    private static final String UPLOAD_FOLDER = "/upload/";
    // 返回给前端的服务器根路径（分布式、CDN场景很有用）
    private static final String URL_SERVER = "http://127.0.0.1:8080/";
    // 允许上传的文件扩展名
    private static final String[] ALLOW_EXTENSIONS = new String[]{

            // 图片
            "jpg", "jpeg", "png", "gif", "bmp"
            // 压缩包
            //"zip", "rar", "gz", "7z", "cab",
            // 音视频,
            //"wav", "avi", "mp4", "mp3", "m3u8", "ogg", "wma", "wmv", "rm", "rmvb", "aac", "mov", "asf", "flv",
            // 文档
            //"doc", "docx", "xls", "xlsx", "ppt", "pptx", "pot", "txt", "csv", "md", "pdf"
    };

    /**
     * 判断文件名是否允许上传
     * @param fileName 文件名
     * @return
     */
    public boolean isAllow(String fileName) {

        String ext = FilenameUtils.getExtension(fileName).toLowerCase();
        for (String allowExtension : ALLOW_EXTENSIONS) {

            if (allowExtension.toLowerCase().equals(ext)) {

                return true;
            }
        }
        return false;
    }

    // 管理员 增、删、查、改 产品信息
    @PostMapping("/add")
    public Result addProduct(String productid, String product_name, String other_info, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用",null);
        if (currentUser == null || (!currentUser.getRole().equals("管理员") && !currentUser.getRole().equals("发货员"))) return new Result(false,"","无权限访问",null);

        return new Result(productService.ProductStorage(currentUser, productid, product_name, other_info),"","",null);
    }

    @GetMapping("/delete")
    public Result deleteProduct(List<String> productids, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);
        return new Result(true,"","",productService.DeleteProduct(productids));
    }

    @GetMapping("/select")
    public Result selectProduct(Product product, Integer pageIndex, Integer pageSize, String timeType, String startTime, String endTime, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        PageHelper.startPage(pageIndex, pageSize);
        List<Product> products = productService.SelectProductInfo(product, timeType, startTime, endTime);
        PageInfo pageInfo = new PageInfo(products);
        Map<String, Object> map = new HashMap<>();
        map.put("pageInfo", pageInfo);
        return new Result(true,"","", map);
    }

    // 经销商 查询与自己相关的产品信息（包含产品编号、安装时间、是否结算安装费）
    @GetMapping("/selectInstallInfo")
    public Result selectInstallInfo(Product product, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用",null);
        if (currentUser == null || (!currentUser.getRole().equals("代理商") && !currentUser.getRole().equals("经销商"))) return new Result(false,"","无权限访问",null);

        // 子用户
        List<Integer> sub = userService.SelectAccountUserIdsByInvitedUserId(currentUser.getUserid());
        Map<String, Object> map = new HashMap<>();

        // 查询本人作为代理商的信息
        Product product1 = new Product();
        if (currentUser.getRole().equals("代理商")){
            product1.setOut_object_userid(currentUser.getUserid());
            List<Map<String, Object>> products = new ArrayList<>();
            productService.SelectProductInfo(product1, null, null, null).forEach(item->{
                Map<String, Object> map_ = new HashMap<>();
                map_.put("productid", item.getProductid());
                map_.put("install_time", item.getInstall_time());
                map_.put("is_complete_install_fee", item.getIs_complete_install_fee());
                products.add(map_);
            });
            map.put("代理商", products);
        }
        // 查询本人作为安装时的代理的信息
        product1.setOut_object_userid(null);
        product1.setInstall_agent_userid(currentUser.getUserid());
        List<Map<String, Object>> products = new ArrayList<>();
        productService.SelectProductInfo(product1, null, null, null).forEach(item->{
            Map<String, Object> map_ = new HashMap<>();
            map_.put("productid", item.getProductid());
            map_.put("install_time", item.getInstall_time());
            map_.put("is_complete_install_fee", item.getIs_complete_install_fee());
            products.add(map_);
        });
        map.put("install", products);
        // 查询子成员的信息
        List<Map<String, Object>> products1 = new ArrayList<>();
        if (sub.size() > 0){
            for(Integer subUser:sub){
                product1.setInstall_agent_userid(subUser);
                productService.SelectProductInfo(product1, null, null, null).forEach(item->{
                    Map<String, Object> map_ = new HashMap<>();
                    map_.put("productid", item.getProductid());
                    map_.put("install_time", item.getInstall_time());
                    map_.put("is_complete_install_fee", item.getIs_complete_install_fee());
                    map_.put("userid", subUser);
                    map_.put("name", item.getInstall_agent_name());
                    products1.add(map_);
                });
            }
        }
        map.put("sub", products1);
        return new Result(true,"","", map);
    }


    @PostMapping("/update")
    public Result updateProduct(Product product, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        Map<String, Object> map = new HashMap<>();
        map.put("productList", productService.UpdateProductInfo(product));
        return new Result(true,"","", map);
    }

    // 基本： 入库 出库 安装
    @GetMapping("/storage")
    public Result storageProduct(Product product, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用",null);
        if (currentUser == null || (!currentUser.getRole().equals("管理员") && !currentUser.getRole().equals("发货员"))) return new Result(false,"","无权限访问",null);
        return new Result(productService.ProductStorage(currentUser, product.getProductid(), product.getProduct_name(), product.getOther_info()),"","", null);
    }

    @GetMapping("/delivery")
    public Result deliveryProduct(Product product, User user, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用",null);
        if (currentUser == null || (!currentUser.getRole().equals("管理员") && !currentUser.getRole().equals("发货员"))) return new Result(false,"","无权限访问",null);
        return new Result(productService.ProductDelivery(currentUser, product, user),"","", null);
    }

    @GetMapping("/InstallFee")
    public Result ProductInstallFee(String productids, String installFee, HttpServletRequest request) {
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"), request.getHeader("SessionId"), null, "已启用", "管理员");
        if (currentUser == null) return new Result(false, "", "无权限访问", null);
        List<String> list = Arrays.asList(productids.split(","));
        System.out.println(list);
        Map<String, List<String>> map = productService.UpdateProductInstallFee(list, installFee);
        return new Result(map.get("success").size() == list.size(),"","", map);
    }

        @GetMapping("/install")
    public Result installProduct(Product product, User user, @RequestParam("file") MultipartFile[] file, HttpServletRequest request){
        String webAppFolder = request.getServletContext().getRealPath("/");

        Product currentProduct = productService.SelectProductByProductid(product.getProductid());
        if (currentProduct.hasInstall()) return new Result(false, "", "当前产品已经被安装", null);
        StringBuilder urlpath = new StringBuilder();
        if (file.length < 3) return new Result(false, "", "安装图片数量不足", null);
        if (file.length > 3) return new Result(false, "", "安装图片数量超过限制", null);
        for (MultipartFile multipartFile : file) {
            String fileName = multipartFile.getOriginalFilename();
            if (isAllow(fileName)) {

                String ext = FilenameUtils.getExtension(fileName).toLowerCase();
                String newFileName = UUID.randomUUID().toString().replace("-", "");
                // 自动创建上传目录
                String targetPath = FilenameUtils.normalize(webAppFolder + "/" + UPLOAD_FOLDER);
                String targetFile = FilenameUtils.normalize(targetPath + "/" + newFileName + "." + ext);
                new File(targetPath).mkdirs();

                try {

                    String urlPath = URL_SERVER + "/" + UPLOAD_FOLDER + "/" + newFileName + "." + ext;
                    urlpath.append(FilenameUtils.normalize(urlPath, true).replace("http:/", "http://").replace("https:/", "https://")+",");
                    multipartFile.transferTo(new File(targetFile));
                } catch (Exception e) {

                    e.printStackTrace();
                    Map<String, Object> resJson = new LinkedHashMap<>();
                    resJson.put("status", "error");
                    resJson.put("message", "文件上传失败：" + e.getMessage());
                    return new Result(false, "", "", resJson);
                }

            } else {

                Map<String, Object> resJson = new LinkedHashMap<>();
                resJson.put("status", "error");
                resJson.put("message", "该类型文件不允许上传");
                return new Result(false, "", "", resJson);
            }
        }
        if (productService.ProductInstall(user, product, String.valueOf(urlpath), product.getInstall_province(), product.getInstall_city(), product.getInstall_district(), product.getInstall_detail_address(), product.getCustomer_name(), product.getCustomer_phone())){
            Map<String, Object> resJson = new LinkedHashMap<>();
            resJson.put("status", "success");
            resJson.put("data", urlpath);
            return new Result(true,"","",resJson);
        }
        return new Result(false,"","调用数据库错误", null);
    }
}
