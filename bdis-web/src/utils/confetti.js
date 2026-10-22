/**
 * 屏幕左右两侧角落礼花发射特效
 */
export function launchCelebrationConfetti() {
  const canvas = document.createElement('canvas')
  canvas.style.position = 'fixed'
  canvas.style.top = '0'
  canvas.style.left = '0'
  canvas.style.width = '100vw'
  canvas.style.height = '100vh'
  canvas.style.pointerEvents = 'none'
  canvas.style.zIndex = '999999'
  document.body.appendChild(canvas)

  const ctx = canvas.getContext('2d')
  let width = (canvas.width = window.innerWidth)
  let height = (canvas.height = window.innerHeight)

  const colors = [
    '#FF3B30', '#FF9500', '#FFCC00', '#34C759', '#007AFF', '#5856D6', '#AF52DE',
    '#FF2D55', '#E040FB', '#00E5FF', '#FFD700', '#00F2FE', '#4FACFE', '#FF6B6B'
  ]

  const particles = []

  function addBurst(originX, originY, angleDeg, spreadDeg, count) {
    for (let i = 0; i < count; i++) {
      const angle = (angleDeg + (Math.random() - 0.5) * spreadDeg) * (Math.PI / 180)
      const speed = Math.random() * 22 + 15
      particles.push({
        x: originX,
        y: originY,
        vx: Math.cos(angle) * speed,
        vy: -Math.abs(Math.sin(angle) * speed),
        size: Math.random() * 9 + 5,
        color: colors[Math.floor(Math.random() * colors.length)],
        rotation: Math.random() * 360,
        rotationSpeed: (Math.random() - 0.5) * 12,
        opacity: 1,
        gravity: 0.52,
        drag: 0.968,
        shape: Math.random() > 0.35 ? 'rect' : 'circle'
      })
    }
  }

  // 左下角发射
  addBurst(20, height, 65, 45, 80)
  // 右下角发射
  addBurst(width - 20, height, 115, 45, 80)

  // 350毫秒后追加第二波喷射，增强层次感
  setTimeout(() => {
    addBurst(40, height, 60, 40, 50)
    addBurst(width - 40, height, 120, 40, 50)
  }, 350)

  let animationFrame
  function animate() {
    ctx.clearRect(0, 0, width, height)
    let activeCount = 0

    for (let i = 0; i < particles.length; i++) {
      const p = particles[i]
      if (p.opacity <= 0) continue

      p.vx *= p.drag
      p.vy = p.vy * p.drag + p.gravity
      p.x += p.vx
      p.y += p.vy
      p.rotation += p.rotationSpeed
      p.opacity -= 0.0075

      if (p.opacity > 0 && p.y < height + 60) {
        activeCount++
        ctx.save()
        ctx.globalAlpha = Math.max(0, p.opacity)
        ctx.translate(p.x, p.y)
        ctx.rotate((p.rotation * Math.PI) / 180)
        ctx.fillStyle = p.color

        if (p.shape === 'rect') {
          ctx.fillRect(-p.size / 2, -p.size / 4, p.size, p.size / 2)
        } else {
          ctx.beginPath()
          ctx.arc(0, 0, p.size / 2.5, 0, Math.PI * 2)
          ctx.fill()
        }
        ctx.restore()
      }
    }

    if (activeCount > 0) {
      animationFrame = requestAnimationFrame(animate)
    } else {
      cancelAnimationFrame(animationFrame)
      if (canvas.parentNode) {
        canvas.parentNode.removeChild(canvas)
      }
    }
  }

  animationFrame = requestAnimationFrame(animate)
}
