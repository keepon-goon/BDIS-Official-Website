export const normalizeUploadPaths = (value) => {
  if (!value || typeof value !== 'string') return value || ''
  return value.replace(/(^|[\s"'(=])\/(?:api\/)?uploads\//g, '$1/api/uploads/')
}

const preferStaticWebp = (url) => {
  return url
    .replace(/(\/static\/banners\/(?:banner[1-3]|BingWallpaper \(3[34]\)))\.(?:png|jpe?g)([?#].*)?$/i, '$1.webp$2')
    .replace(/(\/static\/news\/news[1-9])\.(?:png|jpe?g)([?#].*)?$/i, '$1.webp$2')
}

export const normalizeImageUrl = (url) => {
  return preferStaticWebp(normalizeUploadPaths(url))
}

export const getUploadUrl = (res) => {
  return normalizeImageUrl(res?.url || res?.data?.url || '')
}
