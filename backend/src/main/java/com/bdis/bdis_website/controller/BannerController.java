package com.bdis.bdis_website.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.BannerItem;
import com.bdis.bdis_website.service.BannerItemService;
import java.util.List;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerItemService bannerItemService;

    @GetMapping
    public Result<List<BannerItem>> getBannerList() {
        QueryWrapper<BannerItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByAsc("sort");
        List<BannerItem> bannerList = bannerItemService.list(queryWrapper);
        return Result.success(bannerList);
    }
} // 确保类的大括号完整闭合
