package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.ProjectProgressItem;
import com.bdis.bdis_website.mapper.ProjectProgressMapper;
import com.bdis.bdis_website.service.ProjectProgressService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目进展Service实现类
 * 新增：逗号分隔字符串 → 数组的转换逻辑
 */
@Service
public class ProjectProgressServiceImpl extends ServiceImpl<ProjectProgressMapper, ProjectProgressItem>
        implements ProjectProgressService {

    @Override
    public List<ProjectProgressItem> getProjectProgressList(Integer limit) {
        // 1. 构建查询条件：按排序优先级升序
        QueryWrapper<ProjectProgressItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order");
        // 2. 处理limit参数：传正整数则限制返回条数
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        // 3. 查询数据库列表
        List<ProjectProgressItem> list = this.list(queryWrapper);
        // 4. 关键：将逗号分隔字符串转为数组，赋值给非数据库字段
        list.forEach(item -> {
            // 处理招募角色：空值/空字符串转空数组，否则按逗号分割
            item.setRecruitRolesArray(splitStrToArray(item.getRecruitRoles()));
            // 处理参与人员：同上
            item.setMembersArray(splitStrToArray(item.getMembers()));
        });
        return list;
    }

    // 工具方法：逗号分隔字符串 → 字符串数组
    private String[] splitStrToArray(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new String[0]; // 空值返回空数组，前端无需判空
        }
        return str.split(","); // 按逗号分割
    }
}
