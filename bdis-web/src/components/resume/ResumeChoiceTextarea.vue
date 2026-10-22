<template>
  <div
    class="reagent-choice-card"
    :class="{
      'state-initial': !choice,
      'state-yes': choice === 'yes',
      'state-no': choice === 'no',
      'is-disabled': disabled
    }"
    :data-choice="choice || 'pending'"
  >
    <!-- 底层内容（输入框或婉拒卡片，完全位于液体选择图层之下） -->
    <div class="underlay-content">
      <!-- 选择蓝色（有/小露一手）后，蓝色液体消失，直接露出输入框 -->
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
          class="chamber-textarea"
          @update:model-value="emit('update:modelValue', $event)"
        />
      </div>

      <!-- 选择绿色（无/不告诉你）后，绿色液体消失，露出婉拒寄语 -->
      <div v-if="choice === 'no'" class="underlay-declined-wrap">
        <el-input
          :id="id"
          ref="inputHidden"
          :model-value="modelValue"
          type="textarea"
          class="visually-hidden"
          @update:model-value="emit('update:modelValue', $event)"
        />
        <div class="declined-card-inner">
          <span class="declined-pill">已选择：{{ noLabel }}</span>
          <p class="declined-quote">{{ declinedText }}</p>
          <span class="declined-tip">（点击最左侧蓝色色条可重新展开填写）</span>
        </div>
      </div>
    </div>

    <!-- 顶层液体交互层（双色液体、中心振动隔断膜、挤压气泡喷射） -->
    <div class="liquid-overlay">
      <!-- 蓝色试剂层 (有 / 小露一手) -->
      <button
        type="button"
        class="liquid-side side-blue"
        :class="{
          'is-active-fill': !choice,
          'is-dissolved': choice === 'yes',
          'is-compressed': choice === 'no'
        }"
        :title="choice === 'no' ? `点击切换为：${yesLabel}` : ''"
        :disabled="disabled"
        @click="onSideClick('yes')"
      >
        <!-- 初始状态居中文本 -->
        <span v-if="!choice" class="liquid-label label-initial">{{ yesLabel }}</span>

        <!-- 压缩在最左侧时的竖排指示文本与切换按钮 -->
        <span v-if="choice === 'no'" class="liquid-label label-compressed">{{ yesLabel }}</span>

        <!-- 压缩在最左侧时，超自己左方喷出的大量蓝色气泡 -->
        <div v-if="choice === 'no'" class="spray-bubble-chamber spray-to-left">
          <span v-for="i in 12" :key="`bb-${i}`" :class="`s-bubble bb-${i}`"></span>
        </div>
      </button>

      <!-- 两个液体公用的振动边界膜（仅在初始状态显示，无冒泡，膜本身在一直晃动被双方冲击） -->
      <div v-if="!choice" class="fluid-dividing-membrane">
        <div class="membrane-glow"></div>
        <div class="membrane-line"></div>
        <div class="membrane-impact-pulse"></div>
      </div>

      <!-- 绿色试剂层 (无 / 不告诉你) -->
      <button
        type="button"
        class="liquid-side side-green"
        :class="{
          'is-active-fill': !choice,
          'is-dissolved': choice === 'no',
          'is-compressed': choice === 'yes'
        }"
        :title="choice === 'yes' ? `点击切换为：${noLabel}` : ''"
        :disabled="disabled"
        @click="onSideClick('no')"
      >
        <!-- 初始状态居中文本 -->
        <span v-if="!choice" class="liquid-label label-initial">{{ noLabel }}</span>

        <!-- 压缩在最右侧时的竖排指示文本与切换按钮 -->
        <span v-if="choice === 'yes'" class="liquid-label label-compressed">{{ noLabel }}</span>

        <!-- 压缩在最右侧时，超自己右方喷出的大量绿色气泡 -->
        <div v-if="choice === 'yes'" class="spray-bubble-chamber spray-to-right">
          <span v-for="i in 12" :key="`gb-${i}`" :class="`s-bubble gb-${i}`"></span>
        </div>
      </button>
    </div>

    <!-- 隐藏的重置节点（保证测试兼容性） -->
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
.reagent-choice-card {
  position: relative;
  width: 100%;
  min-height: 124px;
  border-radius: 14px;
  overflow: hidden;
  background: #ffffff;
  border: 1.5px solid #d2d2d7;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
  user-select: none;
}

.reagent-choice-card.state-yes {
  border-color: #0284c7;
  box-shadow: 0 0 0 3px rgba(2, 132, 199, 0.12), 0 4px 12px rgba(2, 132, 199, 0.06);
}

.reagent-choice-card.state-no {
  border-color: #16a34a;
  box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.12), 0 4px 12px rgba(22, 163, 74, 0.06);
}

/* 底层内容区域（输入框与婉拒卡片位于此处） */
.underlay-content {
  position: absolute;
  inset: 0;
  z-index: 1;
  display: flex;
}

.underlay-input-wrap {
  width: 100%;
  height: 100%;
  padding-right: 42px;
  box-sizing: border-box;
}

.chamber-textarea {
  width: 100%;
  height: 100%;
}

.chamber-textarea :deep(.el-textarea__inner) {
  height: 100% !important;
  min-height: 120px !important;
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  padding: 14px 16px;
  color: #1e293b;
}

.chamber-textarea :deep(.el-input__count) {
  background: transparent !important;
  right: 12px;
  bottom: 8px;
}

.underlay-declined-wrap {
  width: 100%;
  height: 100%;
  padding-left: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  background: #f8fafc;
  animation: bdisFadeIn 0.3s ease;
}

.declined-card-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 16px;
  text-align: center;
}

.declined-pill {
  font-size: 12px;
  font-weight: 600;
  color: #15803d;
  background: #dcfce7;
  padding: 2px 12px;
  border-radius: 980px;
}

.declined-quote {
  margin: 2px 0 0;
  font-size: 15px;
  color: #334155;
  font-weight: 600;
}

.declined-tip {
  font-size: 12px;
  color: #94a3b8;
}

/* 顶层液体遮罩交互层 */
.liquid-overlay {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
  display: flex;
}

.liquid-side {
  position: absolute;
  top: 0;
  bottom: 0;
  border: none;
  padding: 0;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-sizing: border-box;
  transition: width 0.42s cubic-bezier(0.25, 1, 0.5, 1), background-color 0.35s ease, opacity 0.3s ease, transform 0.2s ease;
}

/* 蓝色试剂侧 */
.liquid-side.side-blue {
  left: 0;
  width: 50%;
  background: #e0f2fe;
  color: #0369a1;
  pointer-events: auto;
  cursor: pointer;
}

.liquid-side.side-blue:hover:not(.is-dissolved):not(.is-compressed) {
  background: #bae6fd;
}

/* 绿色试剂侧 */
.liquid-side.side-green {
  right: 0;
  width: 50%;
  background: #dcfce7;
  color: #15803d;
  pointer-events: auto;
  cursor: pointer;
}

.liquid-side.side-green:hover:not(.is-dissolved):not(.is-compressed) {
  background: #bbf7d0;
}

/* 选中蓝色：蓝色液体消失露出输入框，绿色压缩到最右侧（宽度压缩到现在的一半：约 34px） */
.reagent-choice-card.state-yes .side-blue {
  width: calc(100% - 34px);
  background: transparent !important;
  pointer-events: none;
}

.reagent-choice-card.state-yes .side-blue .label-initial {
  opacity: 0;
}

.reagent-choice-card.state-yes .side-green {
  width: 34px;
  background: #22c55e;
  cursor: pointer;
  pointer-events: auto;
  box-shadow: -2px 0 8px rgba(34, 197, 94, 0.3);
}

.reagent-choice-card.state-yes .side-green:hover {
  background: #16a34a;
}

/* 选中绿色：绿色液体消失露出寄语，蓝色压缩到最左侧（宽度压缩到现在的一半：约 34px） */
.reagent-choice-card.state-no .side-green {
  width: calc(100% - 34px);
  background: transparent !important;
  pointer-events: none;
}

.reagent-choice-card.state-no .side-green .label-initial {
  opacity: 0;
}

.reagent-choice-card.state-no .side-blue {
  width: 34px;
  background: #0284c7;
  cursor: pointer;
  pointer-events: auto;
  box-shadow: 2px 0 8px rgba(2, 132, 199, 0.3);
}

.reagent-choice-card.state-no .side-blue:hover {
  background: #0369a1;
}

/* 标签样式 */
.liquid-label.label-initial {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: opacity 0.25s ease;
}

.liquid-label.label-compressed {
  writing-mode: vertical-rl;
  text-orientation: upright;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 2px;
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
  pointer-events: none;
  z-index: 3;
}

/* 两个液体公用的振动隔断膜（初始状态显示，晃动激烈，无冒泡） */
.fluid-dividing-membrane {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 14px;
  transform: translateX(-50%);
  z-index: 5;
  pointer-events: none;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: membraneOscillate 1.3s ease-in-out infinite alternate;
}

.membrane-line {
  width: 3.5px;
  height: 100%;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(255, 255, 255, 0.6) 20%,
    rgba(255, 255, 255, 1) 50%,
    rgba(255, 255, 255, 0.6) 80%,
    rgba(255, 255, 255, 0.95) 100%
  );
  border-radius: 99px;
  box-shadow:
    -2px 0 6px rgba(14, 165, 233, 0.85),
    2px 0 6px rgba(34, 197, 94, 0.85),
    0 0 5px #ffffff;
}

.membrane-glow {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    90deg,
    rgba(14, 165, 233, 0.35) 0%,
    rgba(255, 255, 255, 0.75) 50%,
    rgba(34, 197, 94, 0.35) 100%
  );
  filter: blur(5px);
}

.membrane-impact-pulse {
  position: absolute;
  width: 100%;
  height: 44px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.95) 0%, transparent 75%);
  animation: membraneNodeScan 2.1s ease-in-out infinite alternate;
}

@keyframes membraneOscillate {
  0% {
    transform: translateX(calc(-50% - 4px)) scaleX(0.85) skewX(-2.5deg);
  }
  25% {
    transform: translateX(calc(-50% + 5px)) scaleX(1.35) skewX(3deg);
  }
  50% {
    transform: translateX(calc(-50% - 3px)) scaleX(0.9) skewX(-1.8deg);
  }
  75% {
    transform: translateX(calc(-50% + 4px)) scaleX(1.25) skewX(2.2deg);
  }
  100% {
    transform: translateX(calc(-50% - 5px)) scaleX(1.1) skewX(-2.8deg);
  }
}

@keyframes membraneNodeScan {
  0% { top: 5%; opacity: 0.5; }
  100% { top: 70%; opacity: 0.9; }
}

/* 喷射气泡容器 */
.spray-bubble-chamber {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 2;
}

.s-bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0;
}

/* 绿色试剂被挤压时超自己右方喷射的大量气泡 */
.spray-to-right .gb-1  { width: 4px; height: 4px; background: #86efac; top: 16%; left: 4px; animation: sprayRight 1.3s ease-out infinite 0.1s; }
.spray-to-right .gb-2  { width: 6px; height: 6px; background: #ffffff; top: 28%; left: 6px; animation: sprayRight 1.6s ease-out infinite 0.4s; }
.spray-to-right .gb-3  { width: 5px; height: 5px; background: #4ade80; top: 42%; left: 3px; animation: sprayRight 1.4s ease-out infinite 0.2s; }
.spray-to-right .gb-4  { width: 7px; height: 7px; background: #bbf7d0; top: 58%; left: 5px; animation: sprayRight 1.8s ease-out infinite 0.6s; }
.spray-to-right .gb-5  { width: 4px; height: 4px; background: #ffffff; top: 74%; left: 8px; animation: sprayRight 1.2s ease-out infinite 0.3s; }
.spray-to-right .gb-6  { width: 6px; height: 6px; background: #22c55e; top: 22%; left: 2px; animation: sprayRight 1.5s ease-out infinite 0.8s; }
.spray-to-right .gb-7  { width: 5px; height: 5px; background: #86efac; top: 36%; left: 7px; animation: sprayRight 1.4s ease-out infinite 0.5s; }
.spray-to-right .gb-8  { width: 7px; height: 7px; background: #ffffff; top: 50%; left: 4px; animation: sprayRight 1.9s ease-out infinite 0.9s; }
.spray-to-right .gb-9  { width: 4px; height: 4px; background: #4ade80; top: 66%; left: 6px; animation: sprayRight 1.3s ease-out infinite 0.7s; }
.spray-to-right .gb-10 { width: 5px; height: 5px; background: #bbf7d0; top: 82%; left: 3px; animation: sprayRight 1.6s ease-out infinite 1.1s; }
.spray-to-right .gb-11 { width: 6px; height: 6px; background: #ffffff; top: 10%; left: 5px; animation: sprayRight 1.4s ease-out infinite 1.0s; }
.spray-to-right .gb-12 { width: 4px; height: 4px; background: #86efac; top: 90%; left: 7px; animation: sprayRight 1.3s ease-out infinite 1.2s; }

@keyframes sprayRight {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.3);
  }
  20% {
    opacity: 0.95;
    transform: translate(4px, -3px) scale(1);
  }
  60% {
    opacity: 0.85;
    transform: translate(12px, -8px) scale(1.1);
  }
  100% {
    opacity: 0;
    transform: translate(20px, -14px) scale(0.2);
  }
}

/* 蓝色试剂被挤压时超自己左方喷射的大量气泡 */
.spray-to-left .bb-1  { width: 4px; height: 4px; background: #7dd3fc; top: 16%; right: 4px; animation: sprayLeft 1.3s ease-out infinite 0.1s; }
.spray-to-left .bb-2  { width: 6px; height: 6px; background: #ffffff; top: 28%; right: 6px; animation: sprayLeft 1.6s ease-out infinite 0.4s; }
.spray-to-left .bb-3  { width: 5px; height: 5px; background: #38bdf8; top: 42%; right: 3px; animation: sprayLeft 1.4s ease-out infinite 0.2s; }
.spray-to-left .bb-4  { width: 7px; height: 7px; background: #bae6fd; top: 58%; right: 5px; animation: sprayLeft 1.8s ease-out infinite 0.6s; }
.spray-to-left .bb-5  { width: 4px; height: 4px; background: #ffffff; top: 74%; right: 8px; animation: sprayLeft 1.2s ease-out infinite 0.3s; }
.spray-to-left .bb-6  { width: 6px; height: 6px; background: #0284c7; top: 22%; right: 2px; animation: sprayLeft 1.5s ease-out infinite 0.8s; }
.spray-to-left .bb-7  { width: 5px; height: 5px; background: #7dd3fc; top: 36%; right: 7px; animation: sprayLeft 1.4s ease-out infinite 0.5s; }
.spray-to-left .bb-8  { width: 7px; height: 7px; background: #ffffff; top: 50%; right: 4px; animation: sprayLeft 1.9s ease-out infinite 0.9s; }
.spray-to-left .bb-9  { width: 4px; height: 4px; background: #38bdf8; top: 66%; right: 6px; animation: sprayLeft 1.3s ease-out infinite 0.7s; }
.spray-to-left .bb-10 { width: 5px; height: 5px; background: #bae6fd; top: 82%; right: 3px; animation: sprayLeft 1.6s ease-out infinite 1.1s; }
.spray-to-left .bb-11 { width: 6px; height: 6px; background: #ffffff; top: 10%; right: 5px; animation: sprayLeft 1.4s ease-out infinite 1.0s; }
.spray-to-left .bb-12 { width: 4px; height: 4px; background: #7dd3fc; top: 90%; right: 7px; animation: sprayLeft 1.3s ease-out infinite 1.2s; }

@keyframes sprayLeft {
  0% {
    opacity: 0;
    transform: translate(0, 0) scale(0.3);
  }
  20% {
    opacity: 0.95;
    transform: translate(-4px, -3px) scale(1);
  }
  60% {
    opacity: 0.85;
    transform: translate(-12px, -8px) scale(1.1);
  }
  100% {
    opacity: 0;
    transform: translate(-20px, -14px) scale(0.2);
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
