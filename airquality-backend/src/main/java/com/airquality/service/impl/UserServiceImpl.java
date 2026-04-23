package com.airquality.service.impl;

import com.airquality.dto.*;
import com.airquality.entity.User;
import com.airquality.mapper.UserMapper;
import com.airquality.service.UserService;
import com.airquality.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        User existUser = getByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 密码加密
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        // 邮箱和生日注册时不填写，可在个人中心完善
        user.setRole("user");
        user.setStatus(0);
        user.setDeleted(0);

        // 保存用户
        userMapper.insert(user);

        return user;
    }

    @Override
    public LoginResult login(LoginDTO loginDTO) {
        System.out.println("===== 登录调试 =====");
        System.out.println("输入用户名: " + loginDTO.getUsername());
        System.out.println("输入密码: " + loginDTO.getPassword());

        // 查询用户
        User user = getByUsername(loginDTO.getUsername());
        if (user == null) {
            System.out.println("用户不存在");
            throw new RuntimeException("用户名或密码错误");
        }

        System.out.println("数据库用户名: " + user.getUsername());
        System.out.println("数据库密码哈希: " + user.getPassword());
        System.out.println("用户状态: " + user.getStatus());

        // 检查用户状态
        if (user.getStatus() == 1) {
            throw new RuntimeException("账号已被禁用");
        }

        // 验证密码
        boolean passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        System.out.println("密码匹配结果: " + passwordMatch);

        if (!passwordMatch) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 转换为VO
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return new LoginResult(token, userVO);
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserProfileDTO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserProfileDTO profileDTO = new UserProfileDTO();
        BeanUtils.copyProperties(user, profileDTO);
        return profileDTO;
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户名是否被其他用户占用
        if (!user.getUsername().equals(dto.getUsername())) {
            User existUser = getByUsername(dto.getUsername());
            if (existUser != null) {
                throw new RuntimeException("用户名已被使用");
            }
        }

        // 更新信息
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setBirthday(dto.getBirthday());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        // 验证新密码和确认密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 新密码不能与原密码相同
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("新密码不能与原密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public UserStatsDTO getUserStats(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserStatsDTO statsDTO = new UserStatsDTO();

        // 计算账号天数
        if (user.getCreateTime() != null) {
            long days = ChronoUnit.DAYS.between(user.getCreateTime().toLocalDate(), LocalDate.now());
            statsDTO.setAccountDays((int) days);
        } else {
            statsDTO.setAccountDays(0);
        }

        // 以下为模拟数据，实际项目中可以从数据库统计
        statsDTO.setLoginCount(1);
        statsDTO.setQueryCount(0);
        statsDTO.setFavoriteCityCount(0);

        return statsDTO;
    }

    @Override
    public void deleteAccount(Long userId, String password) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 逻辑删除
        user.setDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setAvatar(avatarUrl);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}
