package com.airquality.service;

import com.airquality.dto.*;
import com.airquality.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    User register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return Token和用户信息
     */
    LoginResult login(LoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户视图对象
     */
    UserVO getUserById(Long id);

    /**
     * 获取用户个人信息
     *
     * @param userId 用户ID
     * @return 个人信息
     */
    UserProfileDTO getProfile(Long userId);

    /**
     * 更新个人信息
     *
     * @param userId 用户ID
     * @param dto 更新信息
     */
    void updateProfile(Long userId, UpdateProfileDTO dto);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto 密码信息
     */
    void updatePassword(Long userId, UpdatePasswordDTO dto);

    /**
     * 获取用户统计数据
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    UserStatsDTO getUserStats(Long userId);

    /**
     * 注销账号
     *
     * @param userId 用户ID
     * @param password 密码验证
     */
    void deleteAccount(Long userId, String password);

    /**
     * 更新头像
     *
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     */
    void updateAvatar(Long userId, String avatarUrl);

    /**
     * 登录结果内部类
     */
    class LoginResult {
        private String token;
        private UserVO user;

        public LoginResult(String token, UserVO user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserVO getUser() {
            return user;
        }

        public void setUser(UserVO user) {
            this.user = user;
        }
    }
}
