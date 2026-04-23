<template>
  <div class="article-page">
    <!-- 置顶预警 -->
    <div class="top-alerts" v-if="topArticles.length > 0">
      <div
        v-for="article in topArticles"
        :key="article.id"
        class="alert-item"
        @click="goDetail(article.id)"
      >
        <div class="alert-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 9v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="alert-content">
          <span class="alert-title">{{ article.title }}</span>
          <span class="alert-date">{{ formatDate(article.createTime) }}</span>
        </div>
        <span class="alert-arrow">›</span>
      </div>
    </div>

    <!-- 分类筛选 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <button
          v-for="tab in typeTabs"
          :key="tab.value"
          class="filter-tab"
          :class="{ active: currentType === tab.value }"
          @click="handleTypeChange(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="article-list" v-loading="loading">
      <div
        v-for="article in articleList"
        :key="article.id"
        class="article-card"
        @click="goDetail(article.id)"
      >
        <!-- 封面图 -->
        <div class="card-cover" v-if="article.coverImage">
          <el-image :src="article.coverImage" fit="cover" />
        </div>

        <!-- 卡片内容 -->
        <div class="card-body" :class="{ 'no-cover': !article.coverImage }">
          <div class="card-header">
            <div class="tags">
              <span class="type-tag" :class="getTypeClass(article.type)">
                {{ getTypeName(article.type) }}
              </span>
              <span v-if="article.isTop" class="top-tag">置顶</span>
            </div>
            <span class="post-date">{{ formatDate(article.createTime) }}</span>
          </div>

          <h3 class="card-title">{{ article.title }}</h3>

          <p class="card-summary">{{ article.summary || article.title }}</p>

          <div class="card-footer">
            <div class="stats">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              <span>{{ article.viewCount || 0 }}</span>
            </div>
            <span class="read-more">
              阅读全文
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M8.25 4.5l7.5 7.5-7.5 7.5"/>
              </svg>
            </span>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && articleList.length === 0" description="暂无相关资讯" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="pagination.total > 0">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getArticleList, getTopArticles } from '@/api/article'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const articleList = ref([])
const topArticles = ref([])
const currentType = ref('')

const pagination = reactive({ page: 1, size: 10, total: 0 })

const typeTabs = [
  { label: '全部资讯', value: '' },
  { label: '环保政策', value: '政策' },
  { label: '预警通知', value: '预警' },
  { label: '科普文章', value: '科普' }
]

const typeMap = {
  '政策': { name: '环保政策', class: 'tag-policy' },
  '预警': { name: '预警通知', class: 'tag-warning' },
  '科普': { name: '科普文章', class: 'tag-science' }
}

const getTypeName = (type) => typeMap[type]?.name || type
const getTypeClass = (type) => typeMap[type]?.class || ''

const formatDate = (date) => date ? dayjs(date).format('MM-DD') : ''

const loadTopArticles = async () => {
  try {
    const res = await getTopArticles()
    if (res.code === 200) topArticles.value = res.data || []
  } catch (e) { console.error(e) }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getArticleList({
      type: currentType.value,
      page: pagination.page,
      size: pagination.size
    })
    if (res.code === 200) {
      articleList.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleTypeChange = (type) => {
  currentType.value = type
  pagination.page = 1
  loadData()
}

const goDetail = (id) => router.push(`/article/${id}`)

onMounted(() => {
  loadTopArticles()
  loadData()
})
</script>

<style scoped>
.article-page {
  padding: 24px;
  min-height: calc(100vh - 56px);
  background: #FAFAF8;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;
}

/* 置顶预警 */
.top-alerts {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 22px;
  background: #FFFFFF;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.alert-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(107, 163, 190, 0.12);
  border-color: rgba(107, 163, 190, 0.15);
}

.alert-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #E5C07B 0%, #E5A87B 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.alert-icon svg {
  width: 24px;
  height: 24px;
  color: #FFFFFF;
}

.alert-content {
  flex: 1;
  min-width: 0;
}

.alert-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #2D2D2D;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: -0.01em;
}

.alert-date {
  font-size: 13px;
  color: #8A8A8A;
  margin-top: 3px;
  display: block;
}

.alert-arrow {
  color: #C5C5C5;
  font-size: 22px;
  font-weight: 500;
  transition: all 0.2s;
}

.alert-item:hover .alert-arrow {
  transform: translateX(4px);
  color: #6BA3BE;
}

/* 筛选区 */
.filter-section {
  margin-bottom: 20px;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  padding: 8px;
  background: #FFFFFF;
  border-radius: 14px;
  width: fit-content;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.filter-tab {
  padding: 10px 22px;
  font-size: 14px;
  color: #5A5A5A;
  background: transparent;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s ease;
  font-weight: 500;
}

.filter-tab:hover {
  color: #2D2D2D;
  background: #F5F5F0;
}

.filter-tab.active {
  background: linear-gradient(135deg, #6BA3BE 0%, #7DBE8C 100%);
  color: #FFFFFF;
  box-shadow: 0 4px 12px rgba(107, 163, 190, 0.25);
}

/* 文章列表 */
.article-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-card {
  display: flex;
  gap: 20px;
  padding: 22px;
  background: #FFFFFF;
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 36px rgba(107, 163, 190, 0.12);
  border-color: rgba(107, 163, 190, 0.12);
}

.card-cover {
  width: 180px;
  height: 120px;
  border-radius: 14px;
  overflow: hidden;
  flex-shrink: 0;
}

.card-cover .el-image {
  width: 100%;
  height: 100%;
}

.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.card-body.no-cover {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.tags {
  display: flex;
  gap: 8px;
}

.type-tag {
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.tag-policy {
  background: rgba(107, 163, 190, 0.12);
  color: #6BA3BE;
}
.tag-warning {
  background: rgba(229, 192, 123, 0.15);
  color: #D4A85A;
}
.tag-science {
  background: rgba(125, 190, 140, 0.12);
  color: #5AA868;
}

.top-tag {
  font-size: 12px;
  padding: 5px 12px;
  border-radius: 8px;
  background: rgba(229, 165, 165, 0.15);
  color: #D48585;
  font-weight: 600;
}

.post-date {
  font-size: 13px;
  color: #8A8A8A;
  font-weight: 400;
}

.card-title {
  margin: 0 0 10px 0;
  font-size: 17px;
  font-weight: 600;
  color: #2D2D2D;
  line-height: 1.4;
  letter-spacing: -0.02em;
}

.article-card:hover .card-title {
  color: #6BA3BE;
}

.card-summary {
  flex: 1;
  margin: 0;
  font-size: 14px;
  color: #5A5A5A;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.stats {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #8A8A8A;
  font-size: 13px;
}

.stats svg {
  width: 16px;
  height: 16px;
}

.read-more {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6BA3BE;
  font-weight: 600;
}

.read-more svg {
  width: 14px;
  height: 14px;
  transition: transform 0.2s;
}

.article-card:hover .read-more svg {
  transform: translateX(4px);
}

/* 分页 */
.pagination-wrapper {
  margin-top: 28px;
  display: flex;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .article-page {
    padding: 16px;
  }

  .article-card {
    flex-direction: column;
  }

  .card-cover {
    width: 100%;
    height: 160px;
  }

  .filter-tabs {
    width: 100%;
    overflow-x: auto;
  }

  .filter-tab {
    white-space: nowrap;
  }
}
</style>
