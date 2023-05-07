package com.fuchen.travel.background.controller;

import com.fuchen.travel.background.entity.Page;
import com.fuchen.travel.background.entity.Scenic;
import com.fuchen.travel.background.entity.User;
import com.fuchen.travel.background.service.ScenicService;
import com.fuchen.travel.background.util.TravelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
 */
@Controller
@Slf4j
public class ScenicController {

    @Autowired
    private ScenicService scenicService;

    @Value("${scenic.path.image}")
    private String scenicImage;

    /**
     * 浴室类型管理
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/scenic-control")
    public String scenicControl(Model model, Page page) {
        //获取景点数量
        Integer scenicCount = scenicService.getScenicCount();
        //设置分页数据
        page.setLimit(5);
        page.setPath("/scenic-control");
        page.setRows(scenicCount);
        //获取景点集合
        List<Scenic> scenic = scenicService.getScenic(page.getOffset(), page.getLimit());
        //用户存放景点数据
        List<Map<String, Scenic>> scenicList = new ArrayList<>(scenicCount);
        //遍历景点集合，将其通过map放入list中
        for (int i = 0; i < scenic.size(); i++) {
            Map<String, Scenic> map = new HashMap<>();
            map.put("scenic", scenic.get(i));
            scenicList.add(map);
        }

        model.addAttribute("scenicList", scenicList);

        return "/pages/scenic-control";
    }
    /**
     * 预约订单管理
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/scenic-controll")
    public String scenicControl1(Model model, Page page) {
        //获取景点数量
        Integer scenicCount = scenicService.getScenicCount1();
        //设置分页数据
        page.setLimit(5);
        page.setPath("/scenic-controll");
        page.setRows(scenicCount);
        //获取景点集合
        List<Scenic> scenic = scenicService.getScenic1(page.getOffset(), page.getLimit());
        log.info(scenic.toString());
        //用户存放景点数据
        List<Map<String, Scenic>> scenicList = new ArrayList<>(scenicCount);
        //遍历景点集合，将其通过map放入list中
        for (int i = 0; i < scenic.size(); i++) {
            Map<String, Scenic> map = new HashMap<>();
            map.put("scenic", scenic.get(i));
            scenicList.add(map);
        }

        model.addAttribute("scenicList1", scenicList);

        return "/pages/scenic-controll";
    }

    @PostMapping("/audit")
    @ResponseBody
    public String updateAudit(Integer audit, Integer auditId) {
        scenicService.updateAudit(auditId, audit);
        return TravelUtil.getJsonString(0);
    }

    /**
     * 商品管理
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/scenic-controlll")
    public String scenicControl2(Model model, Page page) {
        //获取景点数量
        Integer scenicCount = scenicService.getScenicCount2();
        //设置分页数据
        page.setLimit(5);
        page.setPath("/scenic-controlll");
        page.setRows(scenicCount);
        //获取景点集合
        List<Scenic> scenic = scenicService.getScenic2(page.getOffset(), page.getLimit());
        //用户存放景点数据
        List<Map<String, Scenic>> scenicList = new ArrayList<>(scenicCount);
        //遍历景点集合，将其通过map放入list中
        for (int i = 0; i < scenic.size(); i++) {
            Map<String, Scenic> map = new HashMap<>();
            map.put("scenic", scenic.get(i));
            scenicList.add(map);
        }

        model.addAttribute("scenicList2", scenicList);

        return "/pages/scenic-controlll";
    }
    /**
     * 商品订单管理
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/scenic-controllll")
    public String scenicControl3(Model model, Page page) {
        //获取景点数量
        Integer scenicCount = scenicService.getScenicCount3();
        //设置分页数据
        page.setLimit(5);
        page.setPath("/scenic-controlll");
        page.setRows(scenicCount);
        //获取景点集合
        List<Scenic> scenic = scenicService.getScenic3(page.getOffset(), page.getLimit());
        //用户存放景点数据
        List<Map<String, Scenic>> scenicList = new ArrayList<>(scenicCount);
        //遍历景点集合，将其通过map放入list中
        for (int i = 0; i < scenic.size(); i++) {
            Map<String, Scenic> map = new HashMap<>();
            map.put("scenic", scenic.get(i));
            scenicList.add(map);
        }

        model.addAttribute("scenicList2", scenicList);

        return "/pages/scenic-controllll";
    }
    /**
     * 获取景区图片
     * @param imageName 景区图片的名称
     * @param response 响应体
     */
    @GetMapping("/scenicImg/{imageName}")
    public void getScenicImage(@PathVariable("imageName") String imageName, HttpServletResponse response) {
        //服务器存放路径
        imageName = scenicImage + "/" + imageName;
        //文件后缀
        String suffix = imageName.substring(imageName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);

        try (FileInputStream inputStream = new FileInputStream(imageName);) {

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
     * 推荐景点
     * @param recommendScenic 景点id
     * @param model
     * @return
     */
    @PostMapping("/recommend")
    public String recommendScenic(String recommendScenic, Model model) {
        //判断推荐景点数量是否已满
        if (scenicService.getScenicRecommendCount() == 6) {
            model.addAttribute("recommendCountMsg", "推荐景点已满！");
            return "/index";
        }

        //判断景点id是否为空
        if (recommendScenic.isEmpty()) {
            model.addAttribute("recommendMsg", "请填写景点id");
            return null;
        }
        //推荐景点
        scenicService.recommend(recommendScenic);
        return "redirect:/index";
    }

    @PostMapping("/removeRecommend")
    public String removeRecommendScenic(String removeRecommend, Model model){
        //判断推荐景点数量是否已满
        if (scenicService.getScenicRecommendCount() == 0) {
            model.addAttribute("recommendCountMsg", "推荐景点不足！");
            return "/index";
        }

        //判断景点id是否为空
        if (removeRecommend.isEmpty()) {
            model.addAttribute("removeRecommendMsg", "请填写景点id");
        }
        //移出推荐景点
        scenicService.removeRecommend(removeRecommend);
        return "redirect:/index";
    }

    /**
     * 添加景点信息
     * @param scenicName 景点名称
     * @param scenicImg 景点图片
     * @param model 模板
     * @return
     */
    @PostMapping("/addScenic")
    public String addScenic(String scenicName, MultipartFile scenicImg, Model model){

        Scenic scenic = new Scenic();
        scenic.setScenicName(scenicName);

        //判断文件后缀
        String filename = scenicImg.getOriginalFilename();
        //获得文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("headerImgMsg","图片格式错误！");
            return "/site/setting";
        }
        //添加景点
        scenicService.addScenic(scenic, scenicImg, filename, suffix);

        return "redirect:/scenic-controll";
    }

    /**
     * 删除景点信息
     * @param list 需要删除的景点id集合
     * @return
     */
    @ResponseBody
    @PostMapping("/removeScenic")
    public String removeScenic(@RequestParam("list[]")List<String> list) {
        //判断集合是否为空
        if (list.size() == 0) {
            TravelUtil.getJsonString(1,"未选择景点！");
        }
        //删除景点信息
        scenicService.removeScenic(list);
        return TravelUtil.getJsonString(0,"景点删除成功！");
    }

    @GetMapping("/scenic/search")
    public String searchUser(String keyword, Model model, Page page){
        //判断关键字是否为空
        if (keyword.isEmpty()) {
            model.addAttribute("searchMsg", "请输入搜素内容！");
            return "/pages/scenic-control";
        }

        //获取用户数量
        Integer scenicCount = scenicService.getScenicCountSearch(keyword);

        //设置分页数据
        page.setLimit(5);
        page.setPath("/scenic/search?keyword=" + keyword);
        page.setRows(scenicCount);
        //分页查询用户集合
        List<Scenic> scenic = scenicService.getScenicSearch(keyword, page.getOffset(), page.getLimit());
        //创建景点集合存放景点数据
        List<Map<String, Scenic>> scenicList = new ArrayList<>(scenicCount);
        //遍历景点集合，将其通过map放入list中
        for (int i = 0; i < scenic.size(); i++) {
            Map<String, Scenic> map = new HashMap<>();
            map.put("scenic", scenic.get(i));
            scenicList.add(map);
        }

        model.addAttribute("scenicList", scenicList);

        return "/pages/scenic-control";
    }
}
