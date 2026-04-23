import request from './request'
import axios from 'axios'

/**
 * 条件查询AQI数据
 */
export function searchAqi(params) {
  return request({
    url: '/aqi/search',
    method: 'get',
    params
  })
}

/**
 * 获取AQI排名
 */
export function getRanking(limit = 10) {
  return request({
    url: '/aqi/ranking',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取某城市AQI趋势
 */
export function getTrend(city = '北京', days = 30) {
  return request({
    url: '/aqi/trend',
    method: 'get',
    params: { city, days }
  })
}

/**
 * 获取空气质量等级分布（饼图数据）
 */
export function getPieData(city, startDate, endDate) {
  return request({
    url: '/aqi/pie',
    method: 'get',
    params: { city, startDate, endDate }
  })
}

/**
 * 多城市对比
 */
export function getMultiCityCompare(cities, date) {
  return request({
    url: '/aqi/compare',
    method: 'get',
    params: { cities, date }
  })
}

/**
 * 导出AQI数据到Excel
 */
export function exportAqi(data) {
  const token = localStorage.getItem('token')
  return axios({
    url: '/api/aqi/export',
    method: 'post',
    data,
    responseType: 'blob',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  }).then(response => {
    return response.data
  })
}

/**
 * 获取首页统计数据
 */
export function getHomeStats(city) {
  return request({
    url: '/aqi/home-stats',
    method: 'get',
    params: { city }
  })
}

/**
 * 获取最新日期各城市AQI数据
 */
export function getLatestCityAqi() {
  return request({
    url: '/aqi/latest-city',
    method: 'get'
  })
}

/**
 * 获取最新日期各城市AQI数据（带坐标）
 */
export function getLatestCityAqiWithCoords() {
  return request({
    url: '/aqi/latest-city-coords',
    method: 'get'
  })
}

/**
 * 获取所有城市列表
 */
export function getAllCities() {
  return request({
    url: '/aqi/cities',
    method: 'get'
  })
}

/**
 * 获取空气质量预警数据
 */
export function getAqiAlerts(city) {
  return request({
    url: '/aqi/alerts',
    method: 'get',
    params: { city }
  })
}

/**
 * 获取历史同比数据
 */
export function getYearCompare(city, year) {
  return request({
    url: '/aqi/year-compare',
    method: 'get',
    params: { city, year }
  })
}

/**
 * 导出CSV格式
 */
export function exportAqiCsv(data) {
  const token = localStorage.getItem('token')
  return axios({
    url: '/api/aqi/export-csv',
    method: 'post',
    data,
    responseType: 'blob',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  }).then(response => {
    return response.data
  })
}
