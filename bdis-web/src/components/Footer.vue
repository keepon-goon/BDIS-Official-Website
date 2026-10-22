<template>
  <footer class="footer">
    <div class="footer-container">
      <!-- 主要内容区域 -->
      <div class="footer-content">
        <!-- 左侧：工作室信息 -->
        <div class="footer-section footer-about">
          <div class="footer-brand">
            <h3 class="footer-title">大数据智能工作室</h3>
            <span class="footer-tag">BDIS 创新实验室</span>
          </div>
          <p class="footer-subtitle">Big Data Intelligence Studio · CQUT</p>
          <p class="footer-desc">
            依托大数据管理与应用专业，由泰迪·重理工大数据智能工作室与重庆理工大学创新实验室联合组建，聚焦创新创业、企业级工程开发与前沿大模型技术实践。
          </p>
        </div>

        <!-- 中间：快速链接 -->
        <div class="footer-section footer-links">
          <h3 class="footer-title">站点导航</h3>
          <ul class="footer-link-list">
            <li><router-link to="/" class="footer-link">首页门户</router-link></li>
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
            <el-icon class="contact-icon"><Location /></el-icon>
            <span class="contact-text">重庆理工大学 花溪校区 博园4号楼305</span>
          </div>
          <div class="contact-item">
            <el-icon class="contact-icon"><Message /></el-icon>
            <span class="contact-text">18862028557@163.com</span>
          </div>
          <div class="contact-item">
            <el-icon class="contact-icon"><ChatDotRound /></el-icon>
            <span class="contact-text">招新交流群：935982021</span>
          </div>
        </div>

        <!-- 右侧：友情链接 -->
        <div class="footer-section footer-partners">
          <h3 class="footer-title">合作单位</h3>
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
                广州泰迪智能科技
              </a>
            </li>
          </ul>
        </div>
      </div>

      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <div class="footer-copyright">
          <p>© {{ currentYear }} 大数据智能工作室 (BDIS) · 保留所有权利</p>
          <a class="footer-beian" href="https://beian.miit.gov.cn/#/Integrated/index" target="_blank" rel="noopener noreferrer">
            苏ICP备2026001180号-1
          </a>
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
  background: linear-gradient(180deg, #0C3D70 0%, #061B33 100%);
  color: #FFFFFF;
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 50px 32px 24px;
  box-sizing: border-box;
}

.footer-content {
  display: grid;
  grid-template-columns: 2.2fr 1.2fr 1.8fr 1.2fr;
  gap: 40px;
  margin-bottom: 36px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.footer-title {
  font-size: 17px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #FFFFFF;
  letter-spacing: 0.5px;
}

.footer-brand .footer-title {
  margin: 0;
}

.footer-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: rgba(2, 132, 199, 0.3);
  border: 1px solid rgba(56, 189, 248, 0.4);
  color: #38BDF8;
  border-radius: 4px;
}

.footer-subtitle {
  font-size: 13px;
  color: #94A3B8;
  margin: 6px 0 14px 0;
  font-weight: 400;
  letter-spacing: 0.5px;
}

.footer-desc {
  font-size: 14px;
  line-height: 1.8;
  color: #CBD5E1;
  margin: 0;
}

.footer-link-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-link-list li {
  margin-bottom: 10px;
}

.footer-link {
  color: #94A3B8;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.25s ease;
  display: inline-flex;
  align-items: center;
}

.footer-link:hover {
  color: #38BDF8;
  transform: translateX(4px);
}

.contact-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 14px;
  font-size: 14px;
  gap: 10px;
}

.contact-icon {
  font-size: 17px;
  color: #38BDF8;
  flex-shrink: 0;
  margin-top: 3px;
}

.contact-text {
  color: #CBD5E1;
  line-height: 1.6;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 20px;
}

.footer-copyright {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.footer-copyright p {
  margin: 0;
  font-size: 13px;
  color: #94A3B8;
}

.footer-beian {
  font-size: 12px;
  color: #64748B;
  transition: color 0.2s ease;
}

.footer-beian:hover {
  color: #94A3B8;
}

@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: 1fr 1fr;
    gap: 30px;
  }
}

@media (max-width: 640px) {
  .footer-container {
    padding: 36px 20px 20px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 26px;
  }

  .footer-copyright {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
}
</style>
