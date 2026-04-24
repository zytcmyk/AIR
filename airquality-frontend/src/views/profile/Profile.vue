<template>
  <div class="profile-page">
    <!-- 页面标题 -->
    <header class="page-header">
      <div class="page-title">
        <h1>个人中心</h1>
        <p>管理您的个人信息和账号设置</p>
      </div>
    </header>

    <div class="page-body">
      <!-- 左侧：用户卡片 -->
      <aside class="user-card">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="handleAvatarClick">
            <img v-if="hasAvatar" :src="userInfo?.avatar" alt="头像" class="avatar-img" />
            <div v-else class="avatar">
              {{ userInfo?.username?.charAt(0).toUpperCase() }}
            </div>
            <div class="avatar-overlay">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/>
                <circle cx="12" cy="13" r="4"/>
              </svg>
            </div>
            <div v-if="avatarLoading" class="avatar-loading">
              <div class="loading-spinner"></div>
            </div>
          </div>
          <!-- 头像操作菜单 -->
          <transition name="fade">
            <div v-if="showAvatarMenu" class="avatar-menu">
              <div class="menu-item" @click="handleViewAvatar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                查看头像
              </div>
              <div class="menu-item" @click="handleChangeAvatar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/>
                  <circle cx="12" cy="13" r="4"/>
                </svg>
                修改头像
              </div>
            </div>
          </transition>
          <!-- 点击遮罩关闭菜单 -->
          <div v-if="showAvatarMenu" class="menu-overlay" @click="showAvatarMenu = false"></div>
          <h3 class="username">{{ userInfo?.username }}</h3>
          <span class="role-tag" :class="userInfo?.role === 'admin' ? 'tag-admin' : 'tag-user'">
            {{ userInfo?.role === 'admin' ? '管理员' : '普通用户' }}
          </span>
        </div>

        <!-- 隐藏的文件输入 -->
        <input type="file" ref="fileInputRef" accept="image/*" style="display: none" @change="handleFileChange" />

        <!-- 统计数据 -->
        <div class="stats-section">
          <div class="stat-item">
            <span class="stat-value">{{ stats.accountDays }}</span>
            <span class="stat-label">账号天数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.queryCount }}</span>
            <span class="stat-label">查询次数</span>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <button class="action-btn" @click="activeTab = 'profile'">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
            编辑资料
          </button>
          <button class="action-btn warning" @click="activeTab = 'password'">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
              <path d="M7 11V7a5 5 0 0110 0v4"/>
            </svg>
            修改密码
          </button>
        </div>
      </aside>

      <!-- 右侧：设置面板 -->
      <main class="settings-panel">
        <div class="panel-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            class="tab-btn"
            :class="{ active: activeTab === tab.value }"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- 基本信息 -->
        <div v-show="activeTab === 'profile'" class="tab-content">
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" class="settings-form">
            <el-form-item label="用户ID">
              <el-input :value="userInfo?.id" disabled />
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="生日" prop="birthday">
              <el-date-picker v-model="profileForm.birthday" type="date" placeholder="选择生日" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
            <el-form-item label="注册时间">
              <el-input :value="formatDateTime(userInfo?.createTime)" disabled />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="loading">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 修改密码 -->
        <div v-show="activeTab === 'password'" class="tab-content">
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" class="settings-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码（6-20位）" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword" :loading="loading">修改密码</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 账号安全 -->
        <div v-show="activeTab === 'security'" class="tab-content">
          <div class="security-warning">
            <div class="warning-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 9v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
            <div class="warning-content">
              <h4>账号注销警告</h4>
              <p>注销账号后，您的所有数据将被永久删除且无法恢复。请谨慎操作。</p>
            </div>
          </div>

          <el-form ref="deleteFormRef" :model="deleteForm" :rules="deleteRules" label-width="100px" class="settings-form">
            <el-form-item label="密码验证" prop="password">
              <el-input v-model="deleteForm.password" type="password" placeholder="请输入密码验证身份" show-password />
            </el-form-item>
            <el-form-item label="确认注销" prop="confirm">
              <el-input v-model="deleteForm.confirm" placeholder="请输入'确认注销'" />
            </el-form-item>
            <el-form-item>
              <el-button type="danger" @click="handleDeleteAccount" :loading="loading">注销账号</el-button>
            </el-form-item>
          </el-form>
        </div>
      </main>
    </div>

    <!-- 头像预览弹窗 -->
    <el-dialog v-model="showAvatarPreview" title="我的头像" width="400px" center>
      <div class="avatar-preview-container">
        <img v-if="userInfo?.avatar" :src="userInfo.avatar" alt="头像" class="avatar-preview-img" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getProfile, updateProfile, updatePassword, getUserStats, deleteAccount, updateAvatar, uploadAvatar } from '@/api/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const tabs = [
  { label: '基本信息', value: 'profile' },
  { label: '修改密码', value: 'password' },
  { label: '账号安全', value: 'security' }
]

const activeTab = ref('profile')
const loading = ref(false)
const userInfo = ref({})
const stats = ref({ accountDays: 0, loginCount: 0, queryCount: 0 })

// 头像相关
const showAvatarMenu = ref(false)
const showAvatarPreview = ref(false)
const fileInputRef = ref(null)
const avatarLoading = ref(false)

// 计算是否有头像
const hasAvatar = computed(() => userInfo.value?.avatar && userInfo.value.avatar.trim() !== '')

// 个人信息表单
const profileFormRef = ref(null)
const profileForm = reactive({ username: '', email: '', birthday: '' })
const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2-20个字符', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }]
}

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) callback(new Error('两次输入的密码不一致'))
  else callback()
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 注销表单
const deleteFormRef = ref(null)
const deleteForm = reactive({ password: '', confirm: '' })
const deleteRules = {
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirm: [{ required: true, message: '请输入确认信息', trigger: 'blur' }]
}

const formatDateTime = (datetime) => datetime ? dayjs(datetime).format('YYYY-MM-DD HH:mm:ss') : '-'

const fetchUserInfo = async () => {
  try {
    const res = await getProfile()
    console.log('获取用户信息:', res)
    if (res.code === 200) {
      userInfo.value = res.data
      console.log('头像URL:', res.data.avatar)
      profileForm.username = res.data.username
      profileForm.email = res.data.email
      profileForm.birthday = res.data.birthday
    }
  } catch (e) { console.error(e) }
}

const fetchStats = async () => {
  try {
    const res = await getUserStats()
    if (res.code === 200) stats.value = res.data
  } catch (e) { console.error(e) }
}

const handleUpdateProfile = async () => {
  const valid = await profileFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await updateProfile(profileForm)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      userStore.setUserInfo({ ...userStore.userInfo, ...profileForm })
      await fetchUserInfo()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch { ElMessage.error('更新失败') }
  finally { loading.value = false }
}

const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await updatePassword(passwordForm)
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch { ElMessage.error('修改失败') }
  finally { loading.value = false }
}

const resetPasswordForm = () => passwordFormRef.value?.resetFields()

const handleDeleteAccount = async () => {
  const valid = await deleteFormRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    await ElMessageBox.confirm('您确定要注销账号吗？此操作不可逆，所有数据将被永久删除！', '危险操作', {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'warning'
    })
    loading.value = true
    const res = await deleteAccount(deleteForm)
    if (res.code === 200) {
      ElMessage.success('账号已注销')
      userStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注销失败')
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('注销失败')
  } finally { loading.value = false }
}

onMounted(() => {
  fetchUserInfo()
  fetchStats()
})

// 头像点击处理
const handleAvatarClick = () => {
  showAvatarMenu.value = true
}

// 查看头像
const handleViewAvatar = () => {
  showAvatarMenu.value = false
  showAvatarPreview.value = true
}

// 修改头像
const handleChangeAvatar = () => {
  showAvatarMenu.value = false
  fileInputRef.value?.click()
}

// 文件选择处理
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }

  // 验证文件大小（5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }

  avatarLoading.value = true
  try {
    // 上传文件到OSS
    const uploadRes = await uploadAvatar(file)
    if (uploadRes.code === 200) {
      // 更新头像URL
      const updateRes = await updateAvatar({ avatar: uploadRes.data })
      if (updateRes.code === 200) {
        ElMessage.success('头像更新成功')
        await fetchUserInfo()
        // 更新store中的用户信息
        userStore.setUserInfo({ ...userStore.userInfo, avatar: uploadRes.data })
      }
    }
  } catch (e) {
    ElMessage.error('头像上传失败')
  } finally {
    avatarLoading.value = false
    // 清空input
    event.target.value = ''
  }
}

</script>

<style scoped>
.profile-page {
  padding: 24px;
  min-height: calc(100vh - 56px);
  background: #F2F2F7;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', Arial, sans-serif;
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.page-title h1 {
  margin: 0 0 6px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.5px;
}

.page-title p {
  margin: 0;
  font-size: 14px;
  color: #86868b;
  font-weight: 400;
}

/* 主体布局 */
.page-body {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

/* 用户卡片 */
.user-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.avatar-section {
  text-align: center;
  padding-bottom: 24px;
  border-bottom: 1px solid #e5e5ea;
  position: relative;
}

.avatar-wrapper {
  position: relative;
  width: 88px;
  height: 88px;
  margin: 0 auto 16px;
  cursor: pointer;
  border-radius: 50%;
  overflow: visible;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  border: 3px solid #6BA3BE;
  box-shadow: 0 4px 12px rgba(107, 163, 190, 0.25);
}

.avatar {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #6BA3BE, #8BBDD4);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 600;
  color: #fff;
  border: 3px solid #6BA3BE;
  box-shadow: 0 4px 12px rgba(107, 163, 190, 0.25);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.avatar-overlay svg {
  width: 28px;
  height: 28px;
  color: #fff;
}

.avatar-loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.username {
  margin: 0 0 12px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
}

.role-tag {
  display: inline-block;
  font-size: 12px;
  font-weight: 500;
  padding: 6px 14px;
  border-radius: 20px;
}

.tag-admin {
  background: rgba(229, 168, 123, 0.12);
  color: #D4A76A;
}

.tag-user {
  background: rgba(107, 163, 190, 0.12);
  color: #6BA3BE;
}

/* 统计 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 24px 0;
  border-bottom: 1px solid #e5e5ea;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: #F9F9FB;
  border-radius: 12px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #6BA3BE;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #86868b;
  margin-top: 4px;
  font-weight: 500;
}

/* 快捷操作 */
.quick-actions {
  padding-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 20px;
  font-size: 14px;
  font-weight: 500;
  color: #6BA3BE;
  background: #F9F9FB;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn svg {
  width: 18px;
  height: 18px;
}

.action-btn:hover {
  background: #E8E8ED;
}

.action-btn:active {
  transform: scale(0.98);
}

.action-btn.warning {
  color: #D48B8B;
}

.action-btn.warning:hover {
  background: rgba(212, 139, 139, 0.1);
}

/* 设置面板 */
.settings-panel {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.panel-tabs {
  display: flex;
  gap: 8px;
  padding: 16px 20px;
  background: #ffffff;
  border-bottom: 1px solid #e5e5ea;
}

.tab-btn {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  color: #86868b;
  background: transparent;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn:hover {
  color: #1d1d1f;
  background: #F9F9FB;
}

.tab-btn.active {
  background: #6BA3BE;
  color: #ffffff;
}

.tab-content {
  padding: 28px;
}

.settings-form {
  max-width: 520px;
}

/* Apple风格表单覆盖 */
.settings-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.settings-form :deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  padding-right: 16px;
}

.settings-form :deep(.el-input__wrapper) {
  background: #F9F9FB;
  border: 1px solid #e5e5ea;
  border-radius: 10px;
  box-shadow: none;
  padding: 4px 14px;
  transition: all 0.2s ease;
}

.settings-form :deep(.el-input__wrapper:hover) {
  border-color: #c7c7cc;
}

.settings-form :deep(.el-input__wrapper.is-focus) {
  border-color: #6BA3BE;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(107, 163, 190, 0.1);
}

.settings-form :deep(.el-input__inner) {
  font-size: 15px;
  color: #1d1d1f;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', Arial, sans-serif;
}

.settings-form :deep(.el-input__inner::placeholder) {
  color: #c7c7cc;
}

.settings-form :deep(.el-input.is-disabled .el-input__wrapper) {
  background: #F2F2F7;
  border-color: #e5e5ea;
}

.settings-form :deep(.el-input.is-disabled .el-input__inner) {
  color: #86868b;
}

/* Apple风格按钮 */
.settings-form :deep(.el-button--primary) {
  background: #6BA3BE;
  border: none;
  border-radius: 10px;
  padding: 12px 28px;
  font-size: 15px;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', Arial, sans-serif;
  box-shadow: 0 2px 8px rgba(107, 163, 190, 0.25);
  transition: all 0.2s ease;
}

.settings-form :deep(.el-button--primary:hover) {
  background: #5A93AE;
}

.settings-form :deep(.el-button--primary:active) {
  transform: scale(0.98);
}

.settings-form :deep(.el-button--danger) {
  background: #D48B8B;
  border: none;
  border-radius: 10px;
  padding: 12px 28px;
  font-size: 15px;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', Arial, sans-serif;
  box-shadow: 0 2px 8px rgba(212, 139, 139, 0.25);
  transition: all 0.2s ease;
}

.settings-form :deep(.el-button--danger:hover) {
  background: #C47B7B;
}

.settings-form :deep(.el-button--default) {
  background: #F9F9FB;
  border: none;
  border-radius: 10px;
  padding: 12px 28px;
  font-size: 15px;
  font-weight: 500;
  color: #1d1d1f;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', Arial, sans-serif;
  transition: all 0.2s ease;
}

.settings-form :deep(.el-button--default:hover) {
  background: #E8E8ED;
}

/* 日期选择器 */
.settings-form :deep(.el-date-editor) {
  --el-date-editor-width: 100%;
}

.settings-form :deep(.el-date-editor .el-input__wrapper) {
  background: #F9F9FB;
  border: 1px solid #e5e5ea;
  border-radius: 10px;
}

/* 安全警告 */
.security-warning {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: rgba(212, 139, 139, 0.06);
  border: 1px solid rgba(212, 139, 139, 0.15);
  border-radius: 12px;
  margin-bottom: 28px;
}

.warning-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(212, 139, 139, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.warning-icon svg {
  width: 22px;
  height: 22px;
  color: #D48B8B;
}

.warning-content h4 {
  margin: 0 0 6px 0;
  font-size: 15px;
  font-weight: 600;
  color: #D48B8B;
}

.warning-content p {
  margin: 0;
  font-size: 13px;
  color: #86868b;
  line-height: 1.5;
}

/* 响应式 */
@media (max-width: 900px) {
  .page-body {
    grid-template-columns: 1fr;
  }
}

/* 头像菜单 */
.avatar-menu {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
  z-index: 100;
  min-width: 120px;
  margin-top: 8px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  font-size: 14px;
  color: #1d1d1f;
  cursor: pointer;
  transition: background 0.2s ease;
}

.menu-item svg {
  width: 18px;
  height: 18px;
}

.menu-item:hover {
  background: #F9F9FB;
}

.menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 99;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-8px);
}

/* 头像预览 */
.avatar-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.avatar-preview-img {
  max-width: 300px;
  max-height: 300px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

</style>