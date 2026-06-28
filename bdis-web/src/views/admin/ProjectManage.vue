<template>
  <div class="page-container">
    <div class="page-header">
      <h2>项目管理</h2>
      <el-button type="primary" @click="handleAdd">新增项目</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="项目名" min-width="140" show-overflow-tooltip />
        <el-table-column prop="tagline" label="简介" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusMap[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="140">
          <template #default="{ row }">
            <el-progress :percentage="row.progress || 0" :stroke-width="14" text-inside />
          </template>
        </el-table-column>
        <el-table-column prop="recruitRoles" label="招募角色" min-width="130" show-overflow-tooltip />
        <el-table-column prop="members" label="成员" min-width="130" show-overflow-tooltip />
        <el-table-column prop="updatedAt" label="更新时间" width="170" />
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image
              v-if="row.coverUrl"
              :src="normalizeImageUrl(row.coverUrl)"
              :preview-src-list="[normalizeImageUrl(row.coverUrl)]"
              fit="cover"
              style="width: 50px; height: 50px; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑项目' : '新增项目'"
      width="650px"
      destroy-on-close
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名">
          <el-input v-model="form.name" placeholder="请输入项目名" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.tagline" type="textarea" :rows="2" placeholder="请输入项目简介" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="(label, key) in statusMap"
              :key="key"
              :label="label"
              :value="key"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="进度">
          <el-slider v-model="form.progress" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="招募角色">
          <el-input v-model="form.recruitRoles" placeholder="多个角色用逗号分隔，如：前端开发,后端开发,UI设计" />
        </el-form-item>
        <el-form-item label="成员">
          <el-input v-model="form.members" placeholder="多个成员用逗号分隔，如：张三,李四,王五" />
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
            action="/api/admin/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <div v-loading="uploadLoading" style="width: 150px; height: 150px; border-radius: 4px; overflow: hidden">
              <el-image
                v-if="form.coverUrl"
                :src="normalizeImageUrl(form.coverUrl)"
                fit="cover"
                style="width: 150px; height: 150px"
              />
              <el-icon v-else style="font-size: 28px; color: #8c939d; width: 150px; height: 150px; border: 1px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center">
                <Plus />
              </el-icon>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="form.link" placeholder="请输入链接" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUploadUrl, normalizeImageUrl } from '@/utils/imageUrl'

const token = localStorage.getItem('adminToken')
const headers = { Authorization: 'Bearer ' + token }
const uploadHeaders = { Authorization: 'Bearer ' + token }

const statusMap = {
  recruiting: '招募中',
  ongoing: '进行中',
  paused: '已暂停',
  done: '已完成'
}

const statusTagType = (status) => {
  const map = { recruiting: 'primary', ongoing: 'success', paused: 'warning', done: 'info' }
  return map[status] || ''
}

const loading = ref(false)
const submitLoading = ref(false)
const uploadLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)

const defaultForm = {
  id: null,
  name: '',
  tagline: '',
  status: '',
  progress: 0,
  recruitRoles: '',
  members: '',
  coverUrl: '',
  link: '',
  sortOrder: 0
}

const form = reactive({ ...defaultForm })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/projects', { headers })
    tableData.value = res.data.data || []
  } catch (e) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, defaultForm)
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

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

const handleUploadSuccess = (res) => {
  uploadLoading.value = false
  if (res.code === 0 || res.url) {
    form.coverUrl = getUploadUrl(res)
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

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    const submitForm = {
      ...form,
      coverUrl: normalizeImageUrl(form.coverUrl)
    }
    if (isEdit.value) {
      await axios.put(`/api/admin/projects/${form.id}`, submitForm, { headers })
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/admin/projects', submitForm, { headers })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该项目吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`/api/admin/projects/${row.id}`, { headers })
      ElMessage.success('删除成功')
      fetchData()
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(fetchData)
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
