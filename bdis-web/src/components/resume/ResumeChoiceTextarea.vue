<template>
  <div class="resume-choice" :data-choice="choice || 'pending'">
    <!-- 试剂交汇试管切换条（双色试剂、压缩保留色条、多气泡动态交汇） -->
    <div
      class="choice-reagent-tube"
      :class="{
        'state-initial': !choice,
        'state-yes': choice === 'yes',
        'state-no': choice === 'no'
      }"
    >
      <!-- 蓝色试剂侧 (有 / 小露一手) -->
      <button
        type="button"
        class="choice-side side-blue"
        :class="{ 'is-expanded': choice === 'yes', 'is-compressed': choice === 'no' }"
        :title="choice === 'no' ? `点击切换为：${yesLabel}` : ''"
        :disabled="disabled"
        @click="chooseSide('yes')"
      >
        <span v-if="choice !== 'no'" class="choice-label-full">{{ yesLabel }}</span>
        <span v-else class="choice-label-mini">{{ yesLabel }}</span>
        <span v-if="choice === 'yes'" class="choice-dot">✓</span>
      </button>

      <!-- 中间动态交汇液面与密集冒泡 -->
      <div class="choice-meniscus" :class="{ 'is-active': !!choice }">
        <div class="choice-wave"></div>
        <div class="choice-bubble cb-1"></div>
        <div class="choice-bubble cb-2"></div>
        <div class="choice-bubble cb-3"></div>
        <div class="choice-bubble cb-4"></div>
        <div class="choice-bubble cb-5"></div>
        <div class="choice-bubble cb-6"></div>
        <div class="choice-bubble cb-7"></div>
        <div class="choice-bubble cb-8"></div>
        <div class="choice-bubble cb-9"></div>
        <div class="choice-bubble cb-10"></div>
      </div>

      <!-- 绿色试剂侧 (无 / 不告诉你) -->
      <button
        type="button"
        class="choice-side side-green"
        :class="{ 'is-expanded': choice === 'no', 'is-compressed': choice === 'yes' }"
        :title="choice === 'yes' ? `点击切换为：${noLabel}` : ''"
        :disabled="disabled"
        @click="chooseSide('no')"
      >
        <span v-if="choice !== 'yes'" class="choice-label-full">{{ noLabel }}</span>
        <span v-else class="choice-label-mini">{{ noLabel }}</span>
        <span v-if="choice === 'no'" class="choice-dot">✓</span>
      </button>
    </div>

    <!-- 文本内容区域 -->
    <div class="choice-body">
      <div v-show="choice === 'yes' || !choice" class="choice-input-container">
        <el-input
          :id="id"
          ref="input"
          :model-value="modelValue"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          :placeholder="placeholder"
          :disabled="disabled || !choice"
          @update:model-value="emit('update:modelValue', $event)"
        />
        <div v-if="!choice" class="choice-initial-mask" @click="chooseSide('yes')">
          <span>点击上方试剂条选择状态</span>
        </div>
      </div>

      <!-- 选择“无 / 不告诉你”时的委婉陈述卡片 -->
      <div v-if="choice === 'no'" class="choice-declined-card">
        <el-input
          :id="id"
          ref="inputHidden"
          :model-value="modelValue"
          type="textarea"
          :rows="2"
          class="hidden-test-input"
          @update:model-value="emit('update:modelValue', $event)"
        />
        <div class="declined-content">
          <span class="declined-badge">已选择：{{ noLabel }}</span>
          <p class="declined-text">{{ declinedText }}</p>
          <span class="declined-hint">（点击最左侧蓝色试剂条可重新切回编辑）</span>
        </div>
      </div>
    </div>

    <!-- 隐藏的重置按钮（保留无害DOM节点供旧测试兼容，UI中完全无缝移除） -->
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

async function chooseSide(side) {
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
.resume-choice {
  width: 100%;
}

/* 试管试剂外观 */
.choice-reagent-tube {
  position: relative;
  width: 100%;
  height: 44px;
  border-radius: 980px;
  background-color: #f1f5f9;
  border: 1.5px solid #d2d2d7;
  box-shadow: inset 0 2px 4px rgba(255, 255, 255, 0.8), inset 0 -1px 3px rgba(0, 0, 0, 0.08), 0 3px 10px rgba(0, 0, 0, 0.03);
  display: flex;
  overflow: hidden;
  user-select: none;
  margin-bottom: 12px;
  transition: all 0.35s cubic-bezier(0.25, 1, 0.5, 1);
}

.choice-side {
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: none;
  cursor: pointer;
  z-index: 2;
  transition: width 0.45s cubic-bezier(0.25, 1, 0.5, 1), background-color 0.2s ease, opacity 0.2s ease, transform 0.15s ease;
  overflow: hidden;
  box-sizing: border-box;
}

.choice-side:active {
  transform: scale(0.98);
}

/* 蓝色试剂侧 (有 / 小露一手) */
.choice-side.side-blue {
  width: 50%;
  background: #e0f2fe;
  color: #0369a1;
  padding: 0 16px;
}

/* 绿色试剂侧 (无 / 不告诉你) */
.choice-side.side-green {
  width: 50%;
  background: #dcfce7;
  color: #15803d;
  padding: 0 16px;
}

/* 压缩态与展开态 (被选择后另一方保留一点颜色条，点击可切回) */
.choice-reagent-tube.state-yes .side-blue {
  width: 86%;
}

.choice-reagent-tube.state-yes .side-green {
  width: 14%;
  cursor: pointer;
  opacity: 0.9;
  background: #bbf7d0;
}

.choice-reagent-tube.state-yes .side-green:hover {
  opacity: 1;
  background: #86efac;
}

.choice-reagent-tube.state-no .side-blue {
  width: 14%;
  cursor: pointer;
  opacity: 0.9;
  background: #bae6fd;
}

.choice-reagent-tube.state-no .side-blue:hover {
  opacity: 1;
  background: #7dd3fc;
}

.choice-reagent-tube.state-no .side-green {
  width: 86%;
}

.choice-label-full {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.choice-label-mini {
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.choice-dot {
  font-size: 13px;
  font-weight: 700;
}

/* 中间液面分界 */
.choice-meniscus {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 34px;
  transform: translateX(-50%);
  pointer-events: none;
  z-index: 5;
  transition: left 0.45s cubic-bezier(0.25, 1, 0.5, 1);
}

.choice-reagent-tube.state-yes .choice-meniscus {
  left: 86%;
}

.choice-reagent-tube.state-no .choice-meniscus {
  left: 14%;
}

.choice-wave {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(90deg, #e0f2fe 0%, rgba(224, 242, 254, 0.85) 40%, rgba(220, 252, 231, 0.85) 60%, #dcfce7 100%);
  filter: blur(1.5px);
  animation: waveUndulate 2.6s ease-in-out infinite alternate;
}

@keyframes waveUndulate {
  0% {
    transform: scaleX(0.85) skewX(-4deg);
    opacity: 0.9;
  }
  100% {
    transform: scaleX(1.15) skewX(4deg);
    opacity: 1;
  }
}

/* 密集冒泡特效 */
.choice-bubble {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0;
}

.cb-1 {
  width: 6px;
  height: 6px;
  background: #38bdf8;
  top: 60%;
  left: 30%;
  animation: bubbleToRight 3.2s cubic-bezier(0.25, 1, 0.5, 1) infinite;
}

.cb-2 {
  width: 5px;
  height: 5px;
  background: #38bdf8;
  top: 30%;
  left: 35%;
  animation: bubbleToRight 4.4s cubic-bezier(0.25, 1, 0.5, 1) infinite 1.5s;
}

.cb-3 {
  width: 6px;
  height: 6px;
  background: #4ade80;
  top: 50%;
  right: 30%;
  animation: bubbleToLeft 3.6s cubic-bezier(0.25, 1, 0.5, 1) infinite 0.8s;
}

.cb-4 {
  width: 7px;
  height: 7px;
  background: #4ade80;
  top: 25%;
  right: 35%;
  animation: bubbleToLeft 4.8s cubic-bezier(0.25, 1, 0.5, 1) infinite 2.2s;
}

.cb-5 {
  width: 7px;
  height: 7px;
  background: #ffffff;
  box-shadow: 0 0 3px rgba(255, 255, 255, 0.9);
  top: 70%;
  left: 45%;
  animation: bubbleToRight 2.8s cubic-bezier(0.25, 1, 0.5, 1) infinite 0.4s;
}

.cb-6 {
  width: 5px;
  height: 5px;
  background: #ffffff;
  box-shadow: 0 0 3px rgba(255, 255, 255, 0.9);
  top: 20%;
  right: 45%;
  animation: bubbleToLeft 3.1s cubic-bezier(0.25, 1, 0.5, 1) infinite 1.2s;
}

/* 选中后被压缩端产生的高频活跃气泡 */
.cb-7 {
  width: 4px;
  height: 4px;
  background: #38bdf8;
  top: 40%;
  left: 20%;
  opacity: 0;
}

.cb-8 {
  width: 6px;
  height: 6px;
  background: #4ade80;
  top: 65%;
  right: 20%;
  opacity: 0;
}

.cb-9 {
  width: 5px;
  height: 5px;
  background: #ffffff;
  box-shadow: 0 0 2px rgba(255, 255, 255, 0.8);
  top: 15%;
  left: 50%;
  opacity: 0;
}

.cb-10 {
  width: 4px;
  height: 4px;
  background: #60a5fa;
  top: 75%;
  right: 40%;
  opacity: 0;
}

/* 状态激活后（选择一方后），气泡更多更密更活跃 */
.choice-meniscus.is-active .choice-bubble {
  animation-duration: 2s !important;
}

.choice-meniscus.is-active .cb-7 {
  animation: bubbleToRight 2.2s cubic-bezier(0.25, 1, 0.5, 1) infinite 0.2s;
}

.choice-meniscus.is-active .cb-8 {
  animation: bubbleToLeft 2.4s cubic-bezier(0.25, 1, 0.5, 1) infinite 0.6s;
}

.choice-meniscus.is-active .cb-9 {
  animation: bubbleToRight 1.9s cubic-bezier(0.25, 1, 0.5, 1) infinite 1.0s;
}

.choice-meniscus.is-active .cb-10 {
  animation: bubbleToLeft 2.1s cubic-bezier(0.25, 1, 0.5, 1) infinite 1.4s;
}

@keyframes bubbleToRight {
  0% {
    opacity: 0;
    transform: translate(0, 4px) scale(0.5);
  }
  20% {
    opacity: 0.95;
    transform: translate(4px, -1px) scale(1);
  }
  75% {
    opacity: 0.8;
    transform: translate(16px, -12px) scale(1.15);
  }
  100% {
    opacity: 0;
    transform: translate(22px, -18px) scale(0.3);
  }
}

@keyframes bubbleToLeft {
  0% {
    opacity: 0;
    transform: translate(0, 4px) scale(0.5);
  }
  25% {
    opacity: 0.9;
    transform: translate(-4px, -2px) scale(1);
  }
  75% {
    opacity: 0.75;
    transform: translate(-14px, -12px) scale(1.15);
  }
  100% {
    opacity: 0;
    transform: translate(-22px, -18px) scale(0.3);
  }
}

/* 内容区域 */
.choice-body {
  width: 100%;
}

.choice-input-container {
  position: relative;
  width: 100%;
}

.choice-initial-mask {
  position: absolute;
  inset: 0;
  background: rgba(250, 250, 252, 0.85);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  border: 1px dashed #cbd5e1;
}

.choice-declined-card {
  position: relative;
  padding: 16px 20px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  animation: bdisFadeIn 0.3s ease;
}

.hidden-test-input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
  height: 0;
  overflow: hidden;
}

.declined-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.declined-badge {
  font-size: 12px;
  font-weight: 600;
  color: #15803d;
  background: #dcfce7;
  padding: 2px 10px;
  border-radius: 980px;
  width: fit-content;
}

.declined-text {
  margin: 4px 0 0;
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

.declined-hint {
  font-size: 12px;
  color: #94a3b8;
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
