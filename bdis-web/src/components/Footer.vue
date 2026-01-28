<template>
  <footer class="footer">
    <div class="footer-container">
      <!-- 主要内容区域 -->
      <div class="footer-content">
        <!-- 左侧：工作室信息 -->
        <div class="footer-section footer-about">
          <h3 class="footer-title">大数据智能工作室</h3>
          <p class="footer-subtitle">Big Data Intelligence Studio · BDIS</p>
          <p class="footer-desc">
            依托大数据管理与应用专业，由泰迪·重理工大数据智能工作室与重庆理工大学创新实验室联合组建，聚焦创新创业、系统开发与大模型训练。
          </p>
        </div>

        <!-- 中间：快速链接 -->
        <div class="footer-section footer-links">
          <h3 class="footer-title">快速链接</h3>
          <ul class="footer-link-list">
            <li><router-link to="/" class="footer-link">首页</router-link></li>
            <li><router-link to="/info/about" class="footer-link">关于我们</router-link></li>
            <li><router-link to="/info/research" class="footer-link">项目进展</router-link></li>
            <li><router-link to="/info/news" class="footer-link">动态公告</router-link></li>
            <li><router-link to="/info/platform" class="footer-link">创新竞赛</router-link></li>
            <li><router-link to="/info/education" class="footer-link">学习资料</router-link></li>
            <li><router-link to="/info/join" class="footer-link">加入我们</router-link></li>
          </ul>
        </div>

        <!-- 右侧：联系方式 -->
        <div class="footer-section footer-contact">
          <h3 class="footer-title">联系我们</h3>
          <div class="contact-item">
            <el-icon class="contact-icon">
              <Location />
            </el-icon>
            <span class="contact-text">重庆理工大学 博园4号楼305</span>
          </div>
          <div class="contact-item">
            <el-icon class="contact-icon">
              <Message />
            </el-icon>
            <span class="contact-text">18862028557@163.com</span>
          </div>
          <div class="contact-item">
            <el-icon class="contact-icon">
              <ChatDotRound />
            </el-icon>
            <span class="contact-text">QQ群：935982021</span>
          </div>
        </div>

        <!-- 右侧：友情链接 -->
        <div class="footer-section footer-partners">
          <h3 class="footer-title">友情链接</h3>
          <ul class="footer-link-list">
            <li>
              <a href="https://www.cqut.edu.cn/" target="_blank" rel="noopener noreferrer" class="footer-link">
                重庆理工大学
              </a>
            </li>
            <li>
              <a href="https://cs.cqut.edu.cn/" target="_blank" rel="noopener noreferrer" class="footer-link">
                计算机科学与工程学院
              </a>
            </li>
            <li>
              <a href="https://www.tipdm.com/" target="_blank" rel="noopener noreferrer" class="footer-link">
                泰迪智能科技
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <div class="footer-copyright">
          <p>© {{ currentYear }} 大数据智能工作室 (BDIS) 版权所有</p>
          <a class="footer-beian btn" href="https://beian.miit.gov.cn/#/Integrated/index">苏ICP备2026001180号-1</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { Location, Message, ChatDotRound } from '@element-plus/icons-vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped>
.footer {
  width: 100%;
  background-color: #0C3D70;
  color: #ffffff;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 32px 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1.5fr 1.5fr;
  gap: 40px;
  margin-bottom: 40px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #ffffff;
  font-family: "MiSans", sans-serif;
}

.footer-subtitle {
  font-size: 14px;
  color: #b0c4de;
  margin: 0 0 12px 0;
  font-weight: 400;
}

.footer-desc {
  font-size: 14px;
  line-height: 1.8;
  color: #d0d8e8;
  margin: 0;
}

.footer-link-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-link-list li {
  margin-bottom: 12px;
}

.footer-link {
  color: #d0d8e8;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
  display: inline-block;
}

.footer-link:hover {
  color: #ffffff;
  text-decoration: underline;
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
}

.contact-icon {
  font-size: 18px;
  margin-right: 10px;
  color: #b0c4de;
  flex-shrink: 0;
}

.contact-text {
  color: #d0d8e8;
  line-height: 1.6;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 24px;
  margin-top: 20px;
}

.footer-copyright {
  text-align: center;
}

.footer-copyright p {
  margin: 4px 0;
  font-size: 13px;
  color: #b0c4de;
}

.footer-beian {
  font-size: 12px;
  color: #9bb3d3;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: 1fr 1fr;
    gap: 30px;
  }
}

@media (max-width: 768px) {
  .footer-container {
    padding: 40px 20px 20px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .footer-section {
    text-align: left;
  }
}
</style>
