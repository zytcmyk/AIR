<template>
  <div class="source-analysis-page">
    <!-- 顶部导航栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <button class="back-btn" @click="goHome">
          <svg viewBox="0 0 24 24" fill="none">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M9 22V12h6v10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>返回首页</span>
        </button>
        <div class="page-title">
          <h1>污染源分析</h1>
          <p>来源追踪 · 贡献分析 · 趋势预测</p>
        </div>
      </div>
      <div class="header-right">
        <el-select v-model="selectedCity" placeholder="选择城市" filterable clearable @change="handleCityChange" class="city-select">
          <el-option v-for="city in cityList" :key="city" :label="city" :value="city" />
        </el-select>
        <el-dropdown trigger="click" class="export-dropdown">
          <el-button type="primary" size="small" class="export-btn">
            <svg viewBox="0 0 24 24" fill="none" class="btn-icon">
              <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>导出报告</span>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="exportToPDF">
                <svg viewBox="0 0 24 24" fill="none" class="dropdown-icon"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" stroke="currentColor" stroke-width="2"/><path d="M14 2v6h6M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2"/></svg>
                导出PDF报告
              </el-dropdown-item>
              <el-dropdown-item @click="exportToExcel">
                <svg viewBox="0 0 24 24" fill="none" class="dropdown-icon"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z" stroke="currentColor" stroke-width="2"/><rect x="8" y="13" width="8" height="6" stroke="currentColor" stroke-width="2"/><path d="M8 13h8v6H8z" stroke="currentColor" stroke-width="2"/></svg>
                导出Excel数据
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div class="current-time">
          <span class="date">{{ currentDate }}</span>
          <span class="time">{{ currentTime }}</span>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="dashboard-body">
      <!-- 左侧面板 -->
      <aside class="panel-left">
        <!-- 筛选条件 -->
        <div class="chart-card filter-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              筛选条件
            </span>
            <el-tooltip content="支持多条件组合筛选，点击查询按钮应用筛选" placement="top">
              <span class="help-icon">
                <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M9 9a3 3 0 115.12 2.12c-.52.52-.84.96-.96 1.44-.12.48-.16.96-.16 1.44M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </span>
            </el-tooltip>
          </div>
          <div class="filter-body">
            <!-- 快捷时间筛选 -->
            <div class="filter-group">
              <label>快捷时间</label>
              <div class="quick-time-btns">
                <button
                  v-for="qt in quickTimeOptions"
                  :key="qt.value"
                  class="quick-time-btn"
                  :class="{ active: activeQuickTime === qt.value }"
                  @click="applyQuickTime(qt.value)"
                >{{ qt.label }}</button>
              </div>
            </div>
            <div class="filter-group">
              <label>污染源类型</label>
              <div class="source-tags">
                <span
                  v-for="type in sourceTypes"
                  :key="type"
                  class="source-tag"
                  :class="{ active: selectedSourceTypes.includes(type) }"
                  :style="selectedSourceTypes.includes(type) ? { background: SOURCE_COLORS[type], borderColor: SOURCE_COLORS[type] } : {}"
                  @click="toggleSourceType(type)"
                >{{ type }}</span>
              </div>
            </div>
            <div class="filter-group">
              <label>日期范围</label>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                size="small"
                class="date-picker"
                @change="activeQuickTime = ''"
              />
            </div>
            <div class="filter-actions">
              <el-button type="primary" size="small" @click="handleSearch" :loading="loading">查询</el-button>
              <el-button size="small" @click="handleReset">重置</el-button>
            </div>
          </div>
        </div>

        <!-- 污染源贡献占比 -->
        <div class="chart-card pie-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M21.21 15.21A10 10 0 118 2.83" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M22 12A10 10 0 0012 2v10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              污染源贡献占比
            </span>
            <el-tooltip content="展示各污染源对空气质量的贡献比例，点击扇区可查看详情" placement="top">
              <span class="help-icon">
                <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M9 9a3 3 0 115.12 2.12c-.52.52-.84.96-.96 1.44-.12.48-.16.96-.16 1.44M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </span>
            </el-tooltip>
          </div>
          <div class="card-body">
            <div v-if="chartLoading.pie" class="skeleton-wrapper">
              <div class="skeleton-circle"></div>
              <div class="skeleton-legend">
                <div class="skeleton-legend-item" v-for="i in 5" :key="i">
                  <div class="skeleton-dot"></div>
                  <div class="skeleton-text"></div>
                </div>
              </div>
            </div>
            <div v-else ref="pieChartRef" class="chart-container" :class="{ 'chart-fade-in': chartReady.pie }"></div>
          </div>
        </div>

        <!-- 城市排行 -->
        <div class="chart-card rank-card">
          <div class="card-header">
            <span class="card-title">
              <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                <path d="M8 21h8M12 17v4M7 4h10l-1 9H8L7 4z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="14" r="2" stroke="currentColor" stroke-width="2"/>
              </svg>
              城市污染源排行
            </span>
            <el-tooltip content="按污染贡献率排序的城市排名，点击城市可快速切换查看" placement="top">
              <span class="help-icon">
                <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M9 9a3 3 0 115.12 2.12c-.52.52-.84.96-.96 1.44-.12.48-.16.96-.16 1.44M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </span>
            </el-tooltip>
          </div>
          <div class="card-body rank-body">
            <template v-if="chartLoading.rank">
              <div class="skeleton-rank-item" v-for="i in 5" :key="i">
                <div class="skeleton-rank-num"></div>
                <div class="skeleton-rank-content">
                  <div class="skeleton-text-sm"></div>
                  <div class="skeleton-bar"></div>
                </div>
              </div>
            </template>
            <template v-else>
              <div
                v-for="(item, index) in rankingData"
                :key="item.city"
                class="rank-item"
                :class="{ 'rank-item-fade-in': chartReady.rank }"
                :style="{ animationDelay: `${index * 80}ms` }"
                @click="selectCity(item.city)"
              >
                <span class="rank-num" :class="getRankClass(index)">{{ index + 1 }}</span>
                <div class="rank-info">
                  <span class="rank-city">{{ item.city }}</span>
                  <span class="rank-source">{{ item.mainSource }}</span>
                </div>
                <div class="rank-bar-wrap">
                  <div class="rank-bar" :style="{ width: item.value + '%', background: SOURCE_COLORS[item.mainSource] || THEME_COLORS.neon }"></div>
                </div>
                <span class="rank-value">{{ item.value }}%</span>
              </div>
            </template>
          </div>
        </div>
      </aside>

      <!-- 右侧主内容区域 -->
      <section class="panel-main">
        <!-- 数据总览 - 一行显示 -->
        <div class="stats-row">
          <!-- 预警状态指示 -->
          <div v-if="alertList.length > 0" class="alert-indicator" @click="showAlertPanel = true">
            <div class="alert-icon-pulse"></div>
            <svg viewBox="0 0 24 24" fill="none" class="alert-icon-svg">
              <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 9v4M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span class="alert-count">{{ alertList.length }}</span>
            <span class="alert-text">预警</span>
          </div>
          <div class="stat-card-item" @click="showSourceInfo('mainSource')">
            <div class="stat-icon-box" style="--icon-gradient: linear-gradient(135deg, #E8A06C, #D4895E);">
              <svg viewBox="0 0 24 24" fill="none"><path d="M13 10V3L4 14h7v7l9-11h-7z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="stat-content-box">
              <span class="stat-label-text">首要污染源</span>
              <span class="stat-value-text">
                {{ mainSource }}
                <span v-if="isSourceOverThreshold(mainSource)" class="warning-badge" title="贡献率超过预警阈值">
                  <svg viewBox="0 0 24 24" fill="none"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" stroke="currentColor" stroke-width="2"/><path d="M12 9v4M12 17h.01" stroke="currentColor" stroke-width="2"/></svg>
                </span>
              </span>
            </div>
          </div>
          <div class="stat-card-item" @click="showSourceInfo('avgContribution')">
            <div class="stat-icon-box" style="--icon-gradient: linear-gradient(135deg, #7BA3B8, #5C8A9E);">
              <svg viewBox="0 0 24 24" fill="none"><path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="stat-content-box">
              <span class="stat-label-text">平均贡献率</span>
              <span class="stat-value-text" :class="{ 'value-warning': parseFloat(avgContribution) > WARNING_THRESHOLD }">
                {{ avgContribution }}%
                <span v-if="parseFloat(avgContribution) > WARNING_THRESHOLD" class="warning-badge" title="贡献率超过预警阈值">
                  <svg viewBox="0 0 24 24" fill="none"><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" stroke="currentColor" stroke-width="2"/><path d="M12 9v4M12 17h.01" stroke="currentColor" stroke-width="2"/></svg>
                </span>
              </span>
            </div>
          </div>
          <div class="stat-card-item" @click="showSourceInfo('cityCount')">
            <div class="stat-icon-box" style="--icon-gradient: linear-gradient(135deg, #8FAF8F, #7A9E7A);">
              <svg viewBox="0 0 24 24" fill="none"><path d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="stat-content-box">
              <span class="stat-label-text">监测城市</span>
              <span class="stat-value-text">{{ cityCount }}<span class="stat-unit-text">个</span></span>
            </div>
          </div>
          <!-- 导出按钮（移动端显示） -->
          <div class="stat-card-item export-stat-btn" @click="showExportDialog = true">
            <div class="stat-icon-box" style="--icon-gradient: linear-gradient(135deg, #6BA3BE, #5A93AE);">
              <svg viewBox="0 0 24 24" fill="none"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
            </div>
            <div class="stat-content-box">
              <span class="stat-label-text">数据导出</span>
              <span class="stat-value-text">导出报告</span>
            </div>
          </div>
        </div>

        <!-- 主内容区 -->
        <div class="main-content-area">
          <!-- 左栏 -->
          <div class="column-left-main">
            <!-- 贡献率趋势分析 -->
            <div class="chart-card trend-card-main">
              <div class="card-header">
                <span class="card-title">贡献率趋势分析</span>
                <div class="card-tools">
                  <el-radio-group v-model="trendPeriod" size="small" @change="loadTrendData">
                    <el-radio-button value="week">周</el-radio-button>
                    <el-radio-button value="month">月</el-radio-button>
                    <el-radio-button value="quarter">季度</el-radio-button>
                  </el-radio-group>
                  <el-tooltip content="展示污染源贡献率随时间的变化趋势，点击图例可高亮显示" placement="top">
                    <span class="help-icon">
                      <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M9 9a3 3 0 115.12 2.12c-.52.52-.84.96-.96 1.44-.12.48-.16.96-.16 1.44M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </span>
                  </el-tooltip>
                </div>
              </div>
              <div class="trend-legend-main">
                <div v-for="type in selectedSourceTypes" :key="type" class="legend-item-main" @click="toggleHighlight(type)" :class="{ dim: highlightedSource && highlightedSource !== type }">
                  <span class="legend-dot-main" :style="{ background: getSourceGradient(type) }"></span>
                  <span class="legend-name-main">{{ type }}</span>
                </div>
              </div>
              <div class="card-body trend-body-main">
                <div v-if="chartLoading.trend" class="skeleton-wrapper skeleton-chart">
                  <div class="skeleton-lines">
                    <div class="skeleton-line" v-for="i in 5" :key="i" :style="{ width: `${100 - i * 10}%`, marginLeft: `${i * 8}%` }"></div>
                  </div>
                </div>
                <div v-else ref="trendChartRef" class="chart-container trend-chart-main" :class="{ 'chart-fade-in': chartReady.trend }"></div>
              </div>
            </div>

            <!-- 污染源详情 -->
            <div class="chart-card detail-card-main">
              <div class="card-header">
                <span class="card-title">
                  <svg viewBox="0 0 24 24" fill="none" class="title-icon">
                    <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  污染源详情
                </span>
                <el-tooltip content="点击卡片可查看该污染源的详细说明" placement="top">
                  <span class="help-icon">
                    <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M9 9a3 3 0 115.12 2.12c-.52.52-.84.96-.96 1.44-.12.48-.16.96-.16 1.44M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  </span>
                </el-tooltip>
              </div>
              <div class="detail-body-main">
                <div
                  v-for="(item, idx) in sourceDetails"
                  :key="item.type"
                  class="detail-item-main"
                  :class="{ 'detail-item-fade-in': chartReady.detail }"
                  :style="{ '--source-color': SOURCE_COLORS[item.type], animationDelay: `${idx * 100}ms` }"
                  @click="showSourceTypeDetail(item.type)"
                >
                  <div class="detail-header-main">
                    <div class="detail-icon-main" :style="{ background: getSourceGradient(item.type) }">
                      <component :is="getSourceIcon(item.type)" />
                    </div>
                    <div class="detail-info-main">
                      <span class="detail-name-main">{{ item.type }}</span>
                      <div class="detail-value-row">
                        <span class="detail-value-main">{{ item.value }}%</span>
                        <span class="detail-trend-pill" :class="item.trend?.direction">
                          <svg v-if="item.trend?.direction === 'up'" viewBox="0 0 24 24" fill="none" class="trend-arrow">
                            <path d="M7 17l5-5 5 5M7 7l5 5 5-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          </svg>
                          <svg v-else-if="item.trend?.direction === 'down'" viewBox="0 0 24 24" fill="none" class="trend-arrow">
                            <path d="M7 7l5 5 5-5M7 17l5-5 5 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          </svg>
                          <svg v-else viewBox="0 0 24 24" fill="none" class="trend-arrow">
                            <path d="M5 12h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                          </svg>
                          <span>{{ item.trend?.value }}</span>
                        </span>
                      </div>
                    </div>
                  </div>
                  <div class="detail-tags-main">
                    <span v-for="tag in item.tags" :key="tag" class="detail-tag-main">{{ tag }}</span>
                  </div>
                  <div class="detail-industries-main">
                    <span class="industry-label-main">主要来源:</span>
                    <span v-for="(ind, idx2) in item.industries" :key="ind" class="industry-item-main">
                      {{ ind }}<span v-if="idx2 < item.industries.length - 1">、</span>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右栏 -->
          <div class="column-right-main">
            <!-- 城市对比柱状图 -->
            <div class="chart-card compare-card-main">
              <div class="card-header">
                <span class="card-title">城市污染源结构对比</span>
                <div class="card-tools">
                  <el-select v-model="compareCities" multiple collapse-tags collapse-tags-tooltip placeholder="选择城市" size="small" class="compare-select" @change="loadCompareData">
                    <el-option v-for="city in cityList" :key="city" :label="city" :value="city" />
                  </el-select>
                  <el-tooltip content="对比不同城市的污染源结构差异，支持多选城市" placement="top">
                    <span class="help-icon">
                      <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M9 9a3 3 0 115.12 2.12c-.52.52-.84.96-.96 1.44-.12.48-.16.96-.16 1.44M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    </span>
                  </el-tooltip>
                </div>
              </div>
              <div class="card-body">
                <div v-if="chartLoading.compare" class="skeleton-wrapper skeleton-chart">
                  <div class="skeleton-bars">
                    <div class="skeleton-bar-stack" v-for="i in 4" :key="i">
                      <div class="skeleton-bar-item" v-for="j in 5" :key="j"></div>
                    </div>
                  </div>
                </div>
                <div v-else ref="compareChartRef" class="chart-container compare-chart-main" :class="{ 'chart-fade-in': chartReady.compare }"></div>
              </div>
            </div>

            <!-- 相关性热力图 -->
            <div class="chart-card correlation-card-main">
              <div class="card-header">
                <span class="card-title">污染源协同效应</span>
                <el-tooltip content="展示不同污染源之间的相关性系数，数值越接近1表示相关性越强" placement="top">
                  <span class="help-icon">
                    <svg viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/><path d="M9 9a3 3 0 115.12 2.12c-.52.52-.84.96-.96 1.44-.12.48-.16.96-.16 1.44M12 17h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                  </span>
                </el-tooltip>
              </div>
              <div class="card-body">
                <div v-if="chartLoading.heatmap" class="skeleton-wrapper skeleton-chart">
                  <div class="skeleton-grid">
                    <div class="skeleton-grid-cell" v-for="i in 25" :key="i"></div>
                  </div>
                </div>
                <div v-else ref="heatmapChartRef" class="chart-container heatmap-chart-main" :class="{ 'chart-fade-in': chartReady.heatmap }"></div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 污染源类型说明弹窗 -->
    <el-dialog
      v-model="sourceTypeDialogVisible"
      :title="currentSourceTypeDetail?.name"
      width="500px"
      class="source-type-dialog"
    >
      <div class="source-detail-content" v-if="currentSourceTypeDetail">
        <div class="source-detail-header">
          <div class="source-detail-icon" :style="{ background: currentSourceTypeDetail.gradient }">
            <component :is="getSourceIcon(currentSourceTypeDetail.name)" />
          </div>
          <div class="source-detail-meta">
            <div class="source-detail-label">{{ currentSourceTypeDetail.category }}</div>
            <div class="source-detail-desc">{{ currentSourceTypeDetail.description }}</div>
          </div>
        </div>
        <div class="source-detail-section">
          <h4>主要来源</h4>
          <ul class="source-detail-list">
            <li v-for="source in currentSourceTypeDetail.sources" :key="source">{{ source }}</li>
          </ul>
        </div>
        <div class="source-detail-section">
          <h4>防控措施</h4>
          <ul class="source-detail-list">
            <li v-for="measure in currentSourceTypeDetail.measures" :key="measure">{{ measure }}</li>
          </ul>
        </div>
        <div class="source-detail-section">
          <h4>健康影响</h4>
          <p class="source-detail-impact">{{ currentSourceTypeDetail.healthImpact }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 预警面板 -->
    <el-dialog
      v-model="showAlertPanel"
      title="污染源预警提示"
      width="520px"
      class="alert-dialog"
    >
      <div class="alert-panel-content">
        <div class="alert-header-info">
          <svg viewBox="0 0 24 24" fill="none" class="alert-header-icon">
            <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" stroke="currentColor" stroke-width="2"/>
            <path d="M12 9v4M12 17h.01" stroke="currentColor" stroke-width="2"/>
          </svg>
          <div class="alert-header-text">
            <div class="alert-header-title">预警阈值说明</div>
            <div class="alert-header-desc">当污染源贡献率超过 <strong>{{ WARNING_THRESHOLD }}%</strong> 时触发预警</div>
          </div>
        </div>
        <div class="alert-list">
          <div v-for="alert in alertList" :key="alert.type" class="alert-item" :style="{ '--alert-color': SOURCE_COLORS[alert.type] }">
            <div class="alert-item-icon">
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" stroke="currentColor" stroke-width="2"/>
                <path d="M12 9v4M12 17h.01" stroke="currentColor" stroke-width="2"/>
              </svg>
            </div>
            <div class="alert-item-content">
              <div class="alert-item-title">{{ alert.type }}</div>
              <div class="alert-item-desc">贡献率 <strong>{{ alert.value }}%</strong> 超过预警阈值</div>
            </div>
            <div class="alert-item-level" :class="alert.level">
              {{ alert.level === 'high' ? '高风险' : alert.level === 'medium' ? '中风险' : '低风险' }}
            </div>
          </div>
          <div v-if="alertList.length === 0" class="alert-empty">
            <svg viewBox="0 0 24 24" fill="none">
              <path d="M22 11.08V12a10 10 0 11-5.93-9.14" stroke="currentColor" stroke-width="2"/>
              <path d="M22 4L12 14.01l-3-3" stroke="currentColor" stroke-width="2"/>
            </svg>
            <span>当前无预警，空气质量状况良好</span>
          </div>
        </div>
        <div class="alert-suggestions" v-if="alertList.length > 0">
          <h4>建议措施</h4>
          <ul>
            <li>加强对高贡献污染源的监测与管控</li>
            <li>根据污染源类型采取针对性减排措施</li>
            <li>关注天气变化，适时启动应急预案</li>
          </ul>
        </div>
      </div>
    </el-dialog>

    <!-- 导出对话框 -->
    <el-dialog
      v-model="showExportDialog"
      title="导出报告"
      width="480px"
      class="export-dialog"
    >
      <div class="export-dialog-content">
        <div class="export-options">
          <div class="export-option-group">
            <label>导出格式</label>
            <el-radio-group v-model="exportFormat">
              <el-radio value="pdf">PDF报告</el-radio>
              <el-radio value="excel">Excel数据</el-radio>
            </el-radio-group>
          </div>
          <div class="export-option-group">
            <label>导出内容</label>
            <el-checkbox-group v-model="exportContent">
              <el-checkbox value="pie">污染源贡献占比</el-checkbox>
              <el-checkbox value="trend">贡献率趋势分析</el-checkbox>
              <el-checkbox value="compare">城市污染源对比</el-checkbox>
              <el-checkbox value="heatmap">污染源协同效应</el-checkbox>
              <el-checkbox value="detail">污染源详情</el-checkbox>
              <el-checkbox value="ranking">城市排行榜</el-checkbox>
            </el-checkbox-group>
          </div>
          <div class="export-option-group">
            <label>筛选条件</label>
            <div class="export-filter-summary">
              <div class="filter-summary-item">
                <span class="filter-label">城市：</span>
                <span class="filter-value">{{ selectedCity }}</span>
              </div>
              <div class="filter-summary-item">
                <span class="filter-label">日期范围：</span>
                <span class="filter-value">{{ dateRange?.[0] }} 至 {{ dateRange?.[1] }}</span>
              </div>
              <div class="filter-summary-item">
                <span class="filter-label">污染源类型：</span>
                <span class="filter-value">{{ selectedSourceTypes.join('、') }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="export-preview">
          <div class="preview-title">导出预览</div>
          <div class="preview-items">
            <div v-for="item in exportContent" :key="item" class="preview-item">
              <svg viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/><path d="M3 9h18M9 21V9" stroke="currentColor" stroke-width="2"/></svg>
              <span>{{ getExportItemName(item) }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showExportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleExport" :loading="exporting">
          <svg viewBox="0 0 24 24" fill="none" class="btn-icon">
            <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4M7 10l5 5 5-5M12 15V3" stroke="currentColor" stroke-width="2"/>
          </svg>
          确认导出
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, h, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { THEME_COLORS, SOURCE_COLORS } from '@/styles/aqi-colors'
import { getCitySourcePie, getTrendByPeriod, getCityRankingBySource, getCorrelations, getSourceDistribution, getCitySourceStructureCompare } from '@/api/source'

const router = useRouter()

// 预警阈值配置
const WARNING_THRESHOLD = 40 // 贡献率超过40%触发预警

// 城市列表
const cityList = ref(['北京', '上海', '广州', '深圳', '成都', '武汉', '西安', '乌鲁木齐', '南京', '杭州'])
const selectedCity = ref('北京')

// 污染源类型
const sourceTypes = ['机动车尾气', '工业排放', '燃煤', '扬尘', '其他']
const selectedSourceTypes = ref([...sourceTypes])

// 日期范围
const dateRange = ref(['2024-01-01', '2024-06-30'])

// 加载状态
const loading = ref(false)

// 图表加载状态
const chartLoading = ref({
  pie: true,
  trend: true,
  compare: true,
  heatmap: true,
  rank: true,
  detail: true
})

// 图表就绪状态
const chartReady = ref({
  pie: false,
  trend: false,
  compare: false,
  heatmap: false,
  rank: false,
  detail: false
})

// 预警相关
const showAlertPanel = ref(false)
const alertList = computed(() => {
  return sourceDetails.value
    .filter(item => item.value > WARNING_THRESHOLD)
    .map(item => ({
      type: item.type,
      value: item.value,
      level: item.value > 50 ? 'high' : item.value > 45 ? 'medium' : 'low'
    }))
    .sort((a, b) => b.value - a.value)
})

// 判断污染源是否超过阈值
const isSourceOverThreshold = (sourceType) => {
  const detail = sourceDetails.value.find(d => d.type === sourceType)
  return detail ? detail.value > WARNING_THRESHOLD : false
}

// 导出相关
const showExportDialog = ref(false)
const exportFormat = ref('pdf')
const exportContent = ref(['pie', 'trend', 'compare', 'heatmap', 'detail', 'ranking'])
const exporting = ref(false)

const exportItemNames = {
  pie: '污染源贡献占比',
  trend: '贡献率趋势分析',
  compare: '城市污染源结构对比',
  heatmap: '污染源协同效应',
  detail: '污染源详情',
  ranking: '城市排行榜'
}

const getExportItemName = (key) => exportItemNames[key] || key

// 导出PDF报告
const exportToPDF = async () => {
  showExportDialog.value = true
  exportFormat.value = 'pdf'
}

// 导出Excel数据
const exportToExcel = async () => {
  showExportDialog.value = true
  exportFormat.value = 'excel'
}

// 执行导出
const handleExport = async () => {
  if (exportContent.value.length === 0) {
    ElMessage.warning('请至少选择一项导出内容')
    return
  }

  exporting.value = true

  try {
    // 模拟导出过程
    await new Promise(resolve => setTimeout(resolve, 1500))

    if (exportFormat.value === 'pdf') {
      // 生成PDF报告内容
      const reportContent = generatePDFReport()
      downloadFile(reportContent, `污染源分析报告_${selectedCity.value}_${currentDate.value}.txt`, 'text/plain')
      ElMessage.success('PDF报告导出成功')
    } else {
      // 生成Excel数据
      const excelContent = generateExcelData()
      downloadFile(excelContent, `污染源数据_${selectedCity.value}_${currentDate.value}.csv`, 'text/csv')
      ElMessage.success('Excel数据导出成功')
    }

    showExportDialog.value = false
  } catch (e) {
    console.error(e)
    ElMessage.error('导出失败，请重试')
  } finally {
    exporting.value = false
  }
}

// 生成PDF报告内容
const generatePDFReport = () => {
  const lines = []
  lines.push('========================================')
  lines.push('        污染源分析报告')
  lines.push('========================================')
  lines.push('')
  lines.push(`报告生成时间：${currentDate.value} ${currentTime.value}`)
  lines.push(`分析城市：${selectedCity.value}`)
  lines.push(`日期范围：${dateRange.value?.[0]} 至 ${dateRange.value?.[1]}`)
  lines.push(`污染源类型：${selectedSourceTypes.value.join('、')}`)
  lines.push('')
  lines.push('----------------------------------------')
  lines.push('一、数据总览')
  lines.push('----------------------------------------')
  lines.push(`首要污染源：${mainSource.value}`)
  lines.push(`平均贡献率：${avgContribution.value}%`)
  lines.push(`监测城市数：${cityCount.value}个`)
  lines.push('')

  if (exportContent.value.includes('detail') && sourceDetails.value.length > 0) {
    lines.push('----------------------------------------')
    lines.push('二、污染源详情')
    lines.push('----------------------------------------')
    sourceDetails.value.forEach((item, idx) => {
      lines.push(`${idx + 1}. ${item.type}`)
      lines.push(`   贡献率：${item.value}%`)
      lines.push(`   标签：${item.tags?.join('、') || '无'}`)
      lines.push(`   主要来源：${item.industries?.join('、') || '无'}`)
      lines.push(`   趋势：${item.trend?.desc || '无变化'}`)
      lines.push('')
    })
  }

  if (exportContent.value.includes('ranking') && rankingData.value.length > 0) {
    lines.push('----------------------------------------')
    lines.push('三、城市污染源排行')
    lines.push('----------------------------------------')
    rankingData.value.forEach((item, idx) => {
      lines.push(`${idx + 1}. ${item.city} - ${item.mainSource} (${item.value}%)`)
    })
    lines.push('')
  }

  if (alertList.value.length > 0) {
    lines.push('----------------------------------------')
    lines.push('四、预警提示')
    lines.push('----------------------------------------')
    lines.push(`预警阈值：${WARNING_THRESHOLD}%`)
    alertList.value.forEach(alert => {
      lines.push(`⚠ ${alert.type} 贡献率 ${alert.value}% 超过预警阈值`)
    })
    lines.push('')
  }

  lines.push('========================================')
  lines.push('        报告结束')
  lines.push('========================================')

  return lines.join('\n')
}

// 生成Excel数据
const generateExcelData = () => {
  const rows = []

  // 表头
  rows.push(['污染源类型', '贡献率(%)', '趋势', '主要来源'])

  // 数据行
  sourceDetails.value.forEach(item => {
    rows.push([
      item.type,
      item.value,
      item.trend?.value || '0%',
      item.industries?.join('; ') || ''
    ])
  })

  // 空行
  rows.push([])
  rows.push(['城市排行'])
  rows.push(['排名', '城市', '主要污染源', '贡献率(%)'])

  rankingData.value.forEach((item, idx) => {
    rows.push([idx + 1, item.city, item.mainSource, item.value])
  })

  return rows.map(row => row.join(',')).join('\n')
}

// 下载文件
const downloadFile = (content, filename, mimeType) => {
  const blob = new Blob([content], { type: mimeType })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

// 快捷时间选项
const quickTimeOptions = [
  { label: '今日', value: 'today' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本季度', value: 'quarter' }
]
const activeQuickTime = ref('')

// 时间显示
const currentDate = ref('')
const currentTime = ref('')
let timeTimer = null

const updateTime = () => {
  const now = new Date()
  currentDate.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
  currentTime.value = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`
}

// 返回首页
const goHome = () => {
  router.push('/home')
}

// 图表引用
const pieChartRef = ref(null)
const trendChartRef = ref(null)
const compareChartRef = ref(null)
const heatmapChartRef = ref(null)

// 图表实例
let pieChart = null
let trendChart = null
let compareChart = null
let heatmapChart = null

// 数据
const rankingData = ref([])
const sourceDetails = ref([])
const trendPeriod = ref('month')
const highlightedSource = ref(null)
const compareCities = ref(['北京', '上海', '广州', '成都'])

// 统计数据
const mainSource = ref('机动车尾气')
const avgContribution = ref('42.5')
const cityCount = ref(10)

// 污染源类型说明弹窗
const sourceTypeDialogVisible = ref(false)
const currentSourceTypeDetail = ref(null)

// 污染源详细说明数据
const sourceTypeDetails = {
  '机动车尾气': {
    name: '机动车尾气',
    category: '移动源污染',
    description: '由汽油、柴油等燃料在机动车发动机内燃烧产生的废气，是城市大气污染的主要来源之一。',
    sources: ['汽油车尾气排放（约占52%）', '柴油车尾气排放（约占35%）', '摩托车及其他车辆（约占13%）'],
    measures: ['推广新能源汽车，提高电动化比例', '优化交通管理，减少拥堵怠速', '加强车辆尾气检测与维护', '实施限行措施，减少高峰期排放'],
    healthImpact: '长期暴露可导致呼吸系统疾病、心血管疾病，增加肺癌风险，对儿童和老年人影响更为显著。',
    gradient: 'linear-gradient(135deg, #6BA3BE, #8BBDD4)'
  },
  '工业排放': {
    name: '工业排放',
    category: '固定源污染',
    description: '工业生产过程中产生的废气排放，包括燃烧废气和工艺废气，是重要的点源污染。',
    sources: ['钢铁冶炼（约占28%）', '化工生产（约占24%）', '水泥建材（约占18%）', '其他工业（约占30%）'],
    measures: ['升级环保设施，提高处理效率', '推进清洁生产，减少源头排放', '实施超低排放改造', '加强在线监测，确保达标排放'],
    healthImpact: '工业废气中的重金属和有机物可导致慢性中毒，长期暴露增加呼吸系统癌症风险。',
    gradient: 'linear-gradient(135deg, #7DBE8C, #9DD4A5)'
  },
  '燃煤': {
    name: '燃煤',
    category: '能源污染',
    description: '煤炭燃烧产生的烟尘和有害气体，是传统的空气污染来源，冬季贡献尤为突出。',
    sources: ['民用散煤燃烧（约占45%）', '工业燃煤锅炉（约占38%）', '电力燃煤发电（约占17%）'],
    measures: ['推进煤改气、煤改电工程', '淘汰落后燃煤设施', '推广清洁煤炭技术', '加强冬季燃煤管控'],
    healthImpact: '燃煤产生的PM2.5和SO2可导致慢性支气管炎、肺气肿，加重哮喘症状。',
    gradient: 'linear-gradient(135deg, #B08BD4, #C5A5E5)'
  },
  '扬尘': {
    name: '扬尘',
    category: '开放源污染',
    description: '建筑施工、道路扬尘、裸露土地等产生的颗粒物，是城市PM10的重要来源。',
    sources: ['建筑施工工地（约占54%）', '道路扬尘（约占46%）'],
    measures: ['施工现场围挡和洒水抑尘', '渣土车密闭运输', '道路机械化清扫', '裸露土地绿化覆盖'],
    healthImpact: '扬尘颗粒可刺激呼吸道，加重慢性呼吸系统疾病，影响能见度和城市景观。',
    gradient: 'linear-gradient(135deg, #E5C07B, #F0D4A5)'
  },
  '其他': {
    name: '其他',
    category: '综合污染源',
    description: '包括生物质燃烧、餐饮油烟、农业活动等分散污染源，需要综合管控。',
    sources: ['生物质燃烧（约占53%）', '餐饮油烟排放（约占47%）'],
    measures: ['禁止露天焚烧秸秆', '餐饮业安装油烟净化设施', '推广清洁能源替代', '加强源头监管执法'],
    healthImpact: '生物质燃烧产生的多环芳烃具有致癌性，餐饮油烟含有多种有害物质。',
    gradient: 'linear-gradient(135deg, #9AB5C5, #C5D5E5)'
  }
}

// 污染源渐变色配置 - 莫兰迪色系
const SOURCE_GRADIENTS = {
  '机动车尾气': 'linear-gradient(135deg, #6BA3BE, #8BBDD4)',
  '工业排放': 'linear-gradient(135deg, #7DBE8C, #9DD4A5)',
  '燃煤': 'linear-gradient(135deg, #B08BD4, #C5A5E5)',
  '扬尘': 'linear-gradient(135deg, #E5C07B, #F0D4A5)',
  '其他': 'linear-gradient(135deg, #9AB5C5, #C5D5E5)'
}

// 获取污染源渐变色
const getSourceGradient = (type) => {
  return SOURCE_GRADIENTS[type] || 'linear-gradient(135deg, #8E9EAB, #6B7B8B)'
}

// 获取污染源图标
const getSourceIcon = (type) => {
  const icons = {
    '机动车尾气': h('svg', { viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9L18 10l-1.4-4.2c-.2-.5-.7-.8-1.2-.8H8.6c-.5 0-1 .3-1.2.8L6 10l-2.5 1.1C2.7 11.3 2 12.1 2 13v3c0 .6.4 1 1 1h2', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
      h('circle', { cx: '7', cy: '17', r: '2', stroke: 'currentColor', 'stroke-width': '2' }),
      h('circle', { cx: '17', cy: '17', r: '2', stroke: 'currentColor', 'stroke-width': '2' })
    ]),
    '工业排放': h('svg', { viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M2 20h20M4 20V10l8-6v6l8-6v16', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
      h('path', { d: 'M8 14v6M12 12v8M16 14v6', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round' })
    ]),
    '燃煤': h('svg', { viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M12 2c-4 4-6 8-6 11a6 6 0 0012 0c0-3-2-7-6-11z', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
      h('path', { d: 'M12 22v-4', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round' })
    ]),
    '扬尘': h('svg', { viewBox: '0 0 24 24', fill: 'none' }, [
      h('path', { d: 'M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
      h('circle', { cx: '9', cy: '7', r: '4', stroke: 'currentColor', 'stroke-width': '2' }),
      h('path', { d: 'M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
    ]),
    '其他': h('svg', { viewBox: '0 0 24 24', fill: 'none' }, [
      h('circle', { cx: '12', cy: '12', r: '10', stroke: 'currentColor', 'stroke-width': '2' }),
      h('path', { d: 'M12 8v4l3 3', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
    ])
  }
  return icons[type] || icons['其他']
}

// 显示污染源类型详情
const showSourceTypeDetail = (type) => {
  currentSourceTypeDetail.value = sourceTypeDetails[type] || null
  sourceTypeDialogVisible.value = true
}

// 显示数据说明
const showSourceInfo = (type) => {
  const infoMap = {
    'mainSource': '当前城市贡献率最高的污染源类型，反映该地区的主要污染来源。',
    'avgContribution': '所选污染源类型的平均贡献率，用于衡量其对空气质量的整体影响程度。',
    'cityCount': '当前系统监测的城市数量，覆盖全国主要城市的空气质量监测站。'
  }
  ElMessage.info(infoMap[type] || '')
}

// 应用快捷时间
const applyQuickTime = (type) => {
  activeQuickTime.value = type
  const now = new Date()
  let startDate, endDate

  const formatDate = (d) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`

  switch (type) {
    case 'today':
      startDate = endDate = formatDate(now)
      break
    case 'week':
      const weekStart = new Date(now)
      weekStart.setDate(now.getDate() - now.getDay() + 1)
      dateRange.value = [formatDate(weekStart), formatDate(now)]
      break
    case 'month':
      dateRange.value = [`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`, formatDate(now)]
      break
    case 'quarter':
      const quarterMonth = Math.floor(now.getMonth() / 3) * 3
      dateRange.value = [`${now.getFullYear()}-${String(quarterMonth + 1).padStart(2, '0')}-01`, formatDate(now)]
      break
  }

  if (type === 'today') {
    dateRange.value = [startDate, endDate]
  }

  handleSearch()
}

// 获取排名样式
const getRankClass = (index) => index < 3 ? `top-${index + 1}` : ''

// 切换污染源类型
const toggleSourceType = (type) => {
  const index = selectedSourceTypes.value.indexOf(type)
  if (index > -1) {
    if (selectedSourceTypes.value.length > 1) {
      selectedSourceTypes.value.splice(index, 1)
    }
  } else {
    selectedSourceTypes.value.push(type)
  }
}

// 高亮污染源
const toggleHighlight = (type) => {
  highlightedSource.value = highlightedSource.value === type ? null : type
}

// 查询
const handleSearch = async () => {
  loading.value = true
  // 重置图表加载状态
  Object.keys(chartLoading.value).forEach(key => {
    chartLoading.value[key] = true
    chartReady.value[key] = false
  })

  await loadAllData()
  await loadTrendData()

  loading.value = false
  ElMessage.success('数据已更新')
}

// 重置
const handleReset = () => {
  selectedSourceTypes.value = [...sourceTypes]
  dateRange.value = ['2024-01-01', '2024-06-30']
  activeQuickTime.value = ''
  handleSearch()
}

// 城市切换
const handleCityChange = async () => {
  await loadAllData()
}

// 选择城市
const selectCity = (city) => {
  selectedCity.value = city
  handleCityChange()
}

// 加载所有数据
const loadAllData = async () => {
  await Promise.all([
    loadPieData(),
    loadTrendData(),
    loadCompareData(),
    loadRankingData(),
    loadCorrelationData(),
    loadSourceDetails()
  ])
}

// 加载饼图数据
const loadPieData = async () => {
  chartLoading.value.pie = true
  chartReady.value.pie = false
  let chartData = []
  try {
    const res = await getCitySourcePie(selectedCity.value, dateRange.value?.[0], dateRange.value?.[1])
    if (res.code === 200 && res.data) {
      const list = res.data.data || res.data.sourceList || []
      chartData = list.map(d => ({
        sourceType: d.name || d.sourceType,
        rate: d.value || d.rate
      }))
    }
  } catch (e) {
    console.error(e)
  }
  // 先关闭loading让DOM元素渲染，再初始化图表
  chartLoading.value.pie = false
  await new Promise(resolve => setTimeout(resolve, 50))
  initPieChart(chartData)
  setTimeout(() => { chartReady.value.pie = true }, 100)
}

// 初始化饼图
const initPieChart = (data) => {
  if (!pieChartRef.value) return
  if (pieChart) {
    pieChart.dispose()
  }
  pieChart = echarts.init(pieChartRef.value)

  let pieData = []
  if (data.length > 0) {
    pieData = data
      .filter(d => selectedSourceTypes.value.includes(d.sourceType))
      .map(d => ({
        name: d.sourceType,
        value: d.rate,
        itemStyle: { color: SOURCE_COLORS[d.sourceType] || THEME_COLORS.neon }
      }))
  } else {
    const baseValues = {
      '机动车尾气': 42.5,
      '工业排放': 25.3,
      '燃煤': 12.8,
      '扬尘': 13.4,
      '其他': 6.0
    }
    pieData = selectedSourceTypes.value.map(type => ({
      name: type,
      value: baseValues[type] || 20,
      itemStyle: { color: SOURCE_COLORS[type] }
    }))
  }

  pieChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.98)',
      borderColor: 'rgba(0, 0, 0, 0.06)',
      borderWidth: 1,
      borderRadius: 12,
      padding: [12, 16],
      textStyle: { color: '#2D2D2D', fontSize: 13, fontWeight: 500 },
      formatter: '{b}<br/>贡献率: <strong>{c}%</strong>'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { color: '#5A5A5A', fontSize: 12, fontWeight: 500 },
      itemGap: 12,
      itemWidth: 12,
      itemHeight: 12,
      icon: 'circle'
    },
    series: [{
      type: 'pie',
      radius: ['45%', '72%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 3,
        shadowBlur: 8,
        shadowColor: 'rgba(0, 0, 0, 0.08)'
      },
      label: { show: false },
      emphasis: {
        scale: true,
        scaleSize: 8,
        label: { show: true, fontSize: 15, fontWeight: 'bold', color: '#2D2D2D' }
      },
      labelLine: { show: false },
      data: pieData,
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: (idx) => idx * 100
    }]
  })
}

// 加载趋势数据
const loadTrendData = async () => {
  chartLoading.value.trend = true
  chartReady.value.trend = false
  let chartData = []
  try {
    const res = await getTrendByPeriod({
      city: selectedCity.value,
      period: trendPeriod.value,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    })
    if (res.code === 200) {
      chartData = (res.data || []).filter(item =>
        selectedSourceTypes.value.includes(item.sourceType)
      )
    }
  } catch (e) {
    console.error(e)
  }
  // 先关闭loading让DOM元素渲染，再初始化图表
  chartLoading.value.trend = false
  await new Promise(resolve => setTimeout(resolve, 50))
  initTrendChart(chartData)
  setTimeout(() => { chartReady.value.trend = true }, 100)
}

// 初始化趋势图
const initTrendChart = (data) => {
  if (!trendChartRef.value) return
  if (trendChart) {
    trendChart.dispose()
  }
  trendChart = echarts.init(trendChartRef.value)

  const colors = {
    '机动车尾气': '#6BA3BE',
    '工业排放': '#7DBE8C',
    '燃煤': '#B08BD4',
    '扬尘': '#E5C07B',
    '其他': '#9AB5C5'
  }

  if (data && data.length > 0) {
    const allPeriods = [...new Set(data.map(item => item.period))].sort()
    const periodLabels = allPeriods.map(p => {
      if (trendPeriod.value === 'week') return p.replace('2024-', '')
      else if (trendPeriod.value === 'month') return p.replace('2024-', '') + '月'
      else return p.replace('2024-', '')
    })

    const groupedData = {}
    data.forEach(item => {
      if (!groupedData[item.sourceType]) groupedData[item.sourceType] = {}
      groupedData[item.sourceType][item.period] = item.avgContributionRate
    })

    const seriesData = selectedSourceTypes.value.map((type) => {
      const typeData = groupedData[type] || {}
      const values = allPeriods.map(period => typeData[period] || 0)
      const color = colors[type] || '#8E9EAB'

      return {
        name: type,
        type: 'line',
        smooth: true,
        data: values,
        lineStyle: { width: 3, color: color },
        itemStyle: { color: color },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: color + '40' },
              { offset: 0.6, color: color + '15' },
              { offset: 1, color: 'transparent' }
            ]
          }
        },
        symbol: 'circle',
        symbolSize: 8,
        showSymbol: false,
        emphasis: {
          focus: 'series',
          itemStyle: { shadowBlur: 12, shadowColor: color }
        },
        animationDuration: 1500,
        animationEasing: 'cubicOut'
      }
    })

    trendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        borderColor: 'rgba(0, 0, 0, 0.06)',
        borderWidth: 1,
        borderRadius: 12,
        padding: [12, 16],
        textStyle: { color: '#2D2D2D', fontSize: 13 },
        axisPointer: { type: 'cross', crossStyle: { color: 'rgba(0,0,0,0.1)' } }
      },
      legend: { show: false },
      grid: { left: 60, right: 30, top: 20, bottom: 40, containLabel: false },
      xAxis: {
        type: 'category',
        data: periodLabels,
        axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
        axisLabel: { color: '#5A5A5A', fontSize: 12, interval: 0, margin: 12 },
        axisTick: { show: false }
      },
      yAxis: {
        type: 'value',
        name: '贡献率(%)',
        nameTextStyle: { color: '#8A8A8A', fontSize: 11, padding: [0, 0, 0, -10] },
        axisLine: { show: false },
        axisLabel: { color: '#8A8A8A', fontSize: 11 },
        splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.04)', type: 'dashed' } }
      },
      series: seriesData
    })
  } else {
    let periods = []
    if (trendPeriod.value === 'week') periods = ['W01', 'W02', 'W03', 'W04', 'W05', 'W06']
    else if (trendPeriod.value === 'month') periods = ['1月', '2月', '3月', '4月', '5月', '6月']
    else if (trendPeriod.value === 'quarter') periods = ['Q1', 'Q2']

    const baseValues = {
      '机动车尾气': [36.3, 37.5, 40.5, 42.5, 44.5, 46.5],
      '工业排放': [27.5, 26.5, 23.5, 22.5, 21.5, 20.5],
      '燃煤': [17.7, 18.1, 13.4, 11.4, 10.4, 9.4],
      '扬尘': [12.9, 12.4, 17.1, 18.1, 18.1, 18.1],
      '其他': [5.6, 5.5, 5.5, 5.5, 5.5, 5.5]
    }

    const seriesData = selectedSourceTypes.value.map((type) => {
      const values = baseValues[type] || periods.map(() => 20)
      const color = colors[type] || '#8E9EAB'

      return {
        name: type,
        type: 'line',
        smooth: true,
        data: values.slice(0, periods.length),
        lineStyle: { width: 3, color: color },
        itemStyle: { color: color },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: color + '40' },
              { offset: 0.6, color: color + '15' },
              { offset: 1, color: 'transparent' }
            ]
          }
        },
        symbol: 'circle',
        symbolSize: 8,
        showSymbol: false,
        emphasis: {
          focus: 'series',
          itemStyle: { shadowBlur: 12, shadowColor: color }
        },
        animationDuration: 1500,
        animationEasing: 'cubicOut'
      }
    })

    trendChart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        borderColor: 'rgba(0, 0, 0, 0.06)',
        borderWidth: 1,
        borderRadius: 12,
        padding: [12, 16],
        textStyle: { color: '#2D2D2D', fontSize: 13 },
        axisPointer: { type: 'cross', crossStyle: { color: 'rgba(0,0,0,0.1)' } }
      },
      legend: { show: false },
      grid: { left: 60, right: 30, top: 20, bottom: 40, containLabel: false },
      xAxis: {
        type: 'category',
        data: periods,
        axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
        axisLabel: { color: '#5A5A5A', fontSize: 12, interval: 0, margin: 12 },
        axisTick: { show: false }
      },
      yAxis: {
        type: 'value',
        name: '贡献率(%)',
        nameTextStyle: { color: '#8A8A8A', fontSize: 11, padding: [0, 0, 0, -10] },
        axisLine: { show: false },
        axisLabel: { color: '#8A8A8A', fontSize: 11 },
        splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.04)', type: 'dashed' } }
      },
      series: seriesData
    })
  }
}

// 加载对比数据
const loadCompareData = async () => {
  if (compareCities.value.length === 0) return
  chartLoading.value.compare = true
  chartReady.value.compare = false
  let chartData = []
  try {
    const res = await getCitySourceStructureCompare(
      compareCities.value,
      dateRange.value?.[0],
      dateRange.value?.[1]
    )
    if (res.code === 200) {
      chartData = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
  // 先关闭loading让DOM元素渲染，再初始化图表
  chartLoading.value.compare = false
  await new Promise(resolve => setTimeout(resolve, 50))
  initCompareChart(chartData)
  setTimeout(() => { chartReady.value.compare = true }, 100)
}

// 初始化对比图
const initCompareChart = (data) => {
  if (!compareChartRef.value) return
  if (compareChart) {
    compareChart.dispose()
  }
  compareChart = echarts.init(compareChartRef.value)

  const cities = compareCities.value
  const colors = {
    '机动车尾气': '#6BA3BE',
    '工业排放': '#7DBE8C',
    '燃煤': '#B08BD4',
    '扬尘': '#E5C07B',
    '其他': '#9AB5C5'
  }

  let seriesData = []

  if (data && data.length > 0) {
    const cityDataMap = {}
    data.forEach(item => {
      if (!cityDataMap[item.city]) cityDataMap[item.city] = {}
      cityDataMap[item.city][item.sourceType] = item.contributionRate
    })

    seriesData = selectedSourceTypes.value.map((type) => {
      const values = cities.map(city => {
        const cityData = cityDataMap[city]
        return cityData ? (cityData[type] || 0) : 0
      })
      return {
        name: type,
        type: 'bar',
        stack: 'total',
        barWidth: 36,
        data: values,
        itemStyle: {
          color: colors[type],
          borderRadius: type === '其他' ? [0, 4, 4, 0] : 0
        },
        animationDelay: (idx) => idx * 100
      }
    })
  } else {
    const cityDataMap = {
      '北京': { '机动车尾气': 42.5, '工业排放': 20.5, '燃煤': 9.4, '扬尘': 18.1, '其他': 5.5 },
      '上海': { '机动车尾气': 38.2, '工业排放': 25.8, '燃煤': 8.5, '扬尘': 21.9, '其他': 5.6 },
      '广州': { '机动车尾气': 49.5, '工业排放': 14.7, '燃煤': 6.4, '扬尘': 20.9, '其他': 8.5 },
      '深圳': { '机动车尾气': 52.3, '工业排放': 12.5, '燃煤': 4.2, '扬尘': 18.8, '其他': 12.2 },
      '成都': { '机动车尾气': 39.8, '工业排放': 18.3, '燃煤': 12.6, '扬尘': 21.8, '其他': 7.5 },
      '武汉': { '机动车尾气': 41.2, '工业排放': 22.8, '燃煤': 15.3, '扬尘': 13.5, '其他': 7.2 },
      '西安': { '机动车尾气': 35.6, '工业排放': 28.4, '燃煤': 18.2, '扬尘': 11.8, '其他': 6.0 },
      '乌鲁木齐': { '机动车尾气': 28.5, '工业排放': 35.2, '燃煤': 22.8, '扬尘': 8.5, '其他': 5.0 },
      '南京': { '机动车尾气': 44.8, '工业排放': 19.6, '燃煤': 10.2, '扬尘': 17.4, '其他': 8.0 },
      '杭州': { '机动车尾气': 46.2, '工业排放': 16.8, '燃煤': 8.5, '扬尘': 19.5, '其他': 9.0 }
    }
    seriesData = selectedSourceTypes.value.map((type) => {
      const values = cities.map(city => {
        const cityData = cityDataMap[city]
        return cityData ? (cityData[type] || 0) : 0
      })
      return {
        name: type,
        type: 'bar',
        stack: 'total',
        barWidth: 36,
        data: values,
        itemStyle: {
          color: colors[type],
          borderRadius: type === '其他' ? [0, 4, 4, 0] : 0
        },
        animationDelay: (idx) => idx * 100
      }
    })
  }

  compareChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.98)',
      borderColor: 'rgba(0, 0, 0, 0.06)',
      borderWidth: 1,
      borderRadius: 12,
      padding: [12, 16],
      textStyle: { color: '#2D2D2D', fontSize: 13 },
      axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(0,0,0,0.03)' } }
    },
    legend: {
      top: 5,
      textStyle: { color: '#5A5A5A', fontSize: 11 },
      itemGap: 16
    },
    grid: { left: 55, right: 25, top: 45, bottom: 35 },
    xAxis: {
      type: 'category',
      data: cities,
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisLabel: { color: '#5A5A5A', fontSize: 12 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      name: '贡献率(%)',
      nameTextStyle: { color: '#8A8A8A', fontSize: 11 },
      axisLine: { show: false },
      axisLabel: { color: '#8A8A8A', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.04)', type: 'dashed' } }
    },
    series: seriesData,
    animationEasing: 'elasticOut',
    animationDuration: 1000
  })
}

// 加载排名数据
const loadRankingData = async () => {
  chartLoading.value.rank = true
  try {
    const res = await getCityRankingBySource({ limit: 10 })
    if (res.code === 200) {
      rankingData.value = (res.data || []).map(item => ({
        city: item.city,
        mainSource: item.mainSourceType || '机动车尾气',
        value: item.contributionRate || 40
      }))
    }
  } catch (e) {
    console.error(e)
    rankingData.value = [
      { city: '乌鲁木齐', mainSource: '工业排放', value: 45.2 },
      { city: '深圳', mainSource: '机动车尾气', value: 50.5 },
      { city: '广州', mainSource: '机动车尾气', value: 47.5 },
      { city: '上海', mainSource: '机动车尾气', value: 45.8 },
      { city: '北京', mainSource: '机动车尾气', value: 44.8 }
    ]
  } finally {
    chartLoading.value.rank = false
    setTimeout(() => { chartReady.value.rank = true }, 100)
  }
}

// 加载相关性数据
const loadCorrelationData = async () => {
  chartLoading.value.heatmap = true
  chartReady.value.heatmap = false
  let chartData = []
  try {
    const res = await getCorrelations()
    if (res.code === 200) {
      chartData = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
  // 先关闭loading让DOM元素渲染，再初始化图表
  chartLoading.value.heatmap = false
  await new Promise(resolve => setTimeout(resolve, 50))
  initHeatmapChart(chartData)
  setTimeout(() => { chartReady.value.heatmap = true }, 100)
}

// 初始化热力图
const initHeatmapChart = (data) => {
  if (!heatmapChartRef.value) return
  if (heatmapChart) {
    heatmapChart.dispose()
  }
  heatmapChart = echarts.init(heatmapChartRef.value)

  const sources = ['机动车', '工业', '燃煤', '扬尘', '其他']
  const matrix = [
    [1.00, 0.82, 0.45, 0.38, 0.25],
    [0.82, 1.00, 0.65, 0.42, 0.35],
    [0.45, 0.65, 1.00, 0.28, 0.52],
    [0.38, 0.42, 0.28, 1.00, 0.18],
    [0.25, 0.35, 0.52, 0.18, 1.00]
  ]

  const correlationData = []
  for (let i = 0; i < sources.length; i++) {
    for (let j = 0; j < sources.length; j++) {
      correlationData.push([i, j, matrix[i][j]])
    }
  }

  heatmapChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      position: 'top',
      backgroundColor: 'rgba(255, 255, 255, 0.98)',
      borderColor: 'rgba(0, 0, 0, 0.06)',
      borderWidth: 1,
      borderRadius: 12,
      padding: [10, 14],
      textStyle: { color: '#2D2D2D', fontSize: 12 },
      formatter: (params) => `${sources[params.data[0]]} - ${sources[params.data[1]]}<br/>相关系数: <strong>${params.data[2].toFixed(2)}</strong>`
    },
    grid: { left: 55, right: 15, top: 15, bottom: 35 },
    xAxis: {
      type: 'category',
      data: sources,
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisLabel: { color: '#5A5A5A', fontSize: 11 }
    },
    yAxis: {
      type: 'category',
      data: sources,
      axisLine: { lineStyle: { color: 'rgba(0, 0, 0, 0.08)' } },
      axisLabel: { color: '#5A5A5A', fontSize: 11 }
    },
    visualMap: {
      min: 0,
      max: 1,
      show: false,
      inRange: { color: ['#F5F3F0', '#E8E4DF', '#C8C4BF', '#A8B8C8', '#8898A8'] }
    },
    series: [{
      type: 'heatmap',
      data: correlationData,
      label: {
        show: true,
        formatter: (params) => params.data[2].toFixed(2),
        color: '#2D2D2D',
        fontSize: 10,
        fontWeight: 500
      },
      emphasis: {
        itemStyle: { shadowBlur: 8, shadowColor: 'rgba(0, 0, 0, 0.15)' }
      },
      itemStyle: {
        borderRadius: 4
      },
      animationDuration: 1500
    }]
  })
}

// 加载污染源详情
const loadSourceDetails = async () => {
  chartLoading.value.detail = true
  try {
    const res = await getSourceDistribution(selectedCity.value)
    if (res.code === 200 && res.data) {
      const list = res.data.data || res.data.sourceList || []
      sourceDetails.value = list.map(d => ({
        type: d.name || d.sourceType,
        value: d.value || d.rate,
        tags: getTagsByType(d.name || d.sourceType),
        industries: getIndustriesByType(d.name || d.sourceType),
        trend: getTrendByType(d.name || d.sourceType)
      }))
      if (sourceDetails.value.length > 0) {
        const sorted = [...sourceDetails.value].sort((a, b) => b.value - a.value)
        mainSource.value = sorted[0]?.type || '机动车尾气'
        avgContribution.value = sorted[0]?.value?.toFixed(1) || '42.5'
      }
    }
  } catch (e) {
    console.error(e)
    sourceDetails.value = [
      { type: '机动车尾气', value: 42.5, tags: ['早晚高峰', '城市核心区'], industries: ['汽油车(52%)', '柴油车(35%)', '摩托车(13%)'], trend: { direction: 'up', value: '+3.2%', desc: '较上月上升' } },
      { type: '工业排放', value: 25.3, tags: ['工业园区', '持续监测'], industries: ['钢铁(28%)', '化工(24%)', '水泥(18%)', '其他(30%)'], trend: { direction: 'down', value: '-2.1%', desc: '较上月下降' } },
      { type: '燃煤', value: 12.8, tags: ['冬季高峰', '逐步替代'], industries: ['民用散煤(45%)', '工业燃煤(38%)', '电力燃煤(17%)'], trend: { direction: 'down', value: '-5.8%', desc: '较上月下降' } },
      { type: '扬尘', value: 13.4, tags: ['春季高发', '需加强管控'], industries: ['建筑工地(54%)', '道路扬尘(46%)'], trend: { direction: 'up', value: '+1.5%', desc: '较上月上升' } },
      { type: '其他', value: 6.0, tags: ['分散源', '综合管控'], industries: ['生物质燃烧(53%)', '餐饮油烟(47%)'], trend: { direction: 'stable', value: '+0.2%', desc: '基本持平' } }
    ]
  } finally {
    chartLoading.value.detail = false
    setTimeout(() => { chartReady.value.detail = true }, 100)
  }
}

const getTagsByType = (type) => {
  const tags = {
    '机动车尾气': ['早晚高峰', '城市核心区'],
    '工业排放': ['工业园区', '持续监测'],
    '燃煤': ['冬季高峰', '逐步替代'],
    '扬尘': ['春季高发', '需加强管控'],
    '其他': ['分散源', '综合管控']
  }
  return tags[type] || []
}

const getIndustriesByType = (type) => {
  const industries = {
    '机动车尾气': ['汽油车(52%)', '柴油车(35%)', '摩托车(13%)'],
    '工业排放': ['钢铁(28%)', '化工(24%)', '水泥(18%)', '其他(30%)'],
    '燃煤': ['民用散煤(45%)', '工业燃煤(38%)', '电力燃煤(17%)'],
    '扬尘': ['建筑工地(54%)', '道路扬尘(46%)'],
    '其他': ['生物质燃烧(53%)', '餐饮油烟(47%)']
  }
  return industries[type] || []
}

const getTrendByType = (type) => {
  const trends = {
    '机动车尾气': { direction: 'up', value: '+3.2%', desc: '较上月上升' },
    '工业排放': { direction: 'down', value: '-2.1%', desc: '较上月下降' },
    '燃煤': { direction: 'down', value: '-5.8%', desc: '较上月下降' },
    '扬尘': { direction: 'up', value: '+1.5%', desc: '较上月上升' },
    '其他': { direction: 'stable', value: '+0.2%', desc: '基本持平' }
  }
  return trends[type] || { direction: 'stable', value: '0%', desc: '无变化' }
}

// 窗口大小变化
const handleResize = () => {
  pieChart?.resize()
  trendChart?.resize()
  compareChart?.resize()
  heatmapChart?.resize()
}

onMounted(async () => {
  updateTime()
  timeTimer = setInterval(updateTime, 1000)

  await loadAllData()

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  trendChart?.dispose()
  compareChart?.dispose()
  heatmapChart?.dispose()
})
</script>

<style scoped>
.source-analysis-page {
  width: 100%;
  min-height: 100vh;
  background: #FAFAF8;
  display: flex;
  flex-direction: column;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', sans-serif;
}

/* 导航栏 */
.dashboard-header {
  height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  color: #6BA3BE;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #FAFAF8;
  border-color: #6BA3BE;
  transform: translateX(-2px);
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

.page-title h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2D2D2D;
  letter-spacing: -0.3px;
}

.page-title p {
  margin: 2px 0 0 0;
  font-size: 11px;
  color: #5A5A5A;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 导出按钮 */
.export-dropdown {
  margin-left: 8px;
}

.export-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #6BA3BE, #5A93AE);
  border: none;
  border-radius: 10px;
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
}

.export-btn:hover {
  background: linear-gradient(135deg, #5A93AE, #4A83A0);
}

.export-btn .btn-icon {
  width: 16px;
  height: 16px;
}

.dropdown-icon {
  width: 16px;
  height: 16px;
  margin-right: 8px;
  color: #6BA3BE;
}

.current-time {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 14px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
}

.current-time .date {
  font-size: 12px;
  color: #5A5A5A;
}

.current-time .time {
  font-size: 14px;
  font-weight: 600;
  color: #6BA3BE;
  font-variant-numeric: tabular-nums;
}

.city-select {
  width: 140px;
}

.city-select :deep(.el-input__wrapper) {
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: none;
  border-radius: 10px;
}

.city-select :deep(.el-input__wrapper:hover) {
  border-color: rgba(107, 163, 190, 0.3);
}

.city-select :deep(.el-input__wrapper:focus-within) {
  border-color: #6BA3BE;
}

.city-select :deep(.el-input__inner) {
  color: #2D2D2D;
}

.dashboard-body {
  flex: 1;
  display: flex;
  padding: 16px;
  gap: 16px;
  overflow: hidden;
}

/* 左侧面板 */
.panel-left {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 卡片样式 */
.chart-card {
  background: #FFFFFF;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.95);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #2D2D2D;
}

.title-icon {
  width: 18px;
  height: 18px;
  color: #6BA3BE;
}

.card-tools {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 帮助图标 */
.help-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(107, 163, 190, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
}

.help-icon:hover {
  background: rgba(107, 163, 190, 0.2);
}

.help-icon svg {
  width: 14px;
  height: 14px;
  color: #6BA3BE;
}

.card-body {
  flex: 1;
  padding: 12px;
}

.chart-container {
  width: 100%;
  height: 100%;
  min-height: 180px;
}

/* 图表淡入动画 */
.chart-fade-in {
  animation: chartFadeIn 0.6s ease-out forwards;
}

@keyframes chartFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* 筛选区 */
.filter-body {
  padding: 12px 16px;
}

.filter-group {
  margin-bottom: 12px;
}

.filter-group label {
  display: block;
  font-size: 12px;
  color: #5A5A5A;
  font-weight: 500;
  margin-bottom: 8px;
}

/* 快捷时间按钮 */
.quick-time-btns {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.quick-time-btn {
  padding: 6px 12px;
  font-size: 12px;
  background: #FAFAF8;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  color: #5A5A5A;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-time-btn:hover {
  border-color: #6BA3BE;
  color: #6BA3BE;
}

.quick-time-btn.active {
  background: linear-gradient(135deg, #6BA3BE, #5A93AE);
  border-color: transparent;
  color: #fff;
}

.source-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.source-tag {
  padding: 4px 10px;
  font-size: 11px;
  background: #FAFAF8;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  color: #5A5A5A;
  cursor: pointer;
  transition: all 0.2s ease;
}

.source-tag:hover {
  border-color: #6BA3BE;
  transform: scale(1.05);
}

.source-tag.active {
  color: #fff;
  border-color: transparent;
  transform: scale(1.05);
}

.filter-actions {
  display: flex;
  gap: 8px;
}

.date-picker {
  width: 100%;
}

.date-picker :deep(.el-input__wrapper) {
  background: #FAFAF8;
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: none;
  border-radius: 10px;
}

.date-picker :deep(.el-input__wrapper:hover) {
  border-color: rgba(107, 163, 190, 0.3);
}

.date-picker :deep(.el-input__wrapper:focus-within) {
  border-color: #6BA3BE;
}

.pie-card .chart-container {
  min-height: 200px;
}

.rank-card {
  flex: 1;
  min-height: 0;
}

.rank-card .rank-body {
  padding: 8px 12px;
  overflow-y: auto;
}

/* 排行表格 */
.rank-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.rank-item:hover {
  background: rgba(107, 163, 190, 0.08);
  transform: translateX(4px);
}

.rank-item-fade-in {
  animation: rankItemFadeIn 0.5s ease-out forwards;
  opacity: 0;
}

@keyframes rankItemFadeIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.rank-num {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  border-radius: 6px;
  background: #F0F0EE;
  color: #5A5A5A;
}

.rank-num.top-1 { background: linear-gradient(135deg, #E5A87B, #F0C4A0); color: #fff; }
.rank-num.top-2 { background: linear-gradient(135deg, #6BA3BE, #8BB8CE); color: #fff; }
.rank-num.top-3 { background: linear-gradient(135deg, #A0B0A0, #B8C8B8); color: #fff; }

.rank-info {
  flex: 0 0 70px;
}

.rank-city {
  display: block;
  font-size: 13px;
  color: #2D2D2D;
  font-weight: 500;
}

.rank-source {
  font-size: 10px;
  color: #5A5A5A;
}

.rank-bar-wrap {
  flex: 1;
  height: 6px;
  background: #F0F0EE;
  border-radius: 3px;
  overflow: hidden;
}

.rank-bar {
  height: 100%;
  border-radius: 3px;
  transition: width 0.8s ease;
}

.rank-value {
  width: 40px;
  text-align: right;
  font-size: 12px;
  font-weight: 600;
  color: #6BA3BE;
}

/* ========== 右侧主内容区域 ========== */
.panel-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
}

/* 数据总览 */
.stats-row {
  display: flex;
  gap: 16px;
  position: relative;
}

/* 预警指示器 */
.alert-indicator {
  position: absolute;
  top: -8px;
  right: -8px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #FF6B6B, #EE5A5A);
  border-radius: 20px;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
  animation: alertPulse 2s infinite;
}

@keyframes alertPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.alert-icon-pulse {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(255, 107, 107, 0.3);
  border-radius: 20px;
  animation: alertIconPulse 1.5s infinite;
}

@keyframes alertIconPulse {
  0% { transform: scale(1); opacity: 1; }
  100% { transform: scale(1.5); opacity: 0; }
}

.alert-icon-svg {
  width: 16px;
  height: 16px;
}

.alert-count {
  font-size: 14px;
  font-weight: 700;
}

.alert-text {
  font-size: 11px;
}

/* 预警标识 */
.warning-badge {
  display: inline-flex;
  align-items: center;
  margin-left: 6px;
  padding: 2px 6px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.15), rgba(255, 107, 107, 0.08));
  border-radius: 8px;
  vertical-align: middle;
}

.warning-badge svg {
  width: 14px;
  height: 14px;
  color: #FF6B6B;
}

.value-warning {
  color: #FF6B6B !important;
}

/* 导出按钮卡片 */
.export-stat-btn {
  flex: 0 0 auto;
  min-width: 140px;
}

.stat-card-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  background: #FFFFFF;
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-card-item:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
}

.stat-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: var(--icon-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.stat-card-item:hover .stat-icon-box {
  transform: scale(1.1) rotate(5deg);
}

.stat-icon-box svg {
  width: 24px;
  height: 24px;
  color: #fff;
}

.stat-content-box {
  flex: 1;
}

.stat-label-text {
  display: block;
  font-size: 12px;
  color: #8A8A8A;
  font-weight: 500;
  margin-bottom: 4px;
}

.stat-value-text {
  font-size: 22px;
  font-weight: 700;
  color: #2D2D2D;
  letter-spacing: -0.5px;
}

.stat-unit-text {
  font-size: 14px;
  font-weight: 500;
  color: #5A5A5A;
  margin-left: 2px;
}

/* 主内容区 */
.main-content-area {
  flex: 1;
  display: flex;
  gap: 16px;
  min-height: 0;
}

.column-left-main {
  flex: 1.3;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.column-right-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 趋势卡片 */
.trend-card-main {
  flex: 1.5;
  min-height: 320px;
  display: flex;
  flex-direction: column;
  background: #FFFFFF;
  border-radius: 18px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.trend-body-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 0 12px 12px;
}

.trend-chart-main {
  flex: 1;
  min-height: 260px;
}

.trend-legend-main {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px 28px;
  padding: 14px 16px;
  background: linear-gradient(180deg, rgba(255,255,255,0.98) 0%, rgba(250,250,248,0.95) 100%);
  border-top: 1px solid rgba(0, 0, 0, 0.04);
}

.legend-item-main {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  opacity: 1;
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 8px;
}

.legend-item-main:hover {
  background: rgba(0, 0, 0, 0.03);
  transform: scale(1.05);
}

.legend-item-main.dim {
  opacity: 0.25;
}

.legend-dot-main {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.legend-name-main {
  font-size: 13px;
  font-weight: 500;
  color: #5A5A5A;
}

/* 污染源详情 */
.detail-card-main {
  flex: 1;
  min-height: 280px;
  background: #FFFFFF;
  border-radius: 18px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.detail-body-main {
  padding: 16px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  max-height: 400px;
  overflow-y: auto;
}

.detail-item-main {
  padding: 14px;
  background: linear-gradient(135deg, #FAFAF8 0%, #F8F8F6 100%);
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.detail-item-main::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--source-color);
  opacity: 0.6;
  border-radius: 4px 0 0 4px;
  transition: width 0.3s ease;
}

.detail-item-main:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.detail-item-main:hover::before {
  width: 6px;
  opacity: 0.8;
}

.detail-item-fade-in {
  animation: detailItemFadeIn 0.6s ease-out forwards;
  opacity: 0;
}

@keyframes detailItemFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.detail-header-main {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
}

.detail-icon-main {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.detail-item-main:hover .detail-icon-main {
  transform: rotate(10deg) scale(1.1);
}

.detail-icon-main svg {
  width: 18px;
  height: 18px;
  color: #fff;
}

.detail-info-main {
  flex: 1;
  min-width: 0;
}

.detail-name-main {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #2D2D2D;
  margin-bottom: 4px;
}

.detail-value-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-value-main {
  font-size: 20px;
  font-weight: 700;
  color: #2D2D2D;
}

.detail-trend-pill {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 3px 8px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
}

.detail-trend-pill.up {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.15), rgba(76, 175, 80, 0.08));
  color: #4CAF50;
}

.detail-trend-pill.down {
  background: linear-gradient(135deg, rgba(244, 67, 54, 0.15), rgba(244, 67, 54, 0.08));
  color: #F44336;
}

.detail-trend-pill.stable {
  background: linear-gradient(135deg, rgba(158, 158, 158, 0.15), rgba(158, 158, 158, 0.08));
  color: #9E9E9E;
}

.trend-arrow {
  width: 12px;
  height: 12px;
}

.detail-tags-main {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 8px;
}

.detail-tag-main {
  font-size: 10px;
  padding: 2px 6px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 5px;
  color: #5A5A5A;
  font-weight: 500;
}

.detail-industries-main {
  font-size: 11px;
  color: #5A5A5A;
  line-height: 1.5;
}

.industry-label-main {
  color: #8A8A8A;
  font-weight: 500;
}

.industry-item-main {
  color: #2D2D2D;
  font-weight: 500;
}

/* 右栏卡片 */
.compare-card-main,
.correlation-card-main {
  flex: 1;
  min-height: 200px;
  background: #FFFFFF;
  border-radius: 18px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.compare-select {
  width: 180px;
}

.compare-chart-main {
  min-height: 180px;
}

.heatmap-chart-main {
  min-height: 160px;
}

/* ========== 骨架屏样式 ========== */
.skeleton-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 180px;
}

.skeleton-circle {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-legend {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.skeleton-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-text {
  width: 60px;
  height: 12px;
  border-radius: 4px;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-text-sm {
  width: 50px;
  height: 10px;
  border-radius: 4px;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-bar {
  width: 80px;
  height: 6px;
  border-radius: 3px;
  margin-top: 6px;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
}

.skeleton-rank-num {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-rank-content {
  flex: 1;
}

.skeleton-chart {
  width: 100%;
  flex-direction: column;
  gap: 16px;
}

.skeleton-lines {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
}

.skeleton-line {
  height: 3px;
  border-radius: 2px;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-bars {
  display: flex;
  gap: 30px;
  justify-content: center;
  padding: 20px;
}

.skeleton-bar-stack {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.skeleton-bar-item {
  width: 40px;
  height: 20px;
  border-radius: 4px;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  padding: 20px;
}

.skeleton-grid-cell {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 4px;
  background: linear-gradient(90deg, #F0F0EE 25%, #E8E8E6 50%, #F0F0EE 75%);
  background-size: 200% 100%;
  animation: skeletonShimmer 1.5s infinite;
}

@keyframes skeletonShimmer {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* ========== 弹窗样式 ========== */
.source-type-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  padding: 16px 20px;
}

.source-type-dialog :deep(.el-dialog__title) {
  font-weight: 600;
  color: #2D2D2D;
}

.source-detail-content {
  padding: 8px 0;
}

.source-detail-header {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.source-detail-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.source-detail-icon svg {
  width: 28px;
  height: 28px;
  color: #fff;
}

.source-detail-meta {
  flex: 1;
}

.source-detail-label {
  display: inline-block;
  padding: 4px 10px;
  background: rgba(107, 163, 190, 0.1);
  border-radius: 6px;
  font-size: 12px;
  color: #6BA3BE;
  font-weight: 500;
  margin-bottom: 8px;
}

.source-detail-desc {
  font-size: 14px;
  color: #5A5A5A;
  line-height: 1.6;
}

.source-detail-section {
  margin-bottom: 16px;
}

.source-detail-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #2D2D2D;
  margin-bottom: 10px;
  padding-left: 12px;
  border-left: 3px solid #6BA3BE;
}

.source-detail-list {
  margin: 0;
  padding-left: 20px;
}

.source-detail-list li {
  font-size: 13px;
  color: #5A5A5A;
  line-height: 1.8;
}

.source-detail-impact {
  font-size: 13px;
  color: #5A5A5A;
  line-height: 1.6;
  background: rgba(229, 168, 123, 0.1);
  padding: 12px;
  border-radius: 8px;
}

/* ========== 预警面板样式 ========== */
.alert-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  padding: 16px 20px;
}

.alert-dialog :deep(.el-dialog__title) {
  font-weight: 600;
  color: #2D2D2D;
}

.alert-panel-content {
  padding: 8px 0;
}

.alert-header-info {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.08), rgba(255, 107, 107, 0.03));
  border-radius: 12px;
  margin-bottom: 16px;
}

.alert-header-icon {
  width: 40px;
  height: 40px;
  color: #FF6B6B;
}

.alert-header-title {
  font-size: 14px;
  font-weight: 600;
  color: #2D2D2D;
  margin-bottom: 4px;
}

.alert-header-desc {
  font-size: 12px;
  color: #5A5A5A;
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 16px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background: #FAFAF8;
  border-radius: 10px;
  border-left: 3px solid var(--alert-color, #FF6B6B);
}

.alert-item-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 107, 107, 0.1);
  border-radius: 8px;
}

.alert-item-icon svg {
  width: 18px;
  height: 18px;
  color: #FF6B6B;
}

.alert-item-content {
  flex: 1;
}

.alert-item-title {
  font-size: 14px;
  font-weight: 600;
  color: #2D2D2D;
}

.alert-item-desc {
  font-size: 12px;
  color: #5A5A5A;
}

.alert-item-level {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.alert-item-level.high {
  background: rgba(244, 67, 54, 0.15);
  color: #F44336;
}

.alert-item-level.medium {
  background: rgba(255, 152, 0, 0.15);
  color: #FF9800;
}

.alert-item-level.low {
  background: rgba(255, 193, 7, 0.15);
  color: #FFC107;
}

.alert-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 30px;
  color: #8A8A8A;
}

.alert-empty svg {
  width: 48px;
  height: 48px;
  color: #4CAF50;
}

.alert-suggestions {
  padding: 14px;
  background: rgba(107, 163, 190, 0.08);
  border-radius: 10px;
}

.alert-suggestions h4 {
  font-size: 13px;
  font-weight: 600;
  color: #2D2D2D;
  margin-bottom: 10px;
}

.alert-suggestions ul {
  margin: 0;
  padding-left: 18px;
}

.alert-suggestions li {
  font-size: 12px;
  color: #5A5A5A;
  line-height: 1.8;
}

/* ========== 导出对话框样式 ========== */
.export-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  padding: 16px 20px;
}

.export-dialog :deep(.el-dialog__title) {
  font-weight: 600;
  color: #2D2D2D;
}

.export-dialog-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.export-options {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.export-option-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.export-option-group label {
  font-size: 13px;
  font-weight: 600;
  color: #2D2D2D;
}

.export-filter-summary {
  padding: 12px;
  background: #FAFAF8;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-summary-item {
  display: flex;
  font-size: 12px;
}

.filter-label {
  color: #8A8A8A;
  width: 80px;
  flex-shrink: 0;
}

.filter-value {
  color: #2D2D2D;
  font-weight: 500;
}

.export-preview {
  padding: 14px;
  background: #FAFAF8;
  border-radius: 10px;
  border: 1px dashed rgba(0, 0, 0, 0.1);
}

.preview-title {
  font-size: 12px;
  font-weight: 600;
  color: #8A8A8A;
  margin-bottom: 10px;
}

.preview-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  background: #FFFFFF;
  border-radius: 6px;
  font-size: 12px;
  color: #5A5A5A;
}

.preview-item svg {
  width: 14px;
  height: 14px;
  color: #6BA3BE;
}

.btn-icon {
  width: 16px;
  height: 16px;
  margin-right: 6px;
}

/* 响应式 */
@media (max-width: 1400px) {
  .panel-left {
    width: 260px;
  }

  .detail-body-main {
    grid-template-columns: 1fr;
  }

  .export-stat-btn {
    display: none;
  }
}

@media (max-width: 1100px) {
  .dashboard-body {
    flex-direction: column;
    overflow-y: auto;
  }

  .panel-left {
    width: 100%;
    flex-direction: row;
  }

  .main-content-area {
    flex-direction: column;
  }

  .column-left-main,
  .column-right-main {
    flex: none;
    width: 100%;
  }

  .chart-card {
    flex: 1;
  }

  .export-dropdown {
    display: none;
  }

  .export-stat-btn {
    display: flex;
  }

  .alert-indicator {
    position: static;
    margin-bottom: 16px;
  }
}
</style>
