<!-- src/components/info/About.vue -->
<template>
    <section class="about-page">
      <!-- 顶部横幅 -->
    
      <!-- 主要内容区域 -->
      <div class="about-container">
        <!-- 工作室简介 -->
        <div class="about-section">
          <div class="section-header">
            <h2 class="section-title">工作室简介</h2>
            <div class="section-divider"></div>
          </div>
          <div class="section-content" v-if="labInfo">
            <div class="intro-card">
              <p class="intro-text">{{ labInfo.introShort }}</p>
              <div class="intro-details">
                <div class="detail-item">
                  <el-icon class="detail-icon"><Location /></el-icon>
                  <span>{{ labInfo.address }}</span>
                </div>
                <div class="detail-item">
                  <el-icon class="detail-icon"><Message /></el-icon>
                  <span>{{ labInfo.email }}</span>
                </div>
                <div class="detail-item">
                  <el-icon class="detail-icon"><ChatDotRound /></el-icon>
                  <span>QQ群：{{ labInfo.qqGroup }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="loading-placeholder">
            <el-skeleton :rows="3" animated />
          </div>
        </div>
  
        <!-- 研究方向 -->
        <div class="about-section">
          <div class="section-header">
            <h2 class="section-title">研究方向</h2>
            <div class="section-divider"></div>
          </div>
          <div class="research-grid">
            <div class="research-card" v-for="direction in researchDirections" :key="direction.id">
              <div class="research-icon">{{ direction.icon }}</div>
              <h3 class="research-title">{{ direction.title }}</h3>
              <p class="research-desc">{{ direction.description }}</p>
            </div>
          </div>
        </div>
  
        <!-- 核心团队 -->
        <div class="about-section">
          <div class="section-header">
            <h2 class="section-title">核心团队</h2>
            <div class="section-divider"></div>
          </div>
          <div class="team-grid" v-if="teamMembers.length > 0">
            <div class="team-card" v-for="member in teamMembers" :key="member.id">
              <div class="team-avatar">
                <el-avatar :size="80" :src="member.avatarUrl">
                  {{ member.name.charAt(0) }}
                </el-avatar>
              </div>
              <h4 class="team-name">{{ member.name }}</h4>
              <p class="team-role">{{ member.role }}</p>
              <p class="team-desc">{{ member.description }}</p>
            </div>
          </div>
          <div v-else class="loading-placeholder">
            <el-skeleton :rows="2" animated />
          </div>
        </div>
  
        <!-- 工作室特色 -->
        <div class="about-section">
          <div class="section-header">
            <h2 class="section-title">工作室特色</h2>
            <div class="section-divider"></div>
          </div>
          <div class="features-list">
            <div class="feature-item" v-for="feature in features" :key="feature.id">
              <div class="feature-number">{{ feature.number }}</div>
              <div class="feature-content">
                <h4 class="feature-title">{{ feature.title }}</h4>
                <p class="feature-desc">{{ feature.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import { Location, Message, ChatDotRound } from '@element-plus/icons-vue'
  
  const labInfo = ref(null)
  const teamMembers = ref([])
  
  // 研究方向数据
  const researchDirections = ref([
    {
      id: 1,
      icon: '📊',
      title: '大数据管理与应用',
      description: '聚焦大数据采集、存储、处理与分析，构建智能数据管理平台'
    },
    {
      id: 2,
      icon: '🤖',
      title: '大模型训练与应用',
      description: '探索大语言模型、多模态模型训练与应用实践'
    },
    {
      id: 3,
      icon: '💻',
      title: '系统开发',
      description: '全栈开发实践，涵盖前端、后端、移动端开发'
    },
    {
      id: 4,
      icon: '🚀',
      title: '创新创业',
      description: '以赛促学，参与各类创新创业竞赛与项目实践'
    }
  ])
  
  // 工作室特色
  const features = ref([
    {
      id: 1,
      number: '01',
      title: '项目驱动',
      description: '通过真实项目实践，提升技术能力和解决问题的能力'
    },
    {
      id: 2,
      number: '02',
      title: '团队协作',
      description: '强调团队合作，培养良好的沟通与协作能力'
    },
    {
      id: 3,
      number: '03',
      title: '技术分享',
      description: '定期组织技术分享会，促进知识交流与传承'
    },
    {
      id: 4,
      number: '04',
      title: '导师指导',
      description: '专业导师指导，提供学术与职业发展建议'
    }
  ])
  
  onMounted(async () => {
    try {
      // 获取实验室信息
      const labRes = await axios.get('/api/lab/info')
      if (labRes.data?.code === 0) {
        labInfo.value = labRes.data.data
      }
    } catch (err) {
      console.error('获取实验室信息失败', err)
    }
  
    try {
      // 获取团队成员
      const teamRes = await axios.get('/api/lab/team')
      if (teamRes.data?.code === 0) {
        teamMembers.value = teamRes.data.data || []
      }
    } catch (err) {
      console.error('获取团队成员失败', err)
    }
  })
  </script>
  
  <style scoped>
  .about-page {
    min-height: calc(100vh - 135px);
    background-color: #f9f9fa;
  }
  
  .about-banner {
    width: 100%;
    height: 280px;
    background: linear-gradient(135deg, #0c3d70 0%, #1a5a8e 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }
  
  .banner-content {
    text-align: center;
  }
  
  .banner-title {
    font-size: 42px;
    font-weight: 600;
    margin: 0 0 12px;
    letter-spacing: 2px;
  }
  
  .banner-subtitle {
    font-size: 18px;
    opacity: 0.9;
    margin: 0;
    letter-spacing: 1px;
  }
  
  .about-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 60px 32px;
  }
  
  .about-section {
    margin-bottom: 80px;
  }
  
  .about-section:last-child {
    margin-bottom: 0;
  }
  
  .section-header {
    margin-bottom: 40px;
  }
  
  .section-title {
    font-size: 32px;
    font-weight: 600;
    color: #0c3d70;
    margin: 0 0 16px;
    text-align: center;
  }
  
  .section-divider {
    width: 60px;
    height: 4px;
    background-color: #0c3d70;
    margin: 0 auto;
  }
  
  .section-content {
    max-width: 900px;
    margin: 0 auto;
  }
  
  .intro-card {
    background: white;
    padding: 40px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
  
  .intro-text {
    font-size: 18px;
    line-height: 1.8;
    color: #333;
    margin: 0 0 32px;
    text-align: center;
  }
  
  .intro-details {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 32px;
  }
  
  .detail-item {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #666;
    font-size: 16px;
  }
  
  .detail-icon {
    font-size: 20px;
    color: #0c3d70;
  }
  
  .research-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
    gap: 32px;
    margin-top: 40px;
  }
  
  .research-card {
    background: white;
    padding: 32px 24px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    text-align: center;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }
  
  .research-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  }
  
  .research-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  .research-title {
    font-size: 20px;
    font-weight: 600;
    color: #0c3d70;
    margin: 0 0 12px;
  }
  
  .research-desc {
    font-size: 14px;
    line-height: 1.6;
    color: #666;
    margin: 0;
  }
  
  .team-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 32px;
    margin-top: 40px;
  }
  
  .team-card {
    background: white;
    padding: 32px 24px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    text-align: center;
    transition: transform 0.3s ease;
  }
  
  .team-card:hover {
    transform: translateY(-4px);
  }
  
  .team-avatar {
    margin-bottom: 16px;
  }
  
  .team-name {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px;
  }
  
  .team-role {
    font-size: 14px;
    color: #0c3d70;
    margin: 0 0 12px;
    font-weight: 500;
  }
  
  .team-desc {
    font-size: 14px;
    line-height: 1.6;
    color: #666;
    margin: 0;
  }
  
  .features-list {
    max-width: 900px;
    margin: 40px auto 0;
  }
  
  .feature-item {
    display: flex;
    gap: 24px;
    background: white;
    padding: 32px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    margin-bottom: 24px;
    align-items: flex-start;
  }
  
  .feature-item:last-child {
    margin-bottom: 0;
  }
  
  .feature-number {
    font-size: 32px;
    font-weight: 700;
    color: #0c3d70;
    line-height: 1;
    flex-shrink: 0;
  }
  
  .feature-content {
    flex: 1;
  }
  
  .feature-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px;
  }
  
  .feature-desc {
    font-size: 16px;
    line-height: 1.6;
    color: #666;
    margin: 0;
  }
  
  .loading-placeholder {
    max-width: 900px;
    margin: 0 auto;
    padding: 40px;
  }
  
  @media (max-width: 768px) {
    .banner-title {
      font-size: 32px;
    }
  
    .about-container {
      padding: 40px 16px;
    }
  
    .section-title {
      font-size: 26px;
    }
  
    .research-grid,
    .team-grid {
      grid-template-columns: 1fr;
    }
  
    .intro-details {
      flex-direction: column;
      gap: 16px;
    }
  
    .feature-item {
      flex-direction: column;
      gap: 16px;
    }
  }
  </style>
  