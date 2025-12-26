// src/mocks/handlers.js
import { http, HttpResponse } from 'msw'; // 替换 rest 为 http + HttpResponse

export const handlers = [
  // 1. 实验室信息接口
  http.get('/api/lab/info', () => {
    return HttpResponse.json({
      code: 0,
      message: "ok",
      data: {
        name: "大数据智能工作室",
        nameEn: "Big Data Intelligence Studio · BDIS",
        introShort: "依托大数据管理与应用专业，由泰迪·重理工大数据智能工作室与重庆理工大学创新实验室联合组建，聚焦创新创业、系统开发与大模型训练。",
        address: "重庆理工大学 博园4号楼305",
        email: "studio@example.com",
        qqGroup: "888888888",
        logoUrl: "/static/logo/bdis-logo.png",
        picture: "@/assets/lab-main.png"
      }
    });
  }),

  // 2. 首页轮播Banner接口
  http.get('/api/home/banners', () => {
    console.log('%c[MSW] 命中 /api/home/banners handler', 'color: red; font-weight: bold;')
    return HttpResponse.json({
      code: 0,
      message: "ok",
      data: [
        {
          id: 1,
          title: "2025-2026年度社团招新启动",
          subtitle: "加入我们，共创精彩",
          description: "覆盖技术、设计、运营等多个方向，提供系统化培训与实践机会",
          buttonLink: "/recruit/2025",
          imageUrl: "/static/banners/banner1.png",
          order: 1,
          active: true
        },
        {
          id: 2,
          title: "省级创新创业大赛获奖",
          subtitle: "以赛促学，斩获佳绩",
          description: "团队凭借'智能校园助手'项目，荣获省级二等奖，彰显技术实力",
          buttonLink: "/project/award/2025",
          imageUrl: "/static/banners/banner2.jpg",
          order: 2,
          active: true
        },
        {
          id: 3,
          title: "校企合作项目落地",
          subtitle: "理论结合实践，赋能成长",
          description: "与某科技公司联合开发的'校园智慧管理系统'正式上线试运行",
          buttonLink: "/cooperation/tech2025",
          imageUrl: "/static/banners/banner3.jpg",
          order: 3,
          active: true
        }
      ]
    });
  }),

  // 3. 新闻列表接口（其他接口同理，保持数据不变，只改响应方式）
  http.get('/api/news', ({ request }) => {
    const url = new URL(request.url);
    const limit = Number(url.searchParams.get('limit') || 5);
    return HttpResponse.json({
      code: 0,
      message: "ok",
      data: [
        {
          id: 1,
          title: "2026春季成员招新说明",
          category: "recruit",
          summary: "面向两江校区同学，欢迎关注大数据智能工作室。",
          publishDate: "2026-02-20",
          link: "/news/1",
          coverThumbUrl: "/static/news/news1.jpg",
          order: 1
        },
        {
          id: 2,
          title: "2月组会：大模型应用与官网开发实践分享",
          category: "activity",
          summary: "分享近期大模型应用与官网开发的实践体会。",
          publishDate: "2026-02-15",
          link: "/news/2",
          coverThumbUrl: "/static/news/news2.jpg",
          order: 2
        },
        {
          id: 3,
          title: "实验室官网测试版上线说明",
          category: "notice",
          summary: "官网处于测试阶段，部分页面与功能仍在建设与优化。",
          publishDate: "2026-02-25",
          link: "/news/3",
          coverThumbUrl: "/static/news/news3.jpg",
          order: 3
        },
        {
          id: 4,
          title: "创新竞赛与组队板块上线公告",
          category: "contest",
          summary: "集中展示赛事信息，支持发布和查询组队。",
          publishDate: "2026-03-01",
          link: "/news/4",
          coverThumbUrl: "/static/news/news4.jpg",
          order: 4
        },
        {
          id: 5,
          title: "企业合作数据标注项目招募成员",
          category: "project_recruit",
          summary: "企业数据标注项目报名中，提供实践证明与补贴。",
          publishDate: "2026-03-05",
          link: "/news/5",
          coverThumbUrl: "/static/news/news5.jpg",
          order: 5
        }
      ].slice(0, limit)
    });
  }),

  // 4. 项目进展接口（科研/项目进展模块）
  http.get('/api/projects/progress', ({ request }) => {
    const url = new URL(request.url)
    const limit = Number(url.searchParams.get('limit') || 5)
    const status = url.searchParams.get('status') // recruiting / ongoing / paused / done

    const all = [
      {
        id: "p-001",
        name: "系统开发：BDIS 官网.V1",
        tagline: "BDIS官网开发，围绕实验室品牌展示与招新传播，完成V1版本的初期开发与联调上线",
        status: "ongoing",
        progress: 82,
        recruitRoles: ["运维"],
        members: ["丁祎", "郭敦权", "杨思琦", "朱玉洁", "李林芝"],
        updatedAt: "2025-11-15",
        coverUrl: "/static/news/news1.jpg",
        link: "/projects/p-001",
        order: 1
      },
      {
        id: "p-002",
        name: "科研申报：心理健康预警分析",
        tagline: "融合多源校园行为数据构建特征体系，完成预警模型基线训练与可复现实验流程，支撑系统原型迭代",
        status: "ongoing",
        progress: 10,
        recruitRoles: ["无"],
        members: ["丁祎", "杨思琦", "朱玉洁", "李林芝"],
        updatedAt: "2026-11-01",
        coverUrl: "/static/news/news2.jpg",
        link: "/projects/p-002",
        order: 2
      },
      {
        id: "p-003",
        name: "企业合作：数据标注与质检",
        tagline: "建立小批量数据标注与抽检质控流程，沉淀统一规范与交付节奏，为后续规模化合作提供可复制范式",
        status: "recruiting",
        progress: 15,
        recruitRoles: ["数据标注", "质检", "项目助理"],
        members: ["招募中"],
        updatedAt: "2025-10-28",
        coverUrl: "/static/news/news3.jpg",
        link: "/news/5",
        order: 3
      },
    ]

    const filtered = status ? all.filter(p => p.status === status) : all
    const sorted = filtered.sort((a, b) => (a.order ?? 0) - (b.order ?? 0))

    return HttpResponse.json({
      code: 0,
      message: "ok",
      data: sorted.slice(0, limit)
    })
  }),

  // 5.创新竞赛接口
  http.get('/api/platform/forum', ({ request }) => {
    const url = new URL(request.url)
    const limit = Number(url.searchParams.get('limit') || 10)

    const role = url.searchParams.get('role')         // 如：建模 / 前端 / PPT/路演
    const type = url.searchParams.get('type')         // wechat / qq / email / link
    const deadlineBefore = url.searchParams.get('deadline_before') // YYYY-MM-DD
    const q = (url.searchParams.get('q') || '').trim() // 搜索关键词：title/summary/roles/members

    const all = [
      {
        id: "f-001",
        title: "挑战杯校赛组队｜缺建模 / PPT / 路演",
        summary: "题目偏“AI+教育”方向，计划本周内定题与分工，目标冲省赛。",
        recruitRoles: ["建模", "PPT/路演"],
        currentSize: 3,
        targetSize: 5,
        membersPreview: ["A", "B", "C"],
        type: "wechat",
        value: "bdis_teamup_01（备注：挑战杯）",
        deadline: "2026-01-05",
        link: "/forum/f-001",
        coverUrl: "/static/news/news1.jpg",
        order: 1
      },
      {
        id: "f-002",
        title: "互联网+ 项目组队｜缺前端（Vue）/ 后端（SpringBoot）",
        summary: "已有产品方向与需求文档，想快速搭出可演示原型，欢迎能落地的同学。",
        recruitRoles: ["前端", "后端"],
        currentSize: 2,
        targetSize: 4,
        membersPreview: ["A", "B"],
        type: "qq",
        value: "789123456（进群备注：互联网+）",
        deadline: "2026-01-12",
        link: "/forum/f-002",
        coverUrl: "/static/news/news2.jpg",
        order: 2
      },
      {
        id: "f-003",
        title: "数学建模校赛｜缺写作 / 数据清洗",
        summary: "希望有人负责论文写作与图表排版，数据部分已有人员负责，目标是拿校一冲省赛。",
        recruitRoles: ["写作", "数据清洗"],
        currentSize: 2,
        targetSize: 3,
        membersPreview: ["A", "B"],
        type: "email",
        value: "teamup@bdis.example.edu（主题写：建模组队）",
        deadline: "2026-01-03",
        link: "/forum/f-003",
        coverUrl: "/static/news/news3.jpg",
        order: 3
      },
      {
        id: "f-004",
        title: "蓝桥杯｜缺算法基础扎实同学（C/C++/Java）",
        summary: "每周两次刷题复盘，主要冲省一，欢迎一起坚持训练。",
        recruitRoles: ["算法"],
        currentSize: 1,
        targetSize: 2,
        membersPreview: ["A"],
        type: "wechat",
        value: "bdis_algo_02（备注：蓝桥）",
        deadline: "2026-02-20",
        link: "/forum/f-004",
        coverUrl: "/static/news/news4.jpg",
        order: 4
      },
      {
        id: "f-005",
        title: "大创申报｜缺调研 / 材料撰写 / PPT",
        summary: "方向是校园数据应用，想找认真靠谱同学一起把申报书写扎实。",
        recruitRoles: ["调研", "材料撰写", "PPT/路演"],
        currentSize: 2,
        targetSize: 5,
        membersPreview: ["A", "B"],
        type: "link",
        value: "https://example.com/join-team（填写表单后拉群）",
        deadline: "2026-01-18",
        link: "/forum/f-005",
        coverUrl: "/static/news/news5.jpg",
        order: 5
      }
    ]
    // --- 过滤 ---
    let filtered = [...all]

    if (role) {
      filtered = filtered.filter(p => (p.recruitRoles || []).some(r => String(r).includes(role)))
    }

    if (type) {
      filtered = filtered.filter(p => p.type === type)
    }

    if (deadlineBefore) {
      filtered = filtered.filter(p => (p.deadline || '') <= deadlineBefore)
    }

    if (q) {
      filtered = filtered.filter(p => {
        const text = `${p.title || ''} ${p.summary || ''} ${(p.recruitRoles || []).join(' ')} ${(p.membersPreview || []).join(' ')}`
        return text.includes(q)
      })
    }


    // 排序
    const sorted = filtered.sort((a, b) => (a.order ?? 0) - (b.order ?? 0))

    return HttpResponse.json({
      code: 0,
      message: "ok",
      data: sorted.slice(0, limit)
    })
  }),

  // 6. 实验室团队成员接口
    http.get('/api/lab/team', () => {
    return HttpResponse.json({
      code: 0,
      message: "ok",
      data: [
        {
          id: 1,
          name: "丁祎",
          role: "核心成员",
          description: "介绍",
          avatarUrl: ""
        },
        {
          id: 2,
          name: "郭墩权",
          role: "核心成员",
          description: "介绍",
          avatarUrl: ""
        },
        {
          id: 3,
          name: "杨思琦",
          role: "核心成员",
          description: "介绍",
          avatarUrl: ""
        },
        {
          id: 4,
          name: "朱玉洁",
          role: "核心成员",
          description: "介绍",
          avatarUrl: ""
        },
        {
          id: 5,
          name: "李林芝",
          role: "核心成员",
          description: "介绍",
          avatarUrl: ""
        }
      ]
    });
  }),
  // 7. 学习资料接口
   http.get('/api/resources', ({ request }) => {
    const url = new URL(request.url)
    const page = Number(url.searchParams.get('page') || 1)
    const limit = Number(url.searchParams.get('limit') || 12)
    const category = url.searchParams.get('category') || 'all'
    const keyword = (url.searchParams.get('keyword') || '').trim().toLowerCase()

    const allResources = [
      {
        id: 1,
        title: 'Vue 3 官方文档中文版',
        description: 'Vue 3 框架的完整中文文档，包含基础语法、组件、路由、状态管理等内容',
        category: 'frontend',
        link: 'https://cn.vuejs.org'
      },
      {
        id: 2,
        title: 'React 18 新特性详解',
        description: '深入解析 React 18 的新特性，包括并发渲染、Suspense 等核心概念',
        category: 'frontend',
        link: 'https://react.dev'
      },
      {
        id: 3,
        title: 'Spring Boot 实战教程',
        description: '从零开始学习 Spring Boot，包含项目搭建、配置、数据库集成等实战内容',
        category: 'backend',
        link: 'https://www.bilibili.com/video/BV1Lq4y1J77x/?spm_id_from=333.337.search-card.all.click'
      },
      {
        id: 4,
        title: 'MySQL 数据库从入门到精通',
        description: 'SQL基础、事务、索引、SQL优化、锁、InnoDB存储引擎、日志、主从复制、分库分表、读写分离',
        category: 'database',
        link: 'https://www.bilibili.com/video/BV1Kr4y1i7ru/'
      },
      {
        id: 5,
        title: 'Python 机器学习入门视频',
        description: '使用 Python 进行机器学习入门，包含数据处理、模型训练、评估等',
        category: 'ai',
        link: 'https://www.bilibili.com/video/BV17W42197Nm/?spm_id_from=333.337.search-card.all.click'
      },
      {
        id: 6,
        title: 'Git 入门教程',
        description: 'Git 入门教程',
        category: 'tools',
        link: 'https://www.runoob.com/git/git-tutorial.html'
      },
      {
        id: 7,
        title: 'TypeScript 进阶指南',
        description: 'TypeScript 高级类型、泛型、装饰器等进阶知识',
        category: 'frontend',
        link: 'https://www.typescriptlang.org'
      },
      {
        id: 8,
        title: 'Node.js 后端开发实战',
        description: '使用 Node.js 开发 RESTful API，包含 Express、中间件、认证等内容',
        category: 'backend',
        link: 'https://www.bilibili.com/video/BV1a34y167AZ/?spm_id_from=333.337.search-card.all.click'
      },
      {
        id: 9,
        title: 'MongoDB 快速入门',
        description: 'NoSQL 数据库 MongoDB 的基础操作、数据建模和查询优化',
        category: 'database',
        link: 'https://www.bilibili.com/video/BV1bJ411x7mq/?spm_id_from=333.337.search-card.all.click'
      },

      {
        id: 10,
        title: 'Docker 容器化部署指南',
        description: 'Docker 容器技术、镜像构建、容器编排等 DevOps 实践',
        category: 'tools',
        link: 'https://www.docker.com'
      },
      {
        id: 11,
        title: 'Webpack 5 配置详解',
        description: 'Webpack 5 模块打包、代码分割、优化配置等前端工程化内容',
        category: 'frontend',
        link: 'https://www.bilibili.com/video/BV14T4y1z7sw/?spm_id_from=333.337.search-card.all.click'
      },
      {
        id: 12,
        title: 'Redis 数据库实战',
        description: 'Redis 数据结构、持久化、集群、缓存策略等高性能缓存实践',
        category: 'database',
        link: 'https://www.bilibili.com/video/BV1cr4y1671t/'
      },
      {
        id: 13,
        title: 'PyTorch 神经网络编程',
        description: '使用 PyTorch 进行神经网络编程，从基础到高级应用',
        category: 'ai',
        link: 'https://www.bilibili.com/video/BV1c5yrBcEEX/?spm_id_from=333.337.search-card.all.click'
      },
      {
        id: 14,
        title: 'Postman API 测试工具使用',
        description: 'Postman 工具的使用方法，包含 API 测试、Mock 服务、自动化测试',
        category: 'tools',
        link: 'https://www.postman.com'
      },

      {
        id: 15,
        title: 'CSS Grid 布局完全指南',
        description: '现代 CSS Grid 布局系统，包含网格创建、对齐、响应式设计',
        category: 'frontend',
        link: 'https://www.cnblogs.com/abc-x/p/10258695.html'
      },

      {
        id: 16,
        title: '自然语言处理入门',
        description: 'NLP 基础概念、文本预处理、词向量、文本分类等',
        category: 'ai',
        link: 'https://www.bilibili.com/video/BV1GByoBfE73/?spm_id_from=333.337.search-card.all.click'
      },
      {
        id: 17,
        title: 'VS Code 开发效率提升技巧',
        description: 'VS Code 快捷键、插件推荐、调试技巧等提升开发效率的方法',
        category: 'tools',
        link: 'https://code.visualstudio.com'
      }
    ]

    // 过滤分类
    let filtered = category === 'all'
      ? allResources
      : allResources.filter(r => r.category === category)

    // 搜索关键词
    if (keyword) {
      filtered = filtered.filter(r =>
        r.title.toLowerCase().includes(keyword) ||
        r.description.toLowerCase().includes(keyword)
      )
    }

    // 排序（按 id 倒序）
    const sorted = filtered.sort((a, b) => b.id - a.id)

    // 分页
    const start = (page - 1) * limit
    const end = start + limit
    const paginatedData = sorted.slice(start, end)

    return HttpResponse.json({
      code: 0,
      message: "ok",
      data: {
        list: paginatedData,
        total: sorted.length,
        page: page,
        limit: limit
      }
    })
  }),
];