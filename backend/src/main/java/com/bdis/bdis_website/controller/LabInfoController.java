package com.bdis.bdis_website.controller;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.LabInfo;
import com.bdis.bdis_website.service.LabInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lab")
public class LabInfoController {

    private final LabInfoService labInfoService;

    // 构造器注入
    public LabInfoController(LabInfoService labInfoService) {
        this.labInfoService = labInfoService;
    }

    // 获取实验室信息（默认查主键为1的记录，实验室信息一般只有一条）
    @GetMapping("/info")
    public Result<LabInfo> getLabInfo() {
        LabInfo labInfo = labInfoService.getById(1L); // 主键为1的实验室信息
        return Result.success(labInfo);
    }
}

