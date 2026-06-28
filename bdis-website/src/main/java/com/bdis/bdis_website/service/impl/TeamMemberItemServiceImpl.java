package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.TeamMemberItem;
import com.bdis.bdis_website.mapper.TeamMemberItemMapper;
import com.bdis.bdis_website.service.TeamMemberItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberItemServiceImpl
        extends ServiceImpl<TeamMemberItemMapper, TeamMemberItem>
        implements TeamMemberItemService {

    @Override
    public List<TeamMemberItem> listAll() {
        return list();
    }
}
