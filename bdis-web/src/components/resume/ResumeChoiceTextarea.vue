<template>
  <div class="resume-choice" :data-choice="choice || 'pending'" :data-phase="phase">
    <div class="resume-choice-panel">
      <el-input :id="id" ref="input" :model-value="modelValue" type="textarea" :rows="4" maxlength="500"
        show-word-limit :placeholder="placeholder" :disabled="disabled || phase !== 'editing'"
        :aria-hidden="phase !== 'editing'" @update:model-value="emit('update:modelValue', $event)" />
      <div v-if="phase !== 'editing'" class="resume-choice-overlay" :class="{ fading: phase === 'fading' }">
        <div v-for="side in ['yes', 'no']" :key="side" class="resume-choice-half"
          :class="[side, { selected: choice === side, hidden: choice && choice !== side }]">
          <div class="resume-choice-fill" @transitionend="finishTransition" />
          <button v-if="!choice" type="button" class="resume-choice-button" :disabled="disabled"
            @click="select(side, $event)">{{ side === 'yes' ? yesLabel : noLabel }}</button>
        </div>
        <p v-if="phase === 'declined'" class="resume-choice-message" role="status">{{ declinedText }}</p>
      </div>
    </div>
    <button v-if="choice" type="button" class="resume-choice-reset" :disabled="disabled || phase === 'covering' || phase === 'fading'"
      @click="reset">重新选择</button>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const props = defineProps({
  id: String, modelValue: String, choice: String, yesLabel: String, noLabel: String,
  placeholder: String, declinedText: String, negativeValue: { type: String, default: '' }, disabled: Boolean
})
const emit = defineEmits(['update:modelValue', 'update:choice'])
const phase = ref(props.choice === 'yes' ? 'editing' : props.choice === 'no' ? 'declined' : 'pending')
const input = ref()

async function settle() {
  phase.value = props.choice === 'yes' ? 'editing' : 'declined'
  if (props.choice === 'yes') {
    await nextTick()
    input.value?.focus()
  }
}

async function select(side, event) {
  if (props.disabled || props.choice) return
  emit('update:modelValue', side === 'no' ? props.negativeValue : props.modelValue === props.negativeValue ? '' : props.modelValue)
  emit('update:choice', side)
  phase.value = 'covering'
  await nextTick()
  if (event.detail === 0 || window.matchMedia('(prefers-reduced-motion: reduce)').matches) settle()
}

function finishTransition(event) {
  if (event.target !== event.currentTarget) return
  if (phase.value === 'covering' && event.propertyName === 'transform') {
    if (props.choice === 'yes') phase.value = 'fading'
    else settle()
  } else if (phase.value === 'fading' && event.propertyName === 'opacity') settle()
}

function reset() {
  emit('update:choice', null)
  phase.value = 'pending'
}
</script>

<style scoped>
.resume-choice { width: 100%; --ease-out: cubic-bezier(0.23, 1, 0.32, 1); --ease-in-out: cubic-bezier(0.77, 0, 0.175, 1); }
.resume-choice-panel { position: relative; overflow: hidden; border-radius: 6px; }
.resume-choice-overlay { position: absolute; inset: 0; }
.resume-choice-half { position: absolute; top: 0; bottom: 0; width: 50%; }
.resume-choice-half.yes { left: 0; }
.resume-choice-half.no { right: 0; }
.resume-choice-half.selected { z-index: 2; }
.resume-choice-half.hidden { opacity: 0; transition: opacity 160ms var(--ease-out); }
.resume-choice-fill { position: absolute; inset: 0; background: #e4f2ff; transform-origin: left center; transition: transform 220ms var(--ease-in-out), opacity 180ms var(--ease-out); }
.no .resume-choice-fill { background: #e5f6eb; transform-origin: right center; }
.selected .resume-choice-fill { transform: scaleX(2); }
.fading .selected .resume-choice-fill { opacity: 0; }
.resume-choice-button { position: relative; width: 100%; height: 100%; border: 0; background: transparent; color: #244963; font: inherit; font-weight: 600; cursor: pointer; transition: transform 160ms var(--ease-out); }
.resume-choice-button:active { transform: scale(0.97); }
.resume-choice-button:focus-visible { outline: 2px solid #409eff; outline-offset: -4px; }
.resume-choice-button:disabled { cursor: wait; }
.resume-choice-message { position: absolute; inset: 0; z-index: 3; display: flex; align-items: center; justify-content: center; margin: 0; padding: 16px; color: #367451; text-align: center; pointer-events: none; }
.resume-choice-reset { display: block; margin: 4px 0 0 auto; border: 0; padding: 2px 0; background: transparent; color: #617184; font: inherit; font-size: 12px; cursor: pointer; }
.resume-choice-reset:disabled { opacity: .5; cursor: wait; }
@media (prefers-reduced-motion: reduce) {
  .resume-choice-fill { transition: opacity 120ms var(--ease-out); }
  .resume-choice-button { transition: none; }
  .resume-choice-button:active { transform: none; }
}
</style>
