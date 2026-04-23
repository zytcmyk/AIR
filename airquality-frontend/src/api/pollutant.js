import request from './request'
import axios from 'axios'

/**
 * 条件查询污染物数据
 */
export function searchPollutant(params) {
  return request({
    url: '/pollutant/search',
    method: 'get',
    params
  })
}

/**
 * 获取某城市某污染物趋势
 */
export function getTrend(city = '北京', pollutantType = 'PM2.5', days = 30) {
  return request({
    url: '/pollutant/trend',
    method: 'get',
    params: { city, pollutantType, days }
  })
}

/**
 * 获取某城市各污染物占比（饼图数据）
 */
export function getPieData(city, startDate, endDate) {
  return request({
    url: '/pollutant/pie',
    method: 'get',
    params: { city, startDate, endDate }
  })
}

/**
 * 获取某城市多个污染物浓度对比
 */
export function getCompare(city, pollutants, date) {
  return request({
    url: '/pollutant/compare',
    method: 'get',
    params: { city, pollutants, date }
  })
}

/**
 * 导出污染物数据到Excel
 */
export function exportPollutant(data) {
  const token = localStorage.getItem('token')
  return axios({
    url: '/api/pollutant/export',
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
 * 获取最新污染物数据
 */
export function getPollutantLatest(city) {
  return request({
    url: '/pollutant/latest',
    method: 'get',
    params: { city }
  })
}

/**
 * 获取污染物统计数据
 */
export function getStats(city) {
  return request({
    url: '/pollutant/stats',
    method: 'get',
    params: { city }
  })
}

/**
 * 获取污染物浓度排名
 */
export function getRanking(limit = 5) {
  return request({
    url: '/pollutant/ranking',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取最新污染物数据列表
 */
export function getLatest(limit = 5) {
  return request({
    url: '/pollutant/latest-list',
    method: 'get',
    params: { limit }
  })
}
