package com.bdis.bdis_website.controller.admin;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.LabInfo;
import com.bdis.bdis_website.service.LabInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/lab-info")
public class AdminLabInfoController {

    @Autowired
    private LabInfoService labInfoService;

    @GetMapping
    public ResponseEntity<Result<LabInfo>> get() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(Result.success(labInfoService.getSiteInfo()));
    }

    @PutMapping
    public Result<LabInfo> update(@Valid @RequestBody LabInfo info) {
        info.setId(1L);
        if (!labInfoService.updateById(info)) {
            return Result.fail("未能更新站点信息，请检查实验室记录是否存在");
        }
        return Result.success(info);
    }
}
