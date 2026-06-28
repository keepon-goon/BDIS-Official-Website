package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.AdminUser;
import com.bdis.bdis_website.mapper.AdminUserMapper;
import com.bdis.bdis_website.service.AdminUserService;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {
    @Override
    public AdminUser findByUsername(String username) {
        QueryWrapper<AdminUser> qw = new QueryWrapper<>();
        qw.eq("username", username);
        return getOne(qw);
    }
}
