import axios from 'axios'

export const feedbackCategories = [
  { value: 'suggestion', label: '工作室与网站' },
  { value: 'equipment', label: '设备问题' },
  { value: 'activity', label: '活动建议' },
  { value: 'other', label: '其他想法' }
]

export const feedbackStatuses = [
  { value: 'UNREAD', label: '未读', type: 'info' },
  { value: 'PROCESSING', label: '处理中', type: 'warning' },
  { value: 'RESOLVED', label: '已处理', type: 'success' }
]

async function request(config) {
  try {
    const response = await axios({ timeout: 60000, ...config })
    if (config.responseType === 'blob') return response.data
    if (response.data.code !== 0) throw new Error(response.data.message || '操作失败，请稍后重试')
    return response.data.data
  } catch (error) {
    if (axios.isCancel(error)) throw error
    let body = error.response?.data
    if (body instanceof Blob) {
      try { body = JSON.parse(await body.text()) } catch { body = null }
    }
    const connectionMessage = error.code === 'ECONNABORTED' ? '请求超时，请稍后重试'
      : error.code === 'ERR_NETWORK' ? '网络连接失败，请稍后重试' : error.message
    throw new Error(body?.message || (error.response ? '请求失败，请稍后重试' : connectionMessage || '网络连接失败，请稍后重试'))
  }
}

export const feedbackApi = {
  submit: data => request({ method: 'post', url: '/api/feedback', data }),
  list: (params, signal) => request({ url: '/api/admin/feedback', params, signal }),
  detail: (id, signal) => request({ url: `/api/admin/feedback/${encodeURIComponent(id)}`, signal }),
  status: (id, status) => request({ method: 'put', url: `/api/admin/feedback/${encodeURIComponent(id)}/status`, data: { status } }),
  image: (url, signal) => request({ url, responseType: 'blob', signal })
}
