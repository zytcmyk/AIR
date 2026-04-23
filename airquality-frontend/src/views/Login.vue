<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <p class="welcome-text">欢迎使用空气质量分析平台</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            size="large"
            class="login-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            class="login-input"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(form)
        userStore.login(res.data)
        ElMessage.success('登录成功')
        router.push('/home')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #E8F4F8 0%, #F5F0E8 50%, #F0E8F5 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 30%, rgba(107, 163, 190, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 70% 70%, rgba(176, 139, 212, 0.08) 0%, transparent 50%),
              radial-gradient(circle at 50% 50%, rgba(125, 190, 140, 0.05) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(2%, 2%) rotate(1deg); }
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: 48px 44px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 8px 40px rgba(107, 163, 190, 0.12), 0 2px 8px rgba(0, 0, 0, 0.04);
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-text {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #2D2D2D;
  letter-spacing: 1px;
}

.login-form {
  margin-top: 0;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.login-input :deep(.el-input__wrapper) {
  background: #FAFAF8 !important;
  border: 1.5px solid transparent !important;
  border-radius: 14px !important;
  box-shadow: none !important;
  padding: 4px 18px !important;
  height: 52px !important;
  transition: all 0.3s ease !important;
}

.login-input :deep(.el-input__wrapper:hover) {
  background: #F5F5F0 !important;
  border-color: rgba(107, 163, 190, 0.2) !important;
}

.login-input :deep(.el-input__wrapper.is-focus) {
  background: #fff !important;
  border-color: #6BA3BE !important;
  box-shadow: 0 0 0 4px rgba(107, 163, 190, 0.12) !important;
}

.login-input :deep(.el-input__inner) {
  color: #2D2D2D !important;
  font-size: 16px !important;
  height: 44px !important;
}

.login-input :deep(.el-input__inner::placeholder) {
  color: #8A8A8A !important;
}

.login-input :deep(.el-input__prefix) {
  color: #8A8A8A;
}

.login-btn {
  width: 100%;
  height: 54px;
  font-size: 17px;
  font-weight: 600;
  border-radius: 14px;
  background: linear-gradient(135deg, #6BA3BE 0%, #7DBE8C 100%);
  border: none;
  margin-top: 10px;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.login-btn:hover {
  background: linear-gradient(135deg, #5A93AE 0%, #6BAE7C 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(107, 163, 190, 0.35);
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  margin-top: 28px;
  font-size: 14px;
  color: #5A5A5A;
}

.login-footer a {
  color: #6BA3BE;
  margin-left: 4px;
  font-weight: 600;
  text-decoration: none;
  transition: color 0.2s;
}

.login-footer a:hover {
  color: #5A93AE;
}
</style>
