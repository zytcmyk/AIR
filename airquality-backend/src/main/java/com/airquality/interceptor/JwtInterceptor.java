package com.airquality.interceptor;

import com.airquality.common.UserContext;
import com.airquality.config.JwtConfig;
import com.airquality.utils.JwtUtil;
import com.alibaba.fastjson2.JSON;
import com.airquality.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取请求头中的Token
        String authHeader = request.getHeader(jwtConfig.getHeader());

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(jwtConfig.getPrefix())) {
            // 去除Bearer前缀
            String token = authHeader.substring(jwtConfig.getPrefix().length()).trim();

            // 验证Token
            if (jwtUtil.validateToken(token)) {
                // 将用户信息存入ThreadLocal
                Long userId = jwtUtil.getUserId(token);
                String username = jwtUtil.getUsername(token);
                UserContext.setUserId(userId);
                UserContext.setUsername(username);

                // 将用户ID存入request attribute
                request.setAttribute("userId", userId);
                request.setAttribute("username", username);

                return true;
            }
        }

        // Token无效，返回401
        writeUnauthorizedResponse(response);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal
        UserContext.clear();
    }

    /**
     * 写入未授权响应
     */
    private void writeUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(JSON.toJSONString(Result.unauthorized("未登录或Token已过期，请重新登录")));
    }
}
