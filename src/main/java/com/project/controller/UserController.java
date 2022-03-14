package com.project.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.Utils.Result;
import com.project.model.Account;
import com.project.model.User;
import com.project.service.UserService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Value("${wechats.APPID}")
    String APPID;
    @Value("${wechats.APPSecret}")
    String APPSecret;
    @Value("${wechats.URL}")
    String URL;

    @GetMapping("/wxLogin")
    public Result wxLogin(String code){
        String url = URL + "?appid=" + APPID + "&secret=" + APPSecret + "&js_code=" + code + "&grant_type=authorization_code";
        // 提交微信远程服务器获取sessionid
        Map<Object, Object> map_ = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();//构建一个Client
            HttpGet get = new HttpGet(url.toString());    //构建一个GET请求
            HttpResponse response = client.execute(get);//提交GET请求
            HttpEntity result = response.getEntity();//拿到返回的HttpResponse的"实体"
            String content = EntityUtils.toString(result);
            map_ = (Map<Object, Object>) JSON.parse(content);
            if (!content.contains("openid")){
                return new Result(false, "400", "", map_);
            }
            System.out.println(content);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "400", "", map_);
        }
        String SessionId = (String)map_.get("session_key");
        String openid = (String)map_.get("openid");
        Account account = userService.SelectAccountByOpenId(openid);
        Map<String, Object> map = new HashMap<>();
        if (account == null){ // 账户不存在
            account = new Account();
            account.setOpenId(openid);
            account.setSessionId(SessionId);
            User user = new User();
            user.setRole("经销商");
            user.setName(openid);
            user.setState("未审核");
            if(!userService.AddUser(user, account)) return new Result(false, "", "用户添加失败", map);
            map.put("SessionId", SessionId);
            map.put("OpenId", openid);
            map.put("Info", userService.SelectUsersByConditions(user).get(0));
            return new Result(true, "100", "新用户，请尽快完善个人信息", map);
        } else {
            // 账户存在 更新账户会话token
            if(userService.UpdateLoginState(openid, SessionId)) {
                map.put("SessionId", SessionId);
                map.put("OpenId", openid);
                map.put("Info", userService.SelectUserByUserid(account.getUserid()));
                return new Result(true, "200", "", map);
            }
            return new Result(false, "", "用户添加失败", map);
        }
    }

    @GetMapping("/completeInfo")
    public Result completeInfo(User user, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"未审核",null);
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        if(user.valid()&&user.getProvince()!=null&&user.getProvince().equals("")
        && user.getCity()!=null&&user.getCity().equals("")
        && user.getDistrict()!=null&&user.getDistrict().equals("")
        && user.getDetail_address()!=null&&user.getDetail_address().equals("")){
            currentUser.setName(user.getName());
            currentUser.setPhone(user.getPhone());
            currentUser.setSex(user.getSex());
            currentUser.setProvince(user.getProvince());
            currentUser.setCity(user.getCity());
            currentUser.setDistrict(user.getDistrict());
            currentUser.setDetail_address(user.getDetail_address());
            if (userService.UpdateUser(currentUser)) return new Result(true,"","用户信息修改成功，等待管理员审核",null);
        }
        return new Result(false,"","用户信息格式错误或不完善",null);
    }

    @GetMapping("/adminLogin")
    public Result adminLogin(String username, String password){
        Account account = userService.SelectAccountByOpenId(username);
        if (account == null || !account.getPassword().equals(password)) return new Result(false, "", "账户密码错误", null);
        User user = userService.SelectUserByUserid(account.getUserid());
        if (user == null) return new Result(false, "", "用户查询错误", null);
        if (!user.getRole().equals("管理员")) return new Result(false, "", "非管理员用户", null);
        account.setSessionId(DigestUtils.md5DigestAsHex((account.getUserid() + new Date().toString()).getBytes()));
        userService.UpdateLoginState(account.getOpenId(), account.getSessionId());
        Map<String, Object> map = new HashMap<>();
        map.put("SessionId", account.getSessionId());
        map.put("OpenId", account.getOpenId());
        map.put("Info", user);
        return new Result(true, "200", "", map);
    }

    @GetMapping("/selectUser")
    public Result SelectUser(HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,null,null);
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        Map<String, Object> map = new HashMap<>();
        map.put("info", currentUser);
        return new Result(true,"","",map);
    }

    @GetMapping("/selectRelateUser") // 查询相关联的用户信息
    public Result SelectRelateUser(HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用",null);
        if (currentUser == null) return new Result(false,"","无权限访问",null);
        // 上级用户
        Account currentAccount = userService.SelectAccountByUserId(currentUser.getUserid());
        User fa = userService.SelectUserByUserid(currentAccount.getInvited_userid());
        if (fa == null) return new Result(false,"","查询失败",null);
        List<User> sub = new ArrayList<>();
        userService.SelectAccountUserIdsByInvitedUserId(currentUser.getUserid()).forEach(item->{sub.add(userService.SelectUserByUserid(item));});
        Map<String, Object> map = new HashMap<>();
        map.put("fa", fa);
        map.put("sub", sub);
        return new Result(true,"","",map);
    }

    @GetMapping("/getDealerAgent")
    public Result getDealerAgent(){
        Map<String, Object> map = new HashMap<>();
        map.put("data",userService.SelectAgentDealer());
        return new Result(true, "", "", map);
    }

    @GetMapping("/selectUsers")
    public Result SelectUsers(User user, Integer pageIndex, Integer pageSize, HttpServletRequest request){

        // 用户控制(仅管理员用户)
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        PageHelper.startPage(pageIndex, pageSize);
        List<User> users = userService.SelectUsersByConditions(user);
        PageInfo pageInfo = new PageInfo(users);
        Map<String, Object> map = new HashMap<>();
        map.put("pageInfo", pageInfo);
        return new Result(true,"","",map);
    }

    @PostMapping("/addUser")
    public Result addUsers(User user, Account account, HttpServletRequest request){
        // 用户控制(仅管理员用户)
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        if (!userService.AddUser(user, account)){
            return new Result(false,"","添加用户失败",null);
        }
        return new Result(true,"","添加用户成功", null);
    }

    @GetMapping("/deleteUsers")
    public Result deleteUsers(List<Integer> list, HttpServletRequest request){
        // 用户控制(仅管理员用户)
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        Map<String, Object> map = new HashMap<>();
        map.put("data", userService.DeleteUsers(list));
        return new Result(true,"","",map);
    }

    @GetMapping("/deleteUser")
    public Result deleteUser(Integer userid, HttpServletRequest request){
        // 用户控制(仅管理员用户)
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        Map<String, Object> map = new HashMap<>();
        map.put("data", userService.DeleteUserByUserid(userid));
        return new Result(true,"","",map);
    }

    @GetMapping("/updateUserByAdmin")
    public Result updateUserByAdmin(User user, HttpServletRequest request){
        // 用户控制(仅管理员用户)
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用","管理员");
        if (currentUser == null) return new Result(false,"","无权限访问",null);

        if (!userService.UpdateUser(user)) return new Result(false,"","更改用户信息失败",null);
        return new Result(true,"","更改用户信息成功",null);
    }

/*
    @GetMapping("/updateUser")
    public Result updateUser(User user, HttpServletRequest request){
        User currentUser = userService.validUserInfo(request.getHeader("OpenId"),request.getHeader("SessionId"),null,"已启用",null);
        if (currentUser == null) return new Result(false,"","无权限访问",null);
        String province = user.getProvince();
        String city = user.getCity();
        String district = user.getDistrict();
        String name = user.getName();
        String sex = user.getSex();
        String detail_address = user.getDetail_address();
        String phone = user.getPhone();

        if (province!=null&&!province.equals("")) currentUser.setProvince(province);
        if (city!=null&&!city.equals("")) currentUser.setCity(city);
        if (district!=null&&!district.equals("")) currentUser.setDistrict(district);
        if (name!=null&&!name.equals("")) currentUser.setName(name);
        if (sex!=null&&!sex.equals("")) currentUser.setSex(sex);
        if (detail_address!=null&&!detail_address.equals("")) currentUser.setDetail_address(detail_address);
        if (phone!=null&&!phone.equals("")) currentUser.setPhone(phone);

        if (!userService.UpdateUser(currentUser)) return new Result(false,"","更改用户信息失败",null);
        return new Result(true,"","更改用户信息成功",null);
    }
*/
}
