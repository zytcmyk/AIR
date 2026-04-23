<template>
  <div class="source-analysis-page">
    <!-- 顶部导航栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <button class="back-btn" @click="goHome">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 22V12h6v10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>返回首页</span>
        </button>
        <div class="page-title">
          <h1>污染源分析</h1>
          <p>来源追踪 · 贡献分析 · 趋势预测</p>
        </div>
      </div>
      <div class="header-right">
        <el-select v-model="selectedCity" placeholder="选择城市" filterable clearable @change="handleCityChange" class="city-select">
          <el-option v-for="city in cityList" :key="city" :label="city" :value="city" />
        </el-select>
        <div class="current-time">
          <span class="date">{{ currentDate }}</span>
          <span class="time">{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="dashboard-body">
      <!-- 左侧面板 -->
      <aside class="panel-left">
        <!-- 筛选条件 -->
        <div class="chart-card filter-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              筛选条件
            </span>
          </div>
          <div class="filter-body">
            <div class="filter-group">
              <label>污染源类型</label>
              <div class="source-tags">
                <span
                  v-for="type in sourceTypes"
                  :key="type"
                  class="source-tag"
                  :class="{ active: selectedSourceTypes.includes(type) }"
                  :style="selectedSourceTypes.includes(type) ? { background: SOURCE_COLORS[type], borderColor: SOURCE_COLORS[type] } : {}"
                  @click="toggleSourceType(type)"
                >{{ type }}</span>
              </div>
            </div>
            <div class="filter-group">
              <label>日期范围</label>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                size="small"
                class="date-picker"
              />
            </div>
            <div class="filter-actions">
              <el-button type="primary" size="small" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" @click="handleReset">重置</el-button>
            </div>
          </div>
        </div>

        <!-- 污染源贡献占比 -->
        <div class="chart-card pie-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M21.21 15.21A10 10 0 118 2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M22 12A10 10 0 0012 2v10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              污染源贡献占比
            </span>
          </div>
          <div class="card-body">
            <div ref="pieChartRef" class="chart-container"></div>
          </div>
        </div>

        <!-- 城市排行 -->
        <div class="chart-card rank-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M8 21h8M12 17v4M7 4h10l-1 9H8L7 4z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="14" r="2" stroke="currentColor" stroke-width="2"/>
              </svg>
              城市污染源排行
            </span>
          </div>
          <div class="card-body rank-body">
            <div v-for="(item, index) in rankingData" :key="item.city" class="rank-item" @click="selectCity(item.city)">
              <span class="rank-num" :class="getRankClass(index)">{{ index + 1 }}</span>
              <div class="rank-info">
                <span class="rank-city">{{ item.city }}</span>
                <span class="rank-source">{{ item.mainSource }}</span>
              </div>
              <div class="rank-bar-wrap">
                <div class="rank-bar" :style="{ width: item.value + '%', background: SOURCE_COLORS[item.mainSource] || THEME_COLORS.neon }"></div>
              </div>
              <span class="rank-value">{{ item.value }}%</span>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中间区域 -->
      <section class="panel-center">
        <!-- 贡献率趋势分析 -->
        <div class="chart-card trend-card">
          <div class="card-header">
            <span class="card-title">贡献率趋势分析</span>
            <div class="card-tools">
              <el-radio-group v-model="trendPeriod" size="small" @change="loadTrendData">
                <el-radio-button value="week">周</el-radio-button>
                <el-radio-button value="month">月</el-radio-button>
                <el-radio-button value="quarter">季度</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="trend-legend">
            <div v-for="type in selectedSourceTypes" :key="type" class="legend-item" @click="toggleHighlight(type)" :class="{ dim: highlightedSource && highlightedSource !== type }">
              <span class="legend-dot" :style="{ background: SOURCE_COLORS[type] }"></span>
              <span class="legend-name">{{ type }}</span>
            </div>
          </div>
          <div class="card-body trend-body">
            <div ref="trendChartRef" class="chart-container trend-chart"></div>
          </div>
        </div>

        <!-- 城市对比分析 -->
        <div class="chart-card compare-card">
          <div class="card-header">
            <span class="card-title">城市污染源结构对比</span>
            <div class="card-tools">
              <el-select v-model="compareCities" multiple collapse-tags collapse-tags-tooltip placeholder="选择城市" size="small" class="compare-select" @change="loadCompareData">
                <el-option v-for="city in cityList" :key="city" :label="city" :value="city" />
              </el-select>
            </div>
          </div>
          <div class="card-body">
            <div ref="compareChartRef" class="chart-container"></div>
          </div>
        </div>
      </section>

      <!-- 右侧面板 -->
      <aside class="panel-right">
        <!-- 数据总览 -->
        <div class="chart-card stats-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              数据总览
            </span>
          </div>
          <div class="stats-body">
            <div class="stat-item">
              <div class="stat-icon" style="background: linear-gradient(135deg, #D48B8B, #E0A0A0);">
                <svg viewBox="0 0 24 24" fill="none"><path d="M13 10V3L4 14h7v7l9-11h-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </div>
              <div class="stat-info">
                <span class="stat-label">首要污染源</span>
                <span class="stat-value">{{ mainSource }}</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon" style="background: linear-gradient(135deg, #6BA3BE, #7DBE8C);">
                <svg viewBox="0 0 24 24" fill="none"><path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </div>
              <div class="stat-info">
                <span class="stat-label">平均贡献率</span>
                <span class="stat-value">{{ avgContribution }}%</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon" style="background: linear-gradient(135deg, #B08BD4, #C4A0E8);">
                <svg viewBox="0 0 24 24" fill="none"><path d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </div>
              <div class="stat-info">
                <span class="stat-label">监测城市</span>
                <span class="stat-value">{{ cityCount }}个</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 污染源详情 -->
        <div class="chart-card detail-card">
          <div class="card-header">
            <span class="card-title">污染源详情</span>
          </div>
          <div class="detail-body">
            <div v-for="item in sourceDetails" :key="item.type" class="detail-item">
              <div class="detail-header">
                <div class="detail-icon" :style="{ background: SOURCE_COLORS[item.type] }">
                  <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/></svg>
                </div>
                <div class="detail-info">
                  <span class="detail-name">{{ item.type }}</span>
                  <span class="detail-value">{{ item.value }}%</span>
                </div>
                <div class="detail-trend" :class="item.trend?.direction">
                  <span class="trend-value">{{ item.trend?.value }}</span>
                  <span class="trend-desc">{{ item.trend?.desc }}</span>
                </div>
              </div>
              <div class="detail-tags">
                <span v-for="tag in item.tags" :key="tag" class="detail-tag">{{ tag }}</span>
              </div>
              <div class="detail-industries">
                <span class="industry-label">主要来源:</span>
                <span v-for="(ind, idx) in item.industries" :key="ind" class="industry-item">
                  {{ ind }}<span v-if="idx < item.industries.length - 1">、</span>
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 相关性分析 -->
        <div class="chart-card correlation-card">
          <div class="card-header">
            <span class="card-title">污染源协同效应</span>
          </div>
          <div class="card-body">
            <div ref="heatmapChartRef" class="chart-container heatmap-chart"></div>
          </div>
        </div>
      </aside>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { THEME_COLORS, SOURCE_COLORS } from '@/styles/aqi-colors'
import { getCitySourcePie, getTrendByPeriod, getCityRankingBySource, getCorrelations, getSourceDistribution, getCitySourceStructureCompare } from '@/api/source'

const router = useRouter()

// 城市列表
const cityList = ref(['北京', '上海', '广州', '深圳', '成都', '武汉', '西安', '乌鲁木齐', '南京', '杭州'])
const selectedCity = ref('北京')

// 污染源类型
const sourceTypes = ['机动车尾气', '工业排放', '燃煤', '扬尘', '其他']
const selectedSourceTypes = ref([...sourceTypes])

// 日期范围
const dateRange = ref(['2024-01-01', '2024-06-30'])

// 加载状态
const loading = ref(false)

// 时间显示
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

// 图表引用
const pieChartRef = ref(null)
const trendChartRef = ref(null)
const compareChartRef = ref(null)
const heatmapChartRef = ref(null)

// 图表实例
let pieChart = null
let trendChart = null
let compareChart = null
let heatmapChart = null

// 数据
const rankingData = ref([])
const sourceDetails = ref([])
const trendPeriod = ref('month')
const highlightedSource = ref(null)
const compareCities = ref(['北京', '上海', '广州', '成都'])

// 统计数据
const mainSource = ref('机动车尾气')
const avgContribution = ref('42.5')
const cityCount = ref(10)

// 获取排名样式
const getRankClass = (index) => index < 3 ? `top-${index + 1}` : ''

// 切换污染源类型
const toggleSourceType = (type) => {
  const index = selectedSourceTypes.value.indexOf(type)
  if (index > -1) {
    if (selectedSourceTypes.value.length > 1) {
      selectedSourceTypes.value.splice(index, 1)
    }
  } else {
    selectedSourceTypes.value.push(type)
  }
}

// 高亮污染源
const toggleHighlight = (type) => {
  highlightedSource.value = highlightedSource.value === type ? null : type
}

// 查询
const handleSearch = async () => {
  loading.value = true
  await loadAllData()
  // 确保趋势图根据筛选条件更新
  await loadTrendData()
  loading.value = false
  ElMessage.success('数据已更新')
}

// 重置
const handleReset = () => {
  selectedSourceTypes.value = [...sourceTypes]
  dateRange.value = ['2024-01-01', '2024-06-30']
  handleSearch()
}

// 城市切换
const handleCityChange = async () => {
  await loadAllData()
}

// 选择城市
const selectCity = (city) => {
  selectedCity.value = city
  handleCityChange()
}

// 加载所有数据
const loadAllData = async () => {
  await Promise.all([
    loadPieData(),
    loadTrendData(),
    loadCompareData(),
    loadRankingData(),
    loadCorrelationData(),
    loadSourceDetails()
  ])
}

// 加载饼图数据
const loadPieData = async () => {
  try {
    const res = await getCitySourcePie(selectedCity.value, dateRange.value?.[0], dateRange.value?.[1])
    if (res.code === 200 && res.data) {
      // 后端返回的是 { city, data, warnings, total }，data是数组
      const list = res.data.data || res.data.sourceList || []
      initPieChart(list.map(d => ({
        sourceType: d.name || d.sourceType,
        rate: d.value || d.rate
      })))
    }
  } catch (e) {
    console.error(e)
    initPieChart([])
  }
}

// 初始化饼图
const initPieChart = (data) => {
  if (!pieChartRef.value) return
  if (pieChart) {
    pieChart.dispose()
  }
  pieChart = echarts.init(pieChartRef.value)

  // 过滤数据，只显示选中的污染源类型
  let pieData = []
  if (data.length > 0) {
    pieData = data
      .filter(d => selectedSourceTypes.value.includes(d.sourceType))
      .map(d => ({
        name: d.sourceType,
        value: d.rate,
        itemStyle: { color: SOURCE_COLORS[d.sourceType] || THEME_COLORS.neon }
      }))
  } else {
    // 模拟数据，只显示选中的污染源类型
    const baseValues = {
      '机动车尾气': 42.5,
      '工业排放': 25.3,
      '燃煤': 12.8,
      '扬尘': 13.4,
      '其他': 6.0
    }
    pieData = selectedSourceTypes.value.map(type => ({
      name: type,
      value: baseValues[type] || 20,
      itemStyle: { color: SOURCE_COLORS[type] }
    }))
  }

  pieChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(107, 163, 190, 0.2)',
      textStyle: { color: '#2D2D2D' },
      formatter: '{b}<br/>贡献率: {c}%'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { color: '#5A5A5A', fontSize: 11 }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: 'transparent', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#2D2D2D' }
      },
      labelLine: { show: false },
      data: pieData
    }]
  })
}

// 加载趋势数据
const loadTrendData = async () => {
  try {
    const res = await getTrendByPeriod({
      city: selectedCity.value,
      period: trendPeriod.value,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    })
    if (res.code === 200) {
      // 过滤数据，只保留选中的污染源类型
      const filteredData = (res.data || []).filter(item =>
        selectedSourceTypes.value.includes(item.sourceType)
      )
      initTrendChart(filteredData)
    }
  } catch (e) {
    console.error(e)
    initTrendChart([])
  }
}

// 初始化趋势图
const initTrendChart = (data) => {
  if (!trendChartRef.value) return
  if (trendChart) {
    trendChart.dispose()
  }
  trendChart = echarts.init(trendChartRef.value)

  // 如果有后端数据，使用后端数据
  if (data && data.length > 0) {
    // 获取所有唯一的周期，按顺序排列
    const allPeriods = [...new Set(data.map(item => item.period))].sort()

    // 格式化X轴标签
    const periodLabels = allPeriods.map(p => {
      if (trendPeriod.value === 'week') {
        return p.replace('2024-', '')
      } else if (trendPeriod.value === 'month') {
        return p.replace('2024-', '') + '月'
      } else {
        return p.replace('2024-', '')
      }
    })

    // 按污染源类型分组，并确保数据按周期顺序排列
    const groupedData = {}
    data.forEach(item => {
      if (!groupedData[item.sourceType]) {
        groupedData[item.sourceType] = {}
      }
      groupedData[item.sourceType][item.period] = item.avgContributionRate
    })

    // 为每个污染源类型构建数据数组，确保与周期对应
    const seriesData = selectedSourceTypes.value.map((type, idx) => {
      const typeData = groupedData[type] || {}
      const values = allPeriods.map(period => typeData[period] || 0)

      return {
        name: type,
        type: 'line',
        smooth: true,
        data: values,
        lineStyle: { width: 2.5, color: SOURCE_COLORS[type] },
        itemStyle: { color: SOURCE_COLORS[type] },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: SOURCE_COLORS[type] + '50' },
              { offset: 0.5, color: SOURCE_COLORS[type] + '20' },
              { offset: 1, color: 'transparent' }
            ]
          }
        },
        symbol: 'circle',
        symbolSize: 8,
        showSymbol: false,
        emphasis: {
          focus: 'series',
          itemStyle: {
            shadowBlur: 15,
            shadowColor: SOURCE_COLORS[type]
          }
        }
      }
    })

    trendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: 'rgba(107, 163, 190, 0.2)',
        textStyle: { color: '#2D2D2D' },
        axisPointer: { type: 'line', lineStyle: { color: 'rgba(107, 163, 190, 0.5)' } }
      },
      legend: { show: false },
      grid: { left: 55, right: 25, top: 15, bottom: 35, containLabel: false },
      xAxis: {
        type: 'category',
        data: periodLabels,
        axisLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.2)' } },
        axisLabel: { color: '#5A5A5A', fontSize: 11, interval: 0 },
        axisTick: { show: false }
      },
      yAxis: {
        type: 'value',
        name: '贡献率(%)',
        nameTextStyle: { color: '#8A8A8A', fontSize: 10 },
        axisLine: { show: false },
        axisLabel: { color: '#8A8A8A', fontSize: 10 },
        splitLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.15)' } }
      },
      series: seriesData
    })
  } else {
    // 使用模拟数据
    let periods = []
    if (trendPeriod.value === 'week') {
      periods = ['W01', 'W02', 'W03', 'W04', 'W05', 'W06']
    } else if (trendPeriod.value === 'month') {
      periods = ['1月', '2月', '3月', '4月', '5月', '6月']
    } else if (trendPeriod.value === 'quarter') {
      periods = ['Q1', 'Q2']
    }

    const seriesData = selectedSourceTypes.value.map((type, idx) => {
      const baseValues = {
        '机动车尾气': [36.3, 37.5, 40.5, 42.5, 44.5, 46.5],
        '工业排放': [27.5, 26.5, 23.5, 22.5, 21.5, 20.5],
        '燃煤': [17.7, 18.1, 13.4, 11.4, 10.4, 9.4],
        '扬尘': [12.9, 12.4, 17.1, 18.1, 18.1, 18.1],
        '其他': [5.6, 5.5, 5.5, 5.5, 5.5, 5.5]
      }
      const values = baseValues[type] || periods.map(() => 20)

      return {
        name: type,
        type: 'line',
        smooth: true,
        data: values.slice(0, periods.length),
        lineStyle: { width: 2.5, color: SOURCE_COLORS[type] },
        itemStyle: { color: SOURCE_COLORS[type] },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: SOURCE_COLORS[type] + '50' },
              { offset: 0.5, color: SOURCE_COLORS[type] + '20' },
              { offset: 1, color: 'transparent' }
            ]
          }
        },
        symbol: 'circle',
        symbolSize: 8,
        showSymbol: false,
        emphasis: {
          focus: 'series',
          itemStyle: {
            shadowBlur: 15,
            shadowColor: SOURCE_COLORS[type]
          }
        }
      }
    })

    trendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: 'rgba(107, 163, 190, 0.2)',
        textStyle: { color: '#2D2D2D' },
        axisPointer: { type: 'line', lineStyle: { color: 'rgba(107, 163, 190, 0.5)' } }
      },
      legend: { show: false },
      grid: { left: 55, right: 25, top: 15, bottom: 35, containLabel: false },
      xAxis: {
        type: 'category',
        data: periods,
        axisLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.2)' } },
        axisLabel: { color: '#5A5A5A', fontSize: 11, interval: 0 },
        axisTick: { show: false }
      },
      yAxis: {
        type: 'value',
        name: '贡献率(%)',
        nameTextStyle: { color: '#8A8A8A', fontSize: 10 },
        axisLine: { show: false },
        axisLabel: { color: '#8A8A8A', fontSize: 10 },
        splitLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.15)' } }
      },
      series: seriesData
    })
  }
}

// 加载对比数据
const loadCompareData = async () => {
  if (compareCities.value.length === 0) return
  try {
    const res = await getCitySourceStructureCompare(
      compareCities.value,
      dateRange.value?.[0],
      dateRange.value?.[1]
    )
    if (res.code === 200) {
      initCompareChart(res.data || [])
    }
  } catch (e) {
    console.error(e)
    initCompareChart([])
  }
}

// 初始化对比图
const initCompareChart = (data) => {
  if (!compareChartRef.value) return
  if (compareChart) {
    compareChart.dispose()
  }
  compareChart = echarts.init(compareChartRef.value)

  const cities = compareCities.value
  let seriesData = []

  // 如果有后端数据，处理后端数据
  if (data && data.length > 0) {
    // 按城市和污染源类型组织数据
    const cityDataMap = {}
    data.forEach(item => {
      if (!cityDataMap[item.city]) {
        cityDataMap[item.city] = {}
      }
      cityDataMap[item.city][item.sourceType] = item.contributionRate
    })

    // 只显示选中的污染源类型
    seriesData = selectedSourceTypes.value.map((type) => {
      const values = cities.map(city => {
        const cityData = cityDataMap[city]
        return cityData ? (cityData[type] || 0) : 0
      })
      return {
        name: type,
        type: 'bar',
        stack: 'total',
        barWidth: 30,
        data: values,
        itemStyle: { color: SOURCE_COLORS[type] }
      }
    })
  } else {
    // 使用模拟数据
    const cityDataMap = {
      '北京': { '机动车尾气': 42.5, '工业排放': 20.5, '燃煤': 9.4, '扬尘': 18.1, '其他': 5.5 },
      '上海': { '机动车尾气': 38.2, '工业排放': 25.8, '燃煤': 8.5, '扬尘': 21.9, '其他': 5.6 },
      '广州': { '机动车尾气': 49.5, '工业排放': 14.7, '燃煤': 6.4, '扬尘': 20.9, '其他': 8.5 },
      '深圳': { '机动车尾气': 52.3, '工业排放': 12.5, '燃煤': 4.2, '扬尘': 18.8, '其他': 12.2 },
      '成都': { '机动车尾气': 39.8, '工业排放': 18.3, '燃煤': 12.6, '扬尘': 21.8, '其他': 7.5 },
      '武汉': { '机动车尾气': 41.2, '工业排放': 22.8, '燃煤': 15.3, '扬尘': 13.5, '其他': 7.2 },
      '西安': { '机动车尾气': 35.6, '工业排放': 28.4, '燃煤': 18.2, '扬尘': 11.8, '其他': 6.0 },
      '乌鲁木齐': { '机动车尾气': 28.5, '工业排放': 35.2, '燃煤': 22.8, '扬尘': 8.5, '其他': 5.0 },
      '南京': { '机动车尾气': 44.8, '工业排放': 19.6, '燃煤': 10.2, '扬尘': 17.4, '其他': 8.0 },
      '杭州': { '机动车尾气': 46.2, '工业排放': 16.8, '燃煤': 8.5, '扬尘': 19.5, '其他': 9.0 }
    }
    seriesData = selectedSourceTypes.value.map((type) => {
      const values = cities.map(city => {
        const cityData = cityDataMap[city]
        return cityData ? (cityData[type] || 0) : 0
      })
      return {
        name: type,
        type: 'bar',
        stack: 'total',
        barWidth: 30,
        data: values,
        itemStyle: { color: SOURCE_COLORS[type] }
      }
    })
  }

  compareChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(107, 163, 190, 0.2)',
      textStyle: { color: '#2D2D2D' },
      axisPointer: { type: 'shadow' }
    },
    legend: {
      top: 5,
      textStyle: { color: '#5A5A5A', fontSize: 10 }
    },
    grid: { left: 50, right: 20, top: 40, bottom: 30 },
    xAxis: {
      type: 'category',
      data: cities,
      axisLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.2)' } },
      axisLabel: { color: '#5A5A5A', fontSize: 10 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: '贡献率(%)',
      nameTextStyle: { color: '#8A8A8A' },
      axisLine: { show: false },
      axisLabel: { color: '#8A8A8A' },
      splitLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.15)' } }
    },
    series: seriesData
  })
}

// 加载排名数据
const loadRankingData = async () => {
  try {
    const res = await getCityRankingBySource({ limit: 10 })
    if (res.code === 200) {
      rankingData.value = (res.data || []).map(item => ({
        city: item.city,
        mainSource: item.mainSourceType || '机动车尾气',
        value: item.contributionRate || 40
      }))
    }
  } catch (e) {
    console.error(e)
    rankingData.value = [
      { city: '乌鲁木齐', mainSource: '工业排放', value: 45.2 },
      { city: '深圳', mainSource: '机动车尾气', value: 50.5 },
      { city: '广州', mainSource: '机动车尾气', value: 47.5 },
      { city: '上海', mainSource: '机动车尾气', value: 45.8 },
      { city: '北京', mainSource: '机动车尾气', value: 44.8 }
    ]
  }
}

// 加载相关性数据
const loadCorrelationData = async () => {
  try {
    const res = await getCorrelations()
    if (res.code === 200) {
      initHeatmapChart(res.data || [])
    }
  } catch (e) {
    console.error(e)
    initHeatmapChart([])
  }
}

// 初始化热力图
const initHeatmapChart = (data) => {
  if (!heatmapChartRef.value) return
  if (heatmapChart) {
    heatmapChart.dispose()
  }
  heatmapChart = echarts.init(heatmapChartRef.value)

  const sources = ['机动车', '工业', '燃煤', '扬尘', '其他']
  const matrix = [
    [1.00, 0.82, 0.45, 0.38, 0.25],
    [0.82, 1.00, 0.65, 0.42, 0.35],
    [0.45, 0.65, 1.00, 0.28, 0.52],
    [0.38, 0.42, 0.28, 1.00, 0.18],
    [0.25, 0.35, 0.52, 0.18, 1.00]
  ]

  const correlationData = []
  for (let i = 0; i < sources.length; i++) {
    for (let j = 0; j < sources.length; j++) {
      correlationData.push([i, j, matrix[i][j]])
    }
  }

  heatmapChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      position: 'top',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: 'rgba(107, 163, 190, 0.2)',
      textStyle: { color: '#2D2D2D' },
      formatter: (params) => `${sources[params.data[0]]} - ${sources[params.data[1]]}<br/>相关系数: ${params.data[2].toFixed(2)}`
    },
    grid: { left: 50, right: 10, top: 10, bottom: 30 },
    xAxis: {
      type: 'category',
      data: sources,
      axisLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.2)' } },
      axisLabel: { color: '#5A5A5A', fontSize: 9 }
    },
    yAxis: {
      type: 'category',
      data: sources,
      axisLine: { lineStyle: { color: 'rgba(107, 163, 190, 0.2)' } },
      axisLabel: { color: '#5A5A5A', fontSize: 9 }
    },
    visualMap: {
      min: 0,
      max: 1,
      show: false,
      inRange: { color: ['#E8F4F8', '#B8D4E3', '#6BA3BE', '#7DBE8C', '#D4A76A'] }
    },
    series: [{
      type: 'heatmap',
      data: correlationData,
      label: {
        show: true,
        formatter: (params) => params.data[2].toFixed(2),
        color: '#2D2D2D',
        fontSize: 8
      },
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowColor: 'rgba(107, 163, 190, 0.3)' }
      }
    }]
  })
}

// 加载污染源详情
const loadSourceDetails = async () => {
  try {
    const res = await getSourceDistribution(selectedCity.value)
    if (res.code === 200 && res.data) {
      // 后端返回的是 { city, data, warnings, total }，data是数组
      const list = res.data.data || res.data.sourceList || []
      sourceDetails.value = list.map(d => ({
        type: d.name || d.sourceType,
        value: d.value || d.rate,
        tags: getTagsByType(d.name || d.sourceType),
        industries: getIndustriesByType(d.name || d.sourceType),
        trend: getTrendByType(d.name || d.sourceType)
      }))
      if (sourceDetails.value.length > 0) {
        const sorted = [...sourceDetails.value].sort((a, b) => b.value - a.value)
        mainSource.value = sorted[0]?.type || '机动车尾气'
        avgContribution.value = sorted[0]?.value?.toFixed(1) || '42.5'
      }
    }
  } catch (e) {
    console.error(e)
    sourceDetails.value = [
      { type: '机动车尾气', value: 42.5, tags: ['早晚高峰', '城市核心区'], industries: ['汽油车(52%)', '柴油车(35%)', '摩托车(13%)'], trend: { direction: 'up', value: '+3.2%', desc: '较上月上升' } },
      { type: '工业排放', value: 25.3, tags: ['工业园区', '持续监测'], industries: ['钢铁(28%)', '化工(24%)', '水泥(18%)', '其他(30%)'], trend: { direction: 'down', value: '-2.1%', desc: '较上月下降' } },
      { type: '燃煤', value: 12.8, tags: ['冬季高峰', '逐步替代'], industries: ['民用散煤(45%)', '工业燃煤(38%)', '电力燃煤(17%)'], trend: { direction: 'down', value: '-5.8%', desc: '较上月下降' } },
      { type: '扬尘', value: 13.4, tags: ['春季高发', '需加强管控'], industries: ['建筑工地(54%)', '道路扬尘(46%)'], trend: { direction: 'up', value: '+1.5%', desc: '较上月上升' } },
      { type: '其他', value: 6.0, tags: ['分散源', '综合管控'], industries: ['生物质燃烧(53%)', '餐饮油烟(47%)'], trend: { direction: 'stable', value: '+0.2%', desc: '基本持平' } }
    ]
  }
}

const getTagsByType = (type) => {
  const tags = {
    '机动车尾气': ['早晚高峰', '城市核心区'],
    '工业排放': ['工业园区', '持续监测'],
    '燃煤': ['冬季高峰', '逐步替代'],
    '扬尘': ['春季高发', '需加强管控'],
    '其他': ['分散源', '综合管控']
  }
  return tags[type] || []
}

const getIndustriesByType = (type) => {
  const industries = {
    '机动车尾气': ['汽油车(52%)', '柴油车(35%)', '摩托车(13%)'],
    '工业排放': ['钢铁(28%)', '化工(24%)', '水泥(18%)', '其他(30%)'],
    '燃煤': ['民用散煤(45%)', '工业燃煤(38%)', '电力燃煤(17%)'],
    '扬尘': ['建筑工地(54%)', '道路扬尘(46%)'],
    '其他': ['生物质燃烧(53%)', '餐饮油烟(47%)']
  }
  return industries[type] || []
}

const getTrendByType = (type) => {
  const trends = {
    '机动车尾气': { direction: 'up', value: '+3.2%', desc: '较上月上升' },
    '工业排放': { direction: 'down', value: '-2.1%', desc: '较上月下降' },
    '燃煤': { direction: 'down', value: '-5.8%', desc: '较上月下降' },
    '扬尘': { direction: 'up', value: '+1.5%', desc: '较上月上升' },
    '其他': { direction: 'stable', value: '+0.2%', desc: '基本持平' }
  }
  return trends[type] || { direction: 'stable', value: '0%', desc: '无变化' }
}

// 窗口大小变化
const handleResize = () => {
  pieChart?.resize()
  trendChart?.resize()
  compareChart?.resize()
  heatmapChart?.resize()
}

onMounted(async () => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)

  await loadAllData()

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  trendChart?.dispose()
  compareChart?.dispose()
  heatmapChart?.dispose()
})
</script>

<style scoped>
.source-analysis-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #E8F4F8 0%, #F5F0E8 50%, #F0E8F5 100%);
  display: flex;
  flex-direction: column;
}

/* 导航栏 - 莫兰迪风格 */
.dashboard-header {
  height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(107, 163, 190, 0.2);
  box-shadow: 0 4px 20px rgba(107, 163, 190, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(107, 163, 190, 0.08);
  border: 1px solid rgba(107, 163, 190, 0.2);
  border-radius: 8px;
  color: #6BA3BE;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.back-btn:hover {
  background: rgba(107, 163, 190, 0.15);
  border-color: #6BA3BE;
  box-shadow: 0 4px 12px rgba(107, 163, 190, 0.15);
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
}

.page-title p {
  margin: 2px 0 0 0;
  font-size: 11px;
  color: #8A8A8A;
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
  padding: 6px 14px;
  background: rgba(107, 163, 190, 0.08);
  border: 1px solid rgba(107, 163, 190, 0.15);
  border-radius: 16px;
}

.current-time .date {
  font-size: 12px;
  color: #5A5A5A;
}

.current-time .time {
  font-size: 14px;
  font-weight: 600;
  color: #6BA3BE;
}

.city-select {
  width: 140px;
}

.city-select :deep(.el-input__wrapper) {
  background: rgba(107, 163, 190, 0.08);
  border: 1px solid rgba(107, 163, 190, 0.2);
  box-shadow: none;
}

.city-select :deep(.el-input__inner) {
  color: #2D2D2D;
}

.dashboard-body {
  flex: 1;
  display: flex;
  padding: 16px;
  gap: 16px;
  overflow: hidden;
}

.panel-left, .panel-right {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-center {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 卡片 - 莫兰迪风格 */
.chart-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(107, 163, 190, 0.15);
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(107, 163, 190, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(107, 163, 190, 0.1);
  background: rgba(107, 163, 190, 0.03);
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

.card-tools {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-body {
  flex: 1;
  padding: 12px;
}

.chart-container {
  width: 100%;
  height: 100%;
  min-height: 180px;
}

/* 筛选区 - 莫兰迪风格 */
.filter-body {
  padding: 12px 16px;
}

.filter-group {
  margin-bottom: 12px;
}

.filter-group label {
  display: block;
  font-size: 12px;
  color: #5A5A5A;
  margin-bottom: 8px;
}

.source-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.source-tag {
  padding: 4px 10px;
  font-size: 11px;
  background: rgba(107, 163, 190, 0.08);
  border: 1px solid rgba(107, 163, 190, 0.2);
  border-radius: 12px;
  color: #5A5A5A;
  cursor: pointer;
  transition: all 0.3s;
}

.source-tag:hover {
  border-color: #6BA3BE;
  box-shadow: 0 2px 8px rgba(107, 163, 190, 0.15);
}

.source-tag.active {
  color: #fff;
  box-shadow: 0 4px 12px rgba(107, 163, 190, 0.25);
}

.filter-actions {
  display: flex;
  gap: 8px;
}

.date-picker {
  width: 100%;
}

.date-picker :deep(.el-input__wrapper) {
  background: rgba(107, 163, 190, 0.05);
  border: 1px solid rgba(107, 163, 190, 0.15);
}

.pie-card .chart-container {
  min-height: 200px;
}

.rank-card {
  flex: 1;
}

.rank-card .rank-body {
  padding: 8px 12px;
  overflow-y: auto;
}

/* 排行表格 - 莫兰迪风格 */
.rank-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.rank-item:hover {
  background: rgba(107, 163, 190, 0.08);
}

.rank-num {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  border-radius: 4px;
  background: rgba(107, 163, 190, 0.1);
  color: #5A5A5A;
}

.rank-num.top-1 { background: linear-gradient(135deg, #D48B8B, #E0A0A0); color: #fff; box-shadow: 0 2px 8px rgba(212, 139, 139, 0.3); }
.rank-num.top-2 { background: linear-gradient(135deg, #6BA3BE, #7DBE8C); color: #fff; box-shadow: 0 2px 8px rgba(107, 163, 190, 0.3); }
.rank-num.top-3 { background: linear-gradient(135deg, #B08BD4, #C4A0FF); color: #fff; box-shadow: 0 2px 8px rgba(176, 139, 212, 0.3); }

.rank-info {
  flex: 0 0 70px;
}

.rank-city {
  display: block;
  font-size: 13px;
  color: #2D2D2D;
  font-weight: 500;
}

.rank-source {
  font-size: 10px;
  color: #8A8A8A;
}

.rank-bar-wrap {
  flex: 1;
  height: 6px;
  background: rgba(107, 163, 190, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.rank-bar {
  height: 100%;
  border-radius: 3px;
}

.rank-value {
  width: 40px;
  text-align: right;
  font-size: 12px;
  font-weight: 600;
  color: #6BA3BE;
}

.trend-card {
  flex: 1.2;
  display: flex;
  flex-direction: column;
}

.trend-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.trend-chart {
  flex: 1;
  min-height: 250px;
  height: 100%;
}

.trend-legend {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px 20px;
  padding: 10px 16px;
  background: rgba(107, 163, 190, 0.02);
  border-top: 1px solid rgba(107, 163, 190, 0.1);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  opacity: 1;
  transition: opacity 0.3s;
}

.legend-item.dim {
  opacity: 0.3;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-name {
  font-size: 12px;
  color: #5A5A5A;
}

.compare-card {
  flex: 1;
}

.compare-select {
  width: 180px;
}

/* 指标卡片 - 莫兰迪风格 */
.stats-body {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: rgba(107, 163, 190, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(107, 163, 190, 0.1);
  transition: all 0.3s;
}

.stat-item:hover {
  border-color: rgba(107, 163, 190, 0.2);
  box-shadow: 0 4px 12px rgba(107, 163, 190, 0.08);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 20px;
  height: 20px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-label {
  display: block;
  font-size: 11px;
  color: #5A5A5A;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #6BA3BE;
}

.detail-body {
  padding: 8px 12px;
  max-height: 400px;
  overflow-y: auto;
}

.detail-item {
  padding: 12px 0;
  border-bottom: 1px solid rgba(107, 163, 190, 0.08);
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.detail-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-icon svg {
  width: 16px;
  height: 16px;
  color: #fff;
}

.detail-info {
  flex: 1;
}

.detail-name {
  display: block;
  font-size: 13px;
  color: #2D2D2D;
}

.detail-value {
  font-size: 16px;
  font-weight: 700;
  color: #6BA3BE;
}

.detail-trend {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
}

/* 趋势色：上升 #D48B8B，下降 #7DBE8C */
.detail-trend.up {
  background: rgba(212, 139, 139, 0.15);
}

.detail-trend.down {
  background: rgba(125, 190, 140, 0.15);
}

.detail-trend.stable {
  background: rgba(107, 163, 190, 0.15);
}

.trend-value {
  font-weight: 600;
}

.detail-trend.up .trend-value {
  color: #D48B8B;
}

.detail-trend.down .trend-value {
  color: #7DBE8C;
}

.detail-trend.stable .trend-value {
  color: #6BA3BE;
}

.trend-desc {
  color: #8A8A8A;
  font-size: 9px;
}

.detail-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 6px;
}

.detail-tag {
  font-size: 10px;
  padding: 2px 6px;
  background: rgba(107, 163, 190, 0.08);
  border-radius: 4px;
  color: #5A5A5A;
}

.detail-industries {
  font-size: 11px;
  color: #5A5A5A;
  line-height: 1.5;
}

.industry-label {
  color: #8A8A8A;
}

.industry-item {
  color: #5A5A5A;
}

.correlation-card .heatmap-chart {
  min-height: 180px;
}

@media (max-width: 1400px) {
  .panel-left, .panel-right {
    width: 280px;
  }
}

@media (max-width: 1100px) {
  .dashboard-body {
    flex-direction: column;
    overflow-y: auto;
  }

  .panel-left, .panel-right {
    width: 100%;
    flex-direction: row;
  }

  .chart-card {
    flex: 1;
  }
}
</style>
