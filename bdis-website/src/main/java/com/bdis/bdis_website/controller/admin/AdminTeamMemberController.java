package com.bdis.bdis_website.controller.admin;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.TeamMemberItem;
import com.bdis.bdis_website.service.TeamMemberItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/team-members")
public class AdminTeamMemberController {

    @Autowired
    private TeamMemberItemService teamMemberService;

    @GetMapping
    public Result<List<TeamMemberItem>> list() {
        return Result.success(teamMemberService.listAll());
    }

    @GetMapping("/{id}")
    public Result<TeamMemberItem> get(@PathVariable Long id) {
        return Result.success(teamMemberService.getById(id));
    }

    @PostMapping
    public Result<TeamMemberItem> create(@RequestBody TeamMemberItem item) {
        teamMemberService.save(item);
        return Result.success(item);
    }

    @PutMapping("/{id}")
    public Result<TeamMemberItem> update(@PathVariable Long id, @RequestBody TeamMemberItem item) {
        item.setId(id);
        teamMemberService.updateById(item);
        return Result.success(item);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teamMemberService.removeById(id);
        return Result.success();
    }
}
