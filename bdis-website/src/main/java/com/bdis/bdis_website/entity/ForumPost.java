package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

// 联系方式类型枚举（文档4.1规定的enum值）
enum ContactType {
    wechat, qq, email, link
}

@Data
@TableName("forum_post") // 绑定数据库表
public class ForumPost {
    private String id; // 主键
    private String title; // 标题
    private String summary; // 一句话简介
    private String[] recruitRoles; // 招募角色（数组类型）
    private Integer currentSize; // 当前人数
    private Integer targetSize; // 目标人数
    private String type; // 联系方式类型（枚举约束）
    private String value; // 联系方式值
    private String deadline; // 截止日期
    private String coverUrl; // 右侧封面图（可选）
    private String link; // 详情链接（V2预留）

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    private Integer sortOrder; // 排序（可选）
}