<template>
  <div class="reagent-choice-wrapper" :data-choice="choice || 'pending'">
    <!-- 破裂外喷气泡群（位于外层 overflow: visible 区域，像试管壁破损向外侧虚空喷发） -->
    <!-- 状态为“无/不告诉你”时：蓝色被压缩在最左侧，向左外侧区域剧烈喷射 -->
    <div v-if="choice === 'no'" class="rupture-spray spray-left" aria-hidden="true">
      <div class="rupture-fissure fissure-left"></div>
      <span v-for="i in 14" :key="`rb-${i}`" :class="`r-bubble rb-${i}`"></span>
    </div>

    <!-- 状态为“有/小露一手”时：绿色被压缩在最右侧，向右外侧区域剧烈喷射 -->
    <div v-if="choice === 'yes'" class="rupture-spray spray-right" aria-hidden="true">
      <div class="rupture-fissure fissure-right"></div>
      <span v-for="i in 14" :key="`rg-${i}`" :class="`r-bubble rg-${i}`"></span>
    </div>

    <!-- 试管主容器 -->
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
        <!-- 选择蓝色时呈现的纯净输入区域（无内部多余边框，与右侧绿色试剂自然融为一体） -->
        <div v-show="choice === 'yes' || !choice" class="underlay-input-wrap">
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

        <!-- 选择绿色时呈现的纯文本寄语（绿色背景保留不显示白色，无标签，仅保留优雅文字） -->
        <div v-if="choice === 'no'" class="underlay-green-stage">
          <el-input
            :id="id"
            ref="inputHidden"
            :model-value="modelValue"
            type="textarea"
            class="visually-hidden"
            @update:model-value="emit('update:modelValue', $event)"
          />
          <div class="pure-statement-container">
            <p class="pure-statement-text">{{ declinedText }}</p>
          </div>
        </div>
      </div>

      <!-- 顶层流体试剂交互层 -->
      <div class="fluid-interaction-layer">
        <!-- 蓝色试剂侧 (有 / 小露一手) -->
        <button
          type="button"
          class="fluid-side side-blue"
          :class="{
            'is-initial': !choice,
            'is-dissolved': choice === 'yes',
            'is-compressed': choice === 'no'
          }"
          :title="choice === 'no' ? `点击切换为：${yesLabel}` : ''"
          :disabled="disabled"
          @click="onSideClick('yes')"
        >
          <!-- 初始状态居中字样 -->
          <span v-if="!choice" class="fluid-label label-initial">{{ yesLabel }}</span>

          <!-- 压缩在左边缘时呈现的果冻质感竖排标签（颜色依旧保持柔和蓝色，不加深） -->
          <span v-if="choice === 'no'" class="fluid-label label-compressed">{{ yesLabel }}</span>
        </button>

        <!-- 初始状态：双向冲击晃动膜（上下两端均严格固定，中段如弹性琴弦高频振荡，无任何气泡） -->
        <div v-if="!choice" class="fixed-ends-membrane" aria-hidden="true">
          <svg class="membrane-svg" viewBox="0 0 30 124" preserveAspectRatio="none">
            <defs>
              <linearGradient id="membraneGrad" x1="0" y1="0" x2="1" y2="0">
                <stop offset="0%" stop-color="#0ea5e9" stop-opacity="0.8" />
                <stop offset="50%" stop-color="#ffffff" stop-opacity="0.95" />
                <stop offset="100%" stop-color="#22c55e" stop-opacity="0.8" />
              </linearGradient>
            </defs>

            <!-- 晃动柔光外晕 -->
            <path
              class="membrane-glow-path"
              fill="none"
              stroke="url(#membraneGrad)"
              stroke-width="6"
              stroke-linecap="round"
              opacity="0.45"
            >
              <animate
                attributeName="d"
                dur="1.35s"
                repeatCount="indefinite"
                values="
                  M 15 0 C 8 36, 22 88, 15 124;
                  M 15 0 C 22 36, 8 88, 15 124;
                  M 15 0 C 9 36, 21 88, 15 124;
                  M 15 0 C 21 36, 9 88, 15 124;
                  M 15 0 C 8 36, 22 88, 15 124
                "
                calcMode="spline"
                keySplines="0.4 0 0.2 1; 0.4 0 0.2 1; 0.4 0 0.2 1; 0.4 0 0.2 1"
              />
            </path>

            <!-- 晃动核心白色冲击膜（上下严格锁定在 15 0 与 15 124） -->
            <path
              class="membrane-core-path"
              fill="none"
              stroke="#ffffff"
              stroke-width="2.5"
              stroke-linecap="round"
            >
              <animate
                attributeName="d"
                dur="1.35s"
                repeatCount="indefinite"
                values="
                  M 15 0 C 8 36, 22 88, 15 124;
                  M 15 0 C 22 36, 8 88, 15 124;
                  M 15 0 C 9 36, 21 88, 15 124;
                  M 15 0 C 21 36, 9 88, 15 124;
                  M 15 0 C 8 36, 22 88, 15 124
                "
                calcMode="spline"
                keySplines="0.4 0 0.2 1; 0.4 0 0.2 1; 0.4 0 0.2 1; 0.4 0 0.2 1"
              />
            </path>

            <!-- 上端严格固定锚栓点 -->
            <circle cx="15" cy="1.5" r="2.5" fill="#ffffff" stroke="#94a3b8" stroke-width="1" />
            <!-- 下端严格固定锚栓点 -->
            <circle cx="15" cy="122.5" r="2.5" fill="#ffffff" stroke="#94a3b8" stroke-width="1" />
          </svg>
        </div>

        <!-- 绿色试剂侧 (无 / 不告诉你) -->
        <button
          type="button"
          class="fluid-side side-green"
          :class="{
            'is-initial': !choice,
            'is-expanded-green': choice === 'no',
            'is-compressed': choice === 'yes'
          }"
          :title="choice === 'yes' ? `点击切换为：${noLabel}` : ''"
          :disabled="disabled"
          @click="onSideClick('no')"
        >
          <!-- 初始状态居中字样 -->
          <span v-if="!choice" class="fluid-label label-initial">{{ noLabel }}</span>

          <!-- 压缩在右边缘时呈现的果冻质感竖排标签（颜色依旧保持柔和绿色，不加深） -->
          <span v-if="choice === 'yes'" class="fluid-label label-compressed">{{ noLabel }}</span>
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
const inputHidden = ref()

async function onSideClick(side) {
  if (props.disabled) return
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
}
</script>

<style scoped>
.reagent-choice-wrapper {
  position: relative;
  width: 100%;
  overflow: visible;
  margin-bottom: 8px;
}

/* 试管主卡片容器 */
.reagent-choice-card {
  position: relative;
  width: 100%;
  min-height: 124px;
  border-radius: 14px;
  overflow: hidden;
  background: #ffffff;
  border: 1.5px solid #d2d2d7;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  transition: border-color 0.28s ease, box-shadow 0.28s ease;
  user-select: none;
}

.reagent-choice-card.state-yes {
  border-color: #7dd3fc;
  box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.15), 0 4px 14px rgba(0, 0, 0, 0.04);
}

.reagent-choice-card.state-no {
  border-color: #86efac;
  box-shadow: 0 0 0 3px rgba(74, 222, 128, 0.15), 0 4px 14px rgba(0, 0, 0, 0.04);
}

/* 底层内容布局 */
.underlay-content {
  position: absolute;
  inset: 0;
  z-index: 1;
  display: flex;
}

/* 输入框无缝集成于左侧画布，右侧留白保证文字与绿色试剂交融而不重叠 */
.underlay-input-wrap {
  width: 100%;
  height: 100%;
  padding-right: 56px;
  box-sizing: border-box;
}

.chamber-seamless-textarea {
  width: 100%;
  height: 100%;
}

.chamber-seamless-textarea :deep(.el-textarea__inner) {
  height: 100% !important;
  min-height: 120px !important;
  border: none !important;
  border-radius: 0 !important;
  box-shadow: none !important;
  background: transparent !important;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  padding: 14px 18px;
  color: #1e293b;
}

.chamber-seamless-textarea :deep(.el-input__count) {
  background: transparent !important;
  right: 14px;
  bottom: 8px;
}

/* 选择绿色时，绿色试剂不消失，背景保持典雅淡绿，中央仅展现高雅纯净文字 */
.underlay-green-stage {
  width: 100%;
  height: 100%;
  padding-left: 54px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  background: #dcfce7;
  animation: bdisFadeIn 0.35s ease;
}

.pure-statement-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 24px;
  text-align: center;
}

.pure-statement-text {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #15803d;
  letter-spacing: 0.8px;
  line-height: 1.5;
}

/* 顶层流体试剂交互层 */
.fluid-interaction-layer {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  display: flex;
}

/* 试剂色块：果冻质感弹性过渡 cubic-bezier(0.34, 1.56, 0.64, 1) */
.fluid-side {
  position: absolute;
  top: 0;
  bottom: 0;
  border: none;
  padding: 0;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  transition: width 0.5s cubic-bezier(0.34, 1.56, 0.64, 1), background-color 0.35s ease, opacity 0.3s ease;
}

/* 蓝色试剂侧（默认与压缩态均保持 #e0f2fe 原色，颜色不加深） */
.fluid-side.side-blue {
  left: 0;
  width: 50%;
  background: #e0f2fe;
  color: #0369a1;
  pointer-events: auto;
  cursor: pointer;
}

.fluid-side.side-blue:hover:not(.is-dissolved):not(.is-compressed) {
  background: #bae6fd;
}

/* 绿色试剂侧（默认与压缩态均保持 #dcfce7 原色，颜色不加深） */
.fluid-side.side-green {
  right: 0;
  width: 50%;
  background: #dcfce7;
  color: #15803d;
  pointer-events: auto;
  cursor: pointer;
}

.fluid-side.side-green:hover:not(.is-compressed) {
  background: #bbf7d0;
}

/* 选择蓝色状态：
   1. 蓝色自身颜色消失（透明），直接露出输入框
   2. 绿色体积压缩至右边缘（约 42px，果冻弹性过渡）
   3. 绿色试剂保持原淡绿色（不加深），内侧边缘呈微渐变弯月液面，与输入框自然交融
*/
.reagent-choice-card.state-yes .side-blue {
  width: calc(100% - 42px);
  background: transparent !important;
  pointer-events: none;
}

.reagent-choice-card.state-yes .side-blue .label-initial {
  opacity: 0;
}

.reagent-choice-card.state-yes .side-green {
  width: 44px;
  background: linear-gradient(90deg, rgba(220, 252, 231, 0.1) 0%, rgba(220, 252, 231, 0.7) 30%, #dcfce7 65%, #dcfce7 100%);
  border-left: 1.5px solid rgba(134, 239, 172, 0.5);
  border-top-left-radius: 20px;
  border-bottom-left-radius: 20px;
  cursor: pointer;
  pointer-events: auto;
  box-shadow: -3px 0 10px rgba(220, 252, 231, 0.5);
}

.reagent-choice-card.state-yes .side-green:hover {
  background: #dcfce7;
  transform: scaleX(1.03);
}

/* 选择绿色状态：
   1. 绿色试剂不消失，展开铺满右侧（绿色保留）
   2. 蓝色压缩至最左侧（约 42px，果冻弹性过渡）
   3. 蓝色试剂保持原淡蓝色（不加深），外边缘呈弯月微弧
*/
.reagent-choice-card.state-no .side-green {
  width: calc(100% - 42px);
  background: transparent !important;
  pointer-events: none;
}

.reagent-choice-card.state-no .side-green .label-initial {
  opacity: 0;
}

.reagent-choice-card.state-no .side-blue {
  width: 44px;
  background: linear-gradient(270deg, rgba(224, 242, 254, 0.1) 0%, rgba(224, 242, 254, 0.7) 30%, #e0f2fe 65%, #e0f2fe 100%);
  border-right: 1.5px solid rgba(186, 230, 253, 0.6);
  border-top-right-radius: 20px;
  border-bottom-right-radius: 20px;
  cursor: pointer;
  pointer-events: auto;
  box-shadow: 3px 0 10px rgba(224, 242, 254, 0.5);
}

.reagent-choice-card.state-no .side-blue:hover {
  background: #e0f2fe;
  transform: scaleX(1.03);
}

/* 文字标签样式 */
.fluid-label.label-initial {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: opacity 0.2s ease;
}

.fluid-label.label-compressed {
  writing-mode: vertical-rl;
  text-orientation: upright;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 2px;
  pointer-events: none;
  z-index: 3;
}

.side-blue .label-compressed {
  color: #0369a1;
}

.side-green .label-compressed {
  color: #15803d;
}

/* 初始状态：上下两端均固定的动态冲击隔断膜（无气泡） */
.fixed-ends-membrane {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 30px;
  transform: translateX(-50%);
  z-index: 5;
  pointer-events: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.membrane-svg {
  width: 100%;
  height: 100%;
  display: block;
}

/* ==========================================================================
   试管外侧破裂喷射气泡特效 (Rupture Leak Spray)
   在被压缩一侧的外侧虚空区域大量喷出，模拟试管壁破裂高压泄出的真实物理效果
   ========================================================================== */
.rupture-spray {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 0;
  overflow: visible;
  pointer-events: none;
  z-index: 25;
}

/* 左侧破口外喷（图二左侧区域喷出） */
.spray-left {
  left: -2px;
}

/* 右侧破口外喷 */
.spray-right {
  right: -2px;
}

/* 破裂缝隙光斑微动效 */
.rupture-fissure {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 48px;
  border-radius: 99px;
  filter: blur(1px);
}

.fissure-left {
  left: 0;
  background: linear-gradient(180deg, transparent, #38bdf8, #ffffff, #38bdf8, transparent);
  box-shadow: -2px 0 8px rgba(56, 189, 248, 0.8);
}

.fissure-right {
  right: 0;
  background: linear-gradient(180deg, transparent, #4ade80, #ffffff, #4ade80, transparent);
  box-shadow: 2px 0 8px rgba(74, 222, 128, 0.8);
}

/* 破损气泡基础样式 */
.r-bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0;
}

/* 蓝色气泡向左侧外部虚空狂喷 */
.spray-left .rb-1  { width: 5px; height: 5px; background: #38bdf8; top: 22%; left: 0; animation: ruptureShootLeft 1.4s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.1s; }
.spray-left .rb-2  { width: 7px; height: 7px; background: #ffffff; top: 34%; left: 0; animation: ruptureShootLeft 1.7s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.3s; }
.spray-left .rb-3  { width: 4px; height: 4px; background: #7dd3fc; top: 46%; left: 0; animation: ruptureShootLeft 1.3s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.15s; }
.spray-left .rb-4  { width: 8px; height: 8px; background: #0284c7; top: 58%; left: 0; animation: ruptureShootLeft 1.9s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.5s; }
.spray-left .rb-5  { width: 5px; height: 5px; background: #ffffff; top: 70%; left: 0; animation: ruptureShootLeft 1.5s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.25s; }
.spray-left .rb-6  { width: 6px; height: 6px; background: #38bdf8; top: 28%; left: 0; animation: ruptureShootLeft 1.6s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.7s; }
.spray-left .rb-7  { width: 4px; height: 4px; background: #bae6fd; top: 40%; left: 0; animation: ruptureShootLeft 1.3s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.4s; }
.spray-left .rb-8  { width: 7px; height: 7px; background: #ffffff; top: 52%; left: 0; animation: ruptureShootLeft 1.8s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.8s; }
.spray-left .rb-9  { width: 5px; height: 5px; background: #7dd3fc; top: 64%; left: 0; animation: ruptureShootLeft 1.4s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.6s; }
.spray-left .rb-10 { width: 6px; height: 6px; background: #0ea5e9; top: 76%; left: 0; animation: ruptureShootLeft 1.7s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 1.0s; }
.spray-left .rb-11 { width: 4px; height: 4px; background: #ffffff; top: 18%; left: 0; animation: ruptureShootLeft 1.2s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.9s; }
.spray-left .rb-12 { width: 6px; height: 6px; background: #38bdf8; top: 82%; left: 0; animation: ruptureShootLeft 1.6s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 1.1s; }
.spray-left .rb-13 { width: 3px; height: 3px; background: #7dd3fc; top: 38%; left: 0; animation: ruptureShootLeft 1.1s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.35s; }
.spray-left .rb-14 { width: 5px; height: 5px; background: #ffffff; top: 60%; left: 0; animation: ruptureShootLeft 1.5s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.65s; }

@keyframes ruptureShootLeft {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.3);
  }
  20% {
    opacity: 0.95;
    transform: translate(-10px, -6px) scale(1.1);
  }
  65% {
    opacity: 0.8;
    transform: translate(-26px, -18px) scale(1);
  }
  100% {
    opacity: 0;
    transform: translate(-42px, -32px) scale(0.2);
  }
}

/* 绿色气泡向右侧外部虚空狂喷 */
.spray-right .rg-1  { width: 5px; height: 5px; background: #4ade80; top: 22%; right: 0; animation: ruptureShootRight 1.4s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.1s; }
.spray-right .rg-2  { width: 7px; height: 7px; background: #ffffff; top: 34%; right: 0; animation: ruptureShootRight 1.7s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.3s; }
.spray-right .rg-3  { width: 4px; height: 4px; background: #86efac; top: 46%; right: 0; animation: ruptureShootRight 1.3s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.15s; }
.spray-right .rg-4  { width: 8px; height: 8px; background: #16a34a; top: 58%; right: 0; animation: ruptureShootRight 1.9s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.5s; }
.spray-right .rg-5  { width: 5px; height: 5px; background: #ffffff; top: 70%; right: 0; animation: ruptureShootRight 1.5s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.25s; }
.spray-right .rg-6  { width: 6px; height: 6px; background: #4ade80; top: 28%; right: 0; animation: ruptureShootRight 1.6s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.7s; }
.spray-right .rg-7  { width: 4px; height: 4px; background: #bbf7d0; top: 40%; right: 0; animation: ruptureShootRight 1.3s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.4s; }
.spray-right .rg-8  { width: 7px; height: 7px; background: #ffffff; top: 52%; right: 0; animation: ruptureShootRight 1.8s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.8s; }
.spray-right .rg-9  { width: 5px; height: 5px; background: #86efac; top: 64%; right: 0; animation: ruptureShootRight 1.4s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.6s; }
.spray-right .rg-10 { width: 6px; height: 6px; background: #22c55e; top: 76%; right: 0; animation: ruptureShootRight 1.7s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 1.0s; }
.spray-right .rg-11 { width: 4px; height: 4px; background: #ffffff; top: 18%; right: 0; animation: ruptureShootRight 1.2s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.9s; }
.spray-right .rg-12 { width: 6px; height: 6px; background: #4ade80; top: 82%; right: 0; animation: ruptureShootRight 1.6s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 1.1s; }
.spray-right .rg-13 { width: 3px; height: 3px; background: #86efac; top: 38%; right: 0; animation: ruptureShootRight 1.1s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.35s; }
.spray-right .rg-14 { width: 5px; height: 5px; background: #ffffff; top: 60%; right: 0; animation: ruptureShootRight 1.5s cubic-bezier(0.2, 0.8, 0.3, 1) infinite 0.65s; }

@keyframes ruptureShootRight {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.3);
  }
  20% {
    opacity: 0.95;
    transform: translate(10px, -6px) scale(1.1);
  }
  65% {
    opacity: 0.8;
    transform: translate(26px, -18px) scale(1);
  }
  100% {
    opacity: 0;
    transform: translate(42px, -32px) scale(0.2);
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
