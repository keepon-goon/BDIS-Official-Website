package com.bdis.bdis_website.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.AnnouncementItem;
import com.bdis.bdis_website.service.AnnouncementItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") // 接口根路径
public class AnnouncementController {

    @Autowired
    private AnnouncementItemService announcementService;

    // 文档3.2接口：GET /api/announcements
    @GetMapping("/announcements")
    public Result<Map<String, Object>> getAnnouncements(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String category
    ) {
        // 1. 调用Service获取分页数据
        IPage<AnnouncementItem> announcementPage = announcementService.getAnnouncementPage(page, limit, category);

        // 2. 组装返回格式（文档要求：data包含list/total/page/limit）
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("list", announcementPage.getRecords()); // 数据列表
        resultData.put("total", announcementPage.getTotal()); // 总条数
        resultData.put("page", announcementPage.getCurrent()); // 当前页码
        resultData.put("limit", announcementPage.getSize()); // 每页条数

        return Result.success(resultData);
    }
}

