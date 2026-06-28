<template>
  <div class="page-container">
    <div class="page-header">
      <h2>论坛帖子管理</h2>
      <el-button type="primary" @click="handleAdd">新增帖子</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
        <el-table-column prop="summary" label="摘要" min-width="180" show-overflow-tooltip />
        <el-table-column prop="recruitRoles" label="招募角色" min-width="120" show-overflow-tooltip />
        <el-table-column label="人数" width="100" align="center">
          <template #default="{ row }">
            {{ row.currentSize || 0 }}/{{ row.targetSize || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="联系方式" width="140">
          <template #default="{ row }">
            <span v-if="row.type">{{ contactTypeMap[row.type] }}: {{ row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="120" />
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.coverUrl"
              :src="normalizeImageUrl(row.coverUrl)"
              :preview-src-list="[normalizeImageUrl(row.coverUrl)]"
              fit="cover"
              style="width: 45px; height: 45px; border-radius: 4px"
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
      :title="isEdit ? '编辑帖子' : '新增帖子'"
      width="650px"
      destroy-on-close
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入摘要" />
        </el-form-item>
        <el-form-item label="招募角色">
          <el-input v-model="form.recruitRoles" placeholder="多个角色用逗号分隔" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="当前人数">
              <el-input-number v-model="form.currentSize" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标人数">
              <el-input-number v-model="form.targetSize" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="10">
            <el-form-item label="联系方式">
              <el-select v-model="form.type" placeholder="类型">
                <el-option
                  v-for="(label, key) in contactTypeMap"
                  :key="key"
                  :label="label"
                  :value="key"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="14">
            <el-form-item label="联系值">
              <el-input v-model="form.value" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="form.deadline"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择截止日期"
          />
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
            <div v-loading="uploadLoading" style="width: 150px; height: 100px; border-radius: 4px; overflow: hidden">
              <el-image
                v-if="form.coverUrl"
                :src="normalizeImageUrl(form.coverUrl)"
                fit="cover"
                style="width: 150px; height: 100px"
              />
              <el-icon v-else style="font-size: 28px; color: #8c939d; width: 150px; height: 100px; border: 1px dashed #d9d9d9; border-radius: 4px; display: flex; align-items: center; justify-content: center">
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

const contactTypeMap = {
  wechat: '微信',
  qq: 'QQ',
  email: '邮箱',
  link: '链接'
}

const loading = ref(false)
const submitLoading = ref(false)
const uploadLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)

const defaultForm = {
  id: null,
  title: '',
  summary: '',
  recruitRoles: '',
  currentSize: 0,
  targetSize: 1,
  type: '',
  value: '',
  deadline: '',
  coverUrl: '',
  link: '',
  sortOrder: 0
}

const form = reactive({ ...defaultForm })

const fetchData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/forum-posts', { headers })
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
      await axios.put(`/api/admin/forum-posts/${form.id}`, submitForm, { headers })
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/admin/forum-posts', submitForm, { headers })
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
  ElMessageBox.confirm('确定删除该帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`/api/admin/forum-posts/${row.id}`, { headers })
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
