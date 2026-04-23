<template>
  <div class="manage-page">
    <!-- 页面标题 -->
    <header class="page-header">
      <div class="page-title">
        <h1>内容管理</h1>
        <p>管理资讯文章内容</p>
      </div>
      <div class="page-actions">
        <el-input v-model="searchForm.title" placeholder="搜索标题" clearable class="search-input" @keyup.enter="handleSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="searchForm.type" placeholder="类型" clearable class="type-select">
          <el-option label="政策" value="政策" />
          <el-option label="预警" value="预警" />
          <el-option label="科普" value="科普" />
        </el-select>
        <el-select v-model="searchForm.status" placeholder="状态" clearable class="status-select">
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
        </el-select>
        <button class="apple-btn primary" @click="handleSearch">搜索</button>
        <button class="apple-btn" @click="handleReset">重置</button>
        <button class="apple-btn primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增文章
        </button>
      </div>
    </header>

    <!-- 数据表格 -->
    <div class="table-card">
      <el-table :data="dataList" v-loading="loading" class="apple-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <span class="type-tag" :class="getTypeClass(row.type)">{{ row.type }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <span class="status-tag" :class="row.status === 1 ? 'tag-published' : 'tag-draft'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="action-btn" @click="handleEdit(row)">编辑</button>
              <button class="action-btn" @click="handlePreview(row)">预览</button>
              <button class="action-btn" :class="row.status === 1 ? 'warning' : 'success'" @click="handlePublish(row)">
                {{ row.status === 1 ? '撤回' : '发布' }}
              </button>
              <button class="action-btn danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" class="apple-pagination" />
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文章' : '新增文章'" width="800px" top="5vh" class="apple-dialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 200px">
            <el-option label="政策" value="政策" />
            <el-option label="预警" value="预警" />
            <el-option label="科普" value="科普" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入文章内容（支持HTML格式）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="apple-btn" @click="dialogVisible = false">取消</button>
        <button class="apple-btn primary" @click="handleSubmit">确定</button>
      </template>
    </el-dialog>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" title="文章预览" width="700px" class="apple-dialog">
      <div class="preview-content">
        <h2 class="preview-title">{{ previewData.title }}</h2>
        <div class="preview-meta">
          <span class="type-tag" :class="getTypeClass(previewData.type)">{{ previewData.type }}</span>
          <span class="preview-date">{{ previewData.createTime }}</span>
        </div>
        <div class="preview-body" v-html="previewData.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getArticleList, createArticle, updateArticle, deleteArticle, publishArticle } from '@/api/admin'

const loading = ref(false)
const dataList = ref([])
const dialogVisible = ref(false)
const previewVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const previewData = ref({})

const searchForm = reactive({ title: '', type: '', status: null })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({ id: null, title: '', content: '', type: '', status: 0 })

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const getTypeClass = (type) => {
  const map = { '政策': 'tag-policy', '预警': 'tag-warning', '科普': 'tag-science' }
  return map[type] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getArticleList({ ...searchForm, page: pagination.page, size: pagination.size })
    if (res.code === 200) {
      dataList.value = res.data.list
      pagination.total = res.data.total
    }
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => {
  searchForm.title = ''
  searchForm.type = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, title: '', content: '', type: '', status: 0 })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, title: row.title, content: row.content, type: row.type, status: row.status })
  dialogVisible.value = true
}

const handlePreview = (row) => {
  previewData.value = row
  previewVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) await updateArticle(form.id, form)
    else await createArticle(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (e !== false) ElMessage.error('操作失败')
  }
}

const handlePublish = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await publishArticle(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '发布成功' : '已撤回')
    loadData()
  } catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该文章？', '提示', { type: 'warning' })
    await deleteArticle(row.id)
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
  flex-wrap: wrap;
}

.search-input { width: 180px; }
.type-select { width: 110px; }
.status-select { width: 110px; }

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

.type-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 500;
}

.tag-policy { background: rgba(107, 163, 190, 0.15); color: #6BA3BE; }
.tag-warning { background: rgba(212, 167, 106, 0.15); color: #D4A76A; }
.tag-science { background: rgba(125, 190, 140, 0.15); color: #7DBE8C; }

.status-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 500;
}

.tag-published { background: rgba(125, 190, 140, 0.15); color: #7DBE8C; }
.tag-draft { background: rgba(142, 142, 147, 0.15); color: #8e8e93; }

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

.action-btn.success { color: #7DBE8C; }
.action-btn.success:hover { background: rgba(125, 190, 140, 0.1); }

.action-btn.danger { color: #D48B8B; }
.action-btn.danger:hover { background: rgba(212, 139, 139, 0.1); }

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 预览样式 */
.preview-content {
  padding: 10px 0;
}

.preview-title {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 700;
  color: #2D2D2D;
  letter-spacing: -0.02em;
}

.preview-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.preview-date {
  font-size: 14px;
  color: #5A5A5A;
}

.preview-body {
  line-height: 1.8;
  color: #2D2D2D;
}

@media (max-width: 900px) {
  .page-header { flex-direction: column; }
  .page-actions { width: 100%; }
  .search-input { width: 100%; }
}
</style>
