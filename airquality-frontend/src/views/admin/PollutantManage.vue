<template>
  <div class="manage-page">
    <header class="page-header">
      <div class="page-title">
        <h1>污染物数据管理</h1>
        <p>管理城市污染物浓度数据</p>
      </div>
      <div class="page-actions">
        <el-input v-model="searchForm.city" placeholder="搜索城市" clearable class="search-input" @keyup.enter="handleSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="searchForm.pollutantType" placeholder="污染物类型" clearable class="type-select">
          <el-option label="PM2.5" value="PM2.5" />
          <el-option label="PM10" value="PM10" />
          <el-option label="SO2" value="SO2" />
          <el-option label="NO2" value="NO2" />
          <el-option label="O3" value="O3" />
          <el-option label="CO" value="CO" />
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
        <el-table-column prop="pollutantType" label="污染物类型" width="120">
          <template #default="{ row }">
            <span class="pollutant-tag" :class="getPollutantClass(row.pollutantType)">{{ row.pollutantType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="concentration" label="浓度值" width="120" sortable>
          <template #default="{ row }">
            <span class="concentration-value">{{ row.concentration }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="100" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑污染物数据' : '新增污染物数据'" width="480px" class="apple-dialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="污染物类型" prop="pollutantType">
          <el-select v-model="form.pollutantType" placeholder="请选择" style="width: 100%">
            <el-option label="PM2.5" value="PM2.5" />
            <el-option label="PM10" value="PM10" />
            <el-option label="SO2" value="SO2" />
            <el-option label="NO2" value="NO2" />
            <el-option label="O3" value="O3" />
            <el-option label="CO" value="CO" />
          </el-select>
        </el-form-item>
        <el-form-item label="浓度值" prop="concentration">
          <el-input-number v-model="form.concentration" :min="0" :precision="2" style="width: 100%" />
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
import { getPollutantList, createPollutant, updatePollutant, deletePollutant, batchDeletePollutant } from '@/api/admin'

const loading = ref(false)
const dataList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedIds = ref([])
const dateRange = ref([])

const searchForm = reactive({ city: '', pollutantType: '' })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const form = reactive({ id: null, city: '', pollutantType: '', concentration: 0, date: '' })

const rules = {
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  pollutantType: [{ required: true, message: '请选择污染物类型', trigger: 'change' }],
  concentration: [{ required: true, message: '请输入浓度值', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const getPollutantClass = (type) => {
  const map = { 'PM2.5': 'tag-pm25', 'PM10': 'tag-pm10', 'SO2': 'tag-so2', 'NO2': 'tag-no2', 'O3': 'tag-o3', 'CO': 'tag-co' }
  return map[type] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...searchForm, page: pagination.page, size: pagination.size }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    } else {
      // 默认查询数据中最新日期附近的数据（数据日期为2022年）
      params.startDate = '2022-03-01'
      params.endDate = '2022-03-31'
    }
    const res = await getPollutantList(params)
    if (res.code === 200) {
      dataList.value = res.data.list
      pagination.total = res.data.total
    }
  } catch (e) {
    console.error('加载污染物数据失败:', e)
    ElMessage.error('加载数据失败，请检查网络连接')
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.page = 1; loadData() }
const handleReset = () => { searchForm.city = ''; searchForm.pollutantType = ''; dateRange.value = []; handleSearch() }
const handleSelectionChange = (selection) => { selectedIds.value = selection.map(item => item.id) }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, city: '', pollutantType: '', concentration: 0, date: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, city: row.city, pollutantType: row.pollutantType, concentration: row.concentration, date: row.date })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) await updatePollutant(form.id, form)
    else await createPollutant(form)
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
    await deletePollutant(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 条记录？`, '提示', { type: 'warning' })
    await batchDeletePollutant(selectedIds.value)
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

.pollutant-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 8px;
  font-weight: 500;
}

.tag-pm25 { background: rgba(125, 190, 140, 0.15); color: #7DBE8C; }
.tag-pm10 { background: rgba(107, 163, 190, 0.15); color: #6BA3BE; }
.tag-so2 { background: rgba(142, 142, 147, 0.15); color: #8e8e93; }
.tag-no2 { background: rgba(212, 167, 106, 0.15); color: #D4A76A; }
.tag-o3 { background: rgba(176, 139, 212, 0.15); color: #B08BD4; }
.tag-co { background: rgba(212, 139, 139, 0.15); color: #D48B8B; }

.concentration-value {
  font-weight: 600;
  color: #2D2D2D;
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
