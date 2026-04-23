package com.airquality.service.impl;

import com.airquality.dto.UserCreateDTO;
import com.airquality.dto.UserListDTO;
import com.airquality.dto.UserUpdateDTO;
import com.airquality.entity.User;
import com.airquality.mapper.UserMapper;
import com.airquality.service.UserManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.MessageDigest;

/**
 * 用户管理服务实现类
 */
@Service
public class UserManageServiceImpl implements UserManageService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<User> getUserList(UserListDTO dto) {
        Page<User> page = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            wrapper.like("username", dto.getUsername());
        }
        if (dto.getStatus() != null) {
            wrapper.eq("status", dto.getStatus());
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");

        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void createUser(UserCreateDTO dto) {
        // 检查用户名是否存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", dto.getUsername());
        wrapper.eq("deleted", 0);
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(md5(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setBirthday(dto.getBirthday());
        user.setRole(dto.getRole() != null ? dto.getRole() : "user");
        user.setStatus(0);
        user.setDeleted(0);

        userMapper.insert(user);
    }

    @Override
    public void updateUser(UserUpdateDTO dto) {
        User user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getBirthday() != null) {
            user.setBirthday(dto.getBirthday());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setDeleted(1);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setPassword(md5("123456"));
        userMapper.updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);
    }

    /**
     * MD5加密
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}
