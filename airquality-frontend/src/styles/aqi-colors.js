/**
 * AQI 空气质量标准色彩配置
 * 严格遵循国家空气质量标准颜色
 */

// AQI 六级标准色
export const AQI_COLORS = {
  level1: '#00E400',  // 优 (0-50) - 鲜绿色
  level2: '#FFFF00',  // 良 (51-100) - 黄色
  level3: '#FF7E00',  // 轻度污染 (101-150) - 橙色
  level4: '#FF0000',  // 中度污染 (151-200) - 红色
  level5: '#99004C',  // 重度污染 (201-300) - 紫色
  level6: '#7E0023'   // 严重污染 (>300) - 褐红色
}

// AQI 等级配置
export const AQI_LEVELS = [
  { name: '优', range: [0, 50], color: AQI_COLORS.level1, bgColor: 'rgba(0, 228, 0, 0.15)' },
  { name: '良', range: [51, 100], color: AQI_COLORS.level2, bgColor: 'rgba(255, 255, 0, 0.15)' },
  { name: '轻度污染', range: [101, 150], color: AQI_COLORS.level3, bgColor: 'rgba(255, 126, 0, 0.15)' },
  { name: '中度污染', range: [151, 200], color: AQI_COLORS.level4, bgColor: 'rgba(255, 0, 0, 0.15)' },
  { name: '重度污染', range: [201, 300], color: AQI_COLORS.level5, bgColor: 'rgba(153, 0, 76, 0.15)' },
  { name: '严重污染', range: [301, 500], color: AQI_COLORS.level6, bgColor: 'rgba(126, 0, 35, 0.15)' }
]

// 大屏主题色 - 莫兰迪风格
export const THEME_COLORS = {
  bg: '#F5F3F0',           // 莫兰迪米白背景
  bgDark: '#EBE8E4',       // 深米色
  panel: 'rgba(255, 255, 255, 0.95)',        // 面板背景
  panelLight: 'rgba(255, 255, 255, 0.9)',   // 浅面板
  border: 'rgba(107, 163, 190, 0.2)',  // 莫兰迪蓝边框
  borderLight: 'rgba(107, 163, 190, 0.1)',
  neon: '#6BA3BE',         // 莫兰迪蓝点缀
  neonGlow: 'rgba(107, 163, 190, 0.3)',
  text: '#2D2D2D',         // 主文字
  textSecondary: '#5A5A5A', // 次要文字
  textMuted: '#8A8A8A',    // 弱化文字
  success: '#7DBE8C',
  warning: '#D4A76A',
  danger: '#D48B8B'
}

// 污染物颜色
export const POLLUTANT_COLORS = {
  'PM2.5': '#FF6B6B',
  'PM10': '#FFA94D',
  'SO2': '#74C0FC',
  'NO2': '#B197FC',
  'O3': '#69DB7C',
  'CO': '#FFD43B'
}

// 污染源颜色 - 莫兰迪风格
export const SOURCE_COLORS = {
  '机动车尾气': '#6BA3BE',
  '工业排放': '#7DBE8C',
  '燃煤': '#B08BD4',
  '扬尘': '#D4A76A',
  '其他': '#8A8A8A'
}

// 根据 AQI 值获取等级信息
export function getAqiLevel(value) {
  if (value <= 50) return AQI_LEVELS[0]
  if (value <= 100) return AQI_LEVELS[1]
  if (value <= 150) return AQI_LEVELS[2]
  if (value <= 200) return AQI_LEVELS[3]
  if (value <= 300) return AQI_LEVELS[4]
  return AQI_LEVELS[5]
}

// 根据 AQI 值获取颜色
export function getAqiColor(value) {
  return getAqiLevel(value).color
}

// ECharts 渐变色生成
export function createGradient(color, direction = 'vertical') {
  const [x, y, x2, y2] = direction === 'vertical' ? [0, 0, 0, 1] : [0, 0, 1, 0]
  return {
    type: 'linear',
    x, y, x2, y2,
    colorStops: [
      { offset: 0, color },
      { offset: 1, color: 'transparent' }
    ]
  }
}

export default {
  AQI_COLORS,
  AQI_LEVELS,
  THEME_COLORS,
  POLLUTANT_COLORS,
  SOURCE_COLORS,
  getAqiLevel,
  getAqiColor,
  createGradient
}
