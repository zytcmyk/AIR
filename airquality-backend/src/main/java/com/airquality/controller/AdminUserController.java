package com.airquality.controller;

import com.airquality.annotation.AdminRequired;
import com.airquality.dto.UserCreateDTO;
import com.airquality.dto.UserListDTO;
import com.airquality.dto.UserUpdateDTO;
import com.airquality.entity.User;
import com.airquality.service.UserManageService;
import com.airquality.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理控制器（管理员）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/user")
@AdminRequired
public class AdminUserController {

    @Resource
    private UserManageService userManageService;

    /**
     * 分页查询用户列表
     * GET /api/admin/user/list
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(UserListDTO dto) {
        Page<User> page = userManageService.getUserList(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("page", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    /**
     * 获取用户详情
     * GET /api/admin/user/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userManageService.getUserById(id);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.success(user);
    }

    /**
     * 创建用户
     * POST /api/admin/user
     */
    @PostMapping
    public Result<Void> createUser(@RequestBody UserCreateDTO dto) {
        try {
            userManageService.createUser(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * PUT /api/admin/user/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        dto.setId(id);
        try {
            userManageService.updateUser(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 删除用户
     * DELETE /api/admin/user/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userManageService.deleteUser(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 重置密码
     * PUT /api/admin/user/{id}/reset-password
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        try {
            userManageService.resetPassword(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 启用/禁用用户
     * PUT /api/admin/user/{id}/status
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            userManageService.updateStatus(id, status);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
