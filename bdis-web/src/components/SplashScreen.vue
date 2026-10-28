<template>
  <Teleport to="body">
    <transition name="splash-fade">
      <div v-if="visible" class="splash-overlay" @click="skip">
        <!-- 纯净白底 + 浅蓝工程微网格（1:1 还原 logo.jpg） -->
        <div class="splash-bg">
          <div class="grid-pattern"></div>
        </div>

        <div class="splash-center" @click.stop>
          <!-- 紧凑比例的 Logo 整体框架（还原原图 1440x311 黄金比例与紧凑间距） -->
          <div class="logo-composite">
            <!-- 左侧：BDIS 矢量线条画出与深海蓝注色 -->
            <div class="bdis-svg-box">
              <svg class="bdis-svg" viewBox="0 0 450 190" xmlns="http://www.w3.org/2000/svg">
                <!-- 第一步：精确深蓝线稿（#19375D），线条更细更锐利 -->
                <text x="0" y="162" class="bdis-draw-stroke">BDIS</text>
                <!-- 第二步：原图深海蓝（#19375D）自然注入填色 -->
                <text x="0" y="162" class="bdis-draw-fill">BDIS</text>
              </svg>
            </div>

            <!-- 右侧：文字排版紧贴 BDIS（间距仅 28px，完美消除中间空档） -->
            <div class="bdis-text-column">
              <!-- 右下角原版淡蓝圆弧水印 -->
              <div class="watermark-arc"></div>

              <div class="en-titles">
                <span class="en-line-1">big data intelligence</span>
                <span class="en-line-2">studio</span>
              </div>
              <div class="cn-title">大数据智能工作室</div>
            </div>
          </div>
        </div>

        <!-- 右上角极简跳过按钮 -->
        <button type="button" class="skip-btn" @click.stop="skip">
          <span>跳过</span>
          <svg viewBox="0 0 24 24" width="13" height="13" fill="none" stroke="currentColor" stroke-width="2.2">
            <polyline points="9 18 15 12 9 6"></polyline>
          </svg>
        </button>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const emit = defineEmits(['finish'])
const visible = ref(true)
let autoTimer = null

const skip = () => {
  if (!visible.value) return
  visible.value = false
  sessionStorage.setItem('bdis_splash_played', '1')
  emit('finish')
}

const handleKey = (e) => {
  if (e.key === 'Escape' || e.key === ' ') {
    skip()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKey)
  // 约 2.2 秒平滑淡出溶入首页
  autoTimer = setTimeout(() => {
    skip()
  }, 2200)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKey)
  if (autoTimer) clearTimeout(autoTimer)
})
</script>

<style scoped>
/* 全屏纯净白底画布 */
.splash-overlay {
  position: fixed;
  inset: 0;
  z-index: 999999;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  user-select: none;
}

/* 1:1 还原 logo.jpg 的浅蓝微网格 */
.splash-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.grid-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(25, 55, 93, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(25, 55, 93, 0.04) 1px, transparent 1px);
  background-size: 32px 32px;
}

.splash-center {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
}

/* 紧凑布局：间距精确还原 logo.jpg 的紧凑亲和感 */
.logo-composite {
  display: flex;
  align-items: center;
  gap: clamp(8px, 1.4vw, 18px);
}

/* 左侧 BDIS 矢量独立包装 */
.bdis-svg-box {
  flex: 0 0 auto;
  width: clamp(210px, 30vw, 420px);
}

.bdis-svg {
  width: 100%;
  height: auto;
  display: block;
}

/* 字体：更细致优雅的经典无衬线，字重改为 700（精准匹配 logo.jpg 骨架） */
.bdis-draw-stroke,
.bdis-draw-fill {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-size: 175px;
  font-weight: 700;
  letter-spacing: 0px;
}

/* 原图严格采样的深蓝十六进制色值：#19375D */
.bdis-draw-stroke {
  fill: transparent;
  stroke: #19375D;
  stroke-width: 1.5px;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-dasharray: 850;
  stroke-dashoffset: 850;
  animation: drawStroke 1.1s cubic-bezier(0.65, 0, 0.35, 1) forwards;
}

@keyframes drawStroke {
  0% {
    stroke-dashoffset: 850;
  }
  100% {
    stroke-dashoffset: 0;
  }
}

/* 线条闭合后，自然注入纯正的 #19375D 实色深蓝 */
.bdis-draw-fill {
  fill: #19375D;
  stroke: transparent;
  opacity: 0;
  animation: fillFadeIn 0.45s 0.92s ease-out forwards;
}

@keyframes fillFadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

/* 右侧文本列：严格对齐原图紧凑排版与垂直居中 */
.bdis-text-column {
  position: relative;
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: clamp(6px, 1vw, 12px);
  padding-bottom: 4px;
}

/* 原图右下角淡蓝圆弧水印（精确色值与透射） */
.watermark-arc {
  position: absolute;
  right: -30px;
  bottom: -40px;
  width: 190px;
  height: 190px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(235, 243, 252, 0.95) 0%, rgba(240, 246, 253, 0.35) 65%, transparent 100%);
  pointer-events: none;
  z-index: -1;
  opacity: 0;
  animation: arcReveal 0.6s 0.7s ease-out forwards;
}

@keyframes arcReveal {
  to {
    opacity: 1;
  }
}

.en-titles {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

/* 英文标题色值 #19375D，字形清晰干练 */
.en-line-1,
.en-line-2 {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-size: clamp(16px, 2.1vw, 29px);
  font-weight: 700;
  line-height: 1.2;
  color: #19375D;
  letter-spacing: 0.1px;
  opacity: 0;
  transform: translateX(-10px);
}

.en-line-1 {
  animation: textSlide 0.45s 0.98s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.en-line-2 {
  animation: textSlide 0.45s 1.06s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* 中文标题色值 #19375D，原版字重 */
.logo-cn-title {
  font-family: "MiSans", "Microsoft YaHei", -apple-system, system-ui, sans-serif;
  font-size: clamp(20px, 2.6vw, 35px);
  font-weight: 850;
  line-height: 1.2;
  color: #19375D;
  letter-spacing: 1.8px;
  opacity: 0;
  transform: translateX(-12px);
  animation: textSlide 0.5s 1.15s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes textSlide {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 跳过按钮 */
.skip-btn {
  position: absolute;
  top: 28px;
  right: 32px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 18px;
  border: 1px solid #dce7f2;
  background: rgba(255, 255, 255, 0.9);
  color: #71879b;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(6px);
}

.skip-btn:hover {
  color: #19375D;
  border-color: #19375D;
  background: #ffffff;
}

/* 离场淡出动画 */
.splash-fade-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.splash-fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}

@media (max-width: 600px) {
  .logo-composite {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .bdis-svg-box {
    width: 240px;
  }
  .skip-btn {
    top: 18px;
    right: 18px;
  }
}
</style>
