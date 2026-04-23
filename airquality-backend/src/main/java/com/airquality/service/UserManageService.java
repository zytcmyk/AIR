package com.airquality.service;

import com.airquality.dto.UserCreateDTO;
import com.airquality.dto.UserListDTO;
import com.airquality.dto.UserUpdateDTO;
import com.airquality.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 用户管理服务接口
 */
public interface UserManageService {

    /**
     * 分页查询用户列表
     */
    Page<User> getUserList(UserListDTO dto);

    /**
     * 获取用户详情
     */
    User getUserById(Long id);

    /**
     * 创建用户
     */
    void createUser(UserCreateDTO dto);

    /**
     * 更新用户
     */
    void updateUser(UserUpdateDTO dto);

    /**
     * 删除用户（逻辑删除）
     */
    void deleteUser(Long id);

    /**
     * 重置密码
     */
    void resetPassword(Long id);

    /**
     * 启用/禁用用户
     */
    void updateStatus(Long id, Integer status);
}
