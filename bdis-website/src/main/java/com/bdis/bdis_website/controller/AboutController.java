package com.bdis.bdis_website.controller;
import com.bdis.bdis_website.service.TeamMemberItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.HashMap;
@RestController
public class AboutController {
    @Resource
    private TeamMemberItemService teamMemberService;

    @GetMapping("/api/lab/team")
    public HashMap<String, Object> getTeam() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("code", 0);
        res.put("message", "ok");
        res.put("data", teamMemberService.listAll());
        return res;
    }
}