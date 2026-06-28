package com.bdis.bdis_website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdis.bdis_website.entity.AdminUser;

public interface AdminUserService extends IService<AdminUser> {
    AdminUser findByUsername(String username);
}
