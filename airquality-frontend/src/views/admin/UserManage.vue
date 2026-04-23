<template>
  <div class="manage-page">
    <!-- 页面标题 -->
    <header class="page-header">
      <div class="page-title">
        <h1>用户管理</h1>
        <p>管理系统用户账号</p>
      </div>
      <div class="page-actions">
        <el-input
          v-model="searchForm.username"
          placeholder="搜索用户名"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="状态筛选" clearable class="status-select">
          <el-option label="正常" :value="0" />
          <el-option label="禁用" :value="1" />
        </el-select>
        <button class="apple-btn primary" @click="handleSearch">搜索</button>
        <button class="apple-btn" @click="handleReset">重置</button>
        <button class="apple-btn primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增用户
        </button>
      </div>
    </header>

    <!-- 数据表格 -->
    <div class="table-card">
      <el-table :data="userList" v-loading="loading" style="width: 100%" class="apple-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <img v-if="row.avatar" :src="row.avatar" class="user-avatar-img" alt="头像" />
              <div v-else class="user-avatar">{{ row.username?.charAt(0).toUpperCase() }}</div>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="birthday" label="生日" width="110" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <span class="role-tag" :class="row.role === 'admin' ? 'tag-admin' : 'tag-user'">
              {{ row.role === 'admin' ? '管理员' : '用户' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <span class="status-tag" :class="row.status === 0 ? 'tag-active' : 'tag-disabled'">
              {{ row.status === 0 ? '正常' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="action-btn" @click="handleEdit(row)">编辑</button>
              <button class="action-btn warning" @click="handleResetPwd(row)">重置密码</button>
              <button class="action-btn" :class="row.status === 0 ? 'danger' : 'success'" @click="handleToggleStatus(row)">
                {{ row.status === 0 ? '禁用' : '启用' }}
              </button>
              <button class="action-btn danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
          class="apple-pagination"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="480px" class="apple-dialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker v-model="form.birthday" type="date" placeholder="选择生日" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="apple-btn" @click="dialogVisible = false">取消</button>
        <button class="apple-btn primary" @click="handleSubmit">确定</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getUserList, createUser, updateUser, deleteUser, resetPassword, updateUserStatus } from '@/api/admin'

const loading = ref(false)
const userList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({ username: '', status: null })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({ id: null, username: '', password: '', email: '', birthday: '', role: 'user', status: 0 })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({ ...searchForm, page: pagination.page, size: pagination.size })
    if (res.code === 200) {
      userList.value = res.data.list
      pagination.total = res.data.total
    }
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => { searchForm.username = ''; searchForm.status = null; handleSearch() }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, username: '', password: '', email: '', birthday: '', role: 'user', status: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, username: row.username, email: row.email, birthday: row.birthday, role: row.role, status: row.status })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateUser(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createUser(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (e !== false) ElMessage.error('操作失败')
  }
}

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${row.username}" 的密码吗？`, '提示', { type: 'warning' })
    await resetPassword(row.id)
    ElMessage.success('密码已重置为 123456')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 0 ? 1 : 0
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '已禁用' : '已启用')
    loadData()
  } catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.manage-page {
  min-height: calc(100vh - 112px);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;
  background: linear-gradient(135deg, #E8F4F8 0%, #F5F0E8 50%, #F0E8F5 100%);
  padding: 24px;
  border-radius: 20px;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 20px;
  flex-wrap: wrap;
}

.page-title h1 {
  margin: 0 0 4px 0;
  font-size: 28px;
  font-weight: 700;
  color: #2D2D2D;
  letter-spacing: -0.02em;
}

.page-title p {
  margin: 0;
  font-size: 14px;
  color: #5A5A5A;
}

.page-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input { width: 180px; }
.status-select { width: 120px; }

/* 莫兰迪风格按钮 */
.apple-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 500;
  color: #2D2D2D;
  background: rgba(255, 255, 255, 0.9);
  border: 1.5px solid rgba(107, 163, 190, 0.2);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.apple-btn:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(107, 163, 190, 0.4);
}

.apple-btn.primary {
  color: #fff;
  background: linear-gradient(135deg, #6BA3BE 0%, #7DBE8C 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(107, 163, 190, 0.25);
}

.apple-btn.primary:hover {
  background: linear-gradient(135deg, #5A93AE 0%, #6BAE7C 100%);
  transform: translateY(-1px);
}

/* 表格卡片 */
.table-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(107, 163, 190, 0.08);
  backdrop-filter: blur(10px);
}

/* 用户单元格 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #6BA3BE 0%, #7DBE8C 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
}

.user-avatar-img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

/* 标签样式 */
.role-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 500;
}

.tag-admin { background: rgba(176, 139, 212, 0.15); color: #B08BD4; }
.tag-user { background: rgba(107, 163, 190, 0.15); color: #6BA3BE; }

.status-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 500;
}

.tag-active { background: rgba(125, 190, 140, 0.15); color: #7DBE8C; }
.tag-disabled { background: rgba(212, 139, 139, 0.15); color: #D48B8B; }

/* 操作按钮 */
.action-btns {
  display: flex;
  gap: 6px;
}

.action-btn {
  padding: 6px 12px;
  font-size: 13px;
  color: #6BA3BE;
  background: transparent;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s;
  font-weight: 500;
}

.action-btn:hover {
  background: rgba(107, 163, 190, 0.1);
}

.action-btn.warning { color: #D4A76A; }
.action-btn.warning:hover { background: rgba(212, 167, 106, 0.1); }

.action-btn.danger { color: #D48B8B; }
.action-btn.danger:hover { background: rgba(212, 139, 139, 0.1); }

.action-btn.success { color: #7DBE8C; }
.action-btn.success:hover { background: rgba(125, 190, 140, 0.1); }

/* 分页 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式 */
@media (max-width: 900px) {
  .page-header { flex-direction: column; }
  .page-actions { width: 100%; flex-wrap: wrap; }
  .search-input, .status-select { width: 100%; }
}
</style>
