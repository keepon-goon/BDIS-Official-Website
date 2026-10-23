<template>
  <div class="reagent-choice-wrapper" :data-choice="choice || 'pending'">
    <!-- 破裂外喷气泡群（所有的气泡均从单一破口高压高速喷涌，绝无多个漏洞） -->
    <!-- 状态为“无/不告诉你”时：蓝色被压缩在最左侧，向左外侧区域自单一破口高速高密喷逸 -->
    <div v-if="choice === 'no'" class="rupture-spray spray-left" aria-hidden="true">
      <div class="rupture-nozzle">
        <div class="rupture-fissure"></div>
        <span v-for="i in 32" :key="`rb-${i}`" :class="`r-bubble rb-${i}`"></span>
      </div>
    </div>

    <!-- 状态为“有/小露一手”时：绿色被压缩在最右侧，向右外侧区域自单一破口高速高密喷逸 -->
    <div v-if="choice === 'yes'" class="rupture-spray spray-right" aria-hidden="true">
      <div class="rupture-nozzle">
        <div class="rupture-fissure"></div>
        <span v-for="i in 32" :key="`rg-${i}`" :class="`r-bubble rg-${i}`"></span>
      </div>
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

      <!-- 顶层流体试剂交互层：
           采用 0.72s cubic-bezier(0.34, 1.45, 0.45, 1) 慢速超弹果冻回弹曲线，动作极其明显且 Q 弹饱满
      -->
      <div class="fluid-interaction-layer">
        <!-- 蓝色试剂侧 (有 / 小露一手)：
             未选择时占 50% 宽，选有(yes)时柔缓淡出露出输入框，选无(no)时以 Q 弹果冻动效压缩至最左侧 52px
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

          <!-- 压缩在左边缘时的【Q 弹微澜羽化凹形液面】（附带果冻回弹挤压动效）与竖排标签 -->
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
                <stop offset="30%" stop-color="#e0f2fe" stop-opacity="0.35" />
                <stop offset="60%" stop-color="#e0f2fe" stop-opacity="0.8" />
                <stop offset="85%" stop-color="#e0f2fe" stop-opacity="0.95" />
                <stop offset="100%" stop-color="#e0f2fe" stop-opacity="1" />
              </linearGradient>
              <linearGradient id="ambientBlueRim" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="#ffffff" stop-opacity="0" />
                <stop offset="30%" stop-color="#bae6fd" stop-opacity="0.45" />
                <stop offset="70%" stop-color="#bae6fd" stop-opacity="0.45" />
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

        <!-- 试剂交汇液面 (Reagent Meniscus Tube Interface)：
             模仿比赛按钮的 .reagent-meniscus，随点击沿管体横向平滑滑动 (0.72s cubic-bezier(0.34, 1.45, 0.45, 1))
             整个滑行过程中保持高度清晰可见，滑至目的地后自然交接
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
             未选择时占 50% 宽，选有(yes)时以 Q 弹果冻动效压缩至最右侧 52px，选无(no)时展开保留绿色背景
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

          <!-- 压缩在右边缘时的【Q 弹微澜羽化凹形液面】（附带果冻回弹挤压动效）与竖排标签 -->
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
                <stop offset="30%" stop-color="#dcfce7" stop-opacity="0.22" />
                <stop offset="60%" stop-color="#dcfce7" stop-opacity="0.6" />
                <stop offset="85%" stop-color="#dcfce7" stop-opacity="0.9" />
                <stop offset="100%" stop-color="#dcfce7" stop-opacity="0.98" />
              </linearGradient>
              <linearGradient id="ambientGreenRim" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stop-color="#ffffff" stop-opacity="0" />
                <stop offset="30%" stop-color="#bbf7d0" stop-opacity="0.45" />
                <stop offset="70%" stop-color="#bbf7d0" stop-opacity="0.45" />
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
  animation: contentGentleFadeIn 0.68s cubic-bezier(0.25, 1, 0.5, 1) both;
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
  animation: contentGentleFadeIn 0.68s cubic-bezier(0.25, 1, 0.5, 1) both;
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
  animation: statementSoftRise 0.68s cubic-bezier(0.34, 1.45, 0.45, 1) both;
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
    transform: translateY(10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ==========================================================================
   顶层流体试剂交互层：
   完全对齐并强化 Q 弹果冻动效机制：
   - 采用 0.72s cubic-bezier(0.34, 1.45, 0.45, 1) 慢速回弹曲线，超冲后回弹微晃
   - 交互按压带有轻微的弹性挤压反馈 (transform: scale(0.975) scaleY(0.96))
   - 中间交汇液面沿管体全程高度清晰横向滑行
   ========================================================================== */
.fluid-interaction-layer {
  position: absolute;
  inset: 0;
  z-index: 10;
  pointer-events: none;
  display: flex;
}

/* 试剂色块：慢速 Q 弹果冻过渡，超冲缓冲回弹 */
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
    width 0.72s cubic-bezier(0.34, 1.45, 0.45, 1),
    background 0.45s ease,
    opacity 0.52s ease,
    transform 0.18s cubic-bezier(0.2, 0.8, 0.3, 1);
}

/* 点击时的即时触觉 Q 弹微挤压响应 */
.fluid-side:active:not(.is-compressed):not(.is-dissolved) {
  transform: scale(0.975) scaleY(0.96);
}

/* 蓝色试剂侧（默认 50% 宽，原色 #e0f2fe） */
.fluid-side.side-blue {
  left: 0;
  width: 50%;
  background: #e0f2fe;
  color: #0369a1;
  pointer-events: auto;
  cursor: pointer;
  z-index: 2;
}

/* 绿色试剂侧（默认 50% 宽，原色 #dcfce7） */
.fluid-side.side-green {
  right: 0;
  width: 50%;
  background: #dcfce7;
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
  background: #bae6fd;
}

.side-green.is-hovered,
.side-green:hover:not(.is-compressed),
.fluid-interaction-layer:has(.side-green:hover) .side-green,
.fluid-interaction-layer:has(.seam-green:hover) .side-green {
  background: #bbf7d0;
}

/* --------------------------------------------------------------------------
   动态试剂交汇液面 (Reagent Meniscus Tube Interface)
   - 随点击沿管体横向平滑滑动 (0.72s cubic-bezier(0.34, 1.45, 0.45, 1))
   - 在滑动过程中保持全幅高能见度，直至抵达端点后才轻柔消隐交接
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
    left 0.72s cubic-bezier(0.34, 1.45, 0.45, 1),
    opacity 0.28s ease 0.44s;
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
  transition: fill 0.25s ease;
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
      - 绿色体积以 0.72s 慢速 Q 弹超冲曲线向右压缩至 52px，并在触壁时产生弹性果冻微震
      - 绿色块在滑行收缩全程保持鲜活饱满的绿色流体质感
   -------------------------------------------------------------------------- */
.reagent-choice-card.state-yes .side-blue {
  width: calc(100% - 52px);
  background: transparent !important;
  opacity: 0;
  pointer-events: none !important;
}

.reagent-choice-card.state-yes .side-blue .label-initial {
  opacity: 0;
  transform: scale(0.9);
}

.reagent-choice-card.state-yes .side-green {
  width: 52px;
  background: linear-gradient(to right, transparent 0%, #dcfce7 16px) !important;
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
  cursor: pointer;
  pointer-events: auto !important;
  z-index: 12;
}

.reagent-choice-card.state-yes .side-green:hover {
  background: linear-gradient(to right, transparent 0%, #bbf7d0 16px) !important;
}

.reagent-choice-card.state-yes .side-green:hover .concave-liquid-svg {
  filter: brightness(1.06);
}

.reagent-choice-card.state-yes .side-green:active {
  transform: scale(0.96);
}

.reagent-choice-card.state-yes .reagent-meniscus-tube * {
  pointer-events: none !important;
}

/* --------------------------------------------------------------------------
   B. 选择绿色 (state-no):
      - 绿色试剂舒缓展开铺满右侧（绿色保留），显示温馨寄语
      - 蓝色以 0.72s 慢速 Q 弹超冲曲线向左压缩至 52px，并在触壁时产生弹性果冻微震
      - side-green 设为透明以完全透出并突显 underlay-green-stage 中的纯净寄语
   -------------------------------------------------------------------------- */
.reagent-choice-card.state-no .side-green {
  width: calc(100% - 52px);
  background: transparent !important;
  opacity: 0;
  pointer-events: none !important;
}

.reagent-choice-card.state-no .side-green .label-initial {
  opacity: 0;
  transform: scale(0.9);
}

.reagent-choice-card.state-no .side-blue {
  width: 52px;
  background: linear-gradient(to right, #e0f2fe 0%, #e0f2fe 36px, transparent 52px) !important;
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
  cursor: pointer;
  pointer-events: auto !important;
  z-index: 12;
}

.reagent-choice-card.state-no .side-blue:hover {
  background: linear-gradient(to right, #bae6fd 0%, #bae6fd 36px, transparent 52px) !important;
}

.reagent-choice-card.state-no .side-blue:hover .concave-liquid-svg {
  filter: brightness(1.06);
}

.reagent-choice-card.state-no .side-blue:active {
  transform: scale(0.96);
}

.reagent-choice-card.state-no .reagent-meniscus-tube * {
  pointer-events: none !important;
}

/* 渐进毛玻璃凹形矢量试剂背景（微澜呼吸 + 果冻触壁回弹 Squash & Stretch） */
.concave-liquid-svg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  display: block;
  pointer-events: none;
}

.svg-green {
  transform-origin: right center;
  animation: jellyWobbleRight 0.72s cubic-bezier(0.34, 1.45, 0.45, 1) both;
}

.svg-blue {
  transform-origin: left center;
  animation: jellyWobbleLeft 0.72s cubic-bezier(0.34, 1.45, 0.45, 1) both;
}

@keyframes jellyWobbleRight {
  0% {
    transform: scaleX(0.68);
    opacity: 0.4;
  }
  42% {
    transform: scaleX(1.26);
    opacity: 1;
  }
  68% {
    transform: scaleX(0.91);
  }
  86% {
    transform: scaleX(1.05);
  }
  100% {
    transform: scaleX(1);
    opacity: 1;
  }
}

@keyframes jellyWobbleLeft {
  0% {
    transform: scaleX(0.68);
    opacity: 0.4;
  }
  42% {
    transform: scaleX(1.26);
    opacity: 1;
  }
  68% {
    transform: scaleX(0.91);
  }
  86% {
    transform: scaleX(1.05);
  }
  100% {
    transform: scaleX(1);
    opacity: 1;
  }
}

/* 文字标签样式：带有柔缓缩放与淡出过渡 */
.fluid-label.label-initial {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: opacity 0.35s ease, transform 0.45s cubic-bezier(0.34, 1.45, 0.45, 1);
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
  animation: compressedLabelJellyFadeIn 0.72s cubic-bezier(0.34, 1.45, 0.45, 1) 0.12s both;
}

@keyframes compressedLabelJellyFadeIn {
  0% {
    opacity: 0;
    transform: translateY(-50%) scale(0.7);
  }
  50% {
    opacity: 0.9;
    transform: translateY(-50%) scale(1.15);
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
   试管外侧高压喷射气泡特效 (High-Pressure Rupture Spray)
   单一破口单点喷发、高密度超喷射流，32 枚气泡全自中心同一破口疾速喷涌出
   ========================================================================== */
.rupture-spray {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 140px;
  pointer-events: none;
  z-index: 25;
}

.spray-left {
  left: 0;
}

.spray-right {
  right: 0;
}

/* 唯一的破口中心喷嘴（保证所有气泡 100% 自同一原点射出） */
.rupture-nozzle {
  position: absolute;
  top: 50%;
  width: 0;
  height: 0;
}

.spray-left .rupture-nozzle {
  left: 0;
}

.spray-right .rupture-nozzle {
  right: 0;
}

/* 唯一的破口微裂缝（单点聚焦破损） */
.rupture-fissure {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 3.5px;
  height: 20px;
  border-radius: 99px;
  opacity: 0.95;
  filter: blur(0.3px);
  animation: fissurePulse 0.9s ease-in-out infinite;
}

.spray-left .rupture-fissure {
  left: -2px;
  background: linear-gradient(to bottom, transparent, #38bdf8 30%, #ffffff 50%, #0284c7 70%, transparent);
  box-shadow: 0 0 10px rgba(56, 189, 248, 0.95), 0 0 4px #ffffff;
}

.spray-right .rupture-fissure {
  right: -2px;
  background: linear-gradient(to bottom, transparent, #4ade80 30%, #ffffff 50%, #16a34a 70%, transparent);
  box-shadow: 0 0 10px rgba(74, 222, 128, 0.95), 0 0 4px #ffffff;
}

@keyframes fissurePulse {
  0%, 100% {
    opacity: 0.6;
    transform: translateY(-50%) scaleY(0.85);
  }
  50% {
    opacity: 1;
    transform: translateY(-50%) scaleY(1.2);
  }
}

/* 所有气泡 100% 绝对统一起点：唯一原点 (0, 0) */
.r-bubble {
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.85), inset 0 0 3px rgba(255, 255, 255, 0.9);
}

/* 左侧自单点喷射轨迹 1: 远距水平疾速喷射 */
@keyframes ruptureJetFarLeft {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.2);
  }
  12% {
    opacity: 1;
    transform: translate(-32px, -6px) scale(1.25);
  }
  50% {
    opacity: 0.92;
    transform: translate(-76px, -18px) scale(1.05);
  }
  80% {
    opacity: 0.6;
    transform: translate(-104px, -34px) scale(0.8);
  }
  100% {
    opacity: 0;
    transform: translate(-124px, -48px) scale(0.1);
  }
}

/* 左侧自单点喷射轨迹 2: 高角斜上飞扬 */
@keyframes ruptureJetHighLeft {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.2);
  }
  15% {
    opacity: 1;
    transform: translate(-26px, -24px) scale(1.3);
  }
  55% {
    opacity: 0.88;
    transform: translate(-64px, -58px) scale(1.0);
  }
  85% {
    opacity: 0.5;
    transform: translate(-88px, -82px) scale(0.7);
  }
  100% {
    opacity: 0;
    transform: translate(-105px, -102px) scale(0.1);
  }
}

/* 左侧自单点喷射轨迹 3: 低角冲出后浮起 */
@keyframes ruptureJetLowLeft {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.2);
  }
  14% {
    opacity: 1;
    transform: translate(-28px, 16px) scale(1.2);
  }
  50% {
    opacity: 0.9;
    transform: translate(-68px, 12px) scale(1.0);
  }
  80% {
    opacity: 0.55;
    transform: translate(-96px, -8px) scale(0.75);
  }
  100% {
    opacity: 0;
    transform: translate(-116px, -24px) scale(0.1);
  }
}

/* 左侧自单点喷射轨迹 4: 超疾速爆发微气泡 */
@keyframes ruptureJetFastLeft {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.1);
  }
  10% {
    opacity: 1;
    transform: translate(-36px, -10px) scale(1.35);
  }
  55% {
    opacity: 0.78;
    transform: translate(-78px, -26px) scale(0.85);
  }
  100% {
    opacity: 0;
    transform: translate(-102px, -38px) scale(0.1);
  }
}

/* 蓝色气泡喷射群 (32枚气泡全部自单点 (0, 0) 射出，绝无分散漏洞) */
.spray-left .rb-1  { width: 5px; height: 5px; margin: -2.5px 0 0 -2.5px; background: #38bdf8; animation: ruptureJetFarLeft 0.85s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.02s; }
.spray-left .rb-2  { width: 8px; height: 8px; margin: -4px 0 0 -4px; background: #ffffff; animation: ruptureJetHighLeft 0.98s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.06s; }
.spray-left .rb-3  { width: 4px; height: 4px; margin: -2px 0 0 -2px; background: #7dd3fc; animation: ruptureJetLowLeft 0.92s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.04s; }
.spray-left .rb-4  { width: 11px; height: 11px; margin: -5.5px 0 0 -5.5px; background: #0284c7; animation: ruptureJetFarLeft 1.15s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.1s; }
.spray-left .rb-5  { width: 6px; height: 6px; margin: -3px 0 0 -3px; background: #ffffff; animation: ruptureJetFastLeft 0.72s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.01s; }
.spray-left .rb-6  { width: 9px; height: 9px; margin: -4.5px 0 0 -4.5px; background: #38bdf8; animation: ruptureJetLowLeft 1.05s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.12s; }
.spray-left .rb-7  { width: 4px; height: 4px; margin: -2px 0 0 -2px; background: #bae6fd; animation: ruptureJetHighLeft 0.82s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.08s; }
.spray-left .rb-8  { width: 12px; height: 12px; margin: -6px 0 0 -6px; background: #ffffff; animation: ruptureJetFarLeft 1.25s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.15s; }
.spray-left .rb-9  { width: 5px; height: 5px; margin: -2.5px 0 0 -2.5px; background: #7dd3fc; animation: ruptureJetHighLeft 0.95s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.18s; }
.spray-left .rb-10 { width: 7px; height: 7px; margin: -3.5px 0 0 -3.5px; background: #0ea5e9; animation: ruptureJetLowLeft 1.02s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.2s; }
.spray-left .rb-11 { width: 3px; height: 3px; margin: -1.5px 0 0 -1.5px; background: #ffffff; animation: ruptureJetFastLeft 0.65s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.05s; }
.spray-left .rb-12 { width: 8px; height: 8px; margin: -4px 0 0 -4px; background: #38bdf8; animation: ruptureJetFarLeft 0.96s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.24s; }
.spray-left .rb-13 { width: 4px; height: 4px; margin: -2px 0 0 -2px; background: #7dd3fc; animation: ruptureJetFastLeft 0.7s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.14s; }
.spray-left .rb-14 { width: 6px; height: 6px; margin: -3px 0 0 -3px; background: #ffffff; animation: ruptureJetHighLeft 1.05s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.28s; }
.spray-left .rb-15 { width: 10px; height: 10px; margin: -5px 0 0 -5px; background: #0284c7; animation: ruptureJetLowLeft 1.18s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.3s; }
.spray-left .rb-16 { width: 5px; height: 5px; margin: -2.5px 0 0 -2.5px; background: #38bdf8; animation: ruptureJetFarLeft 0.88s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.22s; }
.spray-left .rb-17 { width: 7px; height: 7px; margin: -3.5px 0 0 -3.5px; background: #bae6fd; animation: ruptureJetLowLeft 1.08s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.34s; }
.spray-left .rb-18 { width: 3px; height: 3px; margin: -1.5px 0 0 -1.5px; background: #ffffff; animation: ruptureJetHighLeft 0.76s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.16s; }
.spray-left .rb-19 { width: 9px; height: 9px; margin: -4.5px 0 0 -4.5px; background: #0ea5e9; animation: ruptureJetFarLeft 1.12s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.38s; }
.spray-left .rb-20 { width: 4px; height: 4px; margin: -2px 0 0 -2px; background: #7dd3fc; animation: ruptureJetFastLeft 0.68s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.26s; }
.spray-left .rb-21 { width: 6px; height: 6px; margin: -3px 0 0 -3px; background: #ffffff; animation: ruptureJetHighLeft 0.92s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.42s; }
.spray-left .rb-22 { width: 13px; height: 13px; margin: -6.5px 0 0 -6.5px; background: #38bdf8; animation: ruptureJetFarLeft 1.28s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.46s; }
.spray-left .rb-23 { width: 5px; height: 5px; margin: -2.5px 0 0 -2.5px; background: #0284c7; animation: ruptureJetLowLeft 0.98s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.36s; }
.spray-left .rb-24 { width: 4px; height: 4px; margin: -2px 0 0 -2px; background: #ffffff; animation: ruptureJetFastLeft 0.72s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.32s; }
.spray-left .rb-25 { width: 7px; height: 7px; margin: -3.5px 0 0 -3.5px; background: #7dd3fc; animation: ruptureJetHighLeft 0.95s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.48s; }
.spray-left .rb-26 { width: 8px; height: 8px; margin: -4px 0 0 -4px; background: #bae6fd; animation: ruptureJetLowLeft 1.1s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.5s; }
.spray-left .rb-27 { width: 3px; height: 3px; margin: -1.5px 0 0 -1.5px; background: #ffffff; animation: ruptureJetFastLeft 0.62s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.08s; }
.spray-left .rb-28 { width: 6px; height: 6px; margin: -3px 0 0 -3px; background: #0ea5e9; animation: ruptureJetFarLeft 0.92s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.4s; }
.spray-left .rb-29 { width: 9px; height: 9px; margin: -4.5px 0 0 -4.5px; background: #ffffff; animation: ruptureJetHighLeft 1.16s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.54s; }
.spray-left .rb-30 { width: 4px; height: 4px; margin: -2px 0 0 -2px; background: #38bdf8; animation: ruptureJetLowLeft 0.86s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.24s; }
.spray-left .rb-31 { width: 5px; height: 5px; margin: -2.5px 0 0 -2.5px; background: #7dd3fc; animation: ruptureJetFastLeft 0.75s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.44s; }
.spray-left .rb-32 { width: 7px; height: 7px; margin: -3.5px 0 0 -3.5px; background: #ffffff; animation: ruptureJetFarLeft 1.05s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.56s; }

/* 右侧自单点喷射轨迹 1: 远距水平疾速喷射 */
@keyframes ruptureJetFarRight {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.2);
  }
  12% {
    opacity: 1;
    transform: translate(32px, -6px) scale(1.25);
  }
  50% {
    opacity: 0.92;
    transform: translate(76px, -18px) scale(1.05);
  }
  80% {
    opacity: 0.6;
    transform: translate(104px, -34px) scale(0.8);
  }
  100% {
    opacity: 0;
    transform: translate(124px, -48px) scale(0.1);
  }
}

/* 右侧自单点喷射轨迹 2: 高角斜上飞扬 */
@keyframes ruptureJetHighRight {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.2);
  }
  15% {
    opacity: 1;
    transform: translate(26px, -24px) scale(1.3);
  }
  55% {
    opacity: 0.88;
    transform: translate(64px, -58px) scale(1.0);
  }
  85% {
    opacity: 0.5;
    transform: translate(88px, -82px) scale(0.7);
  }
  100% {
    opacity: 0;
    transform: translate(105px, -102px) scale(0.1);
  }
}

/* 右侧自单点喷射轨迹 3: 低角冲出后浮起 */
@keyframes ruptureJetLowRight {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.2);
  }
  14% {
    opacity: 1;
    transform: translate(28px, 16px) scale(1.2);
  }
  50% {
    opacity: 0.9;
    transform: translate(68px, 12px) scale(1.0);
  }
  80% {
    opacity: 0.55;
    transform: translate(96px, -8px) scale(0.75);
  }
  100% {
    opacity: 0;
    transform: translate(116px, -24px) scale(0.1);
  }
}

/* 右侧自单点喷射轨迹 4: 超疾速爆发微气泡 */
@keyframes ruptureJetFastRight {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.1);
  }
  10% {
    opacity: 1;
    transform: translate(36px, -10px) scale(1.35);
  }
  55% {
    opacity: 0.78;
    transform: translate(78px, -26px) scale(0.85);
  }
  100% {
    opacity: 0;
    transform: translate(102px, -38px) scale(0.1);
  }
}

/* 绿色气泡喷射群 (32枚气泡全部自单点 (0, 0) 射出，绝无分散漏洞) */
.spray-right .rg-1  { width: 5px; height: 5px; margin: -2.5px -2.5px 0 0; background: #4ade80; animation: ruptureJetFarRight 0.85s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.02s; }
.spray-right .rg-2  { width: 8px; height: 8px; margin: -4px -4px 0 0; background: #ffffff; animation: ruptureJetHighRight 0.98s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.06s; }
.spray-right .rg-3  { width: 4px; height: 4px; margin: -2px -2px 0 0; background: #86efac; animation: ruptureJetLowRight 0.92s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.04s; }
.spray-right .rg-4  { width: 11px; height: 11px; margin: -5.5px -5.5px 0 0; background: #16a34a; animation: ruptureJetFarRight 1.15s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.1s; }
.spray-right .rg-5  { width: 6px; height: 6px; margin: -3px -3px 0 0; background: #ffffff; animation: ruptureJetFastRight 0.72s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.01s; }
.spray-right .rg-6  { width: 9px; height: 9px; margin: -4.5px -4.5px 0 0; background: #4ade80; animation: ruptureJetLowRight 1.05s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.12s; }
.spray-right .rg-7  { width: 4px; height: 4px; margin: -2px -2px 0 0; background: #bbf7d0; animation: ruptureJetHighRight 0.82s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.08s; }
.spray-right .rg-8  { width: 12px; height: 12px; margin: -6px -6px 0 0; background: #ffffff; animation: ruptureJetFarRight 1.25s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.15s; }
.spray-right .rg-9  { width: 5px; height: 5px; margin: -2.5px -2.5px 0 0; background: #86efac; animation: ruptureJetHighRight 0.95s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.18s; }
.spray-right .rg-10 { width: 7px; height: 7px; margin: -3.5px -3.5px 0 0; background: #22c55e; animation: ruptureJetLowRight 1.02s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.2s; }
.spray-right .rg-11 { width: 3px; height: 3px; margin: -1.5px -1.5px 0 0; background: #ffffff; animation: ruptureJetFastRight 0.65s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.05s; }
.spray-right .rg-12 { width: 8px; height: 8px; margin: -4px -4px 0 0; background: #4ade80; animation: ruptureJetFarRight 0.96s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.24s; }
.spray-right .rg-13 { width: 4px; height: 4px; margin: -2px -2px 0 0; background: #86efac; animation: ruptureJetFastRight 0.7s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.14s; }
.spray-right .rg-14 { width: 6px; height: 6px; margin: -3px -3px 0 0; background: #ffffff; animation: ruptureJetHighRight 1.05s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.28s; }
.spray-right .rg-15 { width: 10px; height: 10px; margin: -5px -5px 0 0; background: #16a34a; animation: ruptureJetLowRight 1.18s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.3s; }
.spray-right .rg-16 { width: 5px; height: 5px; margin: -2.5px -2.5px 0 0; background: #4ade80; animation: ruptureJetFarRight 0.88s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.22s; }
.spray-right .rg-17 { width: 7px; height: 7px; margin: -3.5px -3.5px 0 0; background: #bbf7d0; animation: ruptureJetLowRight 1.08s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.34s; }
.spray-right .rg-18 { width: 3px; height: 3px; margin: -1.5px -1.5px 0 0; background: #ffffff; animation: ruptureJetHighRight 0.76s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.16s; }
.spray-right .rg-19 { width: 9px; height: 9px; margin: -4.5px -4.5px 0 0; background: #22c55e; animation: ruptureJetFarRight 1.12s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.38s; }
.spray-right .rg-20 { width: 4px; height: 4px; margin: -2px -2px 0 0; background: #86efac; animation: ruptureJetFastRight 0.68s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.26s; }
.spray-right .rg-21 { width: 6px; height: 6px; margin: -3px -3px 0 0; background: #ffffff; animation: ruptureJetHighRight 0.92s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.42s; }
.spray-right .rg-22 { width: 13px; height: 13px; margin: -6.5px -6.5px 0 0; background: #4ade80; animation: ruptureJetFarRight 1.28s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.46s; }
.spray-right .rg-23 { width: 5px; height: 5px; margin: -2.5px -2.5px 0 0; background: #16a34a; animation: ruptureJetLowRight 0.98s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.36s; }
.spray-right .rg-24 { width: 4px; height: 4px; margin: -2px -2px 0 0; background: #ffffff; animation: ruptureJetFastRight 0.72s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.32s; }
.spray-right .rg-25 { width: 7px; height: 7px; margin: -3.5px -3.5px 0 0; background: #86efac; animation: ruptureJetHighRight 0.95s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.48s; }
.spray-right .rg-26 { width: 8px; height: 8px; margin: -4px -4px 0 0; background: #bbf7d0; animation: ruptureJetLowRight 1.1s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.5s; }
.spray-right .rg-27 { width: 3px; height: 3px; margin: -1.5px -1.5px 0 0; background: #ffffff; animation: ruptureJetFastRight 0.62s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.08s; }
.spray-right .rg-28 { width: 6px; height: 6px; margin: -3px -3px 0 0; background: #22c55e; animation: ruptureJetFarRight 0.92s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.4s; }
.spray-right .rg-29 { width: 9px; height: 9px; margin: -4.5px -4.5px 0 0; background: #ffffff; animation: ruptureJetHighRight 1.16s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.54s; }
.spray-right .rg-30 { width: 4px; height: 4px; margin: -2px -2px 0 0; background: #4ade80; animation: ruptureJetLowRight 0.86s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.24s; }
.spray-right .rg-31 { width: 5px; height: 5px; margin: -2.5px -2.5px 0 0; background: #86efac; animation: ruptureJetFastRight 0.75s cubic-bezier(0.08, 0.85, 0.2, 1) infinite 0.44s; }
.spray-right .rg-32 { width: 7px; height: 7px; margin: -3.5px -3.5px 0 0; background: #ffffff; animation: ruptureJetFarRight 1.05s cubic-bezier(0.12, 0.8, 0.25, 1) infinite 0.56s; }

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
