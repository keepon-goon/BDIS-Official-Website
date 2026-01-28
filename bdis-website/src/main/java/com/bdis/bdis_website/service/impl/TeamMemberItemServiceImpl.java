package com.bdis.bdis_website.service.impl;

import com.bdis.bdis_website.entity.TeamMemberItem;
import com.bdis.bdis_website.mapper.TeamMemberItemMapper;
import com.bdis.bdis_website.service.TeamMemberItemService;
import org.springframework.stereotype.Service;


import jakarta.annotation.Resource;

import java.util.List;

@Service
public class TeamMemberItemServiceImpl implements TeamMemberItemService {

    @Resource
    private TeamMemberItemMapper mapper;

    @Override
    public List<TeamMemberItem> listAll() {
        return mapper.selectList(null);
    }
}
