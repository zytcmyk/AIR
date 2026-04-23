<template>
  <div class="detail-page">
    <article class="article-container" v-loading="loading">
      <template v-if="article">
        <!-- 文章头部 -->
        <header class="article-header">
          <div class="header-tags">
            <span class="type-tag" :class="getTypeClass(article.type)">
              {{ getTypeName(article.type) }}
            </span>
            <span v-if="article.isTop" class="top-tag">置顶</span>
          </div>
          <div class="header-meta">
            <span class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
              {{ formatDate(article.createTime) }}
            </span>
            <span class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              {{ article.viewCount || 0 }} 次阅读
            </span>
          </div>
        </header>

        <!-- 标题 -->
        <h1 class="article-title">{{ article.title }}</h1>

        <!-- 摘要 -->
        <div class="article-summary" v-if="article.summary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="16" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12.01" y2="8"/>
          </svg>
          <span>{{ article.summary }}</span>
        </div>

        <!-- 封面图 -->
        <div class="article-cover" v-if="article.coverImage">
          <el-image :src="article.coverImage" fit="cover" />
        </div>

        <!-- 正文 -->
        <div class="article-content" v-html="article.content"></div>

        <!-- 底部 -->
        <footer class="article-footer">
          <div class="update-time" v-if="article.updateTime">
            最后更新：{{ formatDate(article.updateTime) }}
          </div>

          <div class="share-section">
            <span class="share-label">分享到</span>
            <div class="share-buttons">
              <button class="share-btn wechat" @click="shareToWechat">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 01.213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 00.167-.054l1.903-1.114a.864.864 0 01.717-.098 10.16 10.16 0 002.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178A1.17 1.17 0 014.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 01-1.162 1.178 1.17 1.17 0 01-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 01.598.082l1.584.926a.272.272 0 00.14.045c.134 0 .24-.111.24-.247 0-.06-.023-.12-.038-.177l-.327-1.233a.582.582 0 01-.023-.156.49.49 0 01.201-.398C23.024 18.48 24 16.82 24 14.98c0-3.21-2.931-5.837-7.062-6.122zm-2.036 2.96c.535 0 .969.44.969.982a.976.976 0 01-.969.983.976.976 0 01-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 01-.969.983.976.976 0 01-.969-.983c0-.542.434-.982.97-.982z"/>
                </svg>
                微信
              </button>
              <button class="share-btn weibo" @click="shareToWeibo">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.739 5.443zM9.05 17.219c-.384.616-1.208 884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.194.573zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm7.563-1.224c-.346-.105-.579-.18-.405-.649.388-1.032.428-1.922.006-2.556-.788-1.187-2.93-1.124-5.369-.032 0 0-.769.334-.573-.271.378-1.2.32-2.206-.268-2.787-1.333-1.318-4.869.048-7.902 3.052C1.903 10.487.236 13.35.236 15.815c0 4.709 6.043 7.574 11.95 7.574 7.75 0 12.914-4.5 12.914-8.081 0-2.162-1.821-3.388-3.041-3.659z"/>
                </svg>
                微博
              </button>
              <button class="share-btn copy" @click="copyLink">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M8 5H6a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2v-1M8 5a2 2 0 002 2h2a2 2 0 002-2M8 5a2 2 0 012-2h2a2 2 0 012 2m0 0h2a2 2 0 012 2v3m2 4H10m0 0l3-3m-3 3l3 3"/>
                </svg>
                复制链接
              </button>
            </div>
          </div>
        </footer>

        <!-- 返回按钮 -->
        <div class="action-bar">
          <button class="back-btn" @click="goBack">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M15.75 19.5L8.25 12l7.5-7.5"/>
            </svg>
            返回列表
          </button>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="文章不存在或未发布" />
    </article>

    <!-- 相关推荐 -->
    <aside class="related-section" v-if="relatedArticles.length > 0">
      <div class="section-header">
        <h2>相关推荐</h2>
      </div>
      <div class="related-list">
        <div
          v-for="item in relatedArticles"
          :key="item.id"
          class="related-item"
          @click="goToArticle(item.id)"
        >
          <span class="related-tag" :class="getTypeClass(item.type)">{{ item.type }}</span>
          <span class="related-title">{{ item.title }}</span>
          <span class="related-date">{{ formatDate(item.createTime) }}</span>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getArticleById, getRelatedArticles } from '@/api/article'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const article = ref(null)
const relatedArticles = ref([])

const typeMap = {
  '政策': { name: '环保政策', class: 'tag-policy' },
  '预警': { name: '预警通知', class: 'tag-warning' },
  '科普': { name: '科普文章', class: 'tag-science' }
}

const getTypeName = (type) => typeMap[type]?.name || type
const getTypeClass = (type) => typeMap[type]?.class || ''

const formatDate = (date) => date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''

const loadData = async () => {
  loading.value = true
  try {
    const res = await getArticleById(route.params.id)
    if (res.code === 200) {
      article.value = res.data
      loadRelatedArticles()
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const loadRelatedArticles = async () => {
  try {
    const res = await getRelatedArticles(route.params.id, 5)
    if (res.code === 200) relatedArticles.value = res.data || []
  } catch (e) { console.error(e) }
}

const goBack = () => router.push('/article')
const goToArticle = (id) => router.push(`/article/${id}`)

const shareToWechat = () => ElMessage.info('请使用微信扫描页面二维码分享')

const shareToWeibo = () => {
  const url = window.location.href
  const title = article.value?.title || ''
  window.open(`https://service.weibo.com/share/share.php?url=${encodeURIComponent(url)}&title=${encodeURIComponent(title)}`, '_blank')
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制')
  } catch { ElMessage.error('复制失败') }
}

watch(() => route.params.id, (newId) => {
  if (newId) {
    loadData()
    window.scrollTo(0, 0)
  }
})

onMounted(() => loadData())
</script>

<style scoped>
.detail-page {
  padding: 24px;
  min-height: calc(100vh - 56px);
  background: #F2F2F7;
  max-width: 860px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;
}

/* 文章容器 */
.article-container {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 32px 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 头部 */
.article-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #F2F2F7;
}

.header-tags {
  display: flex;
  gap: 8px;
}

.type-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: -0.01em;
}

.tag-policy {
  background: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}
.tag-warning {
  background: rgba(255, 149, 0, 0.1);
  color: #FF9500;
}
.tag-science {
  background: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.top-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(255, 59, 48, 0.1);
  color: #FF3B30;
  font-weight: 500;
}

.header-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #86868B;
}

.meta-item svg {
  width: 15px;
  height: 15px;
}

/* 标题 */
.article-title {
  margin: 0 0 24px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1D1D1F;
  line-height: 1.25;
  letter-spacing: -0.03em;
}

/* 摘要 */
.article-summary {
  display: flex;
  gap: 14px;
  padding: 18px 22px;
  background: #F2F2F7;
  border-radius: 12px;
  margin-bottom: 28px;
  font-size: 15px;
  color: #6E6E73;
  line-height: 1.6;
}

.article-summary svg {
  width: 20px;
  height: 20px;
  color: #007AFF;
  flex-shrink: 0;
  margin-top: 2px;
}

/* 封面图 */
.article-cover {
  margin-bottom: 28px;
  border-radius: 12px;
  overflow: hidden;
}

.article-cover .el-image {
  width: 100%;
  max-height: 420px;
}

/* 正文 */
.article-content {
  font-size: 16px;
  color: #1D1D1F;
  line-height: 1.75;
  letter-spacing: -0.01em;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin: 28px 0 14px 0;
  font-weight: 600;
  color: #1D1D1F;
  letter-spacing: -0.02em;
}

.article-content :deep(h1) { font-size: 24px; }
.article-content :deep(h2) { font-size: 20px; }
.article-content :deep(h3) { font-size: 18px; }

.article-content :deep(p) {
  margin: 14px 0;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 12px;
  margin: 20px 0;
}

.article-content :deep(pre) {
  background: #1D1D1F;
  padding: 20px;
  border-radius: 12px;
  overflow-x: auto;
  color: #F5F5F7;
}

.article-content :deep(blockquote) {
  border-left: 4px solid #007AFF;
  padding-left: 20px;
  margin: 20px 0;
  color: #6E6E73;
  font-style: italic;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  padding-left: 28px;
  margin: 14px 0;
}

.article-content :deep(li) {
  margin: 8px 0;
}

.article-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  border-radius: 12px;
  overflow: hidden;
}

.article-content :deep(th),
.article-content :deep(td) {
  border: 1px solid #E5E5EA;
  padding: 12px 16px;
  text-align: left;
}

.article-content :deep(th) {
  background: #F2F2F7;
  font-weight: 600;
  color: #1D1D1F;
}

/* 底部 */
.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 36px;
  padding-top: 24px;
  border-top: 1px solid #F2F2F7;
  flex-wrap: wrap;
  gap: 16px;
}

.update-time {
  font-size: 13px;
  color: #86868B;
}

.share-section {
  display: flex;
  align-items: center;
  gap: 14px;
}

.share-label {
  font-size: 14px;
  color: #6E6E73;
  font-weight: 500;
}

.share-buttons {
  display: flex;
  gap: 10px;
}

.share-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  font-size: 13px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.share-btn svg {
  width: 16px;
  height: 16px;
}

.share-btn.wechat {
  background: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.share-btn.wechat:hover {
  background: rgba(52, 199, 89, 0.2);
}

.share-btn.weibo {
  background: rgba(255, 149, 0, 0.1);
  color: #FF9500;
}

.share-btn.weibo:hover {
  background: rgba(255, 149, 0, 0.2);
}

.share-btn.copy {
  background: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.share-btn.copy:hover {
  background: rgba(0, 122, 255, 0.2);
}

/* 返回按钮 */
.action-bar {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #F2F2F7;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  font-size: 14px;
  color: #007AFF;
  background: rgba(0, 122, 255, 0.08);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.back-btn:hover {
  background: rgba(0, 122, 255, 0.15);
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

/* 相关推荐 */
.related-section {
  margin-top: 20px;
  background: #FFFFFF;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-header {
  margin-bottom: 18px;
}

.section-header h2 {
  margin: 0;
  font-size: 17px;
  font-weight: 600;
  color: #1D1D1F;
  letter-spacing: -0.02em;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: #F2F2F7;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.related-item:hover {
  background: #E8E8ED;
}

.related-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 6px;
  flex-shrink: 0;
  font-weight: 500;
}

.related-title {
  flex: 1;
  font-size: 14px;
  color: #1D1D1F;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.related-date {
  font-size: 12px;
  color: #86868B;
  flex-shrink: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-page {
    padding: 16px;
  }

  .article-container {
    padding: 24px 20px;
  }

  .article-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }

  .article-title {
    font-size: 22px;
  }

  .article-footer {
    flex-direction: column;
    align-items: flex-start;
  }

  .share-buttons {
    flex-wrap: wrap;
  }
}
</style>
