package com.airquality.controller;

import com.airquality.common.UserContext;
import com.airquality.dto.LoginDTO;
import com.airquality.dto.RegisterDTO;
import com.airquality.dto.UserVO;
import com.airquality.entity.User;
import com.airquality.service.UserService;
import com.airquality.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Validated @RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            UserVO userVO = new UserVO();
            org.springframework.beans.BeanUtils.copyProperties(user, userVO);
            return Result.success("注册成功", userVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        try {
            UserService.LoginResult loginResult = userService.login(loginDTO);
            Map<String, Object> data = new HashMap<>();
            data.put("token", loginResult.getToken());
            data.put("user", loginResult.getUser());
            return Result.success("登录成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        UserVO userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT无状态，前端删除Token即可
        return Result.success("退出成功", null);
    }
}
