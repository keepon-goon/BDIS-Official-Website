import axios from 'axios'

const tokenHeader = token => token ? { 'X-Resume-Preview-Token': token } : {}

async function failure(error) {
  let body = error.response?.data
  if (body instanceof Blob) {
    try { body = JSON.parse(await body.text()) } catch { body = null }
  }
  return Object.assign(new Error(body?.message || error.message || '请求失败，请稍后重试'), {
    status: error.response?.status
  })
}

async function request(config) {
  try {
    const { data } = await axios({ timeout: 125000, ...config })
    if (data.code !== 0) throw new Error(data.message || '操作失败')
    return data.data
  } catch (error) {
    throw await failure(error)
  }
}

async function binary(url, headers = {}) {
  try {
    const response = await axios.get(url, { headers, responseType: 'blob', timeout: 125000 })
    if (response.status !== 200 || !response.data.size) throw new Error('文件响应为空或异常，请重新加载预览')
    if (response.headers['content-type']?.includes('application/json')) {
      const body = JSON.parse(await response.data.text())
      throw new Error(body.message || '文件读取失败')
    }
    const disposition = response.headers['content-disposition'] || ''
    const encoded = response.headers['x-resume-filename'] || disposition.match(/filename\*=UTF-8''([^;]+)/i)?.[1]
    return { blob: response.data, filename: encoded ? decodeURIComponent(encoded) : '报名表.docx' }
  } catch (error) {
    throw await failure(error)
  }
}

export function downloadResumeFile({ blob, filename }) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}

export const resumeApi = {
  generate: form => request({ method: 'post', url: '/api/resume/previews', data: form }),
  previewPdf: token => binary('/api/resume/previews/pdf', tokenHeader(token)),
  previewWord: token => binary('/api/resume/previews/word', tokenHeader(token)),
  template: () => binary('/api/resume/template'),
  photo: (token, file) => {
    const data = new FormData()
    data.append('photo', file)
    return request({ method: 'post', url: '/api/resume/previews/photo', headers: tokenHeader(token), data })
  },
  upload: (index, file, sourceToken) => {
    const data = new FormData()
    data.append('resume', file)
    Object.entries(index).forEach(([key, value]) => data.append(key, value))
    return request({ method: 'post', url: '/api/resume/submit', headers: tokenHeader(sourceToken), data })
  },
  confirm: token => request({ method: 'post', url: '/api/resume/confirm', headers: tokenHeader(token) }),
  list: params => request({ url: '/api/admin/resumes', params }),
  detail: id => request({ url: `/api/admin/resumes/${id}` }),
  savedPdf: id => binary(`/api/admin/resumes/${id}/pdf`),
  savedWord: id => binary(`/api/admin/resumes/${id}/word`),
  settings: () => request({ url: '/api/admin/resumes/settings' }),
  updateSettings: data => request({ method: 'put', url: '/api/admin/resumes/settings', data }),
  send: id => request({ method: 'post', url: `/api/admin/resumes/${id}/email` })
}
