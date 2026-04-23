<template>
  <div class="manage-page">
    <header class="page-header">
      <div class="page-title">
        <h1>污染源数据管理</h1>
        <p>管理城市污染源贡献率数据</p>
      </div>
      <div class="page-actions">
        <el-input v-model="searchForm.city" placeholder="搜索城市" clearable class="search-input" @keyup.enter="handleSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="searchForm.sourceType" placeholder="污染源类型" clearable class="type-select">
          <el-option label="工业排放" value="工业排放" />
          <el-option label="机动车尾气" value="机动车尾气" />
          <el-option label="扬尘" value="扬尘" />
          <el-option label="燃煤" value="燃煤" />
          <el-option label="其他" value="其他" />
        </el-select>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="—" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" class="date-picker" />
        <button class="apple-btn primary" @click="handleSearch">搜索</button>
        <button class="apple-btn" @click="handleReset">重置</button>
        <button class="apple-btn primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </button>
        <button class="apple-btn" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </button>
      </div>
    </header>

    <div class="table-card">
      <el-table :data="dataList" v-loading="loading" @selection-change="handleSelectionChange" class="apple-table">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="sourceType" label="污染源类型" width="120">
          <template #default="{ row }">
            <span class="source-tag" :class="getSourceClass(row.sourceType)">{{ row.sourceType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="contributionRate" label="贡献率" width="160" sortable>
          <template #default="{ row }">
            <div class="contribution-cell">
              <div class="contribution-bar">
                <div class="bar-fill" :style="{ width: row.contributionRate + '%' }"></div>
              </div>
              <span class="contribution-value">{{ row.contributionRate }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="110" />
        <el-table-column label="操作" fixed="right" width="140">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="action-btn" @click="handleEdit(row)">编辑</button>
              <button class="action-btn danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" class="apple-pagination" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑污染源数据' : '新增污染源数据'" width="480px" class="apple-dialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="污染源类型" prop="sourceType">
          <el-select v-model="form.sourceType" placeholder="请选择" style="width: 100%">
            <el-option label="工业排放" value="工业排放" />
            <el-option label="机动车尾气" value="机动车尾气" />
            <el-option label="扬尘" value="扬尘" />
            <el-option label="燃煤" value="燃煤" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="贡献率(%)" prop="contributionRate">
          <el-input-number v-model="form.contributionRate" :min="0" :max="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="form.date" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
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
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import { getSourceList, createSource, updateSource, deleteSource, batchDeleteSource } from '@/api/admin'

const loading = ref(false)
const dataList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedIds = ref([])
const dateRange = ref([])

const searchForm = reactive({ city: '', sourceType: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({ id: null, city: '', sourceType: '', contributionRate: 0, date: '' })

const rules = {
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  sourceType: [{ required: true, message: '请选择污染源类型', trigger: 'change' }],
  contributionRate: [{ required: true, message: '请输入贡献率', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const getSourceClass = (type) => {
  const map = { '工业排放': 'tag-industrial', '机动车尾气': 'tag-vehicle', '扬尘': 'tag-dust', '燃煤': 'tag-coal', '其他': 'tag-other' }
  return map[type] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, page: pagination.page, size: pagination.size }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getSourceList(params)
    if (res.code === 200) {
      dataList.value = res.data.list
      pagination.total = res.data.total
    }
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => { searchForm.city = ''; searchForm.sourceType = ''; dateRange.value = []; handleSearch() }
const handleSelectionChange = (selection) => { selectedIds.value = selection.map(item => item.id) }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, city: '', sourceType: '', contributionRate: 0, date: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, city: row.city, sourceType: row.sourceType, contributionRate: row.contributionRate, date: row.date })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) await updateSource(form.id, form)
    else await createSource(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (e !== false) ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该记录？', '提示', { type: 'warning' })
    await deleteSource(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 条记录？`, '提示', { type: 'warning' })
    await batchDeleteSource(selectedIds.value)
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

.search-input { width: 160px; }
.type-select { width: 130px; }
.date-picker { width: 260px; }

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

.apple-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.source-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 500;
}

.tag-industrial { background: rgba(125, 190, 140, 0.15); color: #7DBE8C; }
.tag-vehicle { background: rgba(107, 163, 190, 0.15); color: #6BA3BE; }
.tag-dust { background: rgba(142, 142, 147, 0.15); color: #8e8e93; }
.tag-coal { background: rgba(212, 167, 106, 0.15); color: #D4A76A; }
.tag-other { background: rgba(176, 139, 212, 0.15); color: #B08BD4; }

.contribution-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.contribution-bar {
  flex: 1;
  height: 8px;
  background: rgba(107, 163, 190, 0.1);
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #6BA3BE, #7DBE8C);
  border-radius: 4px;
  transition: width 0.3s;
}

.contribution-value {
  font-size: 13px;
  font-weight: 600;
  color: #2D2D2D;
  min-width: 50px;
  text-align: right;
}

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

.action-btn.danger { color: #D48B8B; }
.action-btn.danger:hover { background: rgba(212, 139, 139, 0.1); }

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 900px) {
  .page-header { flex-direction: column; }
  .page-actions { width: 100%; }
  .search-input, .type-select, .date-picker { width: 100%; }
}
</style>
