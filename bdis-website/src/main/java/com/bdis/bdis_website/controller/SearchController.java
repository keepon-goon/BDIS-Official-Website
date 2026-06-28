package com.bdis.bdis_website.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.AnnouncementItem;
import com.bdis.bdis_website.entity.EducationResource;
import com.bdis.bdis_website.entity.NewsItem;
import com.bdis.bdis_website.service.AnnouncementItemService;
import com.bdis.bdis_website.service.EducationResourceService;
import com.bdis.bdis_website.service.NewsItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private NewsItemService newsItemService;

    @Autowired
    private AnnouncementItemService announcementService;

    @Autowired
    private EducationResourceService resourceService;

    @GetMapping
    public Result<Map<String, Object>> search(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        if (q == null || q.trim().isEmpty()) {
            return Result.fail("搜索关键词不能为空");
        }

        String keyword = q.trim();
        List<Map<String, Object>> results = new ArrayList<>();

        QueryWrapper<NewsItem> newsQw = new QueryWrapper<>();
        newsQw.and(w -> w.like("title", keyword).or().like("summary", keyword));
        newsQw.orderByDesc("publish_date");
        List<NewsItem> newsList = newsItemService.list(newsQw);
        for (NewsItem item : newsList) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "news");
            map.put("id", item.getId());
            map.put("title", item.getTitle());
            map.put("summary", item.getSummary());
            map.put("date", item.getPublishDate());
            map.put("link", "/info/news/" + item.getId());
            results.add(map);
        }

        QueryWrapper<AnnouncementItem> annQw = new QueryWrapper<>();
        annQw.and(w -> w.like("title", keyword).or().like("summary", keyword));
        annQw.orderByDesc("publish_date");
        List<AnnouncementItem> annList = announcementService.list(annQw);
        for (AnnouncementItem item : annList) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "announcement");
            map.put("id", item.getId());
            map.put("title", item.getTitle());
            map.put("summary", item.getSummary());
            map.put("date", item.getPublishDate());
            map.put("link", "/info/news/" + item.getId());
            results.add(map);
        }

        QueryWrapper<EducationResource> resQw = new QueryWrapper<>();
        resQw.and(w -> w.like("title", keyword).or().like("description", keyword));
        List<EducationResource> resList = resourceService.list(resQw);
        for (EducationResource item : resList) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "resource");
            map.put("id", item.getId());
            map.put("title", item.getTitle());
            map.put("summary", item.getDescription());
            map.put("date", "");
            map.put("link", item.getLink());
            results.add(map);
        }

        int total = results.size();
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, total);
        List<Map<String, Object>> paged = start < total ? results.subList(start, end) : new ArrayList<>();

        Map<String, Object> data = new HashMap<>();
        data.put("list", paged);
        data.put("total", total);
        data.put("page", page);
        data.put("limit", limit);
        return Result.success(data);
    }
}
