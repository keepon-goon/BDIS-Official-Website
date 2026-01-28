package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("team_member")
public class TeamMemberItem {

    private Long id;
    private String name;
    private String role;
    private String description;
    private String avatarUrl;

}