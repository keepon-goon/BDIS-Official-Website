package com.bdis.bdis_website.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bdis.bdis_website.entity.EducationResource;
import com.bdis.bdis_website.service.EducationResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.HashMap;

@RestController
public class ResourceController {

    @Resource
    private EducationResourceService service;

    @GetMapping("/api/resources")
    public HashMap<String, Object> getResources(
            Integer page,
            Integer limit,
            String category,
            String keyword
    ) {
        page = page == null ? 1 : page;
        limit = limit == null ? 12 : limit;

        IPage<EducationResource> p = service.page(page, limit, category, keyword);

        HashMap<String, Object> data = new HashMap<>();
        data.put("list", p.getRecords());
        data.put("total", p.getTotal());
        data.put("page", page);
        data.put("limit", limit);

        HashMap<String, Object> res = new HashMap<>();
        res.put("code", 0);
        res.put("message", "ok");
        res.put("data", data);

        return res;
    }
}