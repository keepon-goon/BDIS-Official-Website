<template>
  <Teleport to="body">
    <transition name="splash-fade" @after-leave="onAfterLeave">
      <div v-if="visible" class="splash-overlay" @click="skip">
        <!-- 纯净白底 + 浅蓝工程微网格（1:1 还原 logo.jpg 背景质感） -->
        <div class="splash-bg">
          <div class="grid-pattern"></div>
          <div class="ambient-glow"></div>
        </div>

        <div class="splash-center" @click.stop>
          <!-- 紧凑比例的 Logo 整体框架（还原 logo.jpg 黄金比例与精致排版） -->
          <div class="logo-composite">
            <!-- 左侧：BDIS 矢量流体丝滑揭示 + 流光扫掠 -->
            <div class="bdis-svg-box">
              <!-- 底层：轻盈科技线稿底衬 -->
              <svg class="bdis-svg bdis-svg-blueprint" viewBox="0 0 450 190" xmlns="http://www.w3.org/2000/svg">
                <text x="0" y="162" class="bdis-blueprint-text">BDIS</text>
              </svg>

              <!-- 表层：深海蓝（#19375D）实色层，通过硬件加速 clip-path 丝滑扫出 -->
              <div class="bdis-fluid-reveal">
                <svg class="bdis-svg bdis-svg-solid" viewBox="0 0 450 190" xmlns="http://www.w3.org/2000/svg">
                  <text x="0" y="162" class="bdis-solid-text">BDIS</text>
                </svg>
                <!-- 伴随揭示边缘的高级微光 -->
                <div class="bdis-shimmer-sheen"></div>
              </div>
            </div>

            <!-- 右侧：文字排版紧贴 BDIS，琴键式瀑布流平滑滑入 -->
            <div class="bdis-text-column">
              <!-- 右下角原版淡蓝圆弧水印，自然光晕膨胀 -->
              <div class="watermark-arc"></div>

              <div class="en-titles">
                <span class="en-line-1">big data intelligence</span>
                <span class="en-line-2">studio</span>
              </div>
              <div class="cn-title">大数据智能工作室</div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const emit = defineEmits(['finish'])
const visible = ref(true)
let autoTimer = null

// 触发离场：将 visible 置为 false，让 Vue 完整执行 1 秒的丝滑淡出过渡动画
const skip = () => {
  if (!visible.value) return
  visible.value = false
  if (autoTimer) {
    clearTimeout(autoTimer)
    autoTimer = null
  }
}

// 动画彻底播放完毕后再通知外层 unmount，确保退出动画完整呈现
const onAfterLeave = () => {
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
  // 整体放慢节奏：约 2.7 秒全部舒缓落定，留出约 1.5 秒从容欣赏期，共 4.2 秒后丝滑溶入首页
  autoTimer = setTimeout(() => {
    skip()
  }, 4200)
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
  will-change: opacity, transform, filter;
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
  animation: gridFade 1.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes gridFade {
  0% {
    opacity: 0;
    transform: scale(1.03);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

/* 核心呼吸环境光 */
.ambient-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: min(850px, 92vw);
  height: min(480px, 65vh);
  transform: translate(-50%, -50%);
  background: radial-gradient(ellipse, rgba(230, 242, 255, 0.7) 0%, rgba(240, 247, 255, 0.28) 55%, transparent 75%);
  pointer-events: none;
  opacity: 0;
  animation: glowIn 2.0s 0.15s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes glowIn {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.85);
  }
  100% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

.splash-center {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  animation: centerIn 1.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  will-change: transform, opacity, filter;
}

@keyframes centerIn {
  0% {
    opacity: 0;
    transform: translateY(10px) scale(0.975);
    filter: blur(4px);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
    filter: blur(0px);
  }
}

/* 紧凑布局：间距精确还原 logo.jpg 的紧凑亲和感 */
.logo-composite {
  display: flex;
  align-items: center;
  gap: clamp(8px, 1.4vw, 18px);
}

/* 左侧 BDIS 矢量独立包装 */
.bdis-svg-box {
  position: relative;
  flex: 0 0 auto;
  width: clamp(160px, 28vw, 410px);
}

.bdis-svg {
  width: 100%;
  height: auto;
  display: block;
}

/* 字体：无衬线字体骨架，字重 700 匹配 logo.jpg */
.bdis-blueprint-text,
.bdis-solid-text {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-size: 175px;
  font-weight: 700;
  letter-spacing: 0px;
}

/* 底层轻量级线稿底衬（科技透气感） */
.bdis-blueprint-text {
  fill: transparent;
  stroke: #19375D;
  stroke-width: 1.2px;
  opacity: 0.18;
  animation: blueprintFade 0.9s ease-out forwards;
}

@keyframes blueprintFade {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 0.18;
  }
}

/* 表层硬件加速流体剪裁注入层（放慢至 1.5 秒，水墨流体感更足） */
.bdis-fluid-reveal {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  clip-path: inset(0 100% 0 0);
  animation: fluidReveal 1.5s 0.25s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  will-change: clip-path;
}

@keyframes fluidReveal {
  0% {
    clip-path: inset(0 100% 0 0);
  }
  100% {
    clip-path: inset(0 0% 0 0);
  }
}

/* 原图深海蓝（#19375D）实色深蓝 */
.bdis-solid-text {
  fill: #19375D;
  stroke: transparent;
}

/* 伴随揭示边缘的高级高光流扫线 */
.bdis-shimmer-sheen {
  position: absolute;
  top: -15%;
  bottom: -15%;
  width: 48px;
  background: linear-gradient(90deg, transparent 0%, rgba(66, 133, 244, 0.25) 40%, rgba(255, 255, 255, 0.92) 75%, transparent 100%);
  transform: skewX(-16deg);
  pointer-events: none;
  animation: sheenTravel 1.5s 0.25s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  will-change: left, opacity;
}

@keyframes sheenTravel {
  0% {
    left: -10%;
    opacity: 0;
  }
  16% {
    opacity: 1;
  }
  84% {
    opacity: 1;
  }
  100% {
    left: 105%;
    opacity: 0;
  }
}

/* 右侧文本列：严格对齐原图紧凑排版与垂直居中 */
.bdis-text-column {
  position: relative;
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: clamp(5px, 0.9vw, 12px);
  padding-bottom: 4px;
}

/* 原图右下角淡蓝圆弧水印（精确色值与透射，放慢舒缓展开） */
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
  transform: scale(0.8);
  animation: arcBloom 1.6s 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes arcBloom {
  0% {
    opacity: 0;
    transform: scale(0.8);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

.en-titles {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

/* 英文标题色值 #19375D，字形清晰干练，琴键式平滑滑入（放慢至 1.0 秒） */
.en-line-1,
.en-line-2 {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  font-size: clamp(14px, 2vw, 29px);
  font-weight: 700;
  line-height: 1.2;
  color: #19375D;
  letter-spacing: 0.1px;
  opacity: 0;
  transform: translateX(-22px);
  will-change: transform, opacity, filter;
}

.en-line-1 {
  animation: cascadeSlide 1.0s 0.85s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.en-line-2 {
  animation: cascadeSlide 1.0s 1.15s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

/* 中文标题色值 #19375D，原版字重，从容落定（1.2 秒时长，1.5 秒启动） */
.cn-title {
  font-family: "MiSans", "Microsoft YaHei", -apple-system, system-ui, sans-serif;
  font-size: clamp(18px, 2.5vw, 35px);
  font-weight: 850;
  line-height: 1.2;
  color: #19375D;
  letter-spacing: 1.6px;
  opacity: 0;
  transform: translateX(-24px);
  will-change: transform, opacity, filter;
  animation: cascadeSlide 1.2s 1.50s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes cascadeSlide {
  0% {
    opacity: 0;
    transform: translateX(-22px);
    filter: blur(6px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
    filter: blur(0px);
  }
}

/* 核心：出首页时的宏大转场动画（整整 1.0 秒高质感平滑溶出） */
.splash-fade-leave-active {
  transition: opacity 1.0s cubic-bezier(0.16, 1, 0.3, 1);
  pointer-events: none;
}

.splash-fade-leave-active .splash-center {
  transition: 
    transform 1.0s cubic-bezier(0.16, 1, 0.3, 1),
    opacity 0.85s cubic-bezier(0.16, 1, 0.3, 1),
    filter 1.0s ease;
}

/* 背景蒙版柔和透光，将下方的首页内容自然显现出来 */
.splash-fade-leave-to {
  opacity: 0;
}

/* Logo 标识伴随微放大、柔光高斯虚化、向上悬浮微移升空溶出，极具视觉仪式感 */
.splash-fade-leave-to .splash-center {
  transform: scale(1.08) translateY(-14px);
  opacity: 0;
  filter: blur(10px);
}

/* 移动端精致自适应（保持横向黄金排版，不换行截断） */
@media (max-width: 600px) {
  .logo-composite {
    gap: clamp(6px, 2.5vw, 12px);
  }

  .bdis-svg-box {
    width: clamp(130px, 38vw, 220px);
  }

  .en-line-1,
  .en-line-2 {
    font-size: clamp(9px, 2.6vw, 15px);
  }

  .cn-title {
    font-size: clamp(12px, 3.4vw, 19px);
    letter-spacing: 0.8px;
  }

  .watermark-arc {
    width: 110px;
    height: 110px;
    right: -15px;
    bottom: -20px;
  }
}
</style>
