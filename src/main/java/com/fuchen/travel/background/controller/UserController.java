package com.fuchen.travel.background.controller;

import com.fuchen.travel.background.entity.Page;
import com.fuchen.travel.background.entity.User;
import com.fuchen.travel.background.service.UserService;
import com.fuchen.travel.background.util.HostHolder;
import com.fuchen.travel.background.util.TravelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.StringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @Author 伏辰
 * @Date 2023/1/5
 * 用户-controller层
 */
@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${travel.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${upload.path.image}")
    private String uploadPath;


    /**
     * 用户管理
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/user-control")
    public String getUserControlPage(Model model, Page page, HttpServletRequest request) {

        //获取用户数量
        Integer userCount = userService.getUserCount(request);

        //设置分页数据
        page.setLimit(5);
        page.setPath("/user-control");
        page.setRows(userCount);
        //分页查询用户集合
        List<User> users = userService.getAllUser(page.getOffset(), page.getLimit());
        //创建list用于存放用户列表数据
        List<Map<String, User>> userList = new ArrayList<>(userCount);
        //循环遍历，将数据放入map后加入到list集合中
        for (int i = 0; i < users.size(); i++) {
            Map<String, User> map = new HashMap<>();
            map.put("user",users.get(i));
            userList.add(map);
        }

        model.addAttribute("userList", userList);

        return "/pages/user-control";
    }

    /**
     * 进入设置页面
     * @return
     */
    @GetMapping("/setting")
    public String getSettingPage() {

        return "/pages/setting";
    }

    /**
     * 用户修改密码
     * @param model 视图模板
     * @return 响应页面
     */
    @PostMapping("/user/update")
    public String updatePassword(String password, String passwordRe, Model model){
        if (!password.equals(passwordRe)) {
            model.addAttribute("passwordReMsg", "两次密码不相同");
            return "/pages/setting";
        }
        userService.updatePassword(password);

        return "redirect:/logout";
    }

    /**
     * 添加用户
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @return
     */
    @ResponseBody
    @PostMapping("/addUser")
    public String addUser(String username, String email, String password) {
        //判断当前用户名是否被使用
        if (!userService.isUserExist(username)) {
            //存在返回json
            return TravelUtil.getJsonString(1, "该用户名已存在！");
        }
        //判断当前邮箱是否被使用
        if (!userService.isEmailExist(email)) {
            //存在返回json
            return TravelUtil.getJsonString(2, "该邮箱已被使用！");
        }
        //添加用户
        userService.addUser(username, email, password);

        return TravelUtil.getJsonString(0,"用户添加成功！");
    }

    /**
     * 删除用户
     * @param list 用户的list集合
     * @return
     */
    @PostMapping("/removeUser")
    @ResponseBody
    public String removeUser(@RequestParam("list[]")List<String> list){
        //判断集合是否为空
        if (list.size() == 0) {
            TravelUtil.getJsonString(1,"未选择用户！");
        }
        //删除用户信息
        userService.removeUser(list);
        return TravelUtil.getJsonString(0,"用户删除成功！");
    }

    /**
     * 封禁用户
     * @param list 用户的list集合，id?status，?左边为用户id，右边为当前id用户的状态
     * @return
     */
    @PostMapping("/banUser")
    @ResponseBody
    public String banUser(@RequestParam("list[]") List<String> list){
        //判断集合是否为空
        if (list.size() == 0) {
            TravelUtil.getJsonString(1,"未选择用户！");
        }

        //封禁-解封用户信息
        userService.banUser(list);
        return TravelUtil.getJsonString(0,"操作成功！");
    }


    @GetMapping("/user/ban")
    public String seeUserBan(Model model, Page page){

        Integer userCount = userService.getBanUserCount();

        //设置分页数据
        page.setLimit(5);
        page.setPath("/user/ban");
        page.setRows(userCount);

        //查询封禁用户
        List<User> allUserAdmin = userService.getAllUserBan(page.getOffset(), page.getLimit());
        //创建list用于存放用户列表数据
        List<Map<String, User>> userList = new ArrayList<>(userCount);
        //循环遍历，将数据放入map后加入到list集合中
        for (int i = 0; i < allUserAdmin.size(); i++) {
            Map<String, User> map = new HashMap<>();
            map.put("user",allUserAdmin.get(i));
            userList.add(map);
        }

        model.addAttribute("userList", userList);


        return "/pages/user-control";
    }

    /**
     * 只显示某个类型的用户
     * @param type 用户类型
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/user/userType")
    public String userType(String type, Model model, Page page) {

        Integer userCount = userService.getUserCountAdmin(type);

        //设置分页数据
        page.setLimit(5);
        page.setPath("/user/userType?type="+type);
        page.setRows(userCount);

        List<User> allUserAdmin = userService.getAllUserAdmin(type, page.getOffset(), page.getLimit());
        //创建list用于存放用户列表数据
        List<Map<String, User>> userList = new ArrayList<>(userCount);
        //循环遍历，将数据放入map后加入到list集合中
        for (int i = 0; i < allUserAdmin.size(); i++) {
            Map<String, User> map = new HashMap<>();
            map.put("user",allUserAdmin.get(i));
            userList.add(map);
        }

        model.addAttribute("userList", userList);

        return "/pages/user-control";
    }

    /**
     * 搜索用户信息
     * @param keyword 关键字
     * @param type 用户类型
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/user/search")
    public String searchUser(String keyword, String type, Model model, Page page){
        //判断关键字是否为空
        if (keyword.isEmpty()) {
            model.addAttribute("searchMsg", "请输入搜素内容！");
            return "/pages/user-control";
        }

        //获取用户数量
        Integer userCount = userService.getUserCountSearch(keyword, type);

        //设置分页数据
        page.setLimit(5);
        page.setPath("/user/search?keyword=" + keyword);
        page.setRows(userCount);
        //分页查询用户集合
        List<User> users = userService.getUserSearch(keyword, type, page.getOffset(), page.getLimit());
        //创建list用于存放用户列表数据
        List<Map<String, User>> userList = new ArrayList<>(userCount);
        //循环遍历，将数据放入map后加入到list集合中
        for (int i = 0; i < users.size(); i++) {
            Map<String, User> map = new HashMap<>();
            map.put("user",users.get(i));
            userList.add(map);
        }

        model.addAttribute("userList", userList);

        return "/pages/user-control";
    }

    /**
     * 头像上传腾讯云
     * @param headerImg 头像文件
     * @param model 模型
     * @return 重定向到index页面
     */
    @PostMapping("/header/url")
    public String uploadHeaderToQCloud(MultipartFile headerImg, Model model){
        //判断头像是否为空
        if (headerImg == null) {
            model.addAttribute("headerImgMsg","请上传图片！");
            return "/site/setting";
        }
        //判断文件后缀
        String filename = headerImg.getOriginalFilename();
        //获得文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("headerImgMsg","图片格式错误！");
            return "/site/setting";
        }

        //上传腾讯云
        userService.uploadHeaderToQCloud(headerImg, filename, suffix);

        return "redirect:/setting";
    }



    /**
     * 获取头像
     * @param filename 从请求路径中获取文件名
     * @param response 用于响应图片
     * 废弃
     */
    @Deprecated
    @GetMapping("/user/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //服务器存放路径
        filename = uploadPath + "/" + filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);

        try (FileInputStream inputStream = new FileInputStream(filename);) {

            OutputStream outputStream = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("读写图像失败！" + e.getMessage());
        }
    }

    /**
     * 用户上传头像
     * @param headerImg 上传的头像
     * @param model 视图模板
     * @return 响应页面
     * 废弃
     */
    @Deprecated
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImg, Model model){
        //判断头像是否为空
        if (headerImg == null) {
            model.addAttribute("headerImgMsg","请上传图片！");
            return "/site/setting";
        }
        //判断文件后缀
        String filename = headerImg.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("headerImgMsg","图片格式错误！");
            return "/site/setting";
        }
        //修改头像
        userService.updateHeader(headerImg, filename, suffix);

        return "redirect:/setting";
    }



}
