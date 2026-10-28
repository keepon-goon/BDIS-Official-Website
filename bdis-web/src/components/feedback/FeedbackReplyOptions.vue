<template>
  <fieldset class="reply-options" :disabled="disabled">
    <legend class="reply-heading">想收到实验室朋友的回复吗？ <span>可选</span></legend>
    <ReagentToggle :model-value="modelValue.replyRequested" label="想收到实验室朋友的回复吗？" :disabled="disabled"
      @update:model-value="selectReplyRequested" />

    <Transition name="reply-reveal" @before-leave="isolateLeavingBranch" @enter="restoreBranch">
      <div v-if="modelValue.replyRequested" id="feedback-reply-details" class="reply-details">
        <div ref="visibilityField" class="reply-field">
          <p class="reply-heading">回答在哪里见面？</p>
          <ResumeChoiceTextarea id="feedback-visibility" :choice="modelValue.publicReply === null ? null : modelValue.publicReply ? 'yes' : 'no'" :disabled="disabled"
            yes-label="给其他人也看看?" no-label="我要自己悄悄看"
            @update:choice="selectVisibility($event === 'yes')">
            <p class="reply-statement">给其他人也看看?<small>这次问答可以在官网公开展示，和更多朋友分享。</small></p>
            <template #declined><p class="reply-statement">我要自己悄悄看<small>留下邮箱，实验室朋友会通过邮件回复你。</small></p></template>
          </ResumeChoiceTextarea>
        </div>

        <Transition name="reply-reveal" mode="out-in" @before-leave="isolateLeavingBranch" @enter="restoreBranch" @after-enter="focusNameAfterConversion">
          <div v-if="modelValue.publicReply === true" key="name" ref="nameField" class="reply-field">
            <label class="reply-heading" for="feedback-display-name">要留下你的名字吗？</label>
            <ResumeChoiceTextarea id="feedback-name-choice" v-model:choice="nameChoice" :model-value="modelValue.displayName"
              yes-label="阁下怎么称呼?" no-label="不告诉你" placeholder="阁下怎么称呼?"
              negative-value="匿名" declined-text="那就以「匿名」和大家见面。" :disabled="disabled"
              @update:model-value="update('displayName', $event)">
              <div class="reply-input-wrap"><input id="feedback-display-name" ref="nameInput" data-choice-input
                :value="modelValue.displayName" :disabled="disabled || nameChoice !== 'yes'" maxlength="40" autocomplete="nickname"
                placeholder="阁下怎么称呼?" @input="update('displayName', $event.target.value)" />
                <span>这个名字会和问答一起展示，也可以选择“不告诉你”。</span></div>
            </ResumeChoiceTextarea>
          </div>
          <div v-else-if="modelValue.publicReply === false" key="email" class="reply-field">
            <label class="reply-heading" for="feedback-reply-email">把回信送到哪里？</label>
            <ResumeChoiceTextarea id="feedback-email-choice" :choice="emailChoice" :disabled="disabled"
              yes-label="欸盆友,邮箱给一哈" no-label="忘记了!!!(ps:试试QQ号+@qq.com)" compact-no-label="忘记了!!!"
              @update:choice="emailChoice = $event">
              <div class="reply-input-wrap"><input id="feedback-reply-email" ref="emailInput" data-choice-input type="email"
                :value="modelValue.replyEmail" :disabled="disabled || emailChoice !== 'yes'" maxlength="254" autocomplete="email"
                placeholder="欸盆友,邮箱给一哈" @input="update('replyEmail', $event.target.value)" />
                <span>忘记了!!!(ps:试试QQ号+@qq.com)</span></div>
              <template #declined>
                <div class="reply-convert"><p>是否要转换为公开获得回复</p><div>
                  <button type="button" @click="convertToPublic">是，公开获得回复</button>
                  <button type="button" @click="returnToEmail">否，继续填写邮箱</button>
                </div></div>
              </template>
            </ResumeChoiceTextarea>
          </div>
        </Transition>
        <p v-if="modelValue.publicReply !== null" class="reply-note">{{ modelValue.publicReply ? '我们回复并确认公开后，这次问答才会出现在官网。' : '邮箱仅用于接收回信，不会在官网公开。' }}</p>
      </div>
    </Transition>
  </fieldset>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import ResumeChoiceTextarea from '@/components/resume/ResumeChoiceTextarea.vue'
import ReagentToggle from '@/components/ReagentToggle.vue'

const props = defineProps({ modelValue: { type: Object, required: true }, disabled: Boolean })
const emit = defineEmits(['update:modelValue'])
const nameChoice = ref(null)
const emailChoice = ref(null)
const nameField = ref(null)
const visibilityField = ref(null)
const nameInput = ref(null)
const emailInput = ref(null)
let focusConvertedName = false

function update(key, value) { emit('update:modelValue', { ...props.modelValue, [key]: value }) }

function selectReplyRequested(value) {
  emit('update:modelValue', { ...props.modelValue, replyRequested: value, publicReply: null, displayName: '', replyEmail: '' })
  nameChoice.value = null
  emailChoice.value = null
}

function selectVisibility(value) {
  emit('update:modelValue', { ...props.modelValue, publicReply: value, displayName: '', replyEmail: '' })
  nameChoice.value = null
  emailChoice.value = null
}

function convertToPublic() {
  focusConvertedName = true
  emit('update:modelValue', { ...props.modelValue, publicReply: true, displayName: '', replyEmail: '' })
  nameChoice.value = null
  emailChoice.value = null
}

function isolateLeavingBranch(element) { element.inert = true }
function restoreBranch(element) { element.inert = false }
function focusNameAfterConversion() {
  if (!focusConvertedName || !props.modelValue.publicReply) return
  focusConvertedName = false
  nameField.value?.querySelector('button')?.focus()
}

async function returnToEmail() {
  emailChoice.value = 'yes'
  await nextTick()
  emailInput.value?.focus()
}

function validate() {
  if (!props.modelValue.replyRequested) return ''
  if (props.modelValue.publicReply === null) {
    visibilityField.value?.querySelector('button')?.focus()
    return '请选择公开问答，或选择“我要自己悄悄看”。'
  }
  if (props.modelValue.publicReply) {
    if (!nameChoice.value) {
      nameField.value?.querySelector('button')?.focus()
      return '请选择留下名字，或选择“不告诉你”以匿名展示。'
    }
    if (!props.modelValue.displayName.trim()) {
      nameInput.value?.focus()
      return '请填写你的称呼，或选择“不告诉你”。'
    }
  } else if (emailChoice.value !== 'yes' || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(props.modelValue.replyEmail.trim())) {
    emailInput.value?.focus()
    return '请填写有效的回信邮箱，或通过“忘记了”转换为公开回复。'
  }
  return ''
}

defineExpose({ validate })
</script>

<style scoped>
.reply-options { min-width: 0; border: 0; padding: 0; margin: 0 0 24px; --reply-ease: cubic-bezier(0.25, 1, 0.5, 1); }
.reply-heading { display: block; margin: 0 0 10px; color: #333; font-size: 14px; font-weight: 600; }
.reply-heading > span { margin-left: 6px; font-size: 12px; font-weight: 400; color: #8698a9; }
.reply-convert button:focus-visible { outline: 2px solid #6d9ccc; outline-offset: 3px; }
.reply-details { padding-top: 22px; }
.reply-field + .reply-field { margin-top: 20px; }
.reply-input-wrap { height: 100%; display: flex; flex-direction: column; justify-content: center; gap: 10px; padding: 16px; box-sizing: border-box; }
.reply-input-wrap input { width: 100%; min-width: 0; box-sizing: border-box; padding: 8px 0; border: 0; border-bottom: 1px solid #bdd0e3; border-radius: 0; background: transparent; color: #23415a; font: inherit; font-size: 16px; }
.reply-input-wrap input:focus-visible { outline: 2px solid #6d9ccc; outline-offset: 3px; }
.reply-input-wrap > span, .reply-statement small { color: #6d8296; font-size: 12px; line-height: 1.7; overflow-wrap: anywhere; }
.reply-statement { margin: 0; padding: 22px 16px; color: #375b7e; font-size: 15px; font-weight: 600; line-height: 1.7; }
.reply-statement small { display: block; margin-top: 7px; font-weight: 400; }
.reply-convert { padding: 14px 8px; }
.reply-convert p { margin: 0 0 10px; font-size: 14px; line-height: 1.6; }
.reply-convert > div { display: flex; flex-wrap: wrap; gap: 8px; }
.reply-convert button { padding: 7px 10px; border: 1px solid #bdd0e3; border-radius: 7px; background: #edf4fb; color: #0c3d70; font-size: 12px; line-height: 1.5; }
.reply-note { margin: 14px 0 0; font-size: 12px; line-height: 1.8; color: #6d8296; }
.reply-options :deep(.reagent-choice-wrapper) { margin-bottom: 0; }
.reply-options :deep(.reagent-choice-card) { box-sizing: border-box; min-height: 148px; }
.reply-options :deep(.fluid-label.label-initial) { padding: 10px; font-size: 14px; overflow-wrap: anywhere; line-height: 1.7; }
.reply-options :deep(.fluid-label.label-compressed) { letter-spacing: 0; white-space: nowrap; }
.reply-options :deep(.rupture-spray) { display: none; }
.reply-options :deep(.pure-statement-container) { width: 100%; box-sizing: border-box; padding: 0 12px; }
.reply-options :deep(.fluid-side:focus-visible) { outline: 2px solid #6d9ccc !important; outline-offset: -4px !important; }
.reply-reveal-enter-active, .reply-reveal-leave-active { transition: opacity 200ms var(--reply-ease), transform 200ms var(--reply-ease); }
.reply-reveal-enter-from, .reply-reveal-leave-to { opacity: 0; transform: translateY(-6px); }
@media (hover: none) {
  .reply-options :deep(.side-blue.is-initial) { background: #e6f0fa !important; }
  .reply-options :deep(.side-green.is-initial) { background: #fff !important; }
}
@media (max-width: 560px) {
  .reply-options :deep(.reagent-choice-card) { min-height: 178px; }
  .reply-options :deep(.fluid-label.label-initial) { font-size: 13px; }
}
@media (prefers-reduced-motion: reduce) {
  .reply-reveal-enter-active, .reply-reveal-leave-active { transition-duration: 0ms; }
  .reply-reveal-enter-from, .reply-reveal-leave-to { transform: none; }
  .reply-options :deep(*) { animation: none !important; transition-duration: 0ms !important; }
  .reply-options :deep(.concave-liquid-svg), .reply-options :deep(.reagent-meniscus-tube) { display: none; }
}
</style>
