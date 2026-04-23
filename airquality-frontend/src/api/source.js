import request from './request'
import axios from 'axios'

/**
 * 条件查询污染源数据
 */
export function searchSource(params) {
  return request({
    url: '/source/search',
    method: 'get',
    params
  })
}

/**
 * 获取某城市污染源占比（饼图数据）
 */
export function getCitySourcePie(city, startDate, endDate) {
  return request({
    url: '/source/city-pie',
    method: 'get',
    params: { city, startDate, endDate }
  })
}

/**
 * 获取整体污染源分布（饼图数据）
 */
export function getOverallSourcePie(startDate, endDate) {
  return request({
    url: '/source/overall-pie',
    method: 'get',
    params: { startDate, endDate }
  })
}

/**
 * 获取某城市某污染源趋势
 */
export function getSourceTrend(city = '北京', sourceType = '工业排放', days = 30) {
  return request({
    url: '/source/trend',
    method: 'get',
    params: { city, sourceType, days }
  })
}

/**
 * 获取多城市某污染源贡献率对比
 */
export function getMultiCitySourceCompare(cities, sourceType, date) {
  return request({
    url: '/source/compare',
    method: 'get',
    params: { cities, sourceType, date }
  })
}

/**
 * 导出污染源数据到Excel
 */
export function exportSource(data) {
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

/**
 * 获取某城市污染源贡献分布
 */
export function getSourceDistribution(city, startDate, endDate) {
  return request({
    url: '/source/distribution',
    method: 'get',
    params: { city, startDate, endDate }
  })
}

/**
 * 获取按周期聚合的趋势数据
 */
export function getTrendByPeriod(params) {
  return request({
    url: '/source/trend-by-period',
    method: 'get',
    params
  })
}

/**
 * 获取城市污染贡献排名
 */
export function getCityRankingBySource(params) {
  return request({
    url: '/source/ranking-by-source',
    method: 'get',
    params
  })
}

/**
 * 获取污染源相关性数据（模拟数据）
 */
export function getCorrelations() {
  // 返回模拟数据
  return Promise.resolve({
    code: 200,
    data: [
      { source1: '机动车尾气', source2: '工业排放', correlation: 0.82, description: '呈现强正相关' },
      { source1: '机动车尾气', source2: '燃煤', correlation: 0.45, description: '呈现中等正相关' },
      { source1: '机动车尾气', source2: '扬尘', correlation: 0.38, description: '相关性较弱' },
      { source1: '机动车尾气', source2: '其他', correlation: 0.25, description: '相关性较弱' },
      { source1: '工业排放', source2: '燃煤', correlation: 0.65, description: '呈现较强正相关' },
      { source1: '工业排放', source2: '扬尘', correlation: 0.42, description: '呈现中等正相关' },
      { source1: '工业排放', source2: '其他', correlation: 0.35, description: '相关性一般' },
      { source1: '燃煤', source2: '扬尘', correlation: 0.28, description: '相关性较弱' },
      { source1: '燃煤', source2: '其他', correlation: 0.52, description: '呈现中等正相关' },
      { source1: '扬尘', source2: '其他', correlation: 0.18, description: '相关性很弱' }
    ]
  })
}

/**
 * 获取污染源详情
 */
export function getSourceDetails(city) {
  return request({
    url: '/source/city-pie',
    method: 'get',
    params: { city }
  })
}

/**
 * 获取所有污染源类型
 */
export function getSourceTypes() {
  return Promise.resolve({
    code: 200,
    data: [
      { id: 1, typeName: '机动车尾气', typeCode: 'vehicle', color: '#FF7A45' },
      { id: 2, typeName: '工业排放', typeCode: 'industry', color: '#3B82F6' },
      { id: 3, typeName: '燃煤', typeCode: 'coal', color: '#8B8B8B' },
      { id: 4, typeName: '扬尘', typeCode: 'dust', color: '#D4A76A' },
      { id: 5, typeName: '其他', typeCode: 'other', color: '#A78BFA' }
    ]
  })
}

/**
 * 获取多城市污染源结构对比数据
 */
export function getCitySourceStructureCompare(cities, startDate, endDate) {
  return request({
    url: '/source/city-structure-compare',
    method: 'get',
    params: { cities: cities.join(','), startDate, endDate }
  })
}
