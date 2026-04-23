/**
 * 明亮莫兰迪渐变色配置
 * 低饱和度、明亮有生机的莫兰迪配色
 */

// 明亮莫兰迪基础色
export const morandiColors = {
  // 主色调 - 明亮莫兰迪蓝
  blue: '#6BA3BE',
  blueLight: '#8BBDD4',
  blueDark: '#5A93AE',

  // 功能色 - 明亮莫兰迪系
  green: '#7DBE8C',
  greenLight: '#9DD4A5',
  yellow: '#E5C07B',
  yellowLight: '#F0D4A5',
  pink: '#E5A5A5',
  pinkLight: '#F0C5C5',
  purple: '#B08BD4',
  purpleLight: '#C5A5E5',
  orange: '#E5A87B',

  // 中性色
  gray: '#9AB5C5',
  grayLight: '#C5D5E5',
  grayDark: '#5A5A5A',

  // 文字色
  textPrimary: '#2D2D2D',
  textSecondary: '#5A5A5A',
  textMuted: '#8A8A8A',

  // 背景色
  bgBase: '#FAFAF8',
  bgCard: '#FFFFFF',
  bgHover: '#F5F5F0'
}

// 污染物颜色映射 - 明亮莫兰迪风格
export const pollutantColors = {
  'PM2.5': '#6BA3BE',
  'PM10': '#7DBE8C',
  'SO2': '#E5C07B',
  'NO2': '#B08BD4',
  'O3': '#E5A5A5',
  'CO': '#9AB5C5'
}

// 污染源颜色映射 - 明亮莫兰迪风格
export const sourceColors = {
  '工业排放': '#6BA3BE',
  '机动车尾气': '#8BBDD4',
  '扬尘': '#A5B5C5',
  '燃煤': '#E5C07B',
  '其他': '#C5A5D4'
}

// AQI 等级颜色 - 明亮莫兰迪渐变
export const aqiLevelColors = {
  1: '#7DBE8C',  // 优 - 明亮绿
  2: '#6BA3BE',  // 良 - 明亮蓝
  3: '#E5C07B',  // 轻度污染 - 明亮黄
  4: '#E5A87B',  // 中度污染 - 明亮橙
  5: '#E5A5A5',  // 重度污染 - 明亮粉
  6: '#B08BD4'   // 严重污染 - 明亮紫
}

// 渐变色配置 - 明亮莫兰迪渐变
export const gradients = {
  primary: ['#6BA3BE', '#8BBDD4'],
  success: ['#7DBE8C', '#9DD4A5'],
  warning: ['#E5C07B', '#F0D4A5'],
  danger: ['#E5A5A5', '#F0C5C5'],
  info: ['#B08BD4', '#C5A5E5']
}

// 图表通用配置
export const chartBaseOption = {
  backgroundColor: 'transparent',
  tooltip: {
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: 'rgba(0, 0, 0, 0.06)',
    borderWidth: 1,
    textStyle: {
      color: morandiColors.textPrimary,
      fontFamily: '-apple-system, BlinkMacSystemFont, sans-serif'
    },
    extraCssText: 'box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06); border-radius: 10px;'
  },
  legend: {
    textStyle: {
      color: morandiColors.textSecondary,
      fontFamily: '-apple-system, BlinkMacSystemFont, sans-serif'
    }
  }
}

// 生成渐变色
export function createGradient(echarts, color1, color2) {
  return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: color1 },
    { offset: 1, color: color2 }
  ])
}

// 生成面积图渐变
export function createAreaGradient(echarts, color, opacity = 0.25) {
  const r = parseInt(color.slice(1, 3), 16)
  const g = parseInt(color.slice(3, 5), 16)
  const b = parseInt(color.slice(5, 7), 16)
  return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: `rgba(${r}, ${g}, ${b}, ${opacity})` },
    { offset: 1, color: `rgba(${r}, ${g}, ${b}, 0.02)` }
  ])
}

export default {
  morandiColors,
  pollutantColors,
  sourceColors,
  aqiLevelColors,
  gradients,
  chartBaseOption,
  createGradient,
  createAreaGradient
}
