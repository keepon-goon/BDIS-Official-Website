package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdis.bdis_website.entity.TeamMemberItem;

import java.util.List;

public interface TeamMemberItemService extends IService<TeamMemberItem> {
    List<TeamMemberItem> listAll();
}
