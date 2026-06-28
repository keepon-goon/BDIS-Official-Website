<template>
  <div class="page-container">
    <div class="page-header">
      <h2>团队成员管理</h2>
      <el-button type="primary" @click="handleAdd">新增成员</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="100">
          <template #default="{ row }">
            <el-avatar
              v-if="row.avatarUrl"
              :src="normalizeImageUrl(row.avatarUrl)"
              :size="48"
            />
            <el-avatar v-else :size="48">
              {{ (row.name || '?')[0] }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="140" />
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
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
      :title="isEdit ? '编辑成员' : '新增成员'"
      width="500px"
      destroy-on-close
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input v-model="form.role" placeholder="请输入角色，如：负责人、前端开发" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            action="/api/admin/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <div v-loading="uploadLoading" style="width: 80px; height: 80px; border-radius: 50%; overflow: hidden">
              <el-avatar
                v-if="form.avatarUrl"
                :src="normalizeImageUrl(form.avatarUrl)"
                :size="80"
              />
              <el-icon v-else style="font-size: 28px; color: #8c939d; width: 80px; height: 80px; border: 1px dashed #d9d9d9; border-radius: 50%; display: flex; align-items: center; justify-content: center">
                <Plus />
              </el-icon>
            </div>
          </el-upload>
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

const loading = ref(false)
const submitLoading = ref(false)
const uploadLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)

const defaultForm = {
  id: null,
  name: '',
  role: '',
  description: '',
  avatarUrl: ''
}

const form = reactive({ ...defaultForm })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/team-members', { headers })
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
    form.avatarUrl = getUploadUrl(res)
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
      avatarUrl: normalizeImageUrl(form.avatarUrl)
    }
    if (isEdit.value) {
      await axios.put(`/api/admin/team-members/${form.id}`, submitForm, { headers })
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/admin/team-members', submitForm, { headers })
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
  ElMessageBox.confirm('确定删除该成员吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`/api/admin/team-members/${row.id}`, { headers })
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
