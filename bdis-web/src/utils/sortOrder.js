const NEW_ITEM_KEY = '__new_sort_item__'

const toSortNumber = (value, fallback = 0) => {
  const number = Number(value)
  return Number.isFinite(number) && number >= 0 ? Math.floor(number) : fallback
}

const toItemKey = (item, fallback) => {
  if (item?.id !== undefined && item?.id !== null) {
    return String(item.id)
  }
  return fallback
}

export const createSortContext = ({
  items = [],
  candidate,
  sortField,
  getLabel = (item) => item?.title || item?.name || '未命名内容'
}) => {
  const candidateKey = toItemKey(candidate, NEW_ITEM_KEY)
  const existing = items
    .map((item, index) => ({
      item,
      key: toItemKey(item, `__sort_row_${index}__`),
      currentOrder: toSortNumber(item?.[sortField], index),
      originalIndex: index,
      isCandidate: false,
      label: getLabel(item, false)
    }))
    .filter((item) => item.key !== candidateKey)
    .sort((left, right) => left.currentOrder - right.currentOrder || left.originalIndex - right.originalIndex)

  const requestedOrder = toSortNumber(candidate?.[sortField], 0)
  const candidateMeta = {
    item: candidate,
    key: candidateKey,
    currentOrder: requestedOrder,
    originalIndex: -1,
    isCandidate: true,
    label: getLabel(candidate, true)
  }
  const existingOrders = new Set()
  const hasExistingDuplicates = existing.some((item) => {
    if (existingOrders.has(item.currentOrder)) return true
    existingOrders.add(item.currentOrder)
    return false
  })

  return {
    candidate: candidateMeta,
    candidateKey,
    existing,
    requestedOrder,
    hasExistingDuplicates,
    targetOccupied: existing.some((item) => item.currentOrder === requestedOrder),
    hasConflict: hasExistingDuplicates || existing.some((item) => item.currentOrder === requestedOrder)
  }
}

/** Treat the requested number as a position. Moving to 5 shifts the entries in between. */
export const buildSortPlan = (context) => {
  const { candidate, existing, requestedOrder } = context
  const insertionIndex = Math.min(requestedOrder, existing.length)
  const orderedItems = [
    ...existing.slice(0, insertionIndex),
    candidate,
    ...existing.slice(insertionIndex)
  ]

  return orderedItems.map((item, sortOrder) => ({
    ...item,
    sortOrder
  }))
}

/** Persist a complete order through the backend's transactional batch endpoint. */
export const persistSortPlan = async ({
  axios,
  baseUrl,
  headers,
  plan,
  savedId
}) => {
  const orderedIds = plan.map((entry) => entry.isCandidate ? String(savedId) : entry.key)
  const response = await axios.put(`${baseUrl}/reorder`, orderedIds, { headers })

  if (response?.data?.code !== undefined && response.data.code !== 0) {
    throw new Error(response.data.message || '排序写入失败')
  }
}
