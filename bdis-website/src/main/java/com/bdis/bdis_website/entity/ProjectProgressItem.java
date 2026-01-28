package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 项目进展实体类
 * 完全匹配project_progress_item表结构
 */
@Data
@TableName("projects")
public class ProjectProgressItem {
    private String id; // 项目主键ID，自增
    private String name; // 项目名称（UI主标题）
    private String tagline; // 一句话副标题
    private String status; // 项目状态：recruiting/ongoing/paused/done
    private Integer progress; // 进度（0-100），默认0
    private String recruitRoles; // 数据库字段recruit_roles（驼峰映射）
    private String members; // 数据库字段members
    @TableField(value = "updated_at") // 解决字段名驼峰与数据库下划线映射
    private String updatedAt; // 开始时间/更新时间（前端显示，字符串类型）
    private String coverUrl; // 数据库字段cover_url（封面图地址）
    private String link; // 详情链接
    private Integer sortOrder; // 排序优先级，默认0

    // 非数据库字段：前端用的数组格式角色/成员
    @TableField(exist = false)
    private String[] recruitRolesArray;
    @TableField(exist = false)
    private String[] membersArray;
}
