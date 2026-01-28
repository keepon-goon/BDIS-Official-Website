package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.ProjectProgressItem;
import com.bdis.bdis_website.service.ProjectProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目模块Controller
 * 接口路径/参数不变，返回适配新表结构的字段
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectProgressService projectProgressService;

    /**
     * 项目进展接口
     * GET /api/projects/progress
     * 可选参数：limit（限制返回条数，如?limit=3）
     */
    @GetMapping("/progress")
    public Result<List<ProjectProgressItem>> getProjectProgress(
            @RequestParam(required = false) Integer limit
    ) {
        List<ProjectProgressItem> progressList = projectProgressService.getProjectProgressList(limit);
        return Result.success(progressList);
    }
}
