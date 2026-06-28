package com.bdis.bdis_website.controller.admin;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.LabInfo;
import com.bdis.bdis_website.service.LabInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/lab-info")
public class AdminLabInfoController {

    @Autowired
    private LabInfoService labInfoService;

    @GetMapping
    public Result<LabInfo> get() {
        LabInfo info = labInfoService.getById(1L);
        return Result.success(info);
    }

    @PutMapping
    public Result<LabInfo> update(@RequestBody LabInfo info) {
        info.setId(1L);
        labInfoService.updateById(info);
        return Result.success(info);
    }
}
