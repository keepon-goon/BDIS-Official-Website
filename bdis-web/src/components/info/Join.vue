<template>
  <section class="join-page">
    <div class="join-container">
      <div class="join-section">
        <div class="section-header">
          <h2 class="section-title">写给未来的你</h2>
          <div class="section-divider"></div>
        </div>
        <div class="section-content">
          <div class="intro-card">
            <div class="intro-text">
              <p>我们用算法洞察趋势，用代码把想法落地，用系统承载智能，让技术真正产生价值。</p>

              <p>这里，</p>

              <p class="intro-text-word">◦ 你可以和热爱技术的伙伴一起，做前端、做后端，把一个产品从 0 搭到 1；</p>
              <p class="intro-text-word">◦ 你可以做数据分析、机器学习与深度学习，把模型训出来、跑通实验、拿到可复现的结果；</p>
              <p class="intro-text-word">◦ 你也可以做测试与运维，把系统的稳定性、质量与交付流程打磨到更可靠、更专业。</p>

              <p><strong>我们相信，一个人的力量有限，但一群人的协作无穷。</strong></p>

              <p>如果你想让简历更有分量，让能力更成体系——</p>
              <p>无论你是准备本科就业，还是奔着考研提升，</p>

              <p>那就来吧——</p>

              <p><strong>加入我们，一起用数据与智能，做更多有意义、做得出来、拿得出手的事。</strong></p>
            </div>
            <div class="intro-tags">
              <span class="tag">项目驱动</span>
              <span class="tag">团队协作</span>
              <span class="tag">成长反馈</span>
              <span class="tag">作品沉淀</span>
            </div>
          </div>
        </div>
      </div>

      <div class="join-section">
        <div class="section-header">
          <h2 class="section-title">我们需要的人</h2>
          <div class="section-divider"></div>
        </div>
        <div class="role-grid">
          <div class="role-card" v-for="role in roles" :key="role.title">
            <div class="role-icon">{{ role.icon }}</div>
            <h3 class="role-title">{{ role.title }}</h3>
            <p class="role-desc">{{ role.desc }}</p>
            <div class="role-tags">
              <span class="role-tag" v-for="tag in role.tags" :key="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="join-section">
        <div class="section-header">
          <h2 class="section-title">你会获得</h2>
          <div class="section-divider"></div>
        </div>
        <div class="benefit-grid">
          <div class="benefit-card" v-for="benefit in benefits" :key="benefit.title">
            <div class="benefit-number">{{ benefit.number }}</div>
            <div class="benefit-content">
              <h4 class="benefit-title">{{ benefit.title }}</h4>
              <p class="benefit-desc">{{ benefit.desc }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="join-section">
        <div class="section-header">
          <h2 class="section-title">加入流程</h2>
          <div class="section-divider"></div>
        </div>
        <div class="process-grid">
          <div class="process-card" v-for="step in steps" :key="step.title">
            <div class="process-step">{{ step.step }}</div>
            <h4 class="process-title">{{ step.title }}</h4>
            <p class="process-desc">{{ step.desc }}</p>
          </div>
        </div>
        <p class="process-tip">建议提前了解感兴趣的方向、学一些基础；若不清楚如何准备，可加下方招新群或学长学姐 QQ 咨询。</p>        
      </div>

      <div class="join-section">
        <div class="section-header">
          <h2 class="section-title">联系我们</h2>
          <div class="section-divider"></div>
        </div>
        <div class="cta-card" id="registration">
          <div class="cta-text">
            <p class="cta-title">准备好了就出发</p>
            <p class="cta-desc">只需投递简历即可报名；如有疑问可加招新群或学长学姐 QQ 咨询。</p>
          </div>
          <div class="cta-info">
            <div class="cta-item">
              <span class="cta-label">邮箱</span>
              <span class="cta-value">18862028557@163.com</span>
            </div>
            <div class="cta-item">
              <span class="cta-label">QQ群号</span>
              <span class="cta-value">935982021</span>
            </div>
            <div class="cta-item">
              <span class="cta-label">地点</span>
              <span class="cta-value">重庆理工大学 博园4号楼305</span>
            </div>
          </div>
          <div class="cta-buttons">
            <a class="cta-button" href="/工作室招新报名表.docx" download="工作室招新报名表.docx">
              获取报名表
            </a>
            <button type="button" class="cta-button cta-button-resume" @click="showResumeDialog = true">
              在线提交简历
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 在线提交简历弹窗 -->
    <el-dialog
      v-model="showResumeDialog"
      title="在线提交简历"
      width="420px"
      :close-on-click-modal="false"
      @close="resetResumeForm"
    >
      <p class="resume-dialog-desc">上传 Word 简历（.doc / .docx），提交后将发送至招新邮箱。</p>
      <el-form label-width="80px" label-position="top">
        <el-form-item label="简历文件" required>
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".doc,.docx"
            :on-change="onResumeFileChange"
            :on-exceed="onResumeExceed"
            :file-list="resumeFileList"
          >
            <template #trigger>
              <el-button type="primary" plain>选择 Word 文件</el-button>
            </template>
          </el-upload>
          <div v-if="resumeFile" class="resume-file-name">{{ resumeFile.name }}</div>
        </el-form-item>
        <el-form-item label="姓名（选填）">
          <el-input v-model="resumeForm.name" placeholder="您的姓名" clearable />
        </el-form-item>
        <el-form-item label="邮箱（选填）">
          <el-input v-model="resumeForm.email" placeholder="用于接收回复" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showResumeDialog = false">取消</el-button>
        <el-button type="primary" :loading="resumeSubmitting" @click="submitResume">
          提交并发送至邮箱
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const showResumeDialog = ref(false)
const resumeFileList = ref([])
const resumeFile = ref(null)
const resumeSubmitting = ref(false)
const uploadRef = ref(null)
const resumeForm = reactive({ name: '', email: '' })

function onResumeFileChange(file) {
  resumeFile.value = file.raw
  resumeFileList.value = [file]
}

function onResumeExceed() {
  ElMessage.warning('仅支持上传 1 个文件，请先移除当前文件再选择。')
}

function resetResumeForm() {
  resumeFile.value = null
  resumeFileList.value = []
  resumeForm.name = ''
  resumeForm.email = ''
  uploadRef.value?.clearFiles()
}

async function submitResume() {
  if (!resumeFile.value) {
    ElMessage.warning('请先选择要上传的 Word 简历。')
    return
  }
  const formData = new FormData()
  formData.append('file', resumeFile.value)
  if (resumeForm.name) formData.append('name', resumeForm.name)
  if (resumeForm.email) formData.append('email', resumeForm.email)
  resumeSubmitting.value = true
  try {
    const { data } = await axios.post('/api/join/resume', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (data?.code === 0) {
      ElMessage.success('简历已提交，将发送至招新邮箱。')
      showResumeDialog.value = false
      resetResumeForm()
    } else {
      ElMessage.error(data?.message || '提交失败，请稍后重试。')
    }
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '提交失败，请检查网络或联系管理员。')
  } finally {
    resumeSubmitting.value = false
  }
}

const roles = [
  { icon: '🧩', title: '前端开发', desc: '官网与业务可视化页面搭建，提升交互与表达。', tags: ['Vue3', 'ECharts', 'UI'] },
  { icon: '⚙️', title: '后端开发', desc: '服务与接口设计，保障稳定性与可扩展。', tags: ['SpringBoot', 'API', '数据库'] },
  { icon: '🧱', title: '数据工程', desc: '采集-清洗-入库链路与数据质量治理。', tags: ['ETL', 'Kafka', '质量'] },
  { icon: '📈', title: '数据分析', desc: '指标体系与分析报告，把数据变成决策依据。', tags: ['统计', '可视化', '洞察'] },
  { icon: '🧠', title: 'AI建模', desc: '机器学习/深度学习建模与评估，推进落地。', tags: ['Sklearn', 'PyTorch', '评估'] },
  { icon: '🛠️', title: '工程保障', desc: '测试、部署、监控与发布流程，保证交付稳定。', tags: ['测试', 'CI/CD', '运维'] }
]

const benefits = [
  { number: '01', title: '真实项目体验', desc: '从需求到上线，全流程参与，作品可展示。' },
  { number: '02', title: '稳定的成长节奏', desc: '定期分享与复盘，导师/学长姐带你提速。' },
  { number: '03', title: '团队协作氛围', desc: '明确分工+协作工具，像小型创业团队。' }
]

const steps = [
  { step: '01', title: '投递简历', desc: '将简历投递至招新邮箱或通过本页在线提交即可，无其他硬性要求。' },
  { step: '02', title: '简单沟通', desc: '了解你的兴趣点与学习计划。' },
  { step: '03', title: '正式加入', desc: '进入项目组，开始一起做事。' }
]
</script>

<style scoped>
.join-page {
  min-height: calc(100vh - 135px);
  background-color: #f9f9fa;
}

.join-hero {
  width: 100%;
  height: 280px;
  background: linear-gradient(135deg, #0c3d70 0%, #1a5a8e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.hero-content {
  text-align: center;
  padding: 0 16px;
}

.hero-kicker {
  margin: 0 0 8px;
  font-size: 14px;
  letter-spacing: 4px;
  opacity: 0.8;
}

.hero-title {
  margin: 0 0 12px;
  font-size: 42px;
  font-weight: 600;
  letter-spacing: 2px;
}

.hero-subtitle {
  margin: 0;
  font-size: 18px;
  opacity: 0.9;
}

.join-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 32px;
}

.join-section {
  margin-bottom: 80px;
}

.join-section:last-child {
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
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.intro-text p {
  font-size: 18px;
  line-height: 1.8;
  color: #333;
  margin: 0 0 24px;
  text-align: left;
}

.intro-text .intro-text-word {
  font-size: 18px;
  line-height: 1.8;
  color: #333;
  margin: 0 0 24px;
  padding-left: 45px;
}

.intro-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
}

.tag {
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(12, 61, 112, 0.08);
  color: #0c3d70;
  font-size: 14px;
  font-weight: 500;
}

.role-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 32px;
}

.role-card {
  background: #fff;
  padding: 32px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.role-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.role-icon {
  font-size: 40px;
  margin-bottom: 16px;
}

.role-title {
  font-size: 20px;
  font-weight: 600;
  color: #0c3d70;
  margin: 0 0 12px;
}

.role-desc {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  margin: 0 0 16px;
}

.role-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.role-tag {
  padding: 6px 10px;
  border-radius: 6px;
  background: #f3f6fa;
  color: #0c3d70;
  font-size: 12px;
  font-weight: 500;
}

.benefit-grid {
  max-width: 900px;
  margin: 0 auto;
  display: grid;
  gap: 24px;
}

.benefit-card {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  background: #fff;
  padding: 28px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.benefit-number {
  font-size: 28px;
  font-weight: 700;
  color: #0c3d70;
  line-height: 1;
}

.benefit-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 6px;
  color: #333;
}

.benefit-desc {
  margin: 0;
  color: #666;
  font-size: 15px;
  line-height: 1.6;
}

.process-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 24px;
}

.process-card {
  background: #fff;
  padding: 28px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.process-step {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(12, 61, 112, 0.1);
  color: #0c3d70;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  margin: 0 auto 16px;
}

.process-title {
  margin: 0 0 8px;
  font-size: 18px;
  color: #333;
}

.process-desc {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.process-tip {
  margin: 24px auto 0;
  max-width: 640px;
  text-align: center;
  color: #666;
  font-size: 14px;
  line-height: 1.7;
}

.cta-card {
  background: #fff;
  padding: 36px;
  border-radius: 16px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  display: grid;
  gap: 24px;
  align-items: center;
  text-align: center;
}

.cta-title {
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #0c3d70;
}

.cta-desc {
  margin: 0;
  color: #666;
  font-size: 15px;
  line-height: 1.6;
}

.cta-info {
  display: grid;
  gap: 12px;
  text-align: left;
  max-width: 520px;
  margin: 0 auto;
}

.cta-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #e1e6ef;
}

.cta-label {
  color: #666;
  font-size: 14px;
}

.cta-value {
  color: #0c3d70;
  font-weight: 600;
  font-size: 14px;
}

.cta-buttons {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px;
}

.cta-button {
  padding: 12px 28px;
  border: none;
  border-radius: 999px;
  background: #0c3d70;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(12, 61, 112, 0.25);
}

.cta-button-resume {
  background: #1a5a8e;
}

.cta-button-resume:hover {
  box-shadow: 0 8px 16px rgba(26, 90, 142, 0.3);
}

.resume-dialog-desc {
  margin: 0 0 20px;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.resume-file-name {
  margin-top: 8px;
  font-size: 13px;
  color: #0c3d70;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .join-container {
    padding: 40px 16px;
  }

  .section-title {
    font-size: 26px;
  }

  .benefit-card {
    flex-direction: column;
    gap: 12px;
  }

  .cta-card {
    padding: 28px 20px;
  }

  .cta-item {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
