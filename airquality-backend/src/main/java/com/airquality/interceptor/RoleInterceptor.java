package com.airquality.interceptor;

import com.airquality.annotation.AdminRequired;
import com.airquality.common.UserContext;
import com.airquality.entity.User;
import com.airquality.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.airquality.utils.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 角色权限拦截器
 * 检查用户是否具有管理员权限
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理Controller方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法或类上是否有AdminRequired注解
        AdminRequired adminRequired = handlerMethod.getMethodAnnotation(AdminRequired.class);
        if (adminRequired == null) {
            adminRequired = handlerMethod.getBeanType().getAnnotation(AdminRequired.class);
        }

        // 如果没有AdminRequired注解，放行
        if (adminRequired == null) {
            return true;
        }

        // 获取当前用户ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            sendErrorResponse(response, 401, "请先登录");
            return false;
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            sendErrorResponse(response, 401, "用户不存在");
            return false;
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 1) {
            sendErrorResponse(response, 403, "用户已被禁用");
            return false;
        }

        // 检查用户角色
        if (!"admin".equals(user.getRole())) {
            sendErrorResponse(response, 403, "权限不足，需要管理员权限");
            return false;
        }

        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        Result<Void> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
