import request from './request'
import axios from 'axios'

/**
 * 条件查询污染源数据
 */
export function searchPollutionSource(params) {
  return request({
    url: '/source/search',
    method: 'get',
    params
  })
}

/**
 * 获取某城市污染源贡献分布（饼图数据）
 */
export function getDistribution(city, startDate, endDate) {
  return request({
    url: '/source/distribution',
    method: 'get',
    params: { city, startDate, endDate }
  })
}

/**
 * 获取某城市各污染源趋势
 */
export function getTrend(city, sourceType, days = 30) {
  return request({
    url: '/source/trend',
    method: 'get',
    params: { city, sourceType, days }
  })
}

/**
 * 导出污染源数据到Excel
 */
export function exportPollutionSource(data) {
  const token = localStorage.getItem('token')
  return axios({
    url: '/api/source/export',
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
 * 获取污染源排名
 */
export function getSourceRanking(limit = 10, city) {
  return request({
    url: '/source/ranking',
    method: 'get',
    params: { limit, city }
  })
}
