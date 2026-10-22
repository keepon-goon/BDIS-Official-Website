<template>
  <div class="reagent-choice-wrapper" :data-choice="choice || 'pending'">
    <!-- 破裂外喷气泡群（柔缓物理浮力与微气流逸散，像试管壁微破裂后高压气体柔缓涌出） -->
    <!-- 状态为“无/不告诉你”时：蓝色被压缩在最左侧，向左外侧区域柔缓喷逸 -->
    <div v-if="choice === 'no'" class="rupture-spray spray-left" aria-hidden="true">
      <div class="rupture-fissure fissure-left"></div>
      <span v-for="i in 14" :key="`rb-${i}`" :class="`r-bubble rb-${i}`"></span>
    </div>

    <!-- 状态为“有/小露一手”时：绿色被压缩在最右侧，向右外侧区域柔缓喷逸 -->
    <div v-if="choice === 'yes'" class="rupture-spray spray-right" aria-hidden="true">
      <div class="rupture-fissure fissure-right"></div>
      <span v-for="i in 14" :key="`rg-${i}`" :class="`r-bubble rg-${i}`"></span>
    </div>

    <!-- 试管主容器（纯净 Apple 灰边框，彻底消除任何蓝色提示框与黑色焦点框） -->
    <div
      class="reagent-choice-card"
      :class="{
        'state-initial': !choice,
        'state-yes': choice === 'yes',
        'state-no': choice === 'no',
        'is-disabled': disabled
      }"
    >
      <!-- 底层内容区：选择蓝色时露出无框输入框 -->
      <div class="underlay-content">
        <!-- 选择蓝色时呈现的纯净输入区域（无内部边框，柔缓渐隐露出，与右侧绿色凹形试剂自然融为一体） -->
        <div v-show="choice === 'yes'" class="underlay-input-wrap">
          <el-input
            :id="id"
            ref="input"
            :model-value="modelValue"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            :placeholder="placeholder"
            :disabled="disabled || choice !== 'yes'"
            class="chamber-seamless-textarea"
            @update:model-value="emit('update:modelValue', $event)"
          />
        </div>

        <!-- 选择绿色时呈现的纯文本寄语（绿色背景保留不显示白色，无标签，仅保留优雅文字，无任何黑色框） -->
        <div v-if="choice === 'no'" class="underlay-green-stage">
          <input
            type="hidden"
            :id="id"
            :value="modelValue"
          />
          <div class="pure-statement-container">
            <p class="pure-statement-text">{{ declinedText }}</p>
          </div>
        </div>
      </div>

      <!-- 顶层流体试剂交互层：保持元素常驻，完美模仿上方比赛按钮 cubic-bezier(0.25, 1, 0.5, 1) 的果冻质感过渡 -->
      <div class="fluid-interaction-layer">
        <!-- 蓝色试剂侧 (有 / 小露一手)：
             未选择时占 50% 宽，选有(yes)时柔缓淡出露出输入框，选无(no)时以同款果冻动效压缩至最左侧 52px
        -->
        <button
          type="button"
          class="fluid-side side-blue"
          :class="{
            'is-initial': !choice,
            'is-dissolved': choice === 'yes',
            'is-compressed': choice === 'no',
            'is-hovered': isHoverBlue
          }"
          :title="choice === 'no' ? `点击切换为：${yesLabel}` : ''"
          :disabled="disabled"
          @mouseenter="!choice && (isHoverBlue = true)"
          @mouseleave="!choice && (isHoverBlue = false)"
          @click="onSideClick('yes')"
        >
          <!-- 初始状态居中文本 -->
          <span v-if="!choice" class="fluid-label label-initial text-blue">{{ yesLabel }}</span>

          <!-- 压缩在左边缘时的【超柔缓渐进羽化凹形液面】（毛玻璃微澜，彻底消除生硬线条）与竖排标签 -->
          <svg
            v-if="choice === 'no'"
            class="concave-liquid-svg svg-blue"
            viewBox="0 0 52 124"
            preserveAspectRatio="none"
            aria-hidden="true"
          >
            <defs>
              <linearGradient id="softDiffuseBlueGrad" x1="1" y1="0" x2="0" y2="0">
                <stop offset="0%" stop-color="#e0f2fe" stop-opacity="0" />
                <stop offset="30%" stop-color="#e0f2fe" stop-opacity="0.18" />
                <stop offset="60%" stop-color="#e0f2fe" stop-opacity="0.55" />
                <stop offset="85%" stop-color="#e0f2fe" stop-opacity="0.88" />
                <stop offset="100%" stop-color="#e0f2fe" stop-opacity="0.96" />
              </linearGradient>
              <linearGradient id="ambientBlueRim" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="#ffffff" stop-opacity="0" />
                <stop offset="30%" stop-color="#bae6fd" stop-opacity="0.4" />
                <stop offset="70%" stop-color="#bae6fd" stop-opacity="0.4" />
                <stop offset="100%" stop-color="#ffffff" stop-opacity="0" />
              </linearGradient>
              <filter id="gentleFluidBlurBlue" x="-40%" y="-15%" width="180%" height="130%">
                <feGaussianBlur stdDeviation="3" />
              </filter>
            </defs>
            <path
              class="morphing-concave-path"
              fill="url(#softDiffuseBlueGrad)"
            >
              <animate
                attributeName="d"
                dur="4.2s"
                repeatCount="indefinite"
                values="
                  M 52 0 C 34 38, 34 86, 52 124 L 0 124 L 0 0 Z;
                  M 52 0 C 30 42, 37 82, 52 124 L 0 124 L 0 0 Z;
                  M 52 0 C 35 37, 32 87, 52 124 L 0 124 L 0 0 Z;
                  M 52 0 C 34 38, 34 86, 52 124 L 0 124 L 0 0 Z
                "
                calcMode="spline"
                keySplines="0.45 0 0.55 1; 0.45 0 0.55 1; 0.45 0 0.55 1"
              />
            </path>
            <path
              class="morphing-concave-feather"
              fill="none"
              stroke="url(#ambientBlueRim)"
              stroke-width="3"
              filter="url(#gentleFluidBlurBlue)"
              opacity="0.5"
            >
              <animate
                attributeName="d"
                dur="4.2s"
                repeatCount="indefinite"
                values="
                  M 52 0 C 34 38, 34 86, 52 124;
                  M 52 0 C 30 42, 37 82, 52 124;
                  M 52 0 C 35 37, 32 87, 52 124;
                  M 52 0 C 34 38, 34 86, 52 124
                "
                calcMode="spline"
                keySplines="0.45 0 0.55 1; 0.45 0 0.55 1; 0.45 0 0.55 1"
              />
            </path>
          </svg>
          <span v-if="choice === 'no'" class="fluid-label label-compressed text-blue">{{ yesLabel }}</span>
        </button>

        <!-- 试剂交汇液面 (Reagent Meniscus)：
             模仿比赛按钮的 .reagent-meniscus，随点击沿管体横向平滑滑动 (transition: left 0.48s cubic-bezier(0.25, 1, 0.5, 1))
             初始居中 50%，选有(yes)时随绿色压缩滑向右端，选无(no)时随蓝色压缩滑向左端
             两色共用同一条 S 曲线，0 缝隙，0 白线，悬浮各自全区域完整加深
        -->
        <div
          class="reagent-meniscus-tube"
          aria-hidden="true"
        >
          <svg class="s-seam-svg" viewBox="0 0 60 124" preserveAspectRatio="none">
            <!-- 蓝色咬合部：与左侧 side-blue 颜色完全一致，延伸至 S 曲线中线，悬浮时同步加深 -->
            <path
              class="seam-path seam-blue"
              :class="{ 'is-hovered': isHoverBlue }"
              :fill="isHoverBlue ? '#bae6fd' : '#e0f2fe'"
              @mouseenter="!choice && (isHoverBlue = true)"
              @mouseleave="!choice && (isHoverBlue = false)"
              @click.stop="onSideClick('yes')"
            >
              <animate
                attributeName="d"
                dur="4.5s"
                repeatCount="indefinite"
                values="
                  M 0 0 L 30 0 C 46 34, 14 90, 30 124 L 0 124 Z;
                  M 0 0 L 30 0 C 50 36, 10 88, 30 124 L 0 124 Z;
                  M 0 0 L 30 0 C 43 32, 17 92, 30 124 L 0 124 Z;
                  M 0 0 L 30 0 C 46 34, 14 90, 30 124 L 0 124 Z
                "
                calcMode="spline"
                keySplines="0.45 0 0.55 1; 0.45 0 0.55 1; 0.45 0 0.55 1"
              />
            </path>

            <!-- 绿色咬合部：与右侧 side-green 颜色完全一致，从 S 曲线中线延伸至右侧，无任何白色间隔 -->
            <path
              class="seam-path seam-green"
              :class="{ 'is-hovered': isHoverGreen }"
              :fill="isHoverGreen ? '#bbf7d0' : '#dcfce7'"
              @mouseenter="!choice && (isHoverGreen = true)"
              @mouseleave="!choice && (isHoverGreen = false)"
              @click.stop="onSideClick('no')"
            >
              <animate
                attributeName="d"
                dur="4.5s"
                repeatCount="indefinite"
                values="
                  M 60 0 L 30 0 C 46 34, 14 90, 30 124 L 60 124 Z;
                  M 60 0 L 30 0 C 50 36, 10 88, 30 124 L 60 124 Z;
                  M 60 0 L 30 0 C 43 32, 17 92, 30 124 L 60 124 Z;
                  M 60 0 L 30 0 C 46 34, 14 90, 30 124 L 60 124 Z
                "
                calcMode="spline"
                keySplines="0.45 0 0.55 1; 0.45 0 0.55 1; 0.45 0 0.55 1"
              />
            </path>
          </svg>
        </div>

        <!-- 绿色试剂侧 (无 / 不告诉你)：
             未选择时占 50% 宽，选有(yes)时以比赛按钮同款果冻动效压缩至最右侧 52px，选无(no)时展开保留绿色背景
        -->
        <button
          type="button"
          class="fluid-side side-green"
          :class="{
            'is-initial': !choice,
            'is-expanded': choice === 'no',
            'is-compressed': choice === 'yes',
            'is-hovered': isHoverGreen
          }"
          :title="choice === 'yes' ? `点击切换为：${noLabel}` : ''"
          :disabled="disabled"
          @mouseenter="!choice && (isHoverGreen = true)"
          @mouseleave="!choice && (isHoverGreen = false)"
          @click="onSideClick('no')"
        >
          <!-- 初始状态居中文本 -->
          <span v-if="!choice" class="fluid-label label-initial text-green">{{ noLabel }}</span>

          <!-- 压缩在右边缘时的【超柔缓渐进羽化凹形液面】（毛玻璃微澜，彻底消除生硬线条）与竖排标签 -->
          <svg
            v-if="choice === 'yes'"
            class="concave-liquid-svg svg-green"
            viewBox="0 0 52 124"
            preserveAspectRatio="none"
            aria-hidden="true"
          >
            <defs>
              <linearGradient id="softDiffuseGreenGrad" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#dcfce7" stop-opacity="0" />
                <stop offset="30%" stop-color="#dcfce7" stop-opacity="0.18" />
                <stop offset="60%" stop-color="#dcfce7" stop-opacity="0.55" />
                <stop offset="85%" stop-color="#dcfce7" stop-opacity="0.88" />
                <stop offset="100%" stop-color="#dcfce7" stop-opacity="0.96" />
              </linearGradient>
              <linearGradient id="ambientGreenRim" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="#ffffff" stop-opacity="0" />
                <stop offset="30%" stop-color="#bbf7d0" stop-opacity="0.4" />
                <stop offset="70%" stop-color="#bbf7d0" stop-opacity="0.4" />
                <stop offset="100%" stop-color="#ffffff" stop-opacity="0" />
              </linearGradient>
              <filter id="gentleFluidBlurGreen" x="-40%" y="-15%" width="180%" height="130%">
                <feGaussianBlur stdDeviation="3" />
              </filter>
            </defs>
            <path
              class="morphing-concave-path"
              fill="url(#softDiffuseGreenGrad)"
            >
              <animate
                attributeName="d"
                dur="4.2s"
                repeatCount="indefinite"
                values="
                  M 0 0 C 18 36, 18 88, 0 124 L 52 124 L 52 0 Z;
                  M 0 0 C 22 42, 15 82, 0 124 L 52 124 L 52 0 Z;
                  M 0 0 C 17 37, 20 87, 0 124 L 52 124 L 52 0 Z;
                  M 0 0 C 18 36, 18 88, 0 124 L 52 124 L 52 0 Z
                "
                calcMode="spline"
                keySplines="0.45 0 0.55 1; 0.45 0 0.55 1; 0.45 0 0.55 1"
              />
            </path>
            <path
              class="morphing-concave-feather"
              fill="none"
              stroke="url(#ambientGreenRim)"
              stroke-width="3"
              filter="url(#gentleFluidBlurGreen)"
              opacity="0.5"
            >
              <animate
                attributeName="d"
                dur="4.2s"
                repeatCount="indefinite"
                values="
                  M 0 0 C 18 36, 18 88, 0 124;
                  M 0 0 C 22 42, 15 82, 0 124;
                  M 0 0 C 17 37, 20 87, 0 124;
                  M 0 0 C 18 36, 18 88, 0 124
                "
                calcMode="spline"
                keySplines="0.45 0 0.55 1; 0.45 0 0.55 1; 0.45 0 0.55 1"
              />
            </path>
          </svg>
          <span v-if="choice === 'yes'" class="fluid-label label-compressed text-green">{{ noLabel }}</span>
        </button>
      </div>

      <!-- 隐藏重置节点供测试兼容 -->
      <button
        type="button"
        class="resume-choice-reset visually-hidden"
        aria-hidden="true"
        :disabled="disabled"
        @click="reset"
      >
        重新选择
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const props = defineProps({
  id: String,
  modelValue: String,
  choice: String,
  yesLabel: String,
  noLabel: String,
  placeholder: String,
  declinedText: String,
  negativeValue: { type: String, default: '' },
  disabled: Boolean
})

const emit = defineEmits(['update:modelValue', 'update:choice'])

const input = ref()
const isHoverBlue = ref(false)
const isHoverGreen = ref(false)

async function onSideClick(side) {
  if (props.disabled) return
  isHoverBlue.value = false
  isHoverGreen.value = false
  if (side === 'yes') {
    emit('update:choice', 'yes')
    if (props.modelValue === props.negativeValue) {
      emit('update:modelValue', '')
    }
    await nextTick()
    input.value?.focus()
  } else {
    emit('update:choice', 'no')
    emit('update:modelValue', props.negativeValue)
  }
}

function reset() {
  emit('update:choice', null)
  emit('update:modelValue', '')
  isHoverBlue.value = false
  isHoverGreen.value = false
}
</script>

<style scoped>
.reagent-choice-wrapper {
  position: relative;
  width: 100%;
  overflow: visible;
  margin-bottom: 8px;
}

/* 试管主卡片容器：保持沉静内敛的灰白 Apple 边框，彻底杜绝输入框选中时的蓝色外圈与黑框 */
.reagent-choice-card,
.reagent-choice-card.state-yes,
.reagent-choice-card.state-no,
.reagent-choice-card:focus,
.reagent-choice-card:focus-within {
  position: relative;
  width: 100%;
  min-height: 124px;
  border-radius: 14px;
  overflow: hidden;
  background: #ffffff;
  border: 1.5px solid #d2d2d7 !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03) !important;
  outline: none !important;
  user-select: none;
}

/* 底层内容布局 */
.underlay-content {
  position: absolute;
  inset: 0;
  z-index: 1;
  display: flex;
}

/* 输入框无缝集成于左侧画布，右侧留白保证文字与毛玻璃凹面试剂交融而不重叠 */
.underlay-input-wrap {
  width: 100%;
  height: 100%;
  padding-right: 64px;
  box-sizing: border-box;
  animation: contentGentleFadeIn 0.48s cubic-bezier(0.25, 1, 0.5, 1) both;
}

.chamber-seamless-textarea {
  width: 100%;
  height: 100%;
}

/* 彻底消除输入框被选中时的蓝色提示框、多余阴影与内部内描边 */
.chamber-seamless-textarea :deep(.el-textarea__inner),
.chamber-seamless-textarea :deep(.el-textarea__inner:focus),
.chamber-seamless-textarea :deep(.el-textarea__inner:focus-visible),
.chamber-seamless-textarea :deep(.el-textarea__inner:hover),
.chamber-seamless-textarea :deep(.el-textarea__inner:active),
.chamber-seamless-textarea.is-focus :deep(.el-textarea__inner) {
  height: 100% !important;
  min-height: 120px !important;
  border: none !important;
  border-radius: 0 !important;
  outline: none !important;
  box-shadow: none !important;
  background: transparent !important;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  padding: 14px 18px;
  color: #1e293b;
}

.chamber-seamless-textarea :deep(textarea:focus),
.chamber-seamless-textarea :deep(textarea:focus-visible) {
  outline: none !important;
  box-shadow: none !important;
  border: none !important;
}

.chamber-seamless-textarea :deep(.el-input__count) {
  background: transparent !important;
  right: 14px;
  bottom: 8px;
}

/* 选择绿色时，绿色试剂不消失，背景保持典雅淡绿，中央仅展现高雅纯净文字，无任何黑色边框 */
.underlay-green-stage,
.underlay-green-stage:focus,
.underlay-green-stage:focus-visible {
  width: 100%;
  height: 100%;
  padding-left: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  background: #dcfce7;
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  animation: contentGentleFadeIn 0.48s cubic-bezier(0.25, 1, 0.5, 1) both;
}

.pure-statement-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 24px;
  text-align: center;
  border: none !important;
  outline: none !important;
}

.pure-statement-text {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #15803d;
  letter-spacing: 0.8px;
  line-height: 1.5;
  border: none !important;
  outline: none !important;
  animation: statementSoftRise 0.5s cubic-bezier(0.25, 1, 0.5, 1) both;
}

@keyframes contentGentleFadeIn {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}

@keyframes statementSoftRise {
  0% {
    opacity: 0;
    transform: translateY(6px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ==========================================================================
   顶层流体试剂交互层：
   完全模仿上方比赛按钮动画机制：
   - 试剂侧采用 width 0.48s cubic-bezier(0.25, 1, 0.5, 1) 结合 active 缩放 (transform: scale(0.985))
   - 中间交汇液面沿管体横向平滑滑动 (left 0.48s cubic-bezier(0.25, 1, 0.5, 1))
   ========================================================================== */
.fluid-interaction-layer {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  display: flex;
}

/* 试剂色块：完全对标比赛按钮的流畅度 (0.48s cubic-bezier(0.25, 1, 0.5, 1)) 与轻微触觉回弹 */
.fluid-side,
.fluid-side:focus,
.fluid-side:focus-visible {
  position: absolute;
  top: 0;
  bottom: 0;
  border: none !important;
  outline: none !important;
  outline-offset: 0 !important;
  box-shadow: none !important;
  padding: 0;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  -webkit-tap-highlight-color: transparent !important;
  transition:
    width 0.48s cubic-bezier(0.25, 1, 0.5, 1),
    background-color 0.22s ease,
    opacity 0.42s cubic-bezier(0.25, 1, 0.5, 1),
    transform 0.15s ease;
}

/* 点击时的即时触觉微缩放响应（与比赛按钮一致） */
.fluid-side:active:not(.is-compressed):not(.is-dissolved) {
  transform: scale(0.985);
}

/* 蓝色试剂侧（默认 50% 宽，原色 #e0f2fe） */
.fluid-side.side-blue {
  left: 0;
  width: 50%;
  background-color: #e0f2fe;
  color: #0369a1;
  pointer-events: auto;
  cursor: pointer;
  z-index: 2;
}

/* 绿色试剂侧（默认 50% 宽，原色 #dcfce7） */
.fluid-side.side-green {
  right: 0;
  width: 50%;
  background-color: #dcfce7;
  color: #15803d;
  pointer-events: auto;
  cursor: pointer;
  z-index: 2;
}

/* 悬浮全区域完整加深（无任何矩形生硬截断，与 S 咬合部全幅联动同步加深） */
.side-blue.is-hovered,
.side-blue:hover:not(.is-dissolved):not(.is-compressed),
.fluid-interaction-layer:has(.side-blue:hover) .side-blue,
.fluid-interaction-layer:has(.seam-blue:hover) .side-blue {
  background-color: #bae6fd;
}

.side-green.is-hovered,
.side-green:hover:not(.is-compressed),
.fluid-interaction-layer:has(.side-green:hover) .side-green,
.fluid-interaction-layer:has(.seam-green:hover) .side-green {
  background-color: #bbf7d0;
}

/* --------------------------------------------------------------------------
   动态试剂交汇液面 (Reagent Meniscus Tube Interface)
   对标比赛按钮的 .reagent-meniscus：
   - 宽度 60px，默认 left: 50%
   - 随用户选择，以 left 0.48s cubic-bezier(0.25, 1, 0.5, 1) 平滑横向滑行动画
   - 选有(yes)时随绿色压缩滑向右端 (left: calc(100% - 26px))
   - 选无(no)时随蓝色压缩滑向左端 (left: 26px)
   - 蓝绿两色共用同一条 S 曲线边界，0 间隙，0 白线，悬浮各自全区域完整加深
   -------------------------------------------------------------------------- */
.reagent-meniscus-tube {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 60px;
  transform: translateX(-50%);
  z-index: 4;
  pointer-events: auto;
  transition:
    left 0.48s cubic-bezier(0.25, 1, 0.5, 1),
    opacity 0.45s ease;
}

.reagent-choice-card.state-yes .reagent-meniscus-tube {
  left: calc(100% - 26px);
  opacity: 0;
  pointer-events: none;
}

.reagent-choice-card.state-no .reagent-meniscus-tube {
  left: 26px;
  opacity: 0;
  pointer-events: none;
}

.s-seam-svg {
  width: 100%;
  height: 100%;
  display: block;
}

.seam-path {
  stroke: none !important;
  outline: none !important;
  cursor: pointer;
  transition: fill 0.22s ease;
}

.seam-blue {
  fill: #e0f2fe;
}

.seam-blue.is-hovered,
.seam-blue:hover,
.fluid-interaction-layer:has(.side-blue:hover) .seam-blue,
.fluid-interaction-layer:has(.seam-blue:hover) .seam-blue {
  fill: #bae6fd;
}

.seam-green {
  fill: #dcfce7;
}

.seam-green.is-hovered,
.seam-green:hover,
.fluid-interaction-layer:has(.side-green:hover) .seam-green,
.fluid-interaction-layer:has(.seam-green:hover) .seam-green {
  fill: #bbf7d0;
}

/* --------------------------------------------------------------------------
   状态分支：
   A. 选择蓝色 (state-yes):
      - 蓝色柔缓淡出消失（透明），平滑自然露出输入框
      - 绿色体积以 0.48s 丝滑果冻动效压缩至 52px
      - 压缩在右侧的绿色显示柔缓渐进羽化凹形毛玻璃液面
   -------------------------------------------------------------------------- */
.reagent-choice-card.state-yes .side-blue {
  width: calc(100% - 52px);
  background-color: transparent !important;
  opacity: 0;
  pointer-events: none;
}

.reagent-choice-card.state-yes .side-blue .label-initial {
  opacity: 0;
  transform: scale(0.92);
}

.reagent-choice-card.state-yes .side-green {
  width: 52px;
  background-color: transparent !important;
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
  cursor: pointer;
  pointer-events: auto;
  z-index: 5;
}

.reagent-choice-card.state-yes .side-green:hover .concave-liquid-svg {
  filter: brightness(1.05);
}

/* --------------------------------------------------------------------------
   B. 选择绿色 (state-no):
      - 绿色试剂不消失，舒缓展开铺满右侧（绿色保留），显示温馨寄语
      - 蓝色以 0.48s 丝滑果冻动效向左边缘压缩至 52px
      - 压缩在左侧的蓝色显示柔缓渐进羽化凹形毛玻璃液面
   -------------------------------------------------------------------------- */
.reagent-choice-card.state-no .side-green {
  width: calc(100% - 52px);
  background-color: transparent !important;
  pointer-events: none;
}

.reagent-choice-card.state-no .side-green .label-initial {
  opacity: 0;
  transform: scale(0.92);
}

.reagent-choice-card.state-no .side-blue {
  width: 52px;
  background-color: transparent !important;
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
  cursor: pointer;
  pointer-events: auto;
  z-index: 5;
}

.reagent-choice-card.state-no .side-blue:hover .concave-liquid-svg {
  filter: brightness(1.05);
}

/* 渐进毛玻璃凹形矢量试剂背景（微澜呼吸） */
.concave-liquid-svg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  display: block;
  pointer-events: none;
  animation: concaveFadeIn 0.45s cubic-bezier(0.25, 1, 0.5, 1) both;
}

@keyframes concaveFadeIn {
  0% {
    opacity: 0;
    transform: scaleX(0.85);
  }
  100% {
    opacity: 1;
    transform: scaleX(1);
  }
}

/* 文字标签样式：带有柔缓缩放与淡出过渡 */
.fluid-label.label-initial {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: opacity 0.3s ease, transform 0.4s cubic-bezier(0.25, 1, 0.5, 1);
  pointer-events: none;
}

.fluid-label.label-compressed {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  writing-mode: vertical-rl;
  text-orientation: upright;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 2px;
  pointer-events: none;
  z-index: 6;
  animation: compressedLabelFadeIn 0.45s cubic-bezier(0.25, 1, 0.5, 1) 0.08s both;
}

@keyframes compressedLabelFadeIn {
  0% {
    opacity: 0;
    transform: translateY(-50%) scale(0.85);
  }
  100% {
    opacity: 1;
    transform: translateY(-50%) scale(1);
  }
}

.side-blue .label-compressed {
  left: 12px;
  color: #0369a1;
}

.side-green .label-compressed {
  right: 12px;
  color: #15803d;
}

.text-blue {
  color: #0369a1;
}

.text-green {
  color: #15803d;
}

/* ==========================================================================
   试管外侧破裂喷射气泡特效 (Rupture Leak Spray)
   在被压缩一侧的外侧虚空区域柔缓喷逸，模拟试管微破后气体受浮力与气流缓缓升腾逸散
   ========================================================================== */
.rupture-spray {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 60px;
  pointer-events: none;
  z-index: 25;
}

.spray-left {
  left: -2px;
}

.spray-right {
  right: -2px;
}

.rupture-fissure {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 2px;
  height: 38px;
  border-radius: 99px;
  opacity: 0.65;
  filter: blur(0.5px);
  animation: fissurePulse 2.8s ease-in-out infinite;
}

.fissure-left {
  left: 0;
  background: linear-gradient(to bottom, transparent, #38bdf8 30%, #7dd3fc 70%, transparent);
}

.fissure-right {
  right: 0;
  background: linear-gradient(to bottom, transparent, #4ade80 30%, #86efac 70%, transparent);
}

@keyframes fissurePulse {
  0%, 100% {
    opacity: 0.3;
    transform: translateY(-50%) scaleY(0.8);
  }
  50% {
    opacity: 0.85;
    transform: translateY(-50%) scaleY(1.15);
  }
}

.r-bubble {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0;
  box-shadow: 0 0 6px rgba(255, 255, 255, 0.7);
}

/* 蓝色气泡向左侧外部虚空柔缓逸出 */
.spray-left .rb-1  { width: 5px; height: 5px; background: #38bdf8; top: 22%; left: 0; animation: ruptureFloatLeft 2.5s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.1s; }
.spray-left .rb-2  { width: 7px; height: 7px; background: #ffffff; top: 34%; left: 0; animation: ruptureFloatLeft 3.0s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.4s; }
.spray-left .rb-3  { width: 4px; height: 4px; background: #7dd3fc; top: 46%; left: 0; animation: ruptureFloatLeft 2.3s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.2s; }
.spray-left .rb-4  { width: 8px; height: 8px; background: #0284c7; top: 58%; left: 0; animation: ruptureFloatLeft 3.3s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.7s; }
.spray-left .rb-5  { width: 5px; height: 5px; background: #ffffff; top: 70%; left: 0; animation: ruptureFloatLeft 2.6s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.3s; }
.spray-left .rb-6  { width: 6px; height: 6px; background: #38bdf8; top: 28%; left: 0; animation: ruptureFloatLeft 2.8s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.9s; }
.spray-left .rb-7  { width: 4px; height: 4px; background: #bae6fd; top: 40%; left: 0; animation: ruptureFloatLeft 2.4s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.5s; }
.spray-left .rb-8  { width: 7px; height: 7px; background: #ffffff; top: 52%; left: 0; animation: ruptureFloatLeft 3.1s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.1s; }
.spray-left .rb-9  { width: 5px; height: 5px; background: #7dd3fc; top: 64%; left: 0; animation: ruptureFloatLeft 2.5s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.8s; }
.spray-left .rb-10 { width: 6px; height: 6px; background: #0ea5e9; top: 76%; left: 0; animation: ruptureFloatLeft 2.9s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.3s; }
.spray-left .rb-11 { width: 4px; height: 4px; background: #ffffff; top: 18%; left: 0; animation: ruptureFloatLeft 2.2s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.0s; }
.spray-left .rb-12 { width: 6px; height: 6px; background: #38bdf8; top: 82%; left: 0; animation: ruptureFloatLeft 2.8s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.5s; }
.spray-left .rb-13 { width: 3px; height: 3px; background: #7dd3fc; top: 38%; left: 0; animation: ruptureFloatLeft 2.1s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.45s; }
.spray-left .rb-14 { width: 5px; height: 5px; background: #ffffff; top: 60%; left: 0; animation: ruptureFloatLeft 2.7s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.85s; }

@keyframes ruptureFloatLeft {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.3);
  }
  25% {
    opacity: 0.95;
    transform: translate(-12px, -8px) scale(1.05);
  }
  70% {
    opacity: 0.75;
    transform: translate(-28px, -24px) scale(1.15);
  }
  100% {
    opacity: 0;
    transform: translate(-42px, -38px) scale(0.2);
  }
}

/* 绿色气泡向右侧外部虚空柔缓逸出 */
.spray-right .rg-1  { width: 5px; height: 5px; background: #4ade80; top: 22%; right: 0; animation: ruptureFloatRight 2.5s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.1s; }
.spray-right .rg-2  { width: 7px; height: 7px; background: #ffffff; top: 34%; right: 0; animation: ruptureFloatRight 3.0s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.4s; }
.spray-right .rg-3  { width: 4px; height: 4px; background: #86efac; top: 46%; right: 0; animation: ruptureFloatRight 2.3s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.2s; }
.spray-right .rg-4  { width: 8px; height: 8px; background: #16a34a; top: 58%; right: 0; animation: ruptureFloatRight 3.3s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.7s; }
.spray-right .rg-5  { width: 5px; height: 5px; background: #ffffff; top: 70%; right: 0; animation: ruptureFloatRight 2.6s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.3s; }
.spray-right .rg-6  { width: 6px; height: 6px; background: #4ade80; top: 28%; right: 0; animation: ruptureFloatRight 2.8s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.9s; }
.spray-right .rg-7  { width: 4px; height: 4px; background: #bbf7d0; top: 40%; right: 0; animation: ruptureFloatRight 2.4s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.5s; }
.spray-right .rg-8  { width: 7px; height: 7px; background: #ffffff; top: 52%; right: 0; animation: ruptureFloatRight 3.1s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.1s; }
.spray-right .rg-9  { width: 5px; height: 5px; background: #86efac; top: 64%; right: 0; animation: ruptureFloatRight 2.5s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.8s; }
.spray-right .rg-10 { width: 6px; height: 6px; background: #22c55e; top: 76%; right: 0; animation: ruptureFloatRight 2.9s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.3s; }
.spray-right .rg-11 { width: 4px; height: 4px; background: #ffffff; top: 18%; right: 0; animation: ruptureFloatRight 2.2s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.0s; }
.spray-right .rg-12 { width: 6px; height: 6px; background: #4ade80; top: 82%; right: 0; animation: ruptureFloatRight 2.8s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 1.5s; }
.spray-right .rg-13 { width: 3px; height: 3px; background: #86efac; top: 38%; right: 0; animation: ruptureFloatRight 2.1s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.45s; }
.spray-right .rg-14 { width: 5px; height: 5px; background: #ffffff; top: 60%; right: 0; animation: ruptureFloatRight 2.7s cubic-bezier(0.2, 0.8, 0.25, 1) infinite 0.85s; }

@keyframes ruptureFloatRight {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.3);
  }
  25% {
    opacity: 0.95;
    transform: translate(12px, -8px) scale(1.05);
  }
  70% {
    opacity: 0.75;
    transform: translate(28px, -24px) scale(1.15);
  }
  100% {
    opacity: 0;
    transform: translate(42px, -38px) scale(0.2);
  }
}

.visually-hidden {
  position: absolute !important;
  width: 1px !important;
  height: 1px !important;
  padding: 0 !important;
  margin: -1px !important;
  overflow: hidden !important;
  clip: rect(0, 0, 0, 0) !important;
  white-space: nowrap !important;
  border: 0 !important;
  opacity: 0 !important;
  pointer-events: none !important;
}
</style>
