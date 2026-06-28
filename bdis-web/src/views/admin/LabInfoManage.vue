<template>
  <div class="page-container">
    <div class="page-header">
      <h2>实验室信息设置</h2>
    </div>

    <el-card shadow="never" v-loading="loading">
      <el-form :model="form" label-width="120px" style="max-width: 700px">
        <el-form-item label="实验室名称">
          <el-input v-model="form.name" placeholder="请输入实验室名称" />
        </el-form-item>
        <el-form-item label="英文名称">
          <el-input v-model="form.nameEn" placeholder="请输入英文名称" />
        </el-form-item>
        <el-form-item label="简要介绍">
          <el-input v-model="form.introShort" type="textarea" :rows="4" placeholder="请输入简要介绍" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="QQ群">
          <el-input v-model="form.qqGroup" placeholder="请输入QQ群号" />
        </el-form-item>
        <el-form-item label="Logo">
          <el-upload
            action="/api/admin/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleLogoUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <div v-loading="uploadLoading" style="width: 120px; height: 120px; border-radius: 4px; overflow: hidden">
              <el-image
                v-if="form.logoUrl"
                :src="normalizeImageUrl(form.logoUrl)"
                fit="contain"
                style="width: 120px; height: 120px"
              />
              <el-icon v-else style="font-size: 28px; color: #8c939d; width: 120px; height: 120px; border: 1px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center">
                <Plus />
              </el-icon>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="实验室图片">
          <el-upload
            action="/api/admin/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handlePictureUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <div v-loading="uploadLoading" style="width: 200px; height: 120px; border-radius: 4px; overflow: hidden">
              <el-image
                v-if="form.picture"
                :src="normalizeImageUrl(form.picture)"
                fit="cover"
                style="width: 200px; height: 120px"
              />
              <el-icon v-else style="font-size: 28px; color: #8c939d; width: 200px; height: 120px; border: 1px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center">
                <Plus />
              </el-icon>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="实验室描述">
          <el-input v-model="form.labDesc" type="textarea" :rows="6" placeholder="请输入实验室详细描述" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="submitLoading" style="background-color: #0C3D70; border-color: #0C3D70">
            保存设置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUploadUrl, normalizeImageUrl } from '@/utils/imageUrl'

const token = localStorage.getItem('adminToken')
const headers = { Authorization: 'Bearer ' + token }
const uploadHeaders = { Authorization: 'Bearer ' + token }

const loading = ref(false)
const submitLoading = ref(false)
const uploadLoading = ref(false)

const form = reactive({
  name: '',
  nameEn: '',
  introShort: '',
  address: '',
  email: '',
  qqGroup: '',
  logoUrl: '',
  picture: '',
  labDesc: ''
})

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  uploadLoading.value = true
  return true
}

const handleLogoUploadSuccess = (res) => {
  uploadLoading.value = false
  if (res.code === 0 || res.url) {
    form.logoUrl = getUploadUrl(res)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handlePictureUploadSuccess = (res) => {
  uploadLoading.value = false
  if (res.code === 0 || res.url) {
    form.picture = getUploadUrl(res)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleUploadError = (error) => {
  uploadLoading.value = false
  console.error('上传失败:', error)
  ElMessage.error('图片上传失败，请检查网络或服务器状态')
}

const fetchInfo = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/lab-info', { headers })
    const data = res.data.data || res.data || {}
    Object.keys(form).forEach((key) => {
      if (data[key] !== undefined) {
        form[key] = data[key]
      }
    })
  } catch (e) {
    ElMessage.error('获取实验室信息失败')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  submitLoading.value = true
  try {
    const submitForm = {
      ...form,
      logoUrl: normalizeImageUrl(form.logoUrl),
      picture: normalizeImageUrl(form.picture)
    }
    await axios.put('/api/admin/lab-info', submitForm, { headers })
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(fetchInfo)
</script>

<style scoped>
.page-container {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  color: #0C3D70;
}
</style>
