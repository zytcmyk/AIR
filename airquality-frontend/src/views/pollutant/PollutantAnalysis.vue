<template>
  <div class="pollutant-dashboard">
    <!-- 头部标题 -->
    <header class="dashboard-header">
      <div class="header-content">
        <h1>污染物分析</h1>
        <p>实时监控 SO₂ / NOx / PM 排放数据</p>
      </div>
      <div class="header-actions">
        <el-select v-model="searchForm.city" placeholder="选择城市" clearable class="apple-select">
          <el-option v-for="city in cityList" :key="city" :label="city" :value="city" />
        </el-select>
        <el-select v-model="searchForm.pollutantType" placeholder="污染物类型" clearable class="apple-select">
          <el-option label="PM2.5" value="PM2.5" />
          <el-option label="PM10" value="PM10" />
          <el-option label="SO₂" value="SO2" />
          <el-option label="NO₂" value="NO2" />
          <el-option label="O₃" value="O3" />
          <el-option label="CO" value="CO" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="—"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          class="apple-date"
        />
        <el-button type="primary" class="apple-btn-primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button class="apple-btn-secondary" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
    </header>

    <!-- 第一行：关键指标卡片 -->
    <div class="grid-4">
      <div class="card kpi-card">
        <div class="kpi-icon kpi-icon-blue">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
            <path d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
          </svg>
        </div>
        <div class="kpi-content">
          <div class="kpi-number">{{ stats.totalCities }}<span class="kpi-unit"> 个</span></div>
          <div class="kpi-label">监测城市总数</div>
        </div>
        <div class="kpi-footer">
          <span>数据记录 <strong>{{ stats.totalRecords }}</strong></span>
          <span>污染物 <strong>6种</strong></span>
        </div>
      </div>

      <div class="card kpi-card">
        <div class="kpi-icon kpi-icon-green">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M3 15a4 4 0 004 4h9a5 5 0 10-.1-9.999 5.002 5.002 0 10-9.78 2.096A4.001 4.001 0 003 15z"/>
          </svg>
        </div>
        <div class="kpi-content">
          <div class="kpi-number">{{ stats.pm25Avg }}<span class="kpi-unit"> μg/m³</span></div>
          <div class="kpi-label">PM2.5 平均浓度</div>
        </div>
        <div class="kpi-trend" :class="stats.pm25Trend < 0 ? 'trend-down' : 'trend-up'">
          {{ stats.pm25Trend < 0 ? '↓' : '↑' }} {{ Math.abs(stats.pm25Trend) }}%
        </div>
      </div>

      <div class="card kpi-card">
        <div class="kpi-icon kpi-icon-orange">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"/>
          </svg>
        </div>
        <div class="kpi-content">
          <div class="kpi-number">{{ stats.so2Avg }}<span class="kpi-unit"> μg/m³</span></div>
          <div class="kpi-label">SO₂ 平均浓度</div>
        </div>
        <div class="kpi-footer">
          <span>NO₂: {{ stats.no2Avg }}</span>
          <span>O₃: {{ stats.o3Avg }}</span>
        </div>
      </div>

      <div class="card kpi-card">
        <div class="kpi-icon kpi-icon-purple">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="kpi-content">
          <div class="kpi-number">{{ stats.complianceRate }}<span class="kpi-unit">%</span></div>
          <div class="kpi-label">综合达标率</div>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: stats.complianceRate + '%' }"></div>
        </div>
      </div>
    </div>

    <!-- 第二行: 污染物占比 + 多污染物对比 -->
    <div class="grid-2col">
      <div class="card">
        <div class="card-header">
          <h3>污染物浓度占比</h3>
          <span class="card-badge">按平均浓度</span>
        </div>
        <div ref="pieChartRef" class="chart-container" v-loading="pieLoading"></div>
      </div>
      <div class="card">
        <div class="card-header">
          <h3>多污染物浓度对比</h3>
          <span class="card-badge">当前城市</span>
        </div>
        <div ref="compareChartRef" class="chart-container" v-loading="compareLoading"></div>
      </div>
    </div>

    <!-- 第三行: 趋势 + 排名 + 数据列表 -->
    <div class="grid-3col">
      <div class="card">
        <div class="card-header">
          <h3>各类污染物趋势</h3>
          <span class="card-badge">近30天</span>
        </div>
        <div ref="trendChartRef" class="chart-container small" v-loading="trendLoading"></div>
        <div class="mini-stats">
          <div class="mini-stat">
            <div class="mini-stat-value">{{ stats.pm25Avg }}</div>
            <div class="mini-stat-label">PM2.5</div>
          </div>
          <div class="mini-stat">
            <div class="mini-stat-value">{{ stats.pm10Avg }}</div>
            <div class="mini-stat-label">PM10</div>
          </div>
          <div class="mini-stat">
            <div class="mini-stat-value">{{ stats.so2Avg }}</div>
            <div class="mini-stat-label">SO₂</div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3>污染物浓度排名</h3>
          <span class="card-badge">Top 5</span>
        </div>
        <div class="rank-list">
          <div v-for="(item, index) in rankList" :key="index" class="rank-item">
            <div class="rank-left">
              <span class="rank-num" :class="getRankClass(index)">{{ index + 1 }}</span>
              <span class="rank-city">{{ item.city }}</span>
            </div>
            <div class="rank-right">
              <span class="rank-value">{{ item.value }} μg/m³</span>
              <span class="rank-tag" :style="{ background: getPollutantColor(item.pollutantType) }">{{ item.pollutantType }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3>实时污染物数据</h3>
          <span class="card-badge">最新5条</span>
        </div>
        <div class="data-list">
          <div v-for="(item, index) in latestData" :key="index" class="data-item">
            <div class="data-main">
              <div class="data-city">{{ item.city }}</div>
              <div class="data-meta">{{ item.pollutantType }} · {{ item.date }}</div>
            </div>
            <div class="data-value" :style="{ color: getValueColor(item.pollutantType, item.concentration) }">{{ item.concentration }}</div>
          </div>
        </div>
        <div class="data-footer">
          <span>共 {{ stats.totalRecords }} 条记录</span>
          <span class="refresh-btn" @click="handleSearch">刷新数据</span>
        </div>
      </div>
    </div>

    <!-- 第四行: 趋势分析 + 浓度分布 -->
    <div class="grid-2col">
      <div class="card">
        <div class="card-header">
          <h3>污染物浓度趋势分析</h3>
          <span class="card-badge">近一周</span>
        </div>
        <div ref="multiTrendChartRef" class="chart-container large" v-loading="multiTrendLoading"></div>
      </div>
      <div class="card">
        <div class="card-header">
          <h3>污染物浓度分布统计</h3>
          <span class="card-badge">按污染物类型</span>
        </div>
        <div ref="distributionChartRef" class="chart-container large" v-loading="distributionLoading"></div>
      </div>
    </div>

    <!-- 第五行: 城市对比 + 数据概览 -->
    <div class="grid-2col" style="margin-bottom: 0;">
      <div class="card">
        <div class="card-header">
          <h3>城市污染物浓度对比</h3>
          <span class="card-badge">Top 5 城市</span>
        </div>
        <div ref="cityCompareChartRef" class="chart-container medium" v-loading="cityCompareLoading"></div>
      </div>
      <div class="card">
        <div class="card-header">
          <h3>污染物数据概览</h3>
          <span class="card-badge">实时统计</span>
        </div>
        <div ref="overviewChartRef" class="chart-container medium" v-loading="overviewLoading"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Download } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { searchPollutant, getTrend, getPieData, getCompare, exportPollutant, getStats, getRanking, getLatest } from '@/api/pollutant'
import { getAllCities } from '@/api/aqi'

// 明亮莫兰迪风格配色
const morandiColors = {
  blue: '#6BA3BE',
  green: '#7DBE8C',
  yellow: '#E5C07B',
  pink: '#E5A5A5',
  purple: '#B08BD4',
  orange: '#E5A87B',
  gray: '#9AB5C5',
  lightGray: '#FAFAF8',
  darkGray: '#2D2D2D',
  text: '#2D2D2D',
  textSecondary: '#5A5A5A',
  background: '#FAFAF8'
}

// 污染物颜色映射 - 明亮莫兰迪风格
const pollutantColors = {
  'PM2.5': '#6BA3BE',
  'PM10': '#7DBE8C',
  'SO2': '#E5C07B',
  'NO2': '#B08BD4',
  'O3': '#E5A5A5',
  'CO': '#9AB5C5'
}

// 搜索表单
const searchForm = reactive({ city: '', pollutantType: '', startDate: '', endDate: '' })
const dateRange = ref([])
const cityList = ref([])

// 统计数据
const stats = reactive({
  totalCities: 52,
  totalRecords: 1256,
  pm25Avg: 58.5,
  pm10Avg: 85.2,
  so2Avg: 22.3,
  no2Avg: 35.2,
  o3Avg: 48.6,
  complianceRate: 87.5,
  pm25Trend: -8,
  pm25Compliance: 92.5,
  pm10Compliance: 88.3,
  so2Compliance: 95.2,
  compliantCities: 45
})

// 排名数据
const rankList = ref([
  { city: '石家庄市', value: 125.8, pollutantType: 'PM2.5' },
  { city: '郑州市', value: 112.5, pollutantType: 'PM2.5' },
  { city: '西安市', value: 105.2, pollutantType: 'PM2.5' },
  { city: '济南市', value: 98.6, pollutantType: 'PM2.5' },
  { city: '太原市', value: 95.3, pollutantType: 'PM2.5' }
])

// 最新数据
const latestData = ref([
  { city: '北京市', pollutantType: 'PM2.5', date: '2024-01-15', concentration: 58.5 },
  { city: '上海市', pollutantType: 'PM2.5', date: '2024-01-15', concentration: 42.3 },
  { city: '广州市', pollutantType: 'PM2.5', date: '2024-01-15', concentration: 35.8 },
  { city: '深圳市', pollutantType: 'PM2.5', date: '2024-01-15', concentration: 28.6 },
  { city: '成都市', pollutantType: 'PM2.5', date: '2024-01-15', concentration: 48.2 }
])

// 图表引用
const pieChartRef = ref(null)
const compareChartRef = ref(null)
const trendChartRef = ref(null)
const multiTrendChartRef = ref(null)
const distributionChartRef = ref(null)
const cityCompareChartRef = ref(null)
const overviewChartRef = ref(null)

// 加载状态
const pieLoading = ref(false)
const compareLoading = ref(false)
const trendLoading = ref(false)
const multiTrendLoading = ref(false)
const distributionLoading = ref(false)
const cityCompareLoading = ref(false)
const overviewLoading = ref(false)

// 图表实例
let pieChart, compareChart, trendChart, multiTrendChart, distributionChart, cityCompareChart, overviewChart

// 工具函数
const getRankClass = (index) => {
  if (index === 0) return 'rank-1'
  if (index === 1) return 'rank-2'
  if (index === 2) return 'rank-3'
  return ''
}

const getPollutantColor = (type) => {
  return pollutantColors[type] || morandiColors.blue
}

const getValueColor = (type, value) => {
  const num = parseFloat(value)
  if (type === 'PM2.5') {
    if (num <= 35) return pollutantColors['PM10'] // 莫兰迪绿
    if (num <= 75) return pollutantColors['SO2'] // 莫兰迪黄
    return pollutantColors['O3'] // 莫兰迪粉
  }
  return pollutantColors['PM2.5'] // 莫兰迪蓝
}

// 搜索
const handleSearch = async () => {
  if (dateRange.value?.length === 2) {
    searchForm.startDate = dateRange.value[0]
    searchForm.endDate = dateRange.value[1]
  }
  await loadAllCharts()
  ElMessage.success('数据已刷新')
}

// 导出
const handleExport = async () => {
  if (dateRange.value?.length === 2) {
    searchForm.startDate = dateRange.value[0]
    searchForm.endDate = dateRange.value[1]
  }

  ElMessage.info('正在导出，请稍候...')

  try {
    // 使用后端导出接口
    const token = localStorage.getItem('token')
    console.log('开始导出，参数:', searchForm)

    const response = await fetch('/api/pollutant/export', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(searchForm)
    })

    console.log('响应状态:', response.status)

    if (!response.ok) {
      const text = await response.text()
      console.error('导出失败响应:', text)
      throw new Error('导出失败: ' + response.status)
    }

    const blob = await response.blob()
    console.log('Blob大小:', blob.size)

    if (!blob || blob.size === 0) {
      ElMessage.warning('没有可导出的数据')
      return
    }

    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    const fileName = searchForm.city ? `污染物数据_${searchForm.city}` : '污染物数据'
    a.download = `${fileName}.xlsx`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (e) {
    console.error('导出失败:', e)
    ElMessage.error('导出失败: ' + e.message)
  }
}

// 加载城市列表
const loadCityList = async () => {
  try {
    const res = await getAllCities()
    if (res.code === 200) cityList.value = res.data || []
  } catch (e) { console.error(e) }
}

// 图表通用配置 - 莫兰迪风格
const chartBaseOption = {
  backgroundColor: 'transparent',
  tooltip: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: 'rgba(0, 0, 0, 0.06)',
    borderWidth: 1,
    textStyle: { color: morandiColors.text, fontFamily: '-apple-system, BlinkMacSystemFont, SF Pro Display, sans-serif' },
    extraCssText: 'box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08); border-radius: 8px;'
  }
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    ...chartBaseOption,
    tooltip: { ...chartBaseOption.tooltip, trigger: 'item' },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: { fontSize: 12, color: morandiColors.textSecondary, fontFamily: '-apple-system' }
    },
    series: [{
      name: '污染物占比',
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['55%', '50%'],
      data: [
        { value: 35, name: 'PM2.5', itemStyle: { color: pollutantColors['PM2.5'] } },
        { value: 25, name: 'PM10', itemStyle: { color: pollutantColors['PM10'] } },
        { value: 15, name: 'SO₂', itemStyle: { color: pollutantColors['SO2'] } },
        { value: 12, name: 'NO₂', itemStyle: { color: pollutantColors['NO2'] } },
        { value: 8, name: 'O₃', itemStyle: { color: pollutantColors['O3'] } },
        { value: 5, name: 'CO', itemStyle: { color: pollutantColors['CO'] } }
      ],
      label: { show: true, formatter: '{b}: {d}%', fontSize: 11, color: morandiColors.textSecondary },
      emphasis: { scale: 1.05 },
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 }
    }]
  })
}

// 初始化对比图
const initCompareChart = () => {
  if (!compareChartRef.value) return
  compareChart = echarts.init(compareChartRef.value)
  compareChart.setOption({
    ...chartBaseOption,
    tooltip: { ...chartBaseOption.tooltip, trigger: 'axis' },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: ['PM2.5', 'PM10', 'SO₂', 'NO₂', 'O₃', 'CO'],
      axisLabel: { color: morandiColors.textSecondary, fontFamily: '-apple-system' },
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: morandiColors.textSecondary },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } },
      axisLine: { show: false }
    },
    series: [{
      type: 'bar',
      data: [
        { value: stats.pm25Avg, itemStyle: { color: pollutantColors['PM2.5'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.pm10Avg, itemStyle: { color: pollutantColors['PM10'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.so2Avg, itemStyle: { color: pollutantColors['SO2'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.no2Avg, itemStyle: { color: pollutantColors['NO2'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.o3Avg, itemStyle: { color: pollutantColors['O3'], borderRadius: [8, 8, 0, 0] } },
        { value: 0.8, itemStyle: { color: pollutantColors['CO'], borderRadius: [8, 8, 0, 0] } }
      ],
      barWidth: 32,
      label: { show: true, position: 'top', color: morandiColors.textSecondary, fontSize: 11 }
    }]
  })
}

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  const dates = []
  const values = []
  for (let i = 29; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    dates.push((d.getMonth() + 1) + '/' + d.getDate())
    values.push(Math.floor(Math.random() * 40) + 40)
  }
  trendChart.setOption({
    ...chartBaseOption,
    tooltip: { ...chartBaseOption.tooltip, trigger: 'axis' },
    grid: { left: 40, right: 15, top: 15, bottom: 25 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: { color: morandiColors.textSecondary, fontSize: 10, rotate: 30 },
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: morandiColors.textSecondary, fontSize: 10 },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } },
      axisLine: { show: false }
    },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 4,
      lineStyle: { color: pollutantColors['PM2.5'], width: 2.5 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(107, 163, 190, 0.3)' },
          { offset: 1, color: 'rgba(107, 163, 190, 0.02)' }
        ])
      }
    }]
  })
}

// 初始化多趋势图
const initMultiTrendChart = () => {
  if (!multiTrendChartRef.value) return
  multiTrendChart = echarts.init(multiTrendChartRef.value)
  const weekDates = []
  const pm25Data = []
  const pm10Data = []
  const so2Data = []
  for (let i = 6; i >= 0; i--) {
    const d = new Date()
    d.setDate(d.getDate() - i)
    weekDates.push((d.getMonth() + 1) + '/' + d.getDate())
    pm25Data.push(Math.floor(Math.random() * 30) + 45)
    pm10Data.push(Math.floor(Math.random() * 40) + 60)
    so2Data.push(Math.floor(Math.random() * 15) + 15)
  }
  multiTrendChart.setOption({
    ...chartBaseOption,
    tooltip: { ...chartBaseOption.tooltip, trigger: 'axis' },
    legend: {
      data: ['PM2.5', 'PM10', 'SO₂'],
      bottom: 0,
      textStyle: { color: morandiColors.textSecondary, fontFamily: '-apple-system' }
    },
    grid: { top: 20, left: 45, right: 20, bottom: 40 },
    xAxis: {
      type: 'category',
      data: weekDates,
      axisLabel: { color: morandiColors.textSecondary },
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: '浓度 (μg/m³)',
      nameTextStyle: { color: morandiColors.textSecondary },
      axisLabel: { color: morandiColors.textSecondary },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } },
      axisLine: { show: false }
    },
    series: [
      {
        name: 'PM2.5',
        type: 'line',
        smooth: true,
        data: pm25Data,
        lineStyle: { color: pollutantColors['PM2.5'], width: 2.5 },
        itemStyle: { color: pollutantColors['PM2.5'] },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(107, 163, 190, 0.25)' }, { offset: 1, color: 'rgba(107, 163, 190, 0.02)' }]) }
      },
      {
        name: 'PM10',
        type: 'line',
        smooth: true,
        data: pm10Data,
        lineStyle: { color: pollutantColors['PM10'], width: 2.5 },
        itemStyle: { color: pollutantColors['PM10'] },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(125, 190, 140, 0.25)' }, { offset: 1, color: 'rgba(125, 190, 140, 0.02)' }]) }
      },
      {
        name: 'SO₂',
        type: 'line',
        smooth: true,
        data: so2Data,
        lineStyle: { color: pollutantColors['SO2'], width: 2.5 },
        itemStyle: { color: pollutantColors['SO2'] },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(229, 192, 123, 0.25)' }, { offset: 1, color: 'rgba(229, 192, 123, 0.02)' }]) }
      }
    ]
  })
}

// 初始化分布图
const initDistributionChart = () => {
  if (!distributionChartRef.value) return
  distributionChart = echarts.init(distributionChartRef.value)
  distributionChart.setOption({
    ...chartBaseOption,
    tooltip: { ...chartBaseOption.tooltip, trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { top: 30, left: 50, right: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: ['PM2.5', 'PM10', 'SO₂', 'NO₂', 'O₃', 'CO'],
      axisLabel: { color: morandiColors.textSecondary },
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'μg/m³',
      nameTextStyle: { color: morandiColors.textSecondary },
      axisLabel: { color: morandiColors.textSecondary },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } },
      axisLine: { show: false }
    },
    series: [{
      type: 'bar',
      data: [
        { value: stats.pm25Avg, itemStyle: { color: pollutantColors['PM2.5'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.pm10Avg, itemStyle: { color: pollutantColors['PM10'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.so2Avg, itemStyle: { color: pollutantColors['SO2'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.no2Avg, itemStyle: { color: pollutantColors['NO2'], borderRadius: [8, 8, 0, 0] } },
        { value: stats.o3Avg, itemStyle: { color: pollutantColors['O3'], borderRadius: [8, 8, 0, 0] } },
        { value: 0.8, itemStyle: { color: pollutantColors['CO'], borderRadius: [8, 8, 0, 0] } }
      ],
      barWidth: 28,
      label: { show: true, position: 'top', color: morandiColors.textSecondary, fontSize: 11 }
    }]
  })
}

// 初始化城市对比图
const initCityCompareChart = () => {
  if (!cityCompareChartRef.value) return
  cityCompareChart = echarts.init(cityCompareChartRef.value)
  cityCompareChart.setOption({
    ...chartBaseOption,
    tooltip: { ...chartBaseOption.tooltip, trigger: 'axis' },
    grid: { left: 50, right: 20, top: 10, bottom: 30 },
    xAxis: {
      type: 'category',
      data: ['石家庄', '郑州', '西安', '济南', '太原'],
      axisLabel: { rotate: 15, color: morandiColors.textSecondary },
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: 'PM2.5 (μg/m³)',
      nameTextStyle: { color: morandiColors.textSecondary },
      axisLabel: { color: morandiColors.textSecondary },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.06)' } },
      axisLine: { show: false }
    },
    series: [{
      type: 'bar',
      data: [
        { value: 125.8, itemStyle: { color: pollutantColors['PM2.5'], borderRadius: [8, 8, 0, 0] } },
        { value: 112.5, itemStyle: { color: pollutantColors['PM2.5'], borderRadius: [8, 8, 0, 0] } },
        { value: 105.2, itemStyle: { color: pollutantColors['PM2.5'], borderRadius: [8, 8, 0, 0] } },
        { value: 98.6, itemStyle: { color: pollutantColors['PM2.5'], borderRadius: [8, 8, 0, 0] } },
        { value: 95.3, itemStyle: { color: pollutantColors['PM2.5'], borderRadius: [8, 8, 0, 0] } }
      ],
      barWidth: 24,
      label: { show: true, position: 'top', color: morandiColors.textSecondary }
    }]
  })
}

// 初始化概览图（雷达图）
const initOverviewChart = () => {
  if (!overviewChartRef.value) return
  overviewChart = echarts.init(overviewChartRef.value)
  overviewChart.setOption({
    ...chartBaseOption,
    tooltip: { ...chartBaseOption.tooltip, trigger: 'item' },
    radar: {
      indicator: [
        { name: 'PM2.5', max: 200 },
        { name: 'PM10', max: 200 },
        { name: 'SO₂', max: 100 },
        { name: 'NO₂', max: 100 },
        { name: 'O₃', max: 100 },
        { name: 'CO', max: 5 }
      ],
      axisName: { color: morandiColors.textSecondary, fontSize: 11, fontFamily: '-apple-system' },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      splitArea: { areaStyle: { color: ['rgba(0, 0, 0, 0.02)', 'rgba(0, 0, 0, 0.04)'] } },
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } }
    },
    series: [{
      type: 'radar',
      data: [{
        value: [stats.pm25Avg, stats.pm10Avg, stats.so2Avg, stats.no2Avg, stats.o3Avg, 0.8],
        name: '污染物浓度',
        areaStyle: { color: 'rgba(107, 163, 190, 0.2)' },
        lineStyle: { color: pollutantColors['PM2.5'], width: 2 },
        itemStyle: { color: pollutantColors['PM2.5'] }
      }]
    }]
  })
}

// 加载所有图表
const loadAllCharts = async () => {
  initPieChart()
  initCompareChart()
  initTrendChart()
  initMultiTrendChart()
  initDistributionChart()
  initCityCompareChart()
  initOverviewChart()
}

// 窗口大小变化
const handleResize = () => {
  pieChart?.resize()
  compareChart?.resize()
  trendChart?.resize()
  multiTrendChart?.resize()
  distributionChart?.resize()
  cityCompareChart?.resize()
  overviewChart?.resize()
}

onMounted(() => {
  loadCityList()
  loadAllCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  compareChart?.dispose()
  trendChart?.dispose()
  multiTrendChart?.dispose()
  distributionChart?.dispose()
  cityCompareChart?.dispose()
  overviewChart?.dispose()
})
</script>

<style scoped>
.pollutant-dashboard {
  padding: 20px 24px;
  min-height: calc(100vh - 56px);
  background: #FAFAF8;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* 头部 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-content h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2D2D2D;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.header-content p {
  color: #5A5A5A;
  font-size: 14px;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.apple-select {
  width: 120px;
}

.apple-date {
  width: 260px;
}

.apple-btn-primary {
  background: linear-gradient(135deg, #6BA3BE 0%, #7DBE8C 100%) !important;
  border: none !important;
  border-radius: 10px !important;
  padding: 10px 18px !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
}

.apple-btn-primary:hover {
  background: linear-gradient(135deg, #5A93AE 0%, #6BAE7C 100%) !important;
  transform: scale(1.02);
  box-shadow: 0 4px 16px rgba(107, 163, 190, 0.35) !important;
}

.apple-btn-secondary {
  background: #FFFFFF !important;
  border: 1px solid rgba(0, 0, 0, 0.08) !important;
  border-radius: 10px !important;
  padding: 10px 18px !important;
  color: #2D2D2D !important;
  font-weight: 500 !important;
  transition: all 0.2s ease !important;
}

.apple-btn-secondary:hover {
  background: #F5F5F0 !important;
  border-color: rgba(107, 163, 190, 0.3) !important;
}

/* 网格布局 */
.grid-4 {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.grid-2col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.grid-3col {
  display: grid;
  grid-template-columns: 1fr 0.9fr 1.1fr;
  gap: 16px;
  margin-bottom: 16px;
}

/* 卡片样式 - 苹果风格 */
.card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06), 0 8px 24px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  font-size: 15px;
  font-weight: 600;
  color: #2D2D2D;
  margin: 0;
}

.card-badge {
  font-size: 11px;
  background: rgba(107, 163, 190, 0.12);
  color: #6BA3BE;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
}

/* KPI 卡片 */
.kpi-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.kpi-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.kpi-icon svg {
  width: 22px;
  height: 22px;
}

.kpi-icon-blue {
  background: rgba(107, 163, 190, 0.12);
  color: #6BA3BE;
}

.kpi-icon-green {
  background: rgba(125, 190, 140, 0.12);
  color: #7DBE8C;
}

.kpi-icon-orange {
  background: rgba(229, 192, 123, 0.15);
  color: #E5C07B;
}

.kpi-icon-purple {
  background: rgba(176, 139, 212, 0.12);
  color: #B08BD4;
}

.kpi-content {
  flex: 1;
}

.kpi-number {
  font-size: 32px;
  font-weight: 700;
  color: #2D2D2D;
  line-height: 1.1;
  letter-spacing: -1px;
}

.kpi-unit {
  font-size: 16px;
  font-weight: 500;
  color: #5A5A5A;
}

.kpi-label {
  font-size: 13px;
  color: #5A5A5A;
  margin-top: 4px;
}

.kpi-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #5A5A5A;
  padding-top: 12px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.kpi-footer strong {
  color: #2D2D2D;
  font-weight: 600;
}

.kpi-trend {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 500;
  width: fit-content;
}

.trend-down {
  background: rgba(125, 190, 140, 0.12);
  color: #7DBE8C;
}

.trend-up {
  background: rgba(229, 165, 165, 0.15);
  color: #E5A5A5;
}

/* 进度条 */
.progress-bar {
  background: rgba(0, 0, 0, 0.06);
  border-radius: 10px;
  height: 6px;
  overflow: hidden;
}

.progress-fill {
  background: linear-gradient(90deg, #6BA3BE, #B08BD4);
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
}

/* 图表容器 */
.chart-container {
  height: 220px;
  width: 100%;
}

.chart-container.small {
  height: 160px;
}

.chart-container.large {
  height: 240px;
}

.chart-container.medium {
  height: 180px;
}

/* 小统计块 */
.mini-stats {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.mini-stat {
  flex: 1;
  text-align: center;
  padding: 12px;
  background: #FAFAF8;
  border-radius: 12px;
}

.mini-stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #2D2D2D;
}

.mini-stat-label {
  font-size: 11px;
  color: #5A5A5A;
  margin-top: 2px;
}

/* 排名列表 */
.rank-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rank-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: #FAFAF8;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.rank-item:hover {
  background: #F5F5F0;
}

.rank-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank-num {
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  background: #F5F5F0;
  color: #5A5A5A;
}

.rank-num.rank-1 {
  background: linear-gradient(135deg, #6BA3BE, #B08BD4);
  color: #fff;
}

.rank-num.rank-2 {
  background: linear-gradient(135deg, #7DBE8C, #6BA3BE);
  color: #fff;
}

.rank-num.rank-3 {
  background: linear-gradient(135deg, #E5C07B, #E5A5A5);
  color: #fff;
}

.rank-city {
  font-size: 14px;
  font-weight: 500;
  color: #2D2D2D;
}

.rank-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rank-value {
  font-size: 13px;
  color: #5A5A5A;
}

.rank-tag {
  font-size: 10px;
  padding: 3px 8px;
  border-radius: 6px;
  color: #fff;
  font-weight: 500;
}

/* 数据列表 */
.data-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.data-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: #FAFAF8;
  border-radius: 12px;
}

.data-city {
  font-size: 14px;
  font-weight: 500;
  color: #2D2D2D;
}

.data-meta {
  font-size: 11px;
  color: #5A5A5A;
  margin-top: 2px;
}

.data-value {
  font-size: 18px;
  font-weight: 700;
}

.data-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  font-size: 12px;
  color: #5A5A5A;
}

.refresh-btn {
  color: #6BA3BE;
  cursor: pointer;
  font-weight: 500;
}

.refresh-btn:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 1400px) {
  .grid-4 {
    grid-template-columns: repeat(2, 1fr);
  }
  .grid-3col {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 1100px) {
  .grid-2col {
    grid-template-columns: 1fr;
  }
  .grid-3col {
    grid-template-columns: 1fr;
  }
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .header-actions {
    flex-wrap: wrap;
    width: 100%;
  }
}

@media (max-width: 700px) {
  .pollutant-dashboard {
    padding: 16px;
  }
  .grid-4 {
    grid-template-columns: 1fr;
  }
  .header-content h1 {
    font-size: 24px;
  }
}
</style>
