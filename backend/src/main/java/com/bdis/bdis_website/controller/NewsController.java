package com.bdis.bdis_website.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdis.bdis_website.entity.NewsItem;
import com.bdis.bdis_website.service.NewsItemService;
import com.bdis.bdis_website.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 新闻/动态接口控制器
 * 手动指定查询字段，解决字段笔误和关键字转义问题
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsItemService newsItemService;

    /**
     * 获取新闻列表接口
     * @param limit 可选参数：限制返回条数
     * @param category 可选参数：类别过滤（notice/recruit等）
     * @return 统一返回结果
     */
    @GetMapping
    public Result<List<NewsItem>> getNewsList(
            @RequestParam(required = false, defaultValue = "0") Integer limit,
            @RequestParam(required = false) String category
    ) {
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<NewsItem> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();

        // 1. 手动指定查询字段，对order关键字加反引号转义，彻底杜绝1d笔误
        queryWrapper.select(
                "id",
                "title",
                "category",
                "summary",
                "publish_date",
                "link",
                "cover_thumb_url",
                "`order`"
        );

        // 2. 按order升序排序，关键字加反引号
        queryWrapper.orderByAsc("`order`");

        // 3. 类别过滤条件
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }

        List<NewsItem> newsList;
        // 4. 分页查询（避免直接拼接LIMIT，防止SQL注入）
        if (limit > 0) {
            Page<NewsItem> page = new Page<>(1, limit);
            IPage<NewsItem> newsPage = newsItemService.page(page, queryWrapper);
            newsList = newsPage.getRecords();
        } else {
            newsList = newsItemService.list(queryWrapper);
        }

        // 返回成功结果
        return Result.success(newsList);
    }
}
