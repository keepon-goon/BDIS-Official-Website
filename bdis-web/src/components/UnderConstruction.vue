<template>
  <div class="uc-page">
    <div class="uc-card">
      <div class="uc-icon">🚧</div>
      <h1 class="uc-title">页面建设中</h1>
      <p class="uc-subtitle">
        当前栏目正在建设中，敬请期待。
      </p>

      <p class="uc-text">
        本页面内容尚未完成，我们将尽快建设完成。
        您可以先浏览网站的其他栏目。
      </p>

      <div class="uc-actions">
        <a href="/" class="uc-btn primary">返回首页</a>
        <a href="javascript:history.back()" class="uc-btn ghost">返回上一页</a>
      </div>

      <p class="uc-footer">
        BDIS Big Data Intelligence Studio
      </p>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.uc-page {
  min-height: calc(100vh - 135px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at top, #f4f7fb, #e8edf5);
}

.uc-card {
  max-width: 520px;
  width: 90%;
  padding: 40px 32px 32px;
  border-radius: 20px;
  background: #ffffff;
  box-shadow: 0 18px 45px rgba(0, 26, 68, 0.09);
  text-align: center;
}

.uc-icon {
  font-size: 42px;
  margin-bottom: 16px;
}

.uc-title {
  font-size: 26px;
  color: #0c3d70;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.uc-subtitle {
  margin: 0 0 18px;
  font-size: 14px;
  color: #6a7687;
}

.uc-text {
  margin: 0 0 24px;
  font-size: 14px;
  color: #7b8695;
  line-height: 1.7;
}

.uc-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 12px;
}

.uc-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 110px;
  height: 36px;
  padding: 0 16px;
  border-radius: 18px;
  font-size: 14px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.uc-btn.primary {
  background-color: #0c3d70;
  color: #ffffff;
  border: 1px solid #0c3d70;
}

.uc-btn.primary:hover {
  background-color: #0a325c;
}

.uc-btn.ghost {
  background-color: #ffffff;
  color: #0c3d70;
  border: 1px solid #c0c8d6;
}

.uc-btn.ghost:hover {
  background-color: #f5f7fb;
}

.uc-footer {
  margin: 0;
  margin-top: 8px;
  font-size: 12px;
  color: #b0b8c6;
}
</style>
