<template>
  <div class="tech-panel" :class="{ 'no-padding': noPadding }">
    <!-- 四角装饰 -->
    <div class="corner corner-tl"></div>
    <div class="corner corner-tr"></div>
    <div class="corner corner-bl"></div>
    <div class="corner corner-br"></div>

    <!-- 标题 -->
    <div class="panel-header" v-if="title">
      <div class="header-bg"></div>
      <span class="header-icon" v-if="icon">{{ icon }}</span>
      <span class="header-title">{{ title }}</span>
      <div class="header-extra">
        <slot name="extra"></slot>
      </div>
    </div>

    <!-- 内容 -->
    <div class="panel-body">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: '◈'
  },
  noPadding: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.tech-panel {
  position: relative;
  background: linear-gradient(135deg, #034c6a 0%, #072951 100%);
  border: 1px solid #2C58A6;
  border-radius: 4px;
  box-shadow: 0 0 10px rgba(44, 88, 166, 0.3);
  overflow: hidden;
}

/* 四角装饰 */
.corner {
  position: absolute;
  width: 15px;
  height: 15px;
  border: 2px solid #4788fb;
  box-shadow: 0 0 10px #4788fb;
  z-index: 2;
}

.corner-tl {
  top: -1px;
  left: -1px;
  border-right: none;
  border-bottom: none;
}

.corner-tr {
  top: -1px;
  right: -1px;
  border-left: none;
  border-bottom: none;
}

.corner-bl {
  bottom: -1px;
  left: -1px;
  border-right: none;
  border-top: none;
}

.corner-br {
  bottom: -1px;
  right: -1px;
  border-left: none;
  border-top: none;
}

/* 标题 */
.panel-header {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 15px;
  border-bottom: 1px solid rgba(44, 88, 166, 0.3);
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, #2C58A6 0%, transparent 100%);
}

.header-icon {
  position: relative;
  color: #4788fb;
  text-shadow: 0 0 10px #4788fb;
  font-size: 14px;
}

.header-title {
  position: relative;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.header-extra {
  position: relative;
  margin-left: auto;
}

/* 内容 */
.panel-body {
  padding: 15px;
}

.tech-panel.no-padding .panel-body {
  padding: 0;
}
</style>
