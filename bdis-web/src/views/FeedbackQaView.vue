<template>
  <section class="feedback-qa-page">
    <div class="feedback-qa-container">
      <router-link to="/feedback" class="qa-back">← 返回匿名信箱</router-link>
      <header class="qa-heading">
        <p>来自实验室朋友的回答</p>
        <h1>看看大家的 Q&A</h1>
        <span>把已经解开的疑惑，分享给下一位朋友。</span>
      </header>
      <FeedbackQaList />
      <div class="qa-invite"><p>还有其他想问的？把你的声音也留下来。</p><router-link to="/feedback">去写一封信 →</router-link></div>
    </div>
  </section>
</template>

<script setup>
import FeedbackQaList from '@/components/feedback/FeedbackQaList.vue'
</script>

<style scoped>
.feedback-qa-page { min-height: 65vh; color: #23415a; background: #f8fbfe; }
.feedback-qa-container { max-width: 1000px; margin: 0 auto; padding: 40px 32px 72px; }
.qa-back { display: inline-flex; min-height: 44px; align-items: center; color: #0c3d70; font-size: 14px; }
.qa-heading { margin: 20px 0 32px; }
.qa-heading p { margin: 0 0 12px; color: #6d8296; font-size: 13px; }
.qa-heading h1 { margin: 0 0 14px; color: #0c3d70; font-size: clamp(28px, 4vw, 40px); line-height: 1.4; }
.qa-heading span, .qa-invite p { color: #6d8296; font-size: 14px; line-height: 1.8; }
.qa-invite { text-align: center; padding-top: 8px; }
.qa-invite a { display: inline-flex; padding: 12px 20px; border-radius: 24px; background: #0c3d70; color: #fff; font-size: 14px; }
.feedback-qa-page a:focus-visible { outline: 2px solid #6d9ccc; outline-offset: 3px; }
@media (max-width: 560px) { .feedback-qa-container { padding: 24px 16px 44px; } }
</style>
