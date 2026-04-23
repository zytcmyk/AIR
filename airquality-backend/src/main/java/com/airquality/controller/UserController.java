package com.airquality.controller;

import com.airquality.common.UserContext;
import com.airquality.dto.*;
import com.airquality.service.UserService;
import com.airquality.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人中心控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/profile")
    public Result<UserProfileDTO> getProfile() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        UserProfileDTO profile = userService.getProfile(userId);
        return Result.success(profile);
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Validated @RequestBody UpdateProfileDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        try {
            userService.updateProfile(userId, dto);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@Validated @RequestBody UpdatePasswordDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        try {
            userService.updatePassword(userId, dto);
            return Result.success("密码修改成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    public Result<UserStatsDTO> getUserStats() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        UserStatsDTO stats = userService.getUserStats(userId);
        return Result.success(stats);
    }

    /**
     * 注销账号
     */
    @DeleteMapping("/account")
    public Result<Void> deleteAccount(@Validated @RequestBody DeleteAccountDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        try {
            // 验证确认信息
            if (!"确认注销".equals(dto.getConfirm())) {
                return Result.error("请输入'确认注销'以确认操作");
            }
            userService.deleteAccount(userId, dto.getPassword());
            return Result.success("账号已注销", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新头像
     */
    @PutMapping("/avatar")
    public Result<Void> updateAvatar(@RequestBody AvatarDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        try {
            userService.updateAvatar(userId, dto.getAvatar());
            return Result.success("头像更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
