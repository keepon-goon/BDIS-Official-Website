<template>
  <div class="reagent-tube-wrapper">
    <div class="reagent-tube" :class="{ 'is-yes': modelValue === true, 'is-no': modelValue === false, 'is-disabled': disabled }"
      role="group" :aria-label="label">
      <button type="button" class="reagent-side side-yes" aria-label="是" title="选择：是" :disabled="disabled" :aria-pressed="modelValue === true"
        @click="emit('update:modelValue', true)">
        <span class="reagent-text">是</span>
        <span v-if="modelValue === true" class="reagent-selected-badge">✓ 已选</span>
      </button>
      <div class="reagent-meniscus" aria-hidden="true">
        <div class="meniscus-wave"></div>
        <div class="reagent-bubble bubble-white-1"></div>
        <div class="reagent-bubble bubble-white-2"></div>
        <div class="reagent-bubble bubble-black-1"></div>
        <div class="reagent-bubble bubble-black-2"></div>
      </div>
      <button type="button" class="reagent-side side-no" aria-label="否" title="选择：否" :disabled="disabled" :aria-pressed="modelValue === false"
        @click="emit('update:modelValue', false)">
        <span class="reagent-text">否</span>
        <span v-if="modelValue === false" class="reagent-selected-badge">✓ 已选</span>
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({ modelValue: { type: Boolean, default: null }, disabled: Boolean, label: String })
const emit = defineEmits(['update:modelValue'])
</script>

<style scoped>
.reagent-tube-wrapper {
  width: 100%;
  margin-top: 4px;
}

.reagent-tube {
  position: relative;
  width: 100%;
  height: 48px;
  border-radius: 980px;
  background-color: #0c3d70;
  border: 1.5px solid #bccfe2;
  box-shadow: inset 0 2px 5px rgba(255, 255, 255, 0.7), inset 0 -2px 5px rgba(0, 0, 0, 0.15), 0 4px 14px rgba(0, 0, 0, 0.06);
  display: flex;
  overflow: hidden;
  user-select: none;
}

.reagent-side {
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  cursor: pointer;
  z-index: 2;
  transition: width 0.45s cubic-bezier(0.25, 1, 0.5, 1), transform 0.15s ease;
}

.reagent-side:active {
  transform: scale(0.98);
}


.reagent-side.side-yes {
  width: 50%;
  background: #ffffff;
  color: #0c3d70;
  padding-left: 20px;
}


.reagent-side.side-no {
  width: 50%;
  background: #0c3d70;
  color: #ffffff;
  padding-right: 20px;
}

.reagent-text {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
}

.reagent-selected-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 980px;
  font-weight: 500;
  animation: reagentBadgeIn 0.25s ease;
}

.side-yes .reagent-selected-badge {
  background: rgba(0, 0, 0, 0.08);
  color: #0c3d70;
}

.side-no .reagent-selected-badge {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}


.reagent-tube.is-yes .side-yes {
  width: 62%;
}
.reagent-tube.is-yes .side-no {
  width: 38%;
}

.reagent-tube.is-no .side-yes {
  width: 38%;
}
.reagent-tube.is-no .side-no {
  width: 62%;
}


.reagent-meniscus {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 36px;
  transform: translateX(-50%);
  pointer-events: none;
  z-index: 5;
  transition: left 0.45s cubic-bezier(0.25, 1, 0.5, 1);
}

.reagent-tube.is-yes .reagent-meniscus {
  left: 62%;
}
.reagent-tube.is-no .reagent-meniscus {
  left: 38%;
}


.meniscus-wave {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(90deg, #ffffff 0%, rgba(255, 255, 255, 0.8) 35%, rgba(12, 61, 112, 0.8) 65%, #0c3d70 100%);
  filter: blur(2px);
  animation: waveUndulate 2.8s ease-in-out infinite alternate;
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


.reagent-bubble {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}


.bubble-white-1 {
  width: 7px;
  height: 7px;
  background: #ffffff;
  box-shadow: 0 0 4px rgba(255, 255, 255, 0.8);
  top: 60%;
  left: 30%;
  animation: bubbleToRight 3.4s cubic-bezier(0.25, 1, 0.5, 1) infinite;
}

.bubble-white-2 {
  width: 5px;
  height: 5px;
  background: #ffffff;
  box-shadow: 0 0 3px rgba(255, 255, 255, 0.9);
  top: 30%;
  left: 35%;
  animation: bubbleToRight 4.6s cubic-bezier(0.25, 1, 0.5, 1) infinite 1.8s;
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
    transform: translate(18px, -12px) scale(1.15);
  }
  100% {
    opacity: 0;
    transform: translate(25px, -20px) scale(0.3);
  }
}


.bubble-black-1 {
  width: 6px;
  height: 6px;
  background: #0c3d70;
  box-shadow: 0 0 3px rgba(0, 0, 0, 0.5);
  top: 55%;
  right: 30%;
  animation: bubbleToLeft 4s cubic-bezier(0.25, 1, 0.5, 1) infinite 1s;
}

.bubble-black-2 {
  width: 8px;
  height: 8px;
  background: #0c3d70;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.4);
  top: 25%;
  right: 35%;
  animation: bubbleToLeft 5.2s cubic-bezier(0.25, 1, 0.5, 1) infinite 2.6s;
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
    transform: translate(-16px, -14px) scale(1.15);
  }
  100% {
    opacity: 0;
    transform: translate(-24px, -20px) scale(0.3);
  }
}

.reagent-tube { box-sizing: border-box; }
.reagent-side { box-sizing: border-box; min-width: 0; padding-top: 0; padding-bottom: 0; }
.reagent-side:focus-visible { outline: 2px solid #6d9ccc; outline-offset: -4px; }
.reagent-side:focus:not(:focus-visible) { outline: none; }
.reagent-side:disabled { cursor: not-allowed; }
.reagent-tube.is-disabled { opacity: .55; }
@keyframes reagentBadgeIn { from { opacity: 0; } to { opacity: 1; } }
@media (prefers-reduced-motion: reduce) {
  .reagent-side, .reagent-meniscus { transition: none; }
  .reagent-side:active { transform: none; }
  .meniscus-wave, .reagent-bubble, .reagent-selected-badge { animation: none; }
  .reagent-bubble { display: none; }
}
</style>
