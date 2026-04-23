import request from './request'

// 用户管理
export function getUserList(params) {
  return request({ url: '/admin/user/list', method: 'get', params })
}

export function getUserById(id) {
  return request({ url: `/admin/user/${id}`, method: 'get' })
}

export function createUser(data) {
  return request({ url: '/admin/user', method: 'post', data })
}

export function updateUser(id, data) {
  return request({ url: `/admin/user/${id}`, method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/admin/user/${id}`, method: 'delete' })
}

export function resetPassword(id) {
  return request({ url: `/admin/user/${id}/reset-password`, method: 'put' })
}

export function updateUserStatus(id, status) {
  return request({ url: `/admin/user/${id}/status`, method: 'put', params: { status } })
}

// AQI管理
export function getAqiList(params) {
  return request({ url: '/admin/aqi/list', method: 'get', params })
}

export function getAqiById(id) {
  return request({ url: `/admin/aqi/${id}`, method: 'get' })
}

export function createAqi(data) {
  return request({ url: '/admin/aqi', method: 'post', data })
}

export function updateAqi(id, data) {
  return request({ url: `/admin/aqi/${id}`, method: 'put', data })
}

export function deleteAqi(id) {
  return request({ url: `/admin/aqi/${id}`, method: 'delete' })
}

export function batchDeleteAqi(ids) {
  return request({ url: '/admin/aqi/batch', method: 'delete', data: ids })
}

export function getAqiAbnormal() {
  return request({ url: '/admin/aqi/abnormal', method: 'get' })
}

// 污染物管理
export function getPollutantList(params) {
  return request({ url: '/admin/pollutant/list', method: 'get', params })
}

export function getPollutantById(id) {
  return request({ url: `/admin/pollutant/${id}`, method: 'get' })
}

export function createPollutant(data) {
  return request({ url: '/admin/pollutant', method: 'post', data })
}

export function updatePollutant(id, data) {
  return request({ url: `/admin/pollutant/${id}`, method: 'put', data })
}

export function deletePollutant(id) {
  return request({ url: `/admin/pollutant/${id}`, method: 'delete' })
}

export function batchDeletePollutant(ids) {
  return request({ url: '/admin/pollutant/batch', method: 'delete', data: ids })
}

export function getPollutantAbnormal() {
  return request({ url: '/admin/pollutant/abnormal', method: 'get' })
}

// 污染源管理
export function getSourceList(params) {
  return request({ url: '/admin/source/list', method: 'get', params })
}

export function getSourceById(id) {
  return request({ url: `/admin/source/${id}`, method: 'get' })
}

export function createSource(data) {
  return request({ url: '/admin/source', method: 'post', data })
}

export function updateSource(id, data) {
  return request({ url: `/admin/source/${id}`, method: 'put', data })
}

export function deleteSource(id) {
  return request({ url: `/admin/source/${id}`, method: 'delete' })
}

export function batchDeleteSource(ids) {
  return request({ url: '/admin/source/batch', method: 'delete', data: ids })
}

export function getSourceAbnormal() {
  return request({ url: '/admin/source/abnormal', method: 'get' })
}

// 文章管理
export function getArticleList(params) {
  return request({ url: '/admin/article/list', method: 'get', params })
}

export function getArticleById(id) {
  return request({ url: `/admin/article/${id}`, method: 'get' })
}

export function createArticle(data) {
  return request({ url: '/admin/article', method: 'post', data })
}

export function updateArticle(id, data) {
  return request({ url: `/admin/article/${id}`, method: 'put', data })
}

export function deleteArticle(id) {
  return request({ url: `/admin/article/${id}`, method: 'delete' })
}

export function publishArticle(id, status) {
  return request({ url: `/admin/article/${id}/publish`, method: 'put', params: { status } })
}
