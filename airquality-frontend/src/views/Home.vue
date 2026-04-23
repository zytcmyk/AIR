<template>
  <div class="dashboard-screen">
    <!-- 顶部 Header -->
    <header class="screen-header">
      <div class="header-left">
        <div class="platform-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 40 40" fill="none">
              <circle cx="20" cy="20" r="18" stroke="#6BA3BE" stroke-width="2"/>
              <circle cx="20" cy="20" r="12" stroke="#6BA3BE" stroke-width="1.5" opacity="0.6"/>
              <circle cx="20" cy="20" r="6" fill="#6BA3BE"/>
            </svg>
          </div>
          <div class="logo-text">
            <h1>城市空气质量智能分析平台</h1>
            <p>Urban Air Quality Intelligent Analysis Platform</p>
          </div>
        </div>
      </div>

      <div class="header-center">
        <!-- 城市选择器 -->
        <div class="city-selector">
          <el-select
            v-model="selectedCity"
            placeholder="选择城市"
            filterable
            clearable
            @change="handleCityChange"
            class="city-select"
          >
            <el-option
              v-for="city in cityList"
              :key="city"
              :label="city"
              :value="city"
            />
          </el-select>
        </div>
        <div class="datetime-display">
          <span class="date">{{ currentDate }}</span>
          <span class="time">{{ currentTime }}</span>
          <span class="weekday">{{ currentWeekday }}</span>
        </div>
      </div>

      <div class="header-right">
        <!-- 天气简报 -->
        <div class="weather-info">
          <svg class="weather-icon" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="4" fill="#E5A87B"/>
            <path d="M12 2v2M12 20v2M4 12H2M22 12h-2M6.34 6.34L4.93 4.93M19.07 4.93l-1.41 1.41M6.34 17.66l-1.41 1.41M19.07 19.07l-1.41-1.41" stroke="#E5A87B" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span class="weather-temp">26°C</span>
          <span class="weather-desc">晴</span>
        </div>

        <!-- 预警跑马灯 -->
        <div class="alert-marquee">
          <div class="marquee-label">
            <svg viewBox="0 0 24 24" fill="none" class="alert-icon">
              <path d="M12 9v4M12 17h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            预警
          </div>
          <div class="marquee-content">
            <div class="marquee-track" :style="{ transform: `translateX(${marqueeOffset}px)` }">
              <span v-for="(alert, index) in alertList" :key="index" class="alert-item" :class="alert.level">
                {{ alert.text }}
              </span>
            </div>
          </div>
        </div>

        <!-- 个人中心 -->
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-center">
            <el-avatar :size="32" class="user-avatar" :src="userStore.userInfo?.avatar">
              {{ userStore.userInfo?.username?.charAt(0).toUpperCase() }}
            </el-avatar>
            <span class="user-name">{{ userStore.userInfo?.username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="screen-body">
      <!-- KPI 卡片区域 -->
      <section class="kpi-section">
        <div class="kpi-card" v-for="(kpi, index) in kpiCards" :key="index">
          <div class="kpi-icon" :style="{ background: kpi.iconBg }">
            <component :is="kpi.icon" />
          </div>
          <div class="kpi-content">
            <span class="kpi-label">{{ kpi.label }}</span>
            <span class="kpi-value" :style="{ color: kpi.color }">{{ kpi.value }}</span>
            <span class="kpi-unit">{{ kpi.unit }}</span>
          </div>
          <div class="kpi-trend" :class="kpi.trend > 0 ? 'up' : 'down'">
            <svg viewBox="0 0 24 24" fill="none">
              <path v-if="kpi.trend < 0" d="M12 19V5M5 12l7-7 7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path v-else d="M12 5v14M5 12l7 7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>{{ Math.abs(kpi.trend) }}%</span>
          </div>
        </div>
      </section>

      <!-- 图表区域 -->
      <section class="charts-section">
        <!-- 左侧面板 -->
        <aside class="panel-left">
          <!-- AQI 实时概况 -->
          <div class="panel-card aqi-gauge-section">
            <div class="card-header">
              <span class="card-title">AQI 实时概况</span>
              <span class="card-badge">{{ selectedCity || '全部城市' }}</span>
            </div>
            <div class="gauge-container" ref="gaugeChartRef"></div>
            <div class="gauge-info">
              <div class="compare-row">
                <span class="compare-label">环比昨日</span>
                <span class="compare-value" :class="compareClass">
                  {{ compareValue > 0 ? '+' : '' }}{{ compareValue }}
                  <svg viewBox="0 0 24 24" fill="none" class="trend-icon">
                    <path v-if="compareValue < 0" d="M12 19V5M5 12l7-7 7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path v-else d="M12 5v14M5 12l7 7 7-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </span>
              </div>
            </div>
          </div>

          <!-- 首要污染物雷达图 -->
          <div class="panel-card radar-section">
            <div class="card-header">
              <span class="card-title">首要污染物分析</span>
            </div>
            <div class="radar-container" ref="radarChartRef"></div>
          </div>
        </aside>

        <!-- 中间地图区域 -->
        <section class="center-map">
          <!-- 地图容器 -->
          <div class="map-container" ref="mapChartRef"></div>

          <!-- 地图控制按钮 -->
          <div class="map-controls">
            <el-tooltip :content="showBubble ? '隐藏气泡' : '显示气泡'" placement="left">
              <div class="control-btn" :class="{ active: showBubble }" @click="toggleBubble">
                <svg viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="8" stroke="currentColor" stroke-width="2"/>
                  <circle cx="12" cy="12" r="3" fill="currentColor"/>
                </svg>
              </div>
            </el-tooltip>
          </div>

          <!-- 图例 -->
          <div class="map-legend">
            <div class="legend-title">AQI 等级</div>
            <div class="legend-items">
              <div class="legend-item" v-for="level in aqiLegend" :key="level.name">
                <span class="legend-color" :style="{ background: level.color }"></span>
                <span class="legend-text">{{ level.name }}</span>
                <span class="legend-range">{{ level.range }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- 右侧面板 -->
        <aside class="panel-right">
          <!-- 重点污染源监控排名 -->
          <div class="panel-card source-rank-section">
            <div class="card-header">
              <span class="card-title">重点污染源监控</span>
              <span class="card-more" @click="navigateTo('/source')">查看更多</span>
            </div>
            <div class="rank-list">
              <div
                v-for="(item, index) in sourceRankList"
                :key="index"
                class="rank-item"
              >
                <span class="rank-num" :class="getRankClass(index)">{{ index + 1 }}</span>
                <div class="rank-info">
                  <span class="rank-name">{{ item.name }}</span>
                  <span class="rank-type">{{ item.type }}</span>
                </div>
                <div class="rank-value" :class="item.status">
                  <span class="value-num">{{ item.value }}</span>
                  <span class="value-unit">{{ item.unit }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 环保实时资讯 -->
          <div class="panel-card news-section">
            <div class="card-header">
              <span class="card-title">环保实时资讯</span>
              <span class="card-more" @click="navigateTo('/article')">更多</span>
            </div>
            <div class="news-list" ref="newsListRef">
              <div
                v-for="(news, index) in newsList"
                :key="index"
                class="news-item"
                @click="goArticle(news.id)"
              >
                <span class="news-tag" :class="getNewsTagClass(news.type)">{{ news.type }}</span>
                <span class="news-title">{{ news.title }}</span>
                <span class="news-time">{{ news.time }}</span>
              </div>
            </div>
          </div>
        </aside>
      </section>

      <!-- 四大模块导航 -->
      <section class="module-section">
        <div
          v-for="(module, index) in moduleCards"
          :key="module.path"
          class="module-card"
          @click="navigateTo(module.path)"
        >
          <div class="module-icon" :style="{ background: module.gradient }">
            <component :is="module.icon" />
          </div>
          <div class="module-info">
            <h3>{{ module.title }}</h3>
            <p>{{ module.desc }}</p>
          </div>
          <div class="module-data">
            <span class="data-value" :style="{ color: module.dataColor }">{{ module.value }}</span>
            <span class="data-label">{{ module.dataLabel }}</span>
          </div>
          <svg class="module-arrow" viewBox="0 0 24 24" fill="none">
            <path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, shallowRef, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, TrendCharts, PieChart, Document } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { useUserStore } from '@/store/user'
import { logout } from '@/api/auth'
import { getLatestArticles } from '@/api/article'
import { getHomeStats, getLatestCityAqi, getTrend, getPieData, getAllCities } from '@/api/aqi'
import { getPollutantLatest } from '@/api/pollutant'
import { getSourceRanking } from '@/api/pollutionSource'

// 明亮莫兰迪风格配色
const MORANDI_COLORS = {
  blue: '#6BA3BE',
  green: '#7DBE8C',
  orange: '#E5A87B',
  purple: '#B08BD4',
  red: '#E5A5A5',
  yellow: '#E5C07B',
  teal: '#8BBDD4',
  pink: '#D4A5B5'
}

// AQI 颜色 - 明亮莫兰迪色系
const AQI_COLORS = {
  level1: '#7DBE8C', // 优 - 明亮绿
  level2: '#6BA3BE', // 良 - 明亮蓝
  level3: '#E5C07B', // 轻度 - 明亮黄
  level4: '#E5A87B', // 中度 - 明亮橙
  level5: '#E5A5A5', // 重度 - 明亮粉
  level6: '#B08BD4'  // 严重 - 明亮紫
}

// 获取AQI等级
const getAqiLevel = (aqi) => {
  if (aqi <= 50) return { name: '优', color: AQI_COLORS.level1 }
  if (aqi <= 100) return { name: '良', color: AQI_COLORS.level2 }
  if (aqi <= 150) return { name: '轻度污染', color: AQI_COLORS.level3 }
  if (aqi <= 200) return { name: '中度污染', color: AQI_COLORS.level4 }
  if (aqi <= 300) return { name: '重度污染', color: AQI_COLORS.level5 }
  return { name: '严重污染', color: AQI_COLORS.level6 }
}

const getAqiColor = (aqi) => {
  return getAqiLevel(aqi).color
}

// 导入中国地图数据
import chinaMapData from '@/assets/map/china-full.json'

const router = useRouter()
const userStore = useUserStore()

// 注册中国地图
echarts.registerMap('china', chinaMapData)

// 城市选择器
const selectedCity = ref('')
const cityList = ref([])

// 时间相关
const currentDate = ref('')
const currentTime = ref('')
const currentWeekday = ref('')
let timeTimer = null

const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

const updateTime = () => {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  const h = String(now.getHours()).padStart(2, '0')
  const min = String(now.getMinutes()).padStart(2, '0')
  const s = String(now.getSeconds()).padStart(2, '0')

  currentDate.value = `${y}-${m}-${d}`
  currentTime.value = `${h}:${min}:${s}`
  currentWeekday.value = weekDays[now.getDay()]
}

// 预警跑马灯
const alertList = ref([
  { text: '北京市发布重污染天气橙色预警', level: 'orange' },
  { text: '河北省启动应急响应措施', level: 'yellow' },
  { text: '长三角区域PM2.5浓度上升', level: 'blue' }
])
const marqueeOffset = ref(0)
let marqueeTimer = null

// AQI 数据
const currentAqi = ref(0)
const compareValue = ref(0)
const compareClass = computed(() => compareValue.value < 0 ? 'down' : 'up')
const homeStats = ref(null)

// 污染源排名
const sourceRankList = ref([])

// 新闻列表
const newsList = ref([])

// KPI 卡片数据
const kpiCards = ref([
  {
    label: '当前 AQI',
    value: '--',
    unit: '',
    trend: 0,
    color: MORANDI_COLORS.blue,
    iconBg: `linear-gradient(135deg, ${MORANDI_COLORS.blue}, #4DA3FF)`,
    icon: markRaw(DataAnalysis)
  },
  {
    label: 'PM2.5',
    value: '--',
    unit: 'μg/m³',
    trend: 0,
    color: MORANDI_COLORS.green,
    iconBg: `linear-gradient(135deg, ${MORANDI_COLORS.green}, #6ED58E)`,
    icon: markRaw(TrendCharts)
  },
  {
    label: '首要污染物',
    value: '--',
    unit: '',
    trend: 0,
    color: MORANDI_COLORS.orange,
    iconBg: `linear-gradient(135deg, ${MORANDI_COLORS.orange}, #FFB340)`,
    icon: markRaw(PieChart)
  },
  {
    label: '资讯数量',
    value: '0',
    unit: '条',
    trend: 0,
    color: MORANDI_COLORS.purple,
    iconBg: `linear-gradient(135deg, ${MORANDI_COLORS.purple}, #C77DE8)`,
    icon: markRaw(Document)
  }
])

// 四大模块卡片
const moduleCards = ref([
  {
    title: 'AQI 分析',
    desc: '空气质量指数监测',
    path: '/aqi',
    icon: markRaw(DataAnalysis),
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.blue}, #4DA3FF)`,
    value: '--',
    dataLabel: '当前AQI',
    dataColor: MORANDI_COLORS.blue
  },
  {
    title: '污染物分析',
    desc: '六项污染物深度剖析',
    path: '/pollutant',
    icon: markRaw(TrendCharts),
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.green}, #6ED58E)`,
    value: '--',
    dataLabel: '首要污染物',
    dataColor: MORANDI_COLORS.green
  },
  {
    title: '污染源分析',
    desc: '污染来源贡献追踪',
    path: '/source',
    icon: markRaw(PieChart),
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.orange}, #FFB340)`,
    value: '--',
    dataLabel: '机动车贡献',
    dataColor: MORANDI_COLORS.orange
  },
  {
    title: '资讯中心',
    desc: '政策法规与预警信息',
    path: '/article',
    icon: markRaw(Document),
    gradient: `linear-gradient(135deg, ${MORANDI_COLORS.purple}, #C77DE8)`,
    value: '0',
    dataLabel: '最新资讯',
    dataColor: MORANDI_COLORS.purple
  }
])

// 图表实例
const gaugeChartRef = ref(null)
const radarChartRef = ref(null)
const mapChartRef = ref(null)
let gaugeChart = null
let radarChart = null
let mapChart = null

// 气泡显示控制
const showBubble = ref(true)

// AQI 图例数据
const aqiLegend = [
  { name: '优', range: '0-50', color: AQI_COLORS.level1 },
  { name: '良', range: '51-100', color: AQI_COLORS.level2 },
  { name: '轻度', range: '101-150', color: AQI_COLORS.level3 },
  { name: '中度', range: '151-200', color: AQI_COLORS.level4 },
  { name: '重度', range: '201-300', color: AQI_COLORS.level5 },
  { name: '严重', range: '>300', color: AQI_COLORS.level6 }
]

// 切换气泡显示
const toggleBubble = () => {
  showBubble.value = !showBubble.value
  updateMapBubble()
}

// 导航
const navigateTo = (path) => {
  router.push(path)
}

const goArticle = (id) => {
  if (id) router.push(`/article/${id}`)
}

// 下拉菜单
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await logout()
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
    } catch (e) {
      if (e !== 'cancel') console.error(e)
    }
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

// 样式计算
const getRankClass = (index) => index < 3 ? `top-${index + 1}` : ''
const getNewsTagClass = (type) => {
  const map = { '政策': 'policy', '预警': 'warning', '科普': 'science' }
  return map[type] || ''
}

// 加载资讯
const loadNews = async () => {
  try {
    const res = await getLatestArticles(6)
    if (res.code === 200) {
      newsList.value = (res.data || []).map(item => ({
        id: item.id,
        title: item.title,
        type: item.type,
        time: item.createTime ? item.createTime.slice(5, 10) : ''
      }))
      // 更新模块卡片资讯数量
      moduleCards.value[3].value = newsList.value.length.toString()
      kpiCards.value[3].value = newsList.value.length.toString()
    }
  } catch (e) {
    console.error(e)
  }
}

// 加载首页统计数据
const loadHomeStats = async () => {
  try {
    const res = await getHomeStats(selectedCity.value)
    if (res.code === 200 && res.data) {
      homeStats.value = res.data
      currentAqi.value = res.data.currentAqi || 0
      compareValue.value = res.data.compareValue || 0

      // 更新KPI卡片
      kpiCards.value[0].value = currentAqi.value.toString()
      kpiCards.value[0].color = getAqiColor(currentAqi.value)
      kpiCards.value[0].trend = res.data.compareValue || 0

      // 更新模块卡片AQI值
      moduleCards.value[0].value = currentAqi.value.toString()
      moduleCards.value[0].dataColor = getAqiColor(currentAqi.value)
    }
  } catch (e) {
    console.error(e)
  }
}

// 加载城市AQI数据用于地图
const cityAqiList = ref([])
const loadCityAqi = async () => {
  try {
    const res = await getLatestCityAqi()
    if (res.code === 200) {
      cityAqiList.value = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

// 加载污染物数据用于雷达图
const pollutantData = ref([])
const loadPollutantData = async () => {
  try {
    const res = await getPollutantLatest(selectedCity.value)
    if (res.code === 200 && res.data) {
      pollutantData.value = res.data
      // 找出首要污染物（浓度最高的）
      if (res.data.length > 0) {
        const sorted = [...res.data].sort((a, b) => (b.value || b.concentration || 0) - (a.value || a.concentration || 0))
        moduleCards.value[1].value = sorted[0]?.pollutantType || '--'
        kpiCards.value[2].value = sorted[0]?.pollutantType || '--'

        // 更新PM2.5
        const pm25 = res.data.find(d => d.pollutantType === 'PM2.5')
        if (pm25) {
          kpiCards.value[1].value = (pm25.value || pm25.concentration || 0).toString()
        }
      }
    }
  } catch (e) {
    console.error(e)
  }
}

// 加载城市列表
const loadCityList = async () => {
  try {
    const res = await getAllCities()
    if (res.code === 200) {
      cityList.value = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

// 城市切换处理
const handleCityChange = async () => {
  // 重新加载所有数据
  await Promise.all([
    loadHomeStats(),
    loadPollutantData(),
    loadSourceRanking()
  ])

  // 更新图表
  if (gaugeChart) {
    const level = getAqiLevel(currentAqi.value)
    gaugeChart.setOption({
      series: [{
        itemStyle: { color: level.color },
        data: [{ value: currentAqi.value, name: level.name }]
      }]
    })
  }

  if (radarChart) {
    const pollutants = ['PM2.5', 'PM10', 'SO2', 'NO2', 'O3', 'CO']
    const values = pollutantData.value.length > 0
      ? pollutants.map(p => {
          const found = pollutantData.value.find(d => d.pollutantType === p)
          return found ? found.value : 0
        })
      : [50, 40, 20, 35, 30, 15]
    radarChart.setOption({
      series: [{ data: [{ value: values }] }]
    })
  }
}

// 加载污染源排名
const loadSourceRanking = async () => {
  try {
    const res = await getSourceRanking(5, selectedCity.value)
    if (res.code === 200 && res.data) {
      sourceRankList.value = res.data.map(item => ({
        name: item.city || item.sourceType,
        type: item.sourceType,
        value: item.contributionRate,
        unit: '%',
        status: item.contributionRate > 30 ? 'warning' : 'normal'
      }))
      // 更新模块卡片污染源贡献
      if (res.data.length > 0) {
        const vehicle = res.data.find(d => d.sourceType === '机动车尾气')
        if (vehicle) {
          moduleCards.value[2].value = vehicle.contributionRate + '%'
        }
      }
    }
  } catch (e) {
    console.error(e)
  }
}

// 初始化仪表盘
const initGaugeChart = () => {
  if (!gaugeChartRef.value) return
  gaugeChart = echarts.init(gaugeChartRef.value)

  const level = getAqiLevel(currentAqi.value)

  gaugeChart.setOption({
    backgroundColor: 'transparent',
    series: [{
      type: 'gauge',
      center: ['50%', '60%'],
      radius: '85%',
      startAngle: 200,
      endAngle: -20,
      min: 0,
      max: 300,
      splitNumber: 6,
      itemStyle: {
        color: level.color
      },
      progress: {
        show: true,
        width: 16,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: MORANDI_COLORS.blue },
              { offset: 1, color: level.color }
            ]
          }
        }
      },
      pointer: {
        show: true,
        length: '60%',
        width: 5,
        itemStyle: {
          color: 'auto'
        }
      },
      axisLine: {
        lineStyle: {
          width: 16,
          color: [
            [0.167, AQI_COLORS.level1],
            [0.333, AQI_COLORS.level2],
            [0.5, AQI_COLORS.level3],
            [0.667, AQI_COLORS.level4],
            [0.833, AQI_COLORS.level5],
            [1, AQI_COLORS.level6]
          ]
        }
      },
      axisTick: { show: false },
      splitLine: {
        length: 10,
        lineStyle: { width: 2, color: '#A8A8A8' }
      },
      axisLabel: {
        distance: 22,
        color: '#5A5A5A',
        fontSize: 10
      },
      title: {
        show: true,
        offsetCenter: [0, '75%'],
        fontSize: 13,
        color: '#5A5A5A'
      },
      detail: {
        valueAnimation: true,
        formatter: '{value}',
        fontSize: 32,
        fontWeight: '600',
        offsetCenter: [0, '35%'],
        color: level.color
      },
      data: [{
        value: currentAqi.value,
        name: level.name
      }]
    }]
  })
}

// 初始化雷达图
const initRadarChart = () => {
  if (!radarChartRef.value) return
  radarChart = echarts.init(radarChartRef.value)

  const pollutants = ['PM2.5', 'PM10', 'SO2', 'NO2', 'O3', 'CO']
  // 使用API数据或默认值
  const values = pollutantData.value.length > 0
    ? pollutants.map(p => {
        const found = pollutantData.value.find(d => d.pollutantType === p)
        return found ? found.value : 0
      })
    : [50, 40, 20, 35, 30, 15]

  radarChart.setOption({
    backgroundColor: 'transparent',
    radar: {
      indicator: pollutants.map(p => ({ name: p, max: 100 })),
      shape: 'polygon',
      splitNumber: 4,
      axisName: {
        color: '#5A5A5A',
        fontSize: 11
      },
      splitLine: {
        lineStyle: { color: '#D8D8D8' }
      },
      splitArea: {
        areaStyle: {
          color: ['rgba(125, 190, 140, 0.03)', 'rgba(125, 190, 140, 0.08)']
        }
      },
      axisLine: {
        lineStyle: { color: '#D8D8D8' }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: values,
        name: '污染物浓度',
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          color: MORANDI_COLORS.blue,
          width: 2
        },
        areaStyle: {
          color: {
            type: 'radial',
            x: 0.5, y: 0.5, r: 0.5,
            colorStops: [
              { offset: 0, color: 'rgba(107, 163, 190, 0.15)' },
              { offset: 1, color: 'rgba(107, 163, 190, 0.35)' }
            ]
          }
        },
        itemStyle: {
          color: MORANDI_COLORS.blue,
          borderColor: '#fff',
          borderWidth: 1
        }
      }]
    }]
  })
}

// 省份AQI数据（从API加载或使用默认数据）
const provinceAqiData = ref([])

// 城市PM2.5气泡数据（从API加载）
const cityBubbleData = ref([])

// 城市坐标映射
const cityCoords = {
  '北京': [116.46, 39.92], '上海': [121.48, 31.22], '广州': [113.23, 23.16],
  '深圳': [114.07, 22.62], '天津': [117.2, 39.13], '重庆': [106.54, 29.59],
  '成都': [104.06, 30.67], '武汉': [114.31, 30.52], '西安': [108.95, 34.27],
  '杭州': [120.19, 30.26], '南京': [118.78, 32.04], '济南': [117.0, 36.65],
  '郑州': [113.65, 34.76], '长沙': [112.94, 28.23], '沈阳': [123.38, 41.8],
  '哈尔滨': [126.63, 45.75], '石家庄': [114.48, 38.03], '太原': [112.55, 37.87],
  '合肥': [117.27, 31.86], '福州': [119.3, 26.08], '南昌': [115.89, 28.68],
  '昆明': [102.73, 25.04], '贵阳': [106.71, 26.57], '兰州': [103.73, 36.03],
  '乌鲁木齐': [87.68, 43.77], '呼和浩特': [111.65, 40.82], '南宁': [108.33, 22.84],
  '海口': [110.35, 20.02], '银川': [106.27, 38.47], '西宁': [101.74, 36.56],
  '拉萨': [91.11, 29.97], '大连': [121.62, 38.92], '青岛': [120.33, 36.07],
  '宁波': [121.55, 29.87], '厦门': [118.1, 24.46], '苏州': [120.62, 31.32],
  '无锡': [120.29, 31.59], '东莞': [113.75, 23.05], '佛山': [113.12, 23.02],
  '中卫': [105.19, 37.51], '临夏': [103.21, 35.60], '定西': [104.58, 35.58],
  '庆阳': [107.64, 35.73], '平凉': [106.68, 35.54], '天水': [105.72, 34.58],
  '陇南': [104.92, 33.40], '嘉峪关': [98.29, 39.77], '金昌': [102.19, 38.52],
  '白银': [104.14, 36.54], '武威': [102.64, 37.93], '张掖': [100.45, 38.93],
  '酒泉': [98.49, 39.74], '甘南': [102.91, 34.99]
}

// 省份名称映射
const provinceNames = {
  '北京': '北京市', '天津': '天津市', '河北': '河北省', '山西': '山西省',
  '内蒙古': '内蒙古自治区', '辽宁': '辽宁省', '吉林': '吉林省', '黑龙江': '黑龙江省',
  '上海': '上海市', '江苏': '江苏省', '浙江': '浙江省', '安徽': '安徽省',
  '福建': '福建省', '江西': '江西省', '山东': '山东省', '河南': '河南省',
  '湖北': '湖北省', '湖南': '湖南省', '广东': '广东省', '广西': '广西壮族自治区',
  '海南': '海南省', '重庆': '重庆市', '四川': '四川省', '贵州': '贵州省',
  '云南': '云南省', '西藏': '西藏自治区', '陕西': '陕西省', '甘肃': '甘肃省',
  '青海': '青海省', '宁夏': '宁夏回族自治区', '新疆': '新疆维吾尔自治区',
  '香港': '香港特别行政区', '澳门': '澳门特别行政区', '台湾': '台湾省'
}

// 初始化中国地图
const initMapChart = () => {
  if (!mapChartRef.value) return
  mapChart = echarts.init(mapChartRef.value)

  // 使用API数据或默认数据
  const mapProvinceData = provinceAqiData.value.length > 0 ? provinceAqiData.value : []
  const mapBubbleData = cityBubbleData.value.length > 0 ? cityBubbleData.value : []

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#D8D8D8',
      borderWidth: 1,
      borderRadius: 8,
      padding: [10, 14],
      textStyle: {
        color: '#2D2D2D',
        fontSize: 13
      },
      formatter: function(params) {
        if (params.seriesType === 'map') {
          const data = mapProvinceData.find(d => d.name === params.name)
          if (data) {
            const level = getAqiLevel(data.value)
            return `<div style="font-weight:600;margin-bottom:6px;">${params.name}</div>
                    <div style="display:flex;justify-content:space-between;gap:20px;">
                      <span style="color:#5A5A5A">AQI</span>
                      <span style="color:${level.color};font-weight:600;">${data.value} (${level.name})</span>
                    </div>`
          }
        } else if (params.seriesType === 'scatter') {
          const level = getAqiLevel(params.data.aqi)
          return `<div style="font-weight:600;margin-bottom:6px;">${params.data.name}</div>
                  <div style="display:flex;justify-content:space-between;gap:20px;">
                    <span style="color:#5A5A5A">AQI</span>
                    <span style="color:${level.color};font-weight:600;">${params.data.aqi}</span>
                  </div>`
        }
        return params.name
      }
    },
    geo: {
      map: 'china',
      roam: true,
      zoom: 1.2,
      center: [104.5, 36],
      scaleLimit: {
        min: 0.8,
        max: 5
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          color: '#2D2D2D',
          fontSize: 12
        },
        itemStyle: {
          areaColor: '#D8D8D8',
          borderColor: MORANDI_COLORS.blue,
          borderWidth: 2
        }
      },
      itemStyle: {
        areaColor: '#FAFAF8',
        borderColor: '#D8D8D8',
        borderWidth: 1
      },
      select: {
        itemStyle: {
          areaColor: '#D8D8D8'
        }
      }
    },
    series: [
      // 分级着色地图
      {
        name: 'AQI分布',
        type: 'map',
        map: 'china',
        geoIndex: 0,
        data: mapProvinceData.map(item => {
          const level = getAqiLevel(item.value)
          return {
            name: item.name,
            value: item.value,
            itemStyle: {
              areaColor: level.color,
              opacity: 0.7
            }
          }
        }),
        itemStyle: {
          borderColor: 'rgba(255, 255, 255, 0.5)',
          borderWidth: 0.5
        },
        emphasis: {
          itemStyle: {
            opacity: 0.9
          }
        }
      },
      // 气泡散点图
      {
        name: 'PM2.5浓度',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: showBubble.value ? mapBubbleData.map(item => ({
          name: item.name,
          value: [...item.coord, item.aqi],
          aqi: item.aqi,
          pm25: item.pm25 || item.aqi,
          symbolSize: Math.max(item.aqi / 4, 8),
          itemStyle: {
            color: getAqiColor(item.aqi),
            opacity: 0.85
          }
        })) : [],
        symbolSize: function(val) {
          return Math.max(val[2] / 4, 8)
        },
        label: {
          show: true,
          formatter: '{b}',
          position: 'right',
          color: '#8E8E93',
          fontSize: 10,
          distance: 4
        },
        emphasis: {
          scale: 1.5,
          itemStyle: {
            borderColor: '#fff',
            borderWidth: 2
          }
        },
        zlevel: 10
      },
      // 涟漪效果
      {
        name: '重点城市',
        type: 'effectScatter',
        coordinateSystem: 'geo',
        data: showBubble.value ? mapBubbleData.filter(item => item.aqi > 100).map(item => ({
          name: item.name,
          value: [...item.coord, item.aqi],
          itemStyle: {
            color: getAqiColor(item.aqi)
          }
        })) : [],
        symbolSize: 8,
        showEffectOn: 'render',
        rippleEffect: {
          brushType: 'stroke',
          scale: 3,
          period: 4
        },
        zlevel: 11
      }
    ]
  }

  mapChart.setOption(option)
}

// 更新气泡显示
const updateMapBubble = () => {
  if (!mapChart) return

  const mapBubbleData = cityBubbleData.value.length > 0 ? cityBubbleData.value : []

  const bubbleData = showBubble.value ? mapBubbleData.map(item => ({
    name: item.name,
    value: [...item.coord, item.aqi],
    aqi: item.aqi,
    pm25: item.pm25 || item.aqi,
    symbolSize: Math.max(item.aqi / 4, 8),
    itemStyle: {
      color: getAqiColor(item.aqi),
      opacity: 0.85
    }
  })) : []

  const effectData = showBubble.value ? mapBubbleData.filter(item => item.aqi > 100).map(item => ({
    name: item.name,
    value: [...item.coord, item.aqi],
    itemStyle: {
      color: getAqiColor(item.aqi)
    }
  })) : []

  mapChart.setOption({
    series: [
      {}, // 保持地图series不变
      { data: bubbleData },
      { data: effectData }
    ]
  })
}

// 跑马灯动画
const startMarquee = () => {
  const containerWidth = 300
  let offset = containerWidth

  marqueeTimer = setInterval(() => {
    offset -= 1
    if (offset < -400) {
      offset = containerWidth
    }
    marqueeOffset.value = offset
  }, 30)
}

// 窗口大小变化
const handleResize = () => {
  gaugeChart?.resize()
  radarChart?.resize()
  mapChart?.resize()
}

onMounted(async () => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)
  startMarquee()

  // 加载城市列表
  await loadCityList()

  // 加载数据
  await Promise.all([
    loadHomeStats(),
    loadNews(),
    loadCityAqi(),
    loadPollutantData(),
    loadSourceRanking()
  ])

  // 处理城市AQI数据用于地图
  if (cityAqiList.value.length > 0) {
    // 生成气泡数据
    cityBubbleData.value = cityAqiList.value
      .filter(item => cityCoords[item.city])
      .map(item => ({
        name: item.city,
        coord: cityCoords[item.city],
        aqi: item.aqiValue,
        pm25: Math.round(item.aqiValue * 0.8) // 估算PM2.5
      }))

    // 生成省份AQI数据（按省份聚合）
    const provinceMap = {}
    cityAqiList.value.forEach(item => {
      // 简单的城市名到省份映射
      let province = item.city
      if (item.city.includes('北京')) province = '北京市'
      else if (item.city.includes('上海')) province = '上海市'
      else if (item.city.includes('天津')) province = '天津市'
      else if (item.city.includes('重庆')) province = '重庆市'
      // 其他城市归入对应省份，这里简化处理
      else province = item.city + '市'

      if (!provinceMap[province]) {
        provinceMap[province] = { sum: 0, count: 0 }
      }
      provinceMap[province].sum += item.aqiValue
      provinceMap[province].count++
    })

    provinceAqiData.value = Object.entries(provinceMap).map(([name, data]) => ({
      name,
      value: Math.round(data.sum / data.count)
    }))
  }

  setTimeout(() => {
    initGaugeChart()
    initRadarChart()
    initMapChart()
  }, 100)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  if (marqueeTimer) clearInterval(marqueeTimer)
  window.removeEventListener('resize', handleResize)
  gaugeChart?.dispose()
  radarChart?.dispose()
  mapChart?.dispose()
})
</script>

<style scoped>
.dashboard-screen {
  width: 100%;
  min-height: 100vh;
  background: #FAFAF8;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  display: flex;
  flex-direction: column;
}

/* ========== 顶部 Header ========== */
.screen-header {
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  border-bottom: 1px solid #D8D8D8;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 100;
}

.header-left {
  flex: 1;
}

.platform-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.logo-text h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2D2D2D;
  letter-spacing: 0.5px;
}

.logo-text p {
  margin: 2px 0 0 0;
  font-size: 10px;
  color: #5A5A5A;
  letter-spacing: 0.5px;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.city-selector {
  display: flex;
  align-items: center;
}

.city-select {
  width: 140px;
}

.city-select :deep(.el-input__wrapper) {
  background: #FAFAF8;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: none;
  border-radius: 10px;
}

.city-select :deep(.el-input__wrapper:hover) {
  border-color: rgba(107, 163, 190, 0.3);
}

.city-select :deep(.el-input__wrapper:focus-within) {
  border-color: #6BA3BE;
}

.city-select :deep(.el-input__inner) {
  color: #2D2D2D;
  font-size: 14px;
}

.city-select :deep(.el-input__inner::placeholder) {
  color: #8A8A8A;
}

.datetime-display {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 8px 16px;
  background: #FAFAF8;
  border-radius: 12px;
}

.datetime-display .date {
  font-size: 13px;
  color: #5A5A5A;
}

.datetime-display .time {
  font-size: 18px;
  font-weight: 600;
  color: #2D2D2D;
  font-variant-numeric: tabular-nums;
  letter-spacing: 1px;
}

.datetime-display .weekday {
  font-size: 12px;
  color: #5A5A5A;
  padding-left: 8px;
  border-left: 1px solid rgba(0, 0, 0, 0.1);
}

.header-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
}

.weather-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(229, 168, 123, 0.12);
  border-radius: 12px;
}

.weather-icon {
  width: 20px;
  height: 20px;
}

.weather-temp {
  font-size: 14px;
  font-weight: 600;
  color: #E5A87B;
}

.weather-desc {
  font-size: 12px;
  color: #5A5A5A;
}

.alert-marquee {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 260px;
  height: 32px;
  background: rgba(229, 168, 123, 0.08);
  border: 1px solid rgba(229, 168, 123, 0.2);
  border-radius: 12px;
  overflow: hidden;
}

.marquee-label {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 10px;
  height: 100%;
  background: rgba(229, 168, 123, 0.12);
  color: #E5A87B;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.alert-icon {
  width: 14px;
  height: 14px;
}

.marquee-content {
  flex: 1;
  overflow: hidden;
}

.marquee-track {
  display: flex;
  gap: 40px;
  white-space: nowrap;
}

.alert-item {
  font-size: 12px;
  color: #6B6B6B;
}

.alert-item.orange { color: #E5A87B; }
.alert-item.yellow { color: #E5C07B; }
.alert-item.blue { color: #6BA3BE; }

.user-center {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  background: #FAFAF8;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-center:hover {
  background: #F5F5F0;
}

.user-avatar {
  background: linear-gradient(135deg, #6BA3BE, #7DBE8C);
  color: #fff;
  font-weight: 600;
}

.user-name {
  font-size: 13px;
  color: #2D2D2D;
}

/* ========== 主体内容 ========== */
.screen-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
  overflow: auto;
}

/* ========== KPI 卡片区域 ========== */
.kpi-section {
  display: flex;
  gap: 16px;
}

.kpi-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.kpi-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.kpi-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.kpi-icon :deep(svg) {
  width: 24px;
  height: 24px;
  color: #fff;
}

.kpi-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.kpi-label {
  font-size: 13px;
  color: #5A5A5A;
}

.kpi-value {
  font-size: 28px;
  font-weight: 600;
  line-height: 1.2;
}

.kpi-unit {
  font-size: 12px;
  color: #5A5A5A;
}

.kpi-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

.kpi-trend svg {
  width: 14px;
  height: 14px;
}

.kpi-trend.up {
  background: rgba(229, 165, 165, 0.12);
  color: #E5A5A5;
}

.kpi-trend.down {
  background: rgba(125, 190, 140, 0.12);
  color: #7DBE8C;
}

/* ========== 图表区域 ========== */
.charts-section {
  flex: 1;
  display: flex;
  gap: 20px;
  min-height: 400px;
}

/* ========== 左侧面板 ========== */
.panel-left {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-card {
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #2D2D2D;
}

.card-badge {
  font-size: 12px;
  padding: 4px 10px;
  background: #FAFAF8;
  color: #5A5A5A;
  border-radius: 8px;
}

.card-more {
  font-size: 13px;
  color: #6BA3BE;
  cursor: pointer;
  transition: color 0.2s;
}

.card-more:hover {
  color: #5A93AE;
}

/* AQI 仪表盘 */
.aqi-gauge-section {
  flex: 1.2;
  display: flex;
  flex-direction: column;
}

.gauge-container {
  flex: 1;
  min-height: 200px;
}

.gauge-info {
  padding: 12px 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.compare-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.compare-label {
  font-size: 13px;
  color: #5A5A5A;
}

.compare-value {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 15px;
  font-weight: 600;
}

.compare-value.down {
  color: #7DBE8C;
}

.compare-value.up {
  color: #E5A5A5;
}

.trend-icon {
  width: 16px;
  height: 16px;
}

/* 雷达图 */
.radar-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.radar-container {
  flex: 1;
  min-height: 220px;
}

/* ========== 中间地图 ========== */
.center-map {
  flex: 1;
  position: relative;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.map-container {
  width: 100%;
  height: 100%;
}

/* 地图控制按钮 */
.map-controls {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 10;
}

.control-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FFFFFF;
  border: 1px solid #D8D8D8;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.control-btn:hover {
  border-color: #6BA3BE;
  background: #FAFAF8;
}

.control-btn.active {
  border-color: #6BA3BE;
  background: rgba(107, 163, 190, 0.1);
}

.control-btn svg {
  width: 18px;
  height: 18px;
  color: #5A5A5A;
  transition: color 0.2s;
}

.control-btn:hover svg,
.control-btn.active svg {
  color: #6BA3BE;
}

/* 地图图例 */
.map-legend {
  position: absolute;
  right: 16px;
  bottom: 100px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.legend-title {
  font-size: 13px;
  font-weight: 600;
  color: #2D2D2D;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.legend-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  flex-shrink: 0;
}

.legend-text {
  font-size: 12px;
  color: #2D2D2D;
  width: 36px;
}

.legend-range {
  font-size: 11px;
  color: #5A5A5A;
}

/* ========== 右侧面板 ========== */
.panel-right {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 污染源排名 */
.source-rank-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.rank-list {
  flex: 1;
  padding: 8px 12px;
  overflow-y: auto;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 12px;
  transition: background 0.15s;
  cursor: pointer;
}

.rank-item:hover {
  background: #FAFAF8;
}

.rank-num {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  background: #F5F5F0;
  color: #5A5A5A;
}

.rank-num.top-1 { background: linear-gradient(135deg, #E5A5A5, #D49595); color: #fff; }
.rank-num.top-2 { background: linear-gradient(135deg, #E5A87B, #D4986B); color: #fff; }
.rank-num.top-3 { background: linear-gradient(135deg, #E5C07B, #D4B06B); color: #2D2D2D; }

.rank-info {
  flex: 1;
  min-width: 0;
}

.rank-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2D2D2D;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rank-type {
  font-size: 11px;
  color: #5A5A5A;
}

.rank-value {
  text-align: right;
}

.rank-value.warning .value-num {
  color: #E5A87B;
}

.value-num {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #2D2D2D;
}

.value-unit {
  font-size: 11px;
  color: #5A5A5A;
}

/* 资讯列表 */
.news-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-list {
  flex: 1;
  padding: 8px 12px;
  overflow-y: auto;
}

.news-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.15s;
}

.news-item:hover {
  background: #FAFAF8;
}

.news-tag {
  flex-shrink: 0;
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 6px;
  font-weight: 500;
}

.news-tag.policy { background: rgba(107, 163, 190, 0.12); color: #6BA3BE; }
.news-tag.warning { background: rgba(229, 168, 123, 0.12); color: #E5A87B; }
.news-tag.science { background: rgba(125, 190, 140, 0.12); color: #7DBE8C; }

.news-title {
  flex: 1;
  font-size: 13px;
  color: #2D2D2D;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.news-time {
  flex-shrink: 0;
  font-size: 11px;
  color: #5A5A5A;
}

/* ========== 四大模块导航 ========== */
.module-section {
  display: flex;
  gap: 16px;
}

.module-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s;
}

.module-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.module-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.module-icon :deep(svg) {
  width: 22px;
  height: 22px;
  color: #fff;
}

.module-info {
  flex: 1;
}

.module-info h3 {
  margin: 0 0 4px 0;
  font-size: 15px;
  font-weight: 600;
  color: #2D2D2D;
}

.module-info p {
  margin: 0;
  font-size: 12px;
  color: #5A5A5A;
}

.module-data {
  text-align: right;
}

.data-value {
  display: block;
  font-size: 22px;
  font-weight: 600;
}

.data-label {
  font-size: 11px;
  color: #5A5A5A;
}

.module-arrow {
  width: 20px;
  height: 20px;
  color: #9AB5C5;
  flex-shrink: 0;
  transition: all 0.3s;
}

.module-card:hover .module-arrow {
  color: #6BA3BE;
  transform: translateX(4px);
}

/* ========== 响应式 ========== */
@media (max-width: 1400px) {
  .panel-left, .panel-right {
    width: 280px;
  }
}

@media (max-width: 1100px) {
  .charts-section {
    flex-direction: column;
  }

  .panel-left, .panel-right {
    width: 100%;
    flex-direction: row;
  }

  .panel-card {
    flex: 1;
  }

  .center-map {
    min-height: 400px;
  }

  .kpi-section {
    flex-wrap: wrap;
  }

  .kpi-card {
    flex: 1 1 calc(50% - 8px);
  }

  .module-section {
    flex-wrap: wrap;
  }

  .module-card {
    flex: 1 1 calc(50% - 8px);
  }
}
</style>
