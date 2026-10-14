import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { buildSortPlan, createSortContext, persistSortPlan } from '@/utils/sortOrder'

export const useSortOrder = ({
  tableData,
  isEdit,
  sortField,
  baseUrl,
  headers,
  getLabel,
  onSaved,
  onSuccess
}) => {
  const sortConflictVisible = ref(false)
  const sortConflictItems = ref([])
  const sortDialogTitle = ref('调整排序')
  const sortDialogDescription = ref('用上下按钮调整完整顺序，保存后所有内容会自动重新编号。')
  const sortSubmitLoading = ref(false)
  const pendingAction = ref(null)

  const entriesInChosenOrder = (plan, orderedKeys) => {
    const entryByKey = new Map(plan.map((entry) => [entry.key, entry]))
    return orderedKeys
      .map((key) => entryByKey.get(key))
      .filter(Boolean)
      .map((entry, sortOrder) => ({ ...entry, sortOrder }))
  }

  const saveOrder = async (plan, savedId = null) => {
    await persistSortPlan({ axios, baseUrl, headers, plan, savedId })
    await onSuccess?.()
  }

  const openOrderDialog = (plan, action, title, description) => {
    pendingAction.value = { plan, action }
    sortConflictItems.value = plan
    sortDialogTitle.value = title
    sortDialogDescription.value = description
    sortConflictVisible.value = true
  }

  const executeSubmission = async ({ plan, submitForm, saveItem }) => {
    const wasEdit = isEdit.value
    sortSubmitLoading.value = true
    try {
      const response = await saveItem(submitForm)
      if (response?.data?.code !== undefined && response.data.code !== 0) {
        throw new Error(response.data.message || '保存失败')
      }

      const savedId = submitForm.id ?? response?.data?.data?.id
      if (savedId === undefined || savedId === null || savedId === '') {
        throw new Error('保存成功，但没有获取到内容 ID，排序未完成')
      }
      if (submitForm.id === undefined || submitForm.id === null || submitForm.id === '') {
        onSaved?.(savedId)
      }

      await saveOrder(plan, savedId)
      ElMessage.success(wasEdit ? '更新成功，排序已整理' : '新增成功，排序已整理')
      return true
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '操作失败')
      return false
    } finally {
      sortSubmitLoading.value = false
    }
  }

  const startSortSubmit = async ({ submitForm, saveItem }) => {
    const context = createSortContext({
      items: tableData.value || [],
      candidate: submitForm,
      sortField,
      getLabel
    })
    const plan = buildSortPlan(context)

    if (context.hasConflict) {
      openOrderDialog(
        plan,
        { type: 'submit', submitForm, saveItem },
        '处理重复编号并确认顺序',
        '目标位置已有内容，或列表中存在重复编号。请调整完整顺序；确认后保存并重新编号，确保每项编号唯一。'
      )
      return false
    }

    return executeSubmission({ plan, submitForm, saveItem })
  }

  const openSortManager = () => {
    const items = [...(tableData.value || [])]
      .map((item, index) => ({
        item,
        key: String(item.id),
        currentOrder: Number.isFinite(Number(item?.[sortField])) ? Number(item[sortField]) : index,
        originalIndex: index,
        isCandidate: false,
        label: getLabel(item, false)
      }))
      .sort((left, right) => left.currentOrder - right.currentOrder || left.originalIndex - right.originalIndex)
      .map((item, sortOrder) => ({ ...item, sortOrder }))

    if (items.length === 0) {
      ElMessage.info('当前没有可排序的内容')
      return
    }

    openOrderDialog(
      items,
      { type: 'reorder' },
      '整理排序',
      '可以逐项调整上下顺序，包括当前编号重复的内容；保存后会统一编号为 0、1、2……。'
    )
  }

  const moveToPosition = async (item, position) => {
    const context = createSortContext({
      items: tableData.value || [],
      candidate: { ...item, [sortField]: position },
      sortField,
      getLabel
    })
    const plan = buildSortPlan(context)

    if (context.hasConflict) {
      openOrderDialog(
        plan,
        { type: 'reorder', savedId: item.id },
        '处理重复编号并确认顺序',
        '列表中存在重复编号，或目标位置已有内容。请调整完整顺序；确认后所有内容会重新编号。'
      )
      return
    }

    sortSubmitLoading.value = true
    try {
      await saveOrder(plan, item.id)
      ElMessage.success('已置顶并重新整理排序')
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '排序失败')
    } finally {
      sortSubmitLoading.value = false
    }
  }

  const confirmSortConflict = async (orderedKeys) => {
    const currentAction = pendingAction.value
    if (!currentAction) return false

    const plan = entriesInChosenOrder(currentAction.plan, orderedKeys)
    if (plan.length !== currentAction.plan.length) {
      ElMessage.error('排序清单已变化，请重新打开后再保存')
      return false
    }

    sortConflictVisible.value = false
    pendingAction.value = null

    if (currentAction.action.type === 'submit') {
      return executeSubmission({
        plan,
        submitForm: currentAction.action.submitForm,
        saveItem: currentAction.action.saveItem
      })
    }

    sortSubmitLoading.value = true
    try {
      await saveOrder(plan, currentAction.action.savedId)
      ElMessage.success('顺序已保存，编号已自动整理')
      return true
    } catch (error) {
      ElMessage.error(error.response?.data?.message || error.message || '排序保存失败')
      return false
    } finally {
      sortSubmitLoading.value = false
    }
  }

  const cancelSortConflict = () => {
    pendingAction.value = null
    sortConflictVisible.value = false
  }

  return {
    sortConflictVisible,
    sortConflictItems,
    sortDialogTitle,
    sortDialogDescription,
    sortSubmitLoading,
    startSortSubmit,
    openSortManager,
    moveToPosition,
    confirmSortConflict,
    cancelSortConflict
  }
}
