<template>
  <div class="aqi-analysis-page">
    <!-- 顶部导航栏 -->
    <header class="page-header">
      <div class="header-left">
        <button class="back-btn" @click="goHome">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>返回首页</span>
        </button>
        <div class="page-title">
          <h1>AQI 空气质量分析</h1>
          <p>实时监测 · 趋势分析 · 数据导出</p>
        </div>
      </div>
      <div class="header-right">
        <div class="current-time">
          <span class="date">{{ currentDate }}</span>
          <span class="time">{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <!-- 搜索筛选区域 -->
    <section class="search-section">
      <div class="search-card">
        <div class="search-row">
          <div class="search-item">
            <label>城市名称</label>
            <el-select
              v-model="searchParams.city"
              placeholder="选择城市"
              filterable
              clearable
              class="search-select"
            >
              <el-option
                v-for="city in cityList"
                :key="city"
                :label="city"
                :value="city"
              />
            </el-select>
          </div>
          <div class="search-item">
            <label>开始日期</label>
            <el-date-picker
              v-model="searchParams.startDate"
              type="date"
              placeholder="选择开始日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="search-date"
            />
          </div>
          <div class="search-item">
            <label>结束日期</label>
            <el-date-picker
              v-model="searchParams.endDate"
              type="date"
              placeholder="选择结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="search-date"
            />
          </div>
          <div class="search-item">
            <label>空气质量等级</label>
            <el-select
              v-model="searchParams.qualityLevel"
              placeholder="全部等级"
              clearable
              class="search-select"
            >
              <el-option label="优" value="优" />
              <el-option label="良" value="良" />
              <el-option label="轻度污染" value="轻度污染" />
              <el-option label="中度污染" value="中度污染" />
              <el-option label="重度污染" value="重度污染" />
              <el-option label="严重污染" value="严重污染" />
            </el-select>
          </div>
          <div class="search-actions">
            <el-button type="primary" @click="handleSearch" :loading="searchLoading">
              <svg viewBox="0 0 24 24" fill="none" class="btn-icon">
                <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
                <path d="M21 21l-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              查询
            </el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 主体内容 -->
    <main class="page-body">
      <!-- 第一行：统计卡片 + 预警 -->
      <section class="stats-row">
        <div class="stats-cards">
          <div class="stat-card" v-for="(stat, index) in statsCards" :key="index">
            <div class="stat-icon" :style="{ background: stat.gradient }">
              <component :is="stat.icon" />
            </div>
            <div class="stat-content">
              <span class="stat-label">{{ stat.label }}</span>
              <span class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</span>
              <span class="stat-unit">{{ stat.unit }}</span>
            </div>
          </div>
        </div>
        <!-- 预警通知卡片 -->
        <div class="alert-card">
          <div class="alert-header">
            <svg viewBox="0 0 24 24" fill="none" class="alert-icon">
              <path d="M12 9v4M12 17h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>空气质量预警</span>
            <span class="alert-count" v-if="alertList.length > 0">{{ alertList.length }}</span>
          </div>
          <div class="alert-list" v-if="alertList.length > 0">
            <div
              v-for="(alert, index) in alertList"
              :key="index"
              class="alert-item"
              :class="alert.level"
            >
              <span class="alert-city">{{ alert.city }}</span>
              <span class="alert-message">{{ alert.message }}</span>
              <span class="alert-time">{{ alert.time }}</span>
            </div>
          </div>
          <div class="alert-empty" v-else>
            <svg viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <path d="M8 12l3 3 5-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>当前无预警信息</span>
          </div>
        </div>
      </section>

      <!-- 第二行：趋势图 + 饼状图 -->
      <section class="charts-row">
        <!-- AQI趋势图 -->
        <div class="chart-card trend-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M3 3v18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <path d="M7 16l4-4 4 4 5-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              AQI 趋势分析
            </span>
            <div class="card-controls">
              <el-radio-group v-model="trendDays" size="small" @change="loadTrendData">
                <el-radio-button :value="7">7天</el-radio-button>
                <el-radio-button :value="30">30天</el-radio-button>
                <el-radio-button :value="90">90天</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="card-body">
            <div ref="trendChartRef" class="chart-container"></div>
          </div>
        </div>

        <!-- 空气质量等级饼状图 -->
        <div class="chart-card pie-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M12 2a10 10 0 0110 10" stroke="currentColor" stroke-width="2"/>
              </svg>
              空气质量等级分布
            </span>
            <span class="card-badge">{{ searchParams.city || '全部城市' }}</span>
          </div>
          <div class="card-body">
            <div ref="pieChartRef" class="chart-container"></div>
          </div>
        </div>
      </section>

      <!-- 第三行：排名 + 多城市对比 -->
      <section class="charts-row">
        <!-- AQI排名 -->
        <div class="chart-card rank-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M8 21h8M12 17v4M7 4h10l-1 9H8L7 4z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="14" r="2" stroke="currentColor" stroke-width="2"/>
              </svg>
              城市 AQI 排名
            </span>
            <div class="card-controls">
              <el-select v-model="rankLimit" size="small" @change="loadRankingData" style="width: 100px;">
                <el-option :value="10" label="Top 10" />
                <el-option :value="20" label="Top 20" />
                <el-option :value="50" label="Top 50" />
              </el-select>
            </div>
          </div>
          <div class="card-body">
            <div ref="rankChartRef" class="chart-container"></div>
          </div>
        </div>

        <!-- 多城市对比 -->
        <div class="chart-card compare-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M3 3v18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                <rect x="7" y="10" width="3" height="8" rx="1" stroke="currentColor" stroke-width="2"/>
                <rect x="14" y="6" width="3" height="12" rx="1" stroke="currentColor" stroke-width="2"/>
              </svg>
              多城市 AQI 对比
            </span>
            <div class="card-controls">
              <el-select
                v-model="compareCities"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="选择城市"
                size="small"
                class="compare-select"
                @change="loadCompareData"
              >
                <el-option v-for="city in cityList" :key="city" :label="city" :value="city" />
              </el-select>
            </div>
          </div>
          <div class="card-body">
            <div ref="compareChartRef" class="chart-container"></div>
          </div>
        </div>
      </section>

      <!-- 第四行：同比分析 + 数据表格 -->
      <section class="charts-row">
        <!-- 历史同比分析 -->
        <div class="chart-card year-compare-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
                <path d="M16 2v4M8 2v4M3 10h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              历史同比分析
            </span>
            <div class="card-controls">
              <el-select v-model="compareYear" size="small" @change="loadYearCompareData" style="width: 120px;">
                <el-option :value="2022" label="2022年" />
                <el-option :value="2021" label="2021年" />
                <el-option :value="2020" label="2020年" />
              </el-select>
            </div>
          </div>
          <div class="card-body">
            <div ref="yearCompareChartRef" class="chart-container"></div>
          </div>
        </div>

        <!-- 数据表格 -->
        <div class="chart-card table-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
                <path d="M3 9h18M3 15h18M9 3v18" stroke="currentColor" stroke-width="2"/>
              </svg>
              数据明细
            </span>
            <div class="card-controls">
              <el-button type="primary" size="small" @click="exportExcel" :loading="exportLoading">
                <svg viewBox="0 0 24 24" fill="none" class="btn-icon-sm">
                  <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                导出Excel
              </el-button>
              <el-button size="small" @click="exportCsv" :loading="exportLoading">
                <svg viewBox="0 0 24 24" fill="none" class="btn-icon-sm">
                  <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" stroke="currentColor" stroke-width="2"/>
                  <path d="M14 2v6h6M9 15l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                导出CSV
              </el-button>
            </div>
          </div>
          <div class="card-body table-body">
            <el-table
              :data="tableData"
              stripe
              height="100%"
              v-loading="tableLoading"
              @sort-change="handleSortChange"
            >
              <el-table-column prop="city" label="城市" width="100" fixed />
              <el-table-column prop="date" label="日期" width="120" sortable />
              <el-table-column prop="aqiValue" label="AQI" width="80" sortable>
                <template #default="{ row }">
                  <span class="aqi-value" :style="{ color: getAqiColor(row.aqiValue) }">
                    {{ row.aqiValue }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="qualityLevel" label="质量等级" width="100">
                <template #default="{ row }">
                  <span class="quality-tag" :class="getQualityClass(row.qualityLevel)">
                    {{ row.qualityLevel }}
                  </span>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination-wrapper">
              <el-pagination
                v-model:current-page="pagination.page"
                v-model:page-size="pagination.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="pagination.total"
                layout="total, sizes, prev, pager, next"
                @size-change="handlePageSizeChange"
                @current-change="handlePageChange"
              />
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, shallowRef, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { TrendCharts, DataAnalysis, Warning, Document } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  searchAqi,
  getRanking,
  getTrend,
  getPieData,
  getMultiCityCompare,
  exportAqi,
  getAllCities
} from '@/api/aqi'

const router = useRouter()

// 明亮莫兰迪风格配色
const MORANDI_COLORS = {
  primary: '#6BA3BE',
  success: '#7DBE8C',
  warning: '#E5C07B',
  danger: '#E5A87B',
  purple: '#B08BD4',
  pink: '#E5A5A5',
  teal: '#6BA3BE',
  orange: '#E5A87B',
  gray: '#5A5A5A',
  lightGray: '#8A8A8A',
  text: '#2D2D2D',
  textSecondary: '#5A5A5A',
  textMuted: '#8A8A8A',
  bg: '#FAFAF8',
  cardBg: '#FFFFFF',
  border: 'rgba(0, 0, 0, 0.08)',
  separator: 'rgba(0, 0, 0, 0.06)'
}

// AQI等级颜色
const AQI_COLORS = {
  level1: '#7DBE8C',
  level2: '#6BA3BE',
  level3: '#E5C07B',
  level4: '#E5A87B',
  level5: '#E5A5A5',
  level6: '#B08BD4'
}

// 获取AQI等级
const getAqiLevel = (value) => {
  if (value <= 50) return { name: '优', color: AQI_COLORS.level1 }
  if (value <= 100) return { name: '良', color: AQI_COLORS.level2 }
  if (value <= 150) return { name: '轻度污染', color: AQI_COLORS.level3 }
  if (value <= 200) return { name: '中度污染', color: AQI_COLORS.level4 }
  if (value <= 300) return { name: '重度污染', color: AQI_COLORS.level5 }
  return { name: '严重污染', color: AQI_COLORS.level6 }
}

const getAqiColor = (value) => getAqiLevel(value).color

const getQualityClass = (level) => {
  const map = {
    '优': 'quality-excellent',
    '良': 'quality-good',
    '轻度污染': 'quality-light',
    '中度污染': 'quality-moderate',
    '重度污染': 'quality-heavy',
    '严重污染': 'quality-severe'
  }
  return map[level] || ''
}

// 时间相关
const currentDate = ref('')
const currentTime = ref('')
let timeTimer = null

const updateTime = () => {
  const now = new Date()
  currentDate.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
  currentTime.value = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`
}

// 返回首页
const goHome = () => {
  router.push('/home')
}

// 城市列表
const cityList = ref([])

// 搜索参数
const searchParams = ref({
  city: '',
  startDate: '',
  endDate: '',
  qualityLevel: ''
})
const searchLoading = ref(false)

// 统计卡片
const statsCards = ref([
  {
    label: '平均 AQI',
    value: '--',
    unit: '',
    color: MORANDI_COLORS.primary,
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.primary}, #4DA3FF)`,
    icon: markRaw(DataAnalysis)
  },
  {
    label: '达标天数',
    value: '--',
    unit: '天',
    color: MORANDI_COLORS.success,
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.success}, #6ED58E)`,
    icon: markRaw(TrendCharts)
  },
  {
    label: '优良率',
    value: '--',
    unit: '%',
    color: MORANDI_COLORS.teal,
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.teal}, #8ED4E8)`,
    icon: markRaw(Document)
  },
  {
    label: '预警城市',
    value: '--',
    unit: '个',
    color: MORANDI_COLORS.danger,
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.danger}, #FFB87B)`,
    icon: markRaw(Warning)
  }
])

// 预警列表
const alertList = ref([])

// 趋势图
const trendChartRef = ref(null)
let trendChart = null
const trendDays = ref(30)

// 饼图
const pieChartRef = ref(null)
let pieChart = null

// 排名图
const rankChartRef = ref(null)
let rankChart = null
const rankLimit = ref(20)

// 对比图
const compareChartRef = ref(null)
let compareChart = null
const compareCities = ref([])

// 同比分析图
const yearCompareChartRef = ref(null)
let yearCompareChart = null
const compareYear = ref(2022)

// 数据表格
const tableData = ref([])
const tableLoading = ref(false)
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0
})

// 导出
const exportLoading = ref(false)

// 搜索处理
const handleSearch = async () => {
  searchLoading.value = true
  tableLoading.value = true
  try {
    const res = await searchAqi(searchParams.value)
    if (res.code === 200) {
      const data = res.data || []
      tableData.value = data.slice((pagination.value.page - 1) * pagination.value.pageSize, pagination.value.page * pagination.value.pageSize)
      pagination.value.total = data.length
      updateStats(data)
    }
  } catch (e) {
    console.error(e)
  } finally {
    searchLoading.value = false
    tableLoading.value = false
  }
  // 同时更新其他图表
  loadTrendData()
  loadPieData()
}

const resetSearch = () => {
  searchParams.value = {
    city: '',
    startDate: '',
    endDate: '',
    qualityLevel: ''
  }
  handleSearch()
}

// 更新统计数据
const updateStats = (data) => {
  if (data.length === 0) return

  const avgAqi = Math.round(data.reduce((sum, d) => sum + d.aqiValue, 0) / data.length)
  const goodDays = data.filter(d => d.aqiValue <= 100).length
  const goodRate = Math.round(goodDays / data.length * 100)
  const alertCities = new Set(data.filter(d => d.aqiValue > 150).map(d => d.city)).size

  statsCards.value[0].value = avgAqi
  statsCards.value[1].value = goodDays
  statsCards.value[2].value = goodRate
  statsCards.value[3].value = alertCities

  // 更新预警列表
  const alerts = data.filter(d => d.aqiValue > 150).slice(0, 5).map(d => ({
    city: d.city,
    message: `AQI达到${d.aqiValue}，${d.qualityLevel}`,
    level: d.aqiValue > 200 ? 'severe' : 'warning',
    time: d.date
  }))
  alertList.value = alerts
}

// 加载城市列表
const loadCityList = async () => {
  try {
    const res = await getAllCities()
    if (res.code === 200) {
      cityList.value = res.data || []
      // 自动选择前4个城市用于对比
      if (cityList.value.length > 0 && compareCities.value.length === 0) {
        compareCities.value = cityList.value.slice(0, 4)
      }
    }
  } catch (e) {
    console.error(e)
  }
}

// 加载趋势数据
const loadTrendData = async () => {
  try {
    // 使用搜索城市或第一个可用城市
    const city = searchParams.value.city || (cityList.value.length > 0 ? cityList.value[0] : '中卫')
    const res = await getTrend(city, trendDays.value)
    if (res.code === 200) {
      renderTrendChart(res.data || [])
    }
  } catch (e) {
    console.error(e)
  }
}

// 渲染趋势图
const renderTrendChart = (data) => {
  if (!trendChartRef.value) return
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const dates = data.map(d => d.date ? d.date.slice(5) : '')
  const values = data.map(d => d.aqiValue || 0)

  trendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: MORANDI_COLORS.border,
      borderWidth: 1,
      textStyle: { color: MORANDI_COLORS.text, fontFamily: '-apple-system, BlinkMacSystemFont, sans-serif' },
      formatter: (params) => {
        const val = params[0].value
        const level = getAqiLevel(val)
        return `<div style="font-weight:600;margin-bottom:4px;">${params[0].axisValue}</div>
                <div>AQI: <span style="color:${level.color};font-weight:600;">${val}</span> (${level.name})</div>`
      }
    },
    grid: { left: 50, right: 20, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: MORANDI_COLORS.border } },
      axisLabel: { color: MORANDI_COLORS.textMuted, fontSize: 10 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      max: 300,
      axisLine: { show: false },
      axisLabel: { color: MORANDI_COLORS.textMuted },
      splitLine: { lineStyle: { color: MORANDI_COLORS.separator, type: 'dashed' } }
    },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 4,
      showSymbol: false,
      lineStyle: {
        width: 2.5,
        color: MORANDI_COLORS.primary
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(107, 163, 190, 0.25)' },
            { offset: 1, color: 'rgba(107, 163, 190, 0.02)' }
          ]
        }
      },
      markLine: {
        silent: true,
        symbol: 'none',
        lineStyle: { color: MORANDI_COLORS.success, type: 'dashed', width: 1.5 },
        label: { show: true, position: 'end', formatter: '达标线', color: MORANDI_COLORS.textMuted, fontSize: 10 },
        data: [{ yAxis: 100 }]
      }
    }]
  })
}

// 加载饼图数据
const loadPieData = async () => {
  try {
    const res = await getPieData(searchParams.value.city, searchParams.value.startDate, searchParams.value.endDate)
    if (res.code === 200) {
      renderPieChart(res.data || [])
    }
  } catch (e) {
    console.error(e)
  }
}

// 渲染饼图
const renderPieChart = (data) => {
  if (!pieChartRef.value) return
  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value)
  }

  const pieData = data.length > 0 ? data : [
    { name: '优', value: 0 },
    { name: '良', value: 0 },
    { name: '轻度污染', value: 0 },
    { name: '中度污染', value: 0 },
    { name: '重度污染', value: 0 },
    { name: '严重污染', value: 0 }
  ]

  const colorMap = {
    '优': AQI_COLORS.level1,
    '良': AQI_COLORS.level2,
    '轻度污染': AQI_COLORS.level3,
    '中度污染': AQI_COLORS.level4,
    '重度污染': AQI_COLORS.level5,
    '严重污染': AQI_COLORS.level6
  }

  pieChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: MORANDI_COLORS.border,
      borderWidth: 1,
      textStyle: { color: MORANDI_COLORS.text },
      formatter: '{b}: {c}天 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      textStyle: { color: MORANDI_COLORS.textSecondary, fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: pieData.map(item => ({
        name: item.name,
        value: item.value || item.count || 0,
        itemStyle: { color: colorMap[item.name] || MORANDI_COLORS.gray }
      }))
    }]
  })
}

// 加载排名数据
const loadRankingData = async () => {
  try {
    const res = await getRanking(rankLimit.value)
    if (res.code === 200) {
      renderRankChart(res.data || [])
    }
  } catch (e) {
    console.error(e)
  }
}

// 渲染排名图
const renderRankChart = (data) => {
  if (!rankChartRef.value) return
  if (!rankChart) {
    rankChart = echarts.init(rankChartRef.value)
  }

  const sortedData = [...data].sort((a, b) => a.aqiValue - b.aqiValue)
  const cities = sortedData.map(d => d.city)
  const values = sortedData.map(d => d.aqiValue)

  rankChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: MORANDI_COLORS.border,
      borderWidth: 1,
      textStyle: { color: MORANDI_COLORS.text },
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const val = params[0].value
        const level = getAqiLevel(val)
        return `<div style="font-weight:600;">${params[0].name}</div>
                <div>AQI: <span style="color:${level.color};font-weight:600;">${val}</span></div>`
      }
    },
    grid: { left: 80, right: 50, top: 10, bottom: 20 },
    xAxis: {
      type: 'value',
      max: 300,
      axisLine: { show: false },
      axisLabel: { color: MORANDI_COLORS.textMuted },
      splitLine: { lineStyle: { color: MORANDI_COLORS.separator, type: 'dashed' } }
    },
    yAxis: {
      type: 'category',
      data: cities,
      axisLine: { lineStyle: { color: MORANDI_COLORS.border } },
      axisLabel: { color: MORANDI_COLORS.textSecondary, fontSize: 11 },
      axisTick: { show: false }
    },
    series: [{
      type: 'bar',
      data: values.map(val => ({
        value: val,
        itemStyle: {
          color: getAqiColor(val),
          borderRadius: [0, 6, 6, 0]
        }
      })),
      barWidth: 12,
      label: {
        show: true,
        position: 'right',
        color: MORANDI_COLORS.textSecondary,
        fontSize: 10,
        formatter: '{c}'
      }
    }]
  })
}

// 加载对比数据
const loadCompareData = async () => {
  if (compareCities.value.length === 0) return
  try {
    // 不传日期参数，后端会自动使用最新日期
    const res = await getMultiCityCompare(compareCities.value.join(','), '')
    if (res.code === 200) {
      renderCompareChart(res.data || [])
    }
  } catch (e) {
    console.error(e)
  }
}

// 渲染对比图
const renderCompareChart = (data) => {
  if (!compareChartRef.value) return
  if (!compareChart) {
    compareChart = echarts.init(compareChartRef.value)
  }

  // 如果没有数据，显示提示
  if (!data || data.length === 0) {
    compareChart.setOption({
      backgroundColor: 'transparent',
      title: {
        text: '请选择城市进行对比',
        left: 'center',
        top: 'center',
        textStyle: {
          color: MORANDI_COLORS.textMuted,
          fontSize: 14
        }
      },
      xAxis: { show: false },
      yAxis: { show: false },
      series: []
    })
    return
  }

  const cities = data.map(d => d.city)
  const values = data.map(d => d.aqiValue)

  compareChart.setOption({
    backgroundColor: 'transparent',
    title: { show: false },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: MORANDI_COLORS.border,
      borderWidth: 1,
      textStyle: { color: MORANDI_COLORS.text },
      axisPointer: { type: 'shadow' }
    },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: cities,
      axisLine: { lineStyle: { color: MORANDI_COLORS.border } },
      axisLabel: { color: MORANDI_COLORS.textMuted, fontSize: 10 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      max: 300,
      axisLine: { show: false },
      axisLabel: { color: MORANDI_COLORS.textMuted },
      splitLine: { lineStyle: { color: MORANDI_COLORS.separator, type: 'dashed' } }
    },
    series: [{
      type: 'bar',
      data: values.map((val, index) => ({
        value: val,
        itemStyle: {
          color: getAqiColor(val),
          borderRadius: [6, 6, 0, 0]
        }
      })),
      barWidth: 30,
      label: {
        show: true,
        position: 'top',
        color: MORANDI_COLORS.textSecondary,
        fontSize: 10,
        formatter: '{c}'
      }
    }]
  })
}

// 加载同比数据
const loadYearCompareData = async () => {
  if (!yearCompareChart) return

  // 模拟同比数据
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const currentYearData = months.map(() => Math.floor(Math.random() * 80 + 40))
  const lastYearData = months.map(() => Math.floor(Math.random() * 80 + 50))

  yearCompareChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: MORANDI_COLORS.border,
      borderWidth: 1,
      textStyle: { color: MORANDI_COLORS.text }
    },
    legend: {
      data: [`${compareYear.value}年`, `${compareYear.value - 1}年`],
      textStyle: { color: MORANDI_COLORS.textSecondary, fontSize: 11 },
      top: 0
    },
    grid: { left: 50, right: 20, top: 40, bottom: 30 },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: MORANDI_COLORS.border } },
      axisLabel: { color: MORANDI_COLORS.textMuted, fontSize: 10 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: MORANDI_COLORS.textMuted },
      splitLine: { lineStyle: { color: MORANDI_COLORS.separator, type: 'dashed' } }
    },
    series: [
      {
        name: `${compareYear.value}年`,
        type: 'line',
        data: currentYearData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 4,
        lineStyle: { width: 2.5, color: MORANDI_COLORS.primary },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(107, 163, 190, 0.2)' },
              { offset: 1, color: 'rgba(107, 163, 190, 0.02)' }
            ]
          }
        }
      },
      {
        name: `${compareYear.value - 1}年`,
        type: 'line',
        data: lastYearData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 4,
        lineStyle: { width: 2.5, color: MORANDI_COLORS.warning, type: 'dashed' }
      }
    ]
  })
}

// 导出Excel
const exportExcel = async () => {
  exportLoading.value = true
  try {
    const blob = await exportAqi(searchParams.value)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `AQI数据_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
    console.error(e)
  } finally {
    exportLoading.value = false
  }
}

// 导出CSV
const exportCsv = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  const headers = ['城市', '日期', 'AQI', '质量等级']
  const rows = tableData.value.map(d => [d.city, d.date, d.aqiValue, d.qualityLevel])

  let csvContent = '\uFEFF' + headers.join(',') + '\n'
  rows.forEach(row => {
    csvContent += row.join(',') + '\n'
  })

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `AQI数据_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  window.URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

// 分页处理
const handlePageChange = (page) => {
  pagination.value.page = page
  handleSearch()
}

const handlePageSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.page = 1
  handleSearch()
}

// 排序处理
const handleSortChange = ({ prop, order }) => {
  // 实现排序逻辑
}

// 窗口大小变化
const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
  rankChart?.resize()
  compareChart?.resize()
  yearCompareChart?.resize()
}

onMounted(async () => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)

  // 先加载城市列表
  await loadCityList()

  // 初始化图表
  setTimeout(() => {
    // 初始化趋势图
    if (trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value)
    }
    // 初始化饼图
    if (pieChartRef.value) {
      pieChart = echarts.init(pieChartRef.value)
    }
    // 初始化排名图
    if (rankChartRef.value) {
      rankChart = echarts.init(rankChartRef.value)
    }
    // 初始化对比图
    if (compareChartRef.value) {
      compareChart = echarts.init(compareChartRef.value)
    }
    // 初始化同比图
    if (yearCompareChartRef.value) {
      yearCompareChart = echarts.init(yearCompareChartRef.value)
    }

    // 加载数据（城市列表已加载，compareCities已设置）
    handleSearch()
    loadRankingData()
    loadCompareData()
    loadYearCompareData()
  }, 100)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
  rankChart?.dispose()
  compareChart?.dispose()
  yearCompareChart?.dispose()
})
</script>

<style scoped>
.aqi-analysis-page {
  width: 100%;
  min-height: 100vh;
  background: #FAFAF8;
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;
}

/* ========== 顶部导航栏 ========== */
.page-header {
  height: 60px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  color: #6BA3BE;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #FAFAF8;
  border-color: #6BA3BE;
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

.page-title h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2D2D2D;
  letter-spacing: -0.3px;
}

.page-title p {
  margin: 2px 0 0 0;
  font-size: 11px;
  color: #5A5A5A;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.current-time {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 14px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.current-time .date {
  font-size: 12px;
  color: #5A5A5A;
}

.current-time .time {
  font-size: 14px;
  font-weight: 600;
  color: #6BA3BE;
  font-variant-numeric: tabular-nums;
}

/* ========== 搜索区域 ========== */
.search-section {
  padding: 16px 24px;
}

.search-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.search-row {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  flex-wrap: wrap;
}

.search-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.search-item label {
  font-size: 12px;
  color: #5A5A5A;
  font-weight: 500;
}

.search-select {
  width: 160px;
}

.search-date {
  width: 160px;
}

.search-select :deep(.el-input__wrapper),
.search-date :deep(.el-input__wrapper) {
  background: #FAFAF8;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: none;
  border-radius: 10px;
}

.search-select :deep(.el-input__wrapper:hover),
.search-date :deep(.el-input__wrapper:hover) {
  border-color: rgba(107, 163, 190, 0.3);
}

.search-select :deep(.el-input__wrapper:focus-within),
.search-date :deep(.el-input__wrapper:focus-within) {
  border-color: #6BA3BE;
}

.search-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.btn-icon {
  width: 16px;
  height: 16px;
  margin-right: 4px;
}

.btn-icon-sm {
  width: 14px;
  height: 14px;
  margin-right: 4px;
}

/* ========== 主体内容 ========== */
.page-body {
  flex: 1;
  padding: 0 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: auto;
}

/* ========== 统计卡片行 ========== */
.stats-row {
  display: flex;
  gap: 16px;
}

.stats-cards {
  flex: 1;
  display: flex;
  gap: 16px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon :deep(svg) {
  width: 22px;
  height: 22px;
  color: #fff;
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 12px;
  color: #5A5A5A;
}

.stat-value {
  font-size: 26px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-unit {
  font-size: 11px;
  color: #5A5A5A;
}

/* 预警卡片 */
.alert-card {
  width: 320px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.alert-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  font-size: 14px;
  font-weight: 600;
  color: #2D2D2D;
}

.alert-icon {
  width: 18px;
  height: 18px;
  color: #E5A87B;
}

.alert-count {
  margin-left: auto;
  padding: 2px 8px;
  background: rgba(229, 168, 123, 0.15);
  color: #E5A87B;
  font-size: 12px;
  font-weight: 600;
  border-radius: 10px;
}

.alert-list {
  padding: 8px 12px;
  max-height: 120px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 12px;
  margin-bottom: 6px;
}

.alert-item.warning {
  background: rgba(229, 192, 123, 0.1);
}

.alert-item.severe {
  background: rgba(229, 165, 165, 0.1);
}

.alert-city {
  font-weight: 600;
  color: #2D2D2D;
}

.alert-message {
  flex: 1;
  color: #5A5A5A;
}

.alert-time {
  color: #8A8A8A;
  font-size: 11px;
}

.alert-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  color: #8A8A8A;
  font-size: 13px;
}

.alert-empty svg {
  width: 32px;
  height: 32px;
  margin-bottom: 8px;
  color: #7DBE8C;
}

/* ========== 图表行 ========== */
.charts-row {
  display: flex;
  gap: 16px;
}

.chart-card {
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #2D2D2D;
}

.title-icon {
  width: 18px;
  height: 18px;
  color: #6BA3BE;
}

.card-badge {
  font-size: 11px;
  padding: 4px 10px;
  background: rgba(107, 163, 190, 0.1);
  color: #6BA3BE;
  border-radius: 10px;
}

.card-controls {
  display: flex;
  gap: 8px;
}

.card-body {
  flex: 1;
  padding: 12px;
}

.chart-container {
  width: 100%;
  height: 280px;
}

/* 趋势图卡片 */
.trend-card {
  flex: 1.5;
}

/* 饼图卡片 */
.pie-card {
  flex: 1;
}

/* 排名卡片 */
.rank-card {
  flex: 1;
}

.rank-card .chart-container {
  height: 320px;
}

/* 对比卡片 */
.compare-card {
  flex: 1;
}

.compare-select {
  width: 200px;
}

/* 同比分析卡片 */
.year-compare-card {
  flex: 1;
}

/* 表格卡片 */
.table-card {
  flex: 1.5;
}

.table-body {
  display: flex;
  flex-direction: column;
  padding: 12px;
}

.table-body :deep(.el-table) {
  flex: 1;
}

.aqi-value {
  font-weight: 600;
}

.quality-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.quality-excellent {
  background: rgba(125, 190, 140, 0.15);
  color: #7DBE8C;
}

.quality-good {
  background: rgba(107, 163, 190, 0.15);
  color: #6BA3BE;
}

.quality-light {
  background: rgba(229, 192, 123, 0.15);
  color: #E5C07B;
}

.quality-moderate {
  background: rgba(229, 168, 123, 0.15);
  color: #E5A87B;
}

.quality-heavy {
  background: rgba(229, 165, 165, 0.15);
  color: #E5A5A5;
}

.quality-severe {
  background: rgba(176, 139, 212, 0.15);
  color: #B08BD4;
}

.pagination-wrapper {
  padding-top: 12px;
  display: flex;
  justify-content: flex-end;
}

/* ========== 响应式 ========== */
@media (max-width: 1400px) {
  .stats-cards {
    flex-wrap: wrap;
  }

  .stat-card {
    flex: 1 1 calc(50% - 8px);
  }

  .alert-card {
    width: 100%;
  }

  .stats-row {
    flex-direction: column;
  }
}

@media (max-width: 1100px) {
  .charts-row {
    flex-direction: column;
  }

  .chart-card {
    flex: none;
  }

  .search-row {
    flex-direction: column;
    align-items: stretch;
  }

  .search-item {
    width: 100%;
  }

  .search-select,
  .search-date {
    width: 100%;
  }

  .search-actions {
    margin-left: 0;
    margin-top: 8px;
  }
}
</style>
