package com.bdis.bdis_website.interceptor;

import com.bdis.bdis_website.util.JwtUtil;
import com.bdis.bdis_website.service.AdminUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdminUserService adminUserService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            writeError(response, 401, "未登录，请先登录");
            return false;
        }

        Long userId;
        try {
            userId = jwtUtil.parseToken(token).get("userId", Long.class);
        } catch (Exception e) {
            writeError(response, 401, "Token已过期或无效，请重新登录");
            return false;
        }

        var user = userId == null ? null : adminUserService.getById(userId);
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())) {
            writeError(response, 401, "账号不存在或已停用，请重新登录");
            return false;
        }

        request.setAttribute("adminUserId", user.getId());
        request.setAttribute("adminUsername", user.getUsername());
        return true;
    }

    private void writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("message", message);
        result.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
