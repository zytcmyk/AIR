import request from './request'

/**
 * 获取文章列表（前台）
 */
export function getArticleList(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params
  })
}

/**
 * 获取文章详情
 */
export function getArticleById(id) {
  return request({
    url: `/article/${id}`,
    method: 'get'
  })
}

/**
 * 获取最新文章
 */
export function getLatestArticles(limit = 5) {
  return request({
    url: '/article/latest',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取置顶文章（重要预警）
 */
export function getTopArticles() {
  return request({
    url: '/article/top',
    method: 'get'
  })
}

/**
 * 获取相关推荐
 */
export function getRelatedArticles(id, limit = 3) {
  return request({
    url: `/article/${id}/related`,
    method: 'get',
    params: { limit }
  })
}
