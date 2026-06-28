package com.bdis.bdis_website.controller.admin;

import com.bdis.bdis_website.common.Result;
import com.bdis.bdis_website.entity.AdminUser;
import com.bdis.bdis_website.service.AdminUserService;
import com.bdis.bdis_website.util.JwtUtil;
import com.bdis.bdis_website.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }

        AdminUser user = adminUserService.findByUsername(username);
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.fail("账号已被禁用");
        }

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return Result.fail("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());

        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("adminUserId");
        AdminUser user = adminUserService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        return Result.success(data);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(HttpServletRequest request, @RequestBody Map<String, String> body) {
        Long userId = (Long) request.getAttribute("adminUserId");
        AdminUser user = adminUserService.getById(userId);

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (oldPassword == null || newPassword == null || oldPassword.isEmpty() || newPassword.isEmpty()) {
            return Result.fail("原密码和新密码不能为空");
        }

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return Result.fail("原密码错误");
        }

        user.setPassword(PasswordUtil.encodePassword(newPassword));
        adminUserService.updateById(user);
        return Result.success();
    }
}
