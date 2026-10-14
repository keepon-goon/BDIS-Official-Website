<template>
  <el-dialog
    :model-value="modelValue"
    :title="title"
    width="640px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @update:model-value="handleModelUpdate"
    @close="handleClose"
  >
    <p class="sort-dialog-description">{{ description }}</p>

    <div v-if="orderedItems.length" class="sort-list">
      <div v-for="(item, index) in orderedItems" :key="item.key" class="sort-row">
        <span class="sort-position">{{ index }}</span>
        <div class="sort-item-copy">
          <span class="sort-item-label" :title="item.label">{{ item.label }}</span>
          <el-tag v-if="item.isCandidate" size="small" type="success">本次调整</el-tag>
          <span v-else class="sort-item-current">原序号 {{ item.currentOrder }}</span>
        </div>
        <div class="sort-controls">
          <el-button
            text
            circle
            :disabled="index === 0 || loading"
            aria-label="上移"
            @click="moveItem(index, -1)"
          >
            <el-icon><ArrowUp /></el-icon>
          </el-button>
          <el-button
            text
            circle
            :disabled="index === orderedItems.length - 1 || loading"
            aria-label="下移"
            @click="moveItem(index, 1)"
          >
            <el-icon><ArrowDown /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无可排序内容" />

    <template #footer>
      <el-button :disabled="loading" @click="handleCancel">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">
        保存顺序并重新编号
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  items: {
    type: Array,
    default: () => []
  },
  title: {
    type: String,
    default: '调整排序'
  },
  description: {
    type: String,
    default: '用上下按钮调整顺序，保存后会自动重新编号。'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])
const orderedKeys = ref([])
const orderedItems = computed(() => {
  const itemByKey = new Map(props.items.map((item) => [item.key, item]))
  return orderedKeys.value.map((key) => itemByKey.get(key)).filter(Boolean)
})

watch(
  () => [props.modelValue, props.items],
  () => {
    if (props.modelValue) {
      orderedKeys.value = props.items.map((item) => item.key)
    }
  },
  { deep: true, immediate: true }
)

const moveItem = (index, direction) => {
  const targetIndex = index + direction
  if (targetIndex < 0 || targetIndex >= orderedKeys.value.length) return

  const nextKeys = [...orderedKeys.value]
  ;[nextKeys[index], nextKeys[targetIndex]] = [nextKeys[targetIndex], nextKeys[index]]
  orderedKeys.value = nextKeys
}

const handleModelUpdate = (value) => {
  emit('update:modelValue', value)
  if (!value) emit('cancel')
}

const handleClose = () => {
  emit('update:modelValue', false)
  emit('cancel')
}

const handleCancel = () => {
  emit('update:modelValue', false)
  emit('cancel')
}

const handleConfirm = () => {
  if (orderedKeys.value.length > 0) emit('confirm', [...orderedKeys.value])
}
</script>

<style scoped>
.sort-dialog-description {
  margin: 0 0 16px;
  color: #606266;
  line-height: 1.7;
}

.sort-list {
  max-height: 55vh;
  overflow-y: auto;
  padding-right: 4px;
}

.sort-row {
  display: flex;
  align-items: center;
  min-height: 52px;
  padding: 4px 10px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}

.sort-row + .sort-row {
  margin-top: 8px;
}

.sort-position {
  flex: 0 0 34px;
  color: #0c3d70;
  font-weight: 600;
  text-align: center;
}

.sort-item-copy {
  display: flex;
  flex: 1;
  align-items: center;
  min-width: 0;
  gap: 10px;
}

.sort-item-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sort-item-current {
  flex: 0 0 auto;
  color: #909399;
  font-size: 12px;
}

.sort-controls {
  display: flex;
  flex: 0 0 auto;
  margin-left: 8px;
}
</style>
