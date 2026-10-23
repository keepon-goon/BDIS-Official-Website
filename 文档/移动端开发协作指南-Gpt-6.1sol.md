# BDIS 移动端开发协作指南

适用对象：了解 Spring Boot 基础、没有前端开发经验的两名成员。本文把成员暂称为 A、B，把原项目负责人称为负责人。

编写日期：2026-10-14。依据原项目提交 acc2cb503d74bf511c449e353662dfc27d1f5caf 及当前工作区源码，配套阅读《接口文档-基于当前源码.md》。

**本次交付是两份文档；移动端项目、GitHub 仓库和 PR 尚未创建。** 下文目录、页面和流程是待执行方案，不能当作已经完成的产物。原来的 Word 文档与 mock 只供参考，接口以实际后端源码和真实联调为准。

## 1. 最终要做成什么

制作一个面向手机浏览器的 BDIS 官网，复用现有 Spring Boot 接口，用单独的 GitHub **公开仓库**合作开发。

第一版包括首页、新闻与公告、项目与竞赛、学习资料、关于我们、加入说明、搜索，以及一个可访问的“成长路线”预留页。以内容浏览为主，不要求访客登录。

现有管理后台继续使用原桌面端。报名表下载可以作为加入页面内容，但必须先确认文件来源和地址；在线收集简历作为后续单独 Issue，满足接口文档中的后端前置条件后再接入。没有评论、点赞、在线组队、报名查询等接口，不设计假成功按钮。

做完后，每个人都要能：

1. 说明一个页面如何从点击入口、切换路由、请求接口，到把 JSON 显示出来。
2. 看懂并修改简单模板、样式、条件判断和列表循环；独立调整至少一个小功能。
3. 在浏览器 Network 中区分连接失败、HTTP 错误、业务失败和数据为空。
4. 独立走完 Issue、分支、提交、PR、评审、修改、合并和同步代码。
5. 解释自己提交的一段 AI 辅助代码，并举出一次自己发现并纠正 AI 错误的例子。

不要求背熟整个 Vue 体系。遇到本阶段用不到的知识先不学，先把一个真实闭环做通。

建议以 15～20 个学习日安排，每人每天约 1.5～3 小时；这只是排期参考。按实际验收推进，不以天数代替完成质量。

## 2. 技术选择与最小知识

### 2.1 统一技术

| 项目 | 本次选择 | 理由 |
| --- | --- | --- |
| 页面框架 | Vue 3 + JavaScript | 原桌面端已用 Vue 3，可以对照请求和组件 |
| 工程工具 | Vite，使用官方 create-vue 建项目 | 启动、开发、构建流程明确 |
| 路由 | Vue Router，首版 hash 路由 | 页面可独立访问，静态托管时较易处理刷新 |
| 请求 | 浏览器 fetch | 先认识 HTTP、JSON、异步请求，不同时学习两套请求工具 |
| 样式 | 普通 CSS + Flex/Grid 的基本用法 | 足以完成内容型移动网页 |
| 状态 | 组件内 ref、computed，少量 props | 当前规模先不引入全局状态库 |
| 协作 | Git + GitHub Issues + PR + Actions | 每个变更有目的、评审和构建记录 |
| 校验 | 构建检查 + 有记录的人工验收 | 构建负责检查工程能否生成产物，页面和接口另外核验 |

首版先不用 TypeScript、Pinia、大型组件库、富文本编辑器、自动生成 API SDK、复杂动画或微前端。后续确有问题再单独引入。桌面端使用 Element Plus，不代表移动端必须搬来整套管理组件。

**环境：** 两人使用相同的 Node.js 22 系列补丁版本，至少 22.18.0，并在 README 记录实际版本。当前 Vue 官方建项目文档给出的支持范围包含该版本；原桌面 Vite 7 的最低要求与最新建项目工具可能不同，不直接套用旧教程。[Vue 官方快速开始](https://vuejs.org/guide/quick-start.html)、[Vite 环境要求](https://vite.dev/guide/)。

### 2.2 用后端经验理解前端

| 前端名词 | 先这样理解 | 本项目在哪里用 |
| --- | --- | --- |
| HTML | 页面结构：标题、链接、列表、输入框 | 内容卡片、搜索框 |
| CSS | 控制间距、颜色、排列和屏幕适配 | 单列布局、底部导航 |
| JavaScript | 浏览器执行的业务与交互逻辑 | 点击、请求、处理响应 |
| .vue 文件 | 一个组件的模板、逻辑、样式放在一起 | 一个页面或通用卡片 |
| component / 组件 | 可组合的页面单元 | 顶部栏、空状态 |
| router / 路由 | 决定一个前端 URL 显示哪个组件 | /news/:id |
| props | 父组件传给子组件的数据 | 把新闻项传给卡片 |
| ref | 数据变化后能带动页面更新的变量 | 新闻列表、加载状态 |
| computed | 根据已有数据计算展示值 | 当前列表数量 |
| v-if / v-else | 根据条件决定显示哪一块 | 加载、失败、空数据 |
| v-for / key | 循环渲染列表，并标识每一项 | 新闻卡片 |
| v-model | 输入框与变量同步 | 搜索关键词 |
| @click | 监听点击事件 | 重试、提交搜索 |
| async / await | 等待异步操作完成 | 等待后端响应 |
| fetch / response.json() | 发 HTTP 请求 / 把响应正文解析成 JS 对象 | 获取新闻列表 |
| npm / package.json | 管理依赖和项目命令，可类比 Maven 的部分职责 | 安装、启动、构建 |
| package-lock.json | 记录依赖解析结果，使两人和 CI 安装一致 | 必须提交到 Git |

前端路由不是后端 Controller。浏览器内部进入 `/news/1`，仍然要另外请求 `/api/news/1`。首版使用 hash 路由时，浏览器地址类似 `http://localhost:5173/#/news/1`；下文路由表写的是 hash 后面的内部路径。

## 3. 开始编码前，先把协作条件准备好

### 3.1 由负责人提供的后端联调条件

原目录只有 `bdis-website/sql/init_admin.sql` 这一份可见 SQL，不是完整业务数据库恢复包。不要让两名成员凭实体猜完整数据库。

负责人至少提供：

| 内容 | 要求 |
| --- | --- |
| 测试服务 | 能访问的后端地址，以及使用时段；也可以由负责人协助配置完整本地测试环境 |
| 测试数据 | 新闻、公告、项目、竞赛、资料、团队与实验室信息各有代表性数据 |
| 测试 ID | 每种详情至少一个存在的 ID，以及负责人确认不存在的 ID |
| 图片来源 | 至少一张后端上传图、需要沿用的前端静态图清单或替代地址 |
| 联系与下载内容 | 确认邮箱、地点、群号、报名表是否适合公开及是否最新 |
| 内容边界 | 新闻正文是否可信、是否已经清洗，外链允许使用哪些协议和来源 |
| 环境记录 | 后端提交号、联调日期；不在公开仓库中填写密码、令牌和真实报名信息 |

没有后端时，可以在本地使用与接口契约一致的虚构数据练习，但必须标记“模拟数据，未联调”。真实接口未通的阶段不能验收为完成。

先用只读 GET 核对 `/api/news?limit=1`、`/api/lab/info`。不要通过提交真实简历来测试是否通网。

### 3.2 创建公开仓库

建议仓库名 `bdis-mobile`，只收移动端前端代码和相关文档，放在 `D:/java/bdis-mobile` 等独立目录，**不要嵌套在原仓库里建第二个 .git**。

由 A 在 GitHub 创建，B 全程参与：

1. 新建仓库，选择 **Public**，勾选创建 README，默认分支设为 `main`。README 写用途、负责人、开发者和目前状态。
2. 在仓库设置中邀请 B 为协作者，B 接受邀请，确认具有提交功能分支和评审的权限。
3. 两人分别 clone 同一个仓库。这里使用同仓分支协作，不额外要求 fork。
4. 初始化 README 是仓库启动步骤；从脚手架开始，所有后续变更都走 PR。

公开仓库不复制原后端整份目录、旧 .git 历史、数据库备份、上传文件、个人简历或开发者配置。复用原前端图片、字体、报名表前由负责人确认来源和公开范围；不因“本地已有”就自动上传。

建议新仓库中的 .gitignore 至少包含：

```gitignore
node_modules/
dist/
.env
.env.*
!.env.example
*.log
.idea/
.vscode/
uploads/
```

`.env.example` 只保留非敏感配置示例。以 `VITE_` 开头的前端环境变量会进入浏览器构建，不能放数据库口令、JWT 签名密钥或邮件密码。[Vite 环境变量说明](https://vite.dev/guide/env-and-mode.html)。

### 3.3 两人首次 clone

以下命令在新的开发目录执行，把 URL 中的 owner 换为实际仓库拥有者：

```powershell
Set-Location 'D:/java'
git clone 'https://github.com/owner/bdis-mobile.git'
Set-Location 'D:/java/bdis-mobile'
git status
git remote -v
git switch -c codex/setup-mobile
```

`clone` 下载项目和提交历史；`origin` 是这个远程仓库的简称；`switch -c` 创建并切换到功能分支。命令块中的 owner 是占位符，不是已创建账号或仓库。

A 在该分支中初始化工程：

```powershell
npm create vue@latest .
```

当前目录因已有 README 可能被判定非空。**不要选择删除整个目录或覆盖已有文件。** 如果工具不能保留现有 README，就在没有 Git 仓库的临时空目录创建，再把生成的源文件合入当前分支，保留根目录 .git 和 README，并逐个检查冲突。

创建选项：JavaScript；Vue Router 选“是”；TypeScript、JSX、Pinia、单元测试、端到端测试、额外开发工具等先选“否”。ESLint/Prettier 本阶段也先不加，避免把工具安装当作主任务。生成后让 AI 解释目录，并把路由改为 hash 模式，清理演示页。

```powershell
npm install
npm run dev
npm run build
```

第一次安装生成并提交 package-lock.json。B 和 CI 之后使用 `npm ci` 按锁文件安装，避免各自手工改依赖版本。不要把 node_modules 或 dist 提交上去。

如果 npm 在 Windows PowerShell 中被执行策略拦截，可以使用 `npm.cmd` 运行同一命令，不随意修改整台电脑的策略。

### 3.4 建立主分支保护

公开仓库的 GitHub Free 可以使用受保护分支。针对精确的 `main` 创建保护规则，至少要求 PR、1 名其他成员批准、修改后旧批准失效、讨论解决，并让规则同样约束管理员，不允许强推和删除。[GitHub 管理受保护分支](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches)、[保护设置说明](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)。

操作顺序：

1. 仓库创建且 B 接受邀请后，先启用 PR 和一人评审要求。
2. A 的工程初始化 PR 中加入下一节 CI；B 检查并评审。
3. 待该 PR 的 build 首次运行成功，再在保护设置里选择该检查作为必须通过的状态检查。
4. 确认保护规则生效，再合并工程初始化 PR。后续不得为了合并失败 PR 而关闭保护。

只允许 Squash and merge 作为本项目约定的合并方式。它会把本次 PR 的多个修正提交合成 main 中的一条清楚记录。合并者确认检查和批准均有效，不能由作者自己口头确认代替另一人的 Review。

### 3.5 最小 CI：自动检查能否构建

在移动端新仓库的 `.github/workflows/build.yml` 中建立以下工作流，**此处只是文档中的待创建内容，本次没有生成该配置文件**：

```yaml
name: 移动端检查

on:
  pull_request:
    branches: [main]
  push:
    branches: [main]

permissions:
  contents: read

jobs:
  build:
    name: build
    runs-on: ubuntu-latest
    timeout-minutes: 10
    steps:
      - uses: actions/checkout@v6
      - uses: actions/setup-node@v7
        with:
          node-version: '22'
          cache: npm
      - run: npm ci
      - run: npm run build
```

`checkout` 取得代码，`setup-node` 准备 Node，`npm ci` 安装锁文件依赖，`build` 生成生产构建。Action 版本按本次核对的官方文档示例选取，实际建立时可再核对官方发布情况。[GitHub Node.js 构建说明](https://docs.github.com/en/actions/tutorials/build-and-test-code/nodejs)。

工作流放在新仓库根目录，不在原 `bdis-web` 中运行；因此没有 working-directory 配置。不要添加目录过滤导致部分 PR 不产生必需检查，也不配置自动部署或把后端密码放进 CI。

**绿色 build 只说明构建命令成功，不证明页面好用、接口联通或手机显示正常。** 这些必须在 PR 中附真实验收证据。

## 4. 页面、目录和两人分工

### 4.1 第一版页面契约

底部导航先做“首页、动态、项目、资料、更多”。“更多”入口展示关于、加入、搜索和成长路线。项目区提供项目与竞赛的清楚切换入口。

| 路由内部路径 | 页面与核心内容 | 实际接口 | 主要实现者 / 评审者 |
| --- | --- | --- | --- |
| / | 简介、轮播或单图、少量动态、快捷入口 | /api/lab/info、/api/banner、/api/news?limit=3 | A / B |
| /updates | 新闻/公告切换；各自分类和列表 | /api/news、/api/announcements | A 负责新闻，B 负责公告，互审 |
| /news/:id | 新闻标题、日期、正文或空状态 | /api/news/{id} | A / B |
| /announcements/:id | 公告详情 | /api/announcements/{id} | B / A |
| /projects | 项目卡片、状态、进度 | /api/projects/progress | B / A |
| /projects/:id | 项目字段、成员与角色 | /api/projects/{id} | B / A |
| /competitions | 创新竞赛/招募信息 | /api/platform/forum?limit=20 | A / B |
| /competitions/:id | 人数、角色、截止时间等 | /api/platform/forum/{id} | A / B |
| /resources | 资料分类、关键词、分页、外链 | /api/resources | B / A |
| /about | 实验室、联系信息、团队 | /api/lab/info、/api/lab/team | A / B |
| /join | 加入说明、联系信息、经确认的报名表下载 | 联系信息用 /api/lab/info；下载是静态资源 | A / B |
| /search | 关键词、结果类型、分页与跳转 | /api/search | B / A |
| /growth | 成长路线预留页、状态和返回入口 | 首版不请求不存在的接口 | B 搭骨架，A 验证扩展 |
| 未匹配路径 | 404 和返回首页 | 无 | B / A |

这是一份新移动端路由设计，不是后端已经存在的路径清单。竞赛 limit=20 是第一版的展示上限，应明确写“最多展示 20 条”；现有接口没有 total 和翻页，不能加一个失效的“下一页”。

首页不追求复杂轮播，第一张图片和入口可用即可。新闻列表无服务端分页，可先请求完整列表再做明确标注的前端分页，或直接列表展示；不能把传 page 当作后端分页。

### 4.2 推荐目录

```text
bdis-mobile/
  .github/
    workflows/build.yml
    pull_request_template.md
  docs/
    接口文档.md
    开发协作指南.md
    开发进度.md
    联调记录.md
    learning-a.md
    learning-b.md
  public/
  src/
    api/
      http.js
      news.js
      announcements.js
      projects.js
      competitions.js
      resources.js
      lab.js
      search.js
    components/
      PageHeader.vue
      BottomNav.vue
      RequestState.vue
    layouts/
      MobileLayout.vue
    router/
      index.js
    views/
      HomeView.vue
      UpdatesView.vue
      NewsDetailView.vue
      AnnouncementDetailView.vue
      ProjectListView.vue
      ProjectDetailView.vue
      CompetitionListView.vue
      CompetitionDetailView.vue
      ResourceListView.vue
      AboutView.vue
      JoinView.vue
      SearchView.vue
      GrowthView.vue
      NotFoundView.vue
    App.vue
    main.js
    style.css
  .env.example
  .gitignore
  package.json
  package-lock.json
  README.md
```

按任务逐步增加文件，不在第一天让 AI 生成所有页面。新闻和公告的列表可以是 UpdatesView 下的小组件，文件名由对应 Issue 约定。只有被两处实际复用时才抽共享组件，不为了“架构完整”先造大量目录。

API 文件只负责请求和返回数据，页面负责加载、错误、空数据和展示。http.js 只做 HTTP 与业务码处理，先不做复杂重试、令牌刷新或缓存。

将配套接口文档复制为 `docs/接口文档.md`，本指南复制为 `docs/开发协作指南.md`，通过 PR 纳入新仓库。接口文档中的后端路径仍指向原项目，不表示移动端仓库里有后端。

### 4.3 公共文件的协作规则

`router/index.js`、`style.css`、`package.json`、锁文件、导航、Vite 配置是容易冲突的文件。

- 修改前在 Issue/PR 中说明涉及哪些公共文件；两人错开处理同一个公共文件。
- 页面文件可以并行编写；需要接入路由时先同步最新 main。
- 不顺手格式化对方整页代码，不在页面 PR 里升级依赖或重构全部请求方式。
- 某人开发受阻时另一人帮助定位，仍由原作者理解并提交修正，不把所有接口工作固定交给同一个人。

## 5. 从启动到真实手机联调

### 5.1 开发代理

所有 API 请求先统一用相对路径 `/api/...`。由 B 在网络闭环任务中为新项目配置 Vite 的 `server.proxy`：

| 前缀 | 代理目标 | 是否移除前缀 |
| --- | --- | --- |
| /api | 本机后端默认 http://localhost:8081，或负责人提供的测试地址 | 否，后端真实路径包含 /api |
| /uploads | 与上面相同 | 否 |

让 AI 在现有 vite.config 中补这两条规则，并逐行解释 target 和 changeOrigin。两人共用测试后端时，约定同一目标；若每台电脑必须不同，使用被忽略的 `.env.local` 保存一个代理目标，由 Vite 配置读取，`.env.example` 给出不含口令的示例。

不要把整个网址重复拼两次，也不要既用 baseURL=/api 又请求 /api/news。后端若由另一台电脑提供，代理 target 不能继续写成自己机器的 localhost。

Vite 代理和 host 属于开发服务器配置，build 后不会自动转移到生产服务器。[Vite 开发服务器说明](https://vite.dev/config/server-options.html)。

### 5.2 浏览器和手机

先在电脑浏览器检查 Network，再进行同一局域网真机访问：

```powershell
npm run dev -- --host 0.0.0.0
```

手机连接同一网络，访问终端显示的 Network 地址，例如 `http://192.168.x.x:5173`，端口以终端实际输出为准。电脑上可以使用 localhost，手机上 localhost 指手机自己。

手机请求相对路径后，由电脑上的 Vite 代理转到后端；因此后端和手机不必直接用同一个 localhost。若不能访问，检查设备网络、Vite 监听地址、实际端口和允许本地开发的防火墙规则，不关闭整台电脑的防火墙。

### 5.3 图片和链接

检查三种来源：

1. `/api/uploads/xxx.png`：来自后端，走 /api 代理。
2. `/uploads/...`：后端静态资源映射，走 /uploads 代理；不要一律替换成只支持单段文件的 /api/uploads。
3. `/static/...` 或原前端 assets：需要移动端自己拥有对应资源，或改用负责人确认的地址。复制后检查文件大小和实际加载情况。

外链只接受经确认的 http/https，联系邮箱单独使用 mailto。站内链接按自己路由跳转，搜索和轮播中的旧桌面路径先做明确映射。禁止把 `javascript:` 等任意内容直接交给链接组件。

## 6. 第一个必须亲自跑通的闭环

目标只有一个：**打开一个页面，读取真实新闻列表，显示加载、错误、空数据和正常列表。**

本节代码供成员理解和手写练习，不代表本次已在仓库中实现。分 4 步完成：

1. 手写一个标题、一条静态新闻和一个按钮，改变一处 CSS，确认热更新。
2. 理解下面的请求函数，放到 api/http.js；在 Network 中找到真实 GET 请求。
3. 建立 news.js 并在页面中调用，确认 result.data 是数组。
4. 关闭测试后端或临时使用约定的失败地址，观察错误状态，再恢复；不要修改生产配置模拟失败。

api/http.js 的最小读取函数：

```javascript
export async function getData(path) {
  const response = await fetch(path)
  if (!response.ok) {
    throw new Error('请求失败：HTTP ' + response.status)
  }
  const result = await response.json()
  if (result.code !== 0) {
    throw new Error(result.message || '业务处理失败')
  }
  return result.data
}
```

`response.ok` 检查 HTTP 200～299；`response.json()` 把响应正文解析成对象；`throw` 把失败交给页面的 catch。函数返回的已经是包装里的 data，所以页面不用再写一遍 .data。fetch 遇到 HTTP 404/500 不会自动当作网络异常，需要主动检查。

api/news.js：

```javascript
import { getData } from './http.js'

export function getNewsList() {
  return getData('/api/news?limit=5')
}
```

这是第一份练习请求，只读取 5 条。正式列表是否取全部由 Issue 决定，不能据此显示“全部只有 5 条”。分类筛选时用 URLSearchParams 编码参数，不手工拼中文搜索值。

练习页面的核心代码：

```vue
<script setup>
import { onMounted, ref } from 'vue'
import { getNewsList } from '../api/news.js'

const items = ref([])
const loading = ref(false)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    items.value = await getNewsList()
  } catch (e) {
    error.value = e.message || '加载失败，请重试'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <main>
    <h1>工作室动态</h1>
    <p v-if="loading">正在加载</p>
    <div v-else-if="error">
      <p>{{ error }}</p>
      <button type="button" @click="load">重新加载</button>
    </div>
    <p v-else-if="items.length === 0">暂无动态</p>
    <ul v-else>
      <li v-for="item in items" :key="item.id">
        {{ item.title }}
      </li>
    </ul>
  </main>
</template>
```

说明：

- 在脚本中用 `items.value` 访问 ref 的内容，模板中 Vue 会自动展开，所以写 `items.length`。
- `onMounted(load)` 表示组件初次显示后执行加载；不要写成在模板里反复调用请求函数。
- `try/catch/finally` 对应成功处理、失败提示和无论成功失败都结束加载。
- `key` 使用记录的稳定 ID，不用列表位置当作业务 ID。
- 这段只演示列表，不包含详情路由和最终样式。完成后 A、B 各用自己的话解释请求链，再进入其他页面。

详情页还需处理路由参数变化：同一个组件从 ID 1 切到 ID 2 时可能被复用，不能只在首次 onMounted 中加载。请 AI 解释一个监听 route.params.id 的简单实现，并测试连续打开两个详情。搜索连续请求可能先后乱序，也要确保页面最终展示最近一次关键词的结果。

## 7. 每次只推进一个验收单位

**下表每一行就是最小验收单位，通常对应一个 Issue 和一个 PR。** 行内步骤是该单位的操作顺序。验收通过后，先更新 `docs/开发进度.md`，记录 PR、证据和复述结果，再开始下一行；可以在该 PR 合并前把最终验收记录补入同一 PR。

状态使用“未开始 / 进行中 / 待评审 / 已验收 / 阻塞”。本表初始均为未开始；不要因为读完指南就打勾。

| 单位 | 作者 / 评审 | 依赖 | 本轮只做的 3～4 步 | 必须提供的验收证据 |
| --- | --- | --- | --- | --- |
| T00 协作准备 | A 主办，B 确认 | 无 | 创建公开仓库；邀请 B；确认保护规则与后端条件；建立任务清单 | 两人可访问、main 受保护、测试地址和数据来源已确认 |
| T01 工程初始化 | A / B | T00 | 建 Vue 工程；确认启动构建；加入 CI 与忽略规则；提出首个 PR | B 在自己电脑 npm ci/build 成功；CI 首次成功并设为必需检查 |
| T02 页面骨架 | B / A | T01 | 建移动布局；底部导航；空/错状态组件；404 | 320/390 宽度无横向溢出，导航可用 |
| T03 新闻闭环 | A / B | T02 | 配合 B 确认代理；实现公共读取函数和新闻列表；做新闻详情；检查四种状态 | 真实新闻 GET、列表→详情→返回、null 详情和失败重试 |
| T04 公告闭环 | B / A | T03 | 接公告分页；实现分类；独立公告详情；与新闻切换 | 正确使用 data.list/total，新闻公告相同数字 ID 不串内容 |
| T05 首页 | A / B | T03 | 接简介；接轮播/首图；接少量动态；串联入口 | 真实图片和接口数据，任何单区失败不让整页空白 |
| T06 项目闭环 | B / A | T03 | 项目列表；详情；角色成员与进度；空值展示 | String ID 未转数字，两个不同详情可连续打开 |
| T07 竞赛闭环 | A / B | T06 | 列表；详情；人数/截止时间展示；与项目区连接 | limit 为正数，无假分页，无“点击即入队” |
| T08 资料闭环 | B / A | T04 | 分类；关键词；分页与状态重置；外链 | 分类变化回第一页，组合筛选异常如实记 Issue，链接可访问 |
| T09 关于与加入 | A / B | T05 | 实验室信息；团队；加入说明；下载地址或暂不展示下载入口 | 联系数据来源可追溯，文件真实可下载，成员空字段不崩溃 |
| T10 搜索闭环 | B / A | T04、T08 | 编码关键词；分页；按 type 跳转；清空/快速重复搜索 | 公告不跳新闻详情，最近关键词不被旧响应覆盖 |
| T11 扩展空间 | B / A | T02 | 增加 /growth；独立 GrowthView；更多菜单入口；写扩展说明 | 可直接访问且可返回，不请求虚构 API；A 能说明如何添加新内容 |
| T12 小步独立练习 | A、B 各一个 PR，互审 | 各自页面完成 | 各选自己页面一个小改动；先自己写；AI 检查；演示和复述 | 每人一个可核对 diff；说明写法、数据来源和边界 |
| T13 联合验收与交付 | A、B 共同，负责人确认 | T04～T12 | 全链路回归；真机检查；修复阻断问题；整理构建与学习记录 | 完成第 12 节清单，已知问题有负责人、严重度和处理结论 |

A、B 在共同任务里也分别保留自己负责的 Issue/PR。T00 的仓库创建是启动例外，其余文件变更全部由 PR 记录。T13 如需修复多处问题，拆成每个局部问题一个小 PR。

T12 可选的练习：给已存在的空状态增加清除筛选按钮；给自己页面增加明确的当前页提示；为缺失封面增加本地占位图。先写出验收条件，不能为练习增加不存在的后端功能。两人不能共同使用一个账号代替分别提交和评审。

开发进度建议记录为：

| 单位 | 状态 | 作者 | Issue | PR | 后端版本/日期 | 验收证据 | 需要复述的知识 | 剩余问题 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| T03 | 未开始 | A | 待建 | 待建 | 待联调 | 待补 | HTTP、ref、条件渲染 | 待补 |

## 8. 每个功能都走正规的 PR 流程

使用 GitHub flow：短功能分支围绕一个可评审任务，合并后删除远端功能分支，下一件事重新从最新 main 建分支。[GitHub flow 官方说明](https://docs.github.com/en/get-started/using-github/github-flow)。

### 8.1 先建立 Issue

Issue 至少写清：

```text
目标：手机端显示新闻列表和详情
范围：NewsList/NewsDetail、news.js、必要路由
接口：GET /api/news、GET /api/news/{id}
验收：正常、加载、空数据、失败重试、详情不存在、返回
作者：A
评审：B
公共文件：router/index.js
不包含：公告、后台管理
```

标题用动词描述结果，如“实现移动端新闻列表与详情”。复杂任务先拆分，不用“完成全部前端”这种无法评审的大 Issue。

### 8.2 开始写之前同步代码

确认当前修改已经提交或妥善保留，再执行：

```powershell
git switch main
git pull --ff-only origin main
git switch -c codex/news-pages
```

`--ff-only` 表示只接受能直接快进的更新。遇到分叉时停下来查看原因，不让 Git 自动生成自己不理解的合并。功能分支统一用 `codex/任务名`，两人各用自己的分支。

### 8.3 提交前检查并 push

```powershell
git status
git diff
npm run build
git add src/api/news.js src/views/NewsDetailView.vue
git diff --cached
git commit -m "feat: 增加移动端新闻详情"
git push -u origin codex/news-pages
```

git add 的文件只是示例，应列出本次真实文件，包括需要新增的列表和路由；不要机械复制不完整清单。`git diff` 看未暂存变化，`git diff --cached` 看本次将要提交的内容，`-u` 绑定本地分支与远端对应分支。

提交只包含当前 Issue 的修改。避免直接 `git add .` 混入无关配置；不使用 `reset --hard`、`clean -fd` 或强推来解决自己还没理解的问题。

### 8.4 创建 PR

在 GitHub 从功能分支向 `main` 提 PR，指定另一人为 Reviewer。PR 标题描述结果，正文可以采用：

```markdown
关联：Closes #实际Issue编号

变更：
- 实现了哪些用户可见行为。
- 使用了哪些真实接口。

验证：
- npm run build：通过，运行日期为……
- 手机/浏览器与宽度：……
- 真实接口与后端版本：……
- 正常、空数据、错误、详情不存在分别如何验证：……
- 附必要截图或录屏，去除个人资料和令牌。

AI 辅助：
- AI 帮助了哪一部分。
- 我能解释的关键逻辑。
- 我发现并修正的问题。

剩余问题：
- 无，或链接到明确的 Issue。
```

只声明真实执行过的检查。模拟响应截图标明模拟；构建成功和接口成功分开记录。

### 8.5 评审不是只点 Approve

评审者在自己电脑检出 PR 分支，安装依赖、运行和核对。以 A 的分支为例：

```powershell
git fetch origin
git switch -c review-news origin/codex/news-pages
npm ci
npm run dev
npm run build
```

review-news 是本地评审分支名；只在首次创建时使用 -c。再次评审先确认工作区干净，用 fetch 更新，再按实际分支状态同步，不重复机械创建同名分支。

重点检查：

- 请求路径、参数、data 结构和 ID 类型是否符合接口文档。
- 加载、错误、空列表、null 详情有没有正确区分。
- 320/390 宽度能否操作，底部导航是否遮挡，图片或长链接是否撑宽。
- 改动是否只围绕本 Issue，作者能否解释关键方法。
- CI 是否成功，PR 描述是否与实际结果相符。

发现问题时给出具体位置、触发方式和预期，用 Request changes 阻止带问题合并。修正由作者继续 push 到**原功能分支**，现有 PR 自动更新；不是为每轮修正另建 PR。修改后重新构建并重新评审。

### 8.6 处理冲突

先提交自己已经完成的工作，确认工作区干净，然后在**功能分支**中同步：

```powershell
git fetch origin
git merge origin/main
```

出现冲突时，用编辑器查看双方改动，理解后保留需要的结果，删除冲突标记，再暂存冲突文件、完成合并提交、重新构建和 push。不要直接“全部接受我方”或“全部接受对方”。不确定时让 AI 只解释冲突块，并请另一人一起确认。

本项目先用 merge 同步功能分支，不要求新手学习 rebase 后强推。共享的 main 不做强推。

### 8.7 合并后同步

另一人批准、讨论解决、必需检查通过后，用 Squash and merge 合并，并删除远端功能分支。作者和评审者各自回到 main：

```powershell
git switch main
git pull --ff-only origin main
npm ci
```

下一项任务从最新 main 建新分支。Squash 后本地功能分支不一定被 Git 认定为已普通合并，不要为了清理强行使用 -D；本地分支可先保留。main 中已经存在完整提交，不意味着下一分支可以继续从旧功能分支派生。

## 9. 如何使用 AI，同时保证自己有收获

### 9.1 默认约定

把下面的规则放到每次新 AI 会话开头：

```text
我是只了解 Spring Boot 基础的前端新手。
请先读取当前代码、接口文档和当前 Issue，不参考过时接口文档。
本次只推进一个明确的小闭环，先说明拟修改的文件和理由。
不要一次生成整个项目，不增加未约定的接口或依赖。
我确认后再允许实际修改；如果我要求自己写，就只给指导和检查。
代码不添加注释，讲解写在回答或学习记录中，全部使用简体中文和 UTF-8。
任何“测试通过”都必须说明真实执行的命令与结果。
我说“1”或“完成”后，请优先主动读取实际文件检查，再安排下一步。
最小阶段验收完成后先更新开发进度，再进入下一阶段。
```

AI 可以解释语法、给小段实现、检查请求、定位错误和审阅差异。本人负责确认需求、运行代码、核对实际响应、理解关键逻辑，以及对提交负责。

### 9.2 可以直接复制的任务提示词

**开始新闻列表：**

```text
请阅读 docs/接口文档.md 中新闻接口，以及现有页面骨架。
这次只做真实新闻列表，先列出需要改的文件，控制在一个小闭环。
说明 HTTP 请求、ref、v-for、loading/error/empty 各自负责什么。
先给我 3～4 个连续步骤让我手写，不要直接修改。
```

**检查已经写好的页面：**

```text
我已经完成当前 Issue，请直接读取实际文件和 git diff。
先判断是否符合接口契约，再检查四种状态、路由参数和手机宽度。
请指出真实问题及触发方式，不把未运行的检查写成通过。
我来修改，你先解释最小改法。
```

**定位联调错误：**

```text
现象：……
请求 URL、HTTP 状态、脱敏后的响应：……
控制台报错：……
预期行为：……
请先区分网络、代理、HTTP、业务码和字段读取问题。
只分析当前接口，不建议重写整个项目。
```

**请求 AI 做一次局部实现：**

```text
我授权你只修改以下文件：……
目标和验收条件：……
请采用最基础的 JavaScript/Vue 写法，不加依赖、不改相邻模块。
完成后逐段解释，列出真实验证结果和未验证部分。
```

**评审同伴 PR：**

```text
请只读审查这次 PR 差异，并对照真实接口文档。
找出能触发的功能错误、移动端显示问题或缺失验收。
每个问题说明文件、原因、触发条件和建议，不直接修改。
不要用重构建议掩盖当前功能是否正确。
```

### 9.3 每人保留一份简短学习记录

每个已验收 PR 写 5 行就够：

```text
这次完成：
使用的接口与 data 结构：
我新理解的一个前端概念：
AI 建议中我验证或纠正的一点：
不看 AI 我能独立修改的部分：
```

每人每个主要阶段给对方做一次 3～5 分钟复述。对方追问“这个变量从哪里来”“错误时走哪里”“如果 data=null 会怎样”。解释不清楚就回到该小闭环，不继续堆新页面。

T12 的小改动先不让 AI 直接写，自己做完后再让 AI 检查。最终每人至少完成 4 个自己主导的功能 PR、4 次实际交叉评审、1 次独立小改动，以及 1 条完整的接口排障记录；这些记录可以重叠在阶段任务中，不额外造无用任务。

## 10. 扩展空间怎么留

`/growth` 必须有独立页面文件和“更多”中的导航入口。第一版展示标题“成长路线”、明确的“内容筹备中”说明以及返回入口；可以有简单的空布局，但不放伪装成真实数据的内容。

同时在 README 的扩展说明写清：

| 位置 | 后续怎么接 |
| --- | --- |
| router/index.js | 已注册 /growth，不需改其他业务路由 |
| views/GrowthView.vue | 后续内容主要放在此页 |
| 导航 | 已有固定入口 |
| api/growth.js | 只有后端真的提供成长路线 API 时才新增 |
| 公共布局 | 复用 MobileLayout 和请求状态组件 |
| 数据边界 | 当前没有成长路线后端接口，不调用 /api/growth 等虚构地址 |

留一个未开始的扩展 Issue，例如“将成长路线页补充为后端学习路径”。先确定内容来源、页面目标、负责人和验收条件，再安排后续 PR；原第一版能够独立交付，不依赖该扩展内容完成。

扩展验收由 A 口头说明并实际找到文件：新增一张静态学习卡片只需改 GrowthView；若以后接真实接口，需要增加 API 文件和状态处理。说明完保持预留页状态，不要求在本轮填满。

## 11. 手机页面的最低要求

先照顾 320～430 CSS 像素宽度的手机，再检查 768 宽度和桌面浏览。

| 项目 | 验收方法 |
| --- | --- |
| 无横向溢出 | 320、375/390、430、768 宽度逐页检查；不能用 overflow-x:hidden 掩盖被裁掉的内容 |
| 单列与间距 | 正文清楚，卡片间距一致，不把桌面多栏直接缩小 |
| 点击区域 | 主要按钮与导航按项目标准尽量达到约 44×44 CSS 像素，不只有很小的文字能点 |
| 底部导航 | 不盖住正文，兼顾手机底部安全区域 |
| 图片 | 保持合理比例，缺失时有占位；首图以外可按需懒加载 |
| 富文本 | 图片不超容器；长词可换行；表格在局部容器内滚动 |
| 表单与筛选 | 有 label；键盘打开后仍能操作；筛选回第一页 |
| 颜色与状态 | 不只靠颜色表示失败；错误有文字和重试方式 |
| 返回与刷新 | 列表进详情再返回可用；hash 深链接直接打开和刷新可用 |
| 慢网与断网 | 正在加载可见，失败后能重试，不无限转圈 |
| 长标题与空值 | 长标题不撑宽，缺图/无角色/无成员显示合理 |
| 导航语义 | 跳转用链接，动作使用按钮；图片有对应文字或合适替代说明 |

4 种数据状态都要验：加载中、正常、空数据、失败。空列表不是异常；空详情与空列表的提示也不同。测试参数错误时不修改真实业务数据。

正文 HTML 若未明确清洗方案，先展示安全文本；确认可信且采用清洗后再渲染 HTML，并核对不会显示原始标签。不要仅为了让截图好看直接绕过内容处理。

## 12. 最终交付与验收

### 12.1 功能与联调

- [ ] 所有第一版路由都能从导航到达，404 能返回。
- [ ] 首页、新闻、公告、项目、竞赛、资料、关于和搜索使用真实测试接口。
- [ ] 新闻与公告详情分开请求，搜索公告结果没有串到新闻。
- [ ] 页码、分类、关键词组合正常；已确认的后端缺陷有 Issue 和处理结论。
- [ ] 详情为空、列表为空、请求失败、长文本和坏图状态均验证。
- [ ] 下载和外链是真实可访问地址；未准备好的下载入口隐藏或明确说明。
- [ ] 未开放真实简历收集，除非独立 Issue 的后端前置条件已验收。
- [ ] 扩展页有路由、文件、入口、说明和待办 Issue。

### 12.2 工程与协作

- [ ] 公开仓库链接可访问，A、B 分别有自己的提交和评审。
- [ ] main 已受保护，后续变更通过 PR，无未解释的直接推送。
- [ ] 必需 build 检查通过，PR 有另一人有效批准。
- [ ] 全新 clone 后，按 README 执行 npm ci、npm run dev、npm run build 可工作。
- [ ] 实际 Node/npm 版本、代理配置方法和后端联调前提已记录。
- [ ] 文档进度与真实产物一致，未验证项没有标为通过。
- [ ] 两人的学习记录、独立小改动和排障过程可追溯。
- [ ] 代码和文档 UTF-8，没有上传个人资料、口令、令牌和无关构建目录。

### 12.3 运行与交付证据

产物至少包括仓库 URL、最终提交号、代表性 PR 链接、320/390/768 截图、一次手机实际操作记录、真实接口联调日期和后端版本、构建命令结果，以及剩余问题清单。

原始请求中的个人资料和令牌先脱敏。存在阻断问题时写“未通过”和下一责任人，不能只发漂亮截图就结束验收。

生产部署由负责人提供网站域名和接口入口。`npm run build` 的 dist 是静态产物，不能双击 index.html 代替部署。生产服务器需要实际处理 /api 和 /uploads 的转发，Vite 开发代理不会随 dist 生效；手机也不能访问开发者电脑的 localhost。

首版 hash 路由的页面刷新不要求额外的 history fallback；若后续改为 history 路由，新增一个独立部署任务配置前端页面回退，并确认 API 404 不被回退成 HTML。GitHub Pages 可以托管静态前端，但不会运行 Spring Boot，也不会提供 Vite 代理；不要把能显示首页当作接口已部署。

上线或演示前，至少在目标环境验证首页、一个详情深链接、一次搜索、图片和下载；开发环境结果不替代目标环境验收。

## 13. 常见问题从哪里查

| 现象 | 先检查 | 对应学习点 |
| --- | --- | --- |
| 200 但页面显示失败 | JSON 的 code、message、data | HTTP 成功与业务成功不同 |
| .map 或 .length 报错 | 实际 data 是数组、分页对象还是 null | 响应结构和空值 |
| 新闻不翻页 | 新闻没有 page 分页契约 | 不向不存在的功能传参数 |
| 公告详情打开成另一篇新闻 | type、路由和 API 是否混用 | 数据标识与资源类型 |
| 分类“全部”没有结果 | 是否错误传 category=all | 不同接口的参数差异 |
| 手机首页能开但请求失败 | 是否硬编码 localhost，代理是否工作 | 设备地址和请求链 |
| npm run dev:mock 没有假数据 | 环境变量与 MSW 启动条件 | 脚本名称不是运行证据 |
| 头像或首图 404 | 图片属于后端还是原前端 public/assets | 资源所属服务器 |
| 项目 ID 变了 | 是否把 String ID 转数字 | 字符串与数值精度 |
| 当前关键词显示上一次结果 | 较早请求晚返回覆盖了新状态 | 异步请求顺序 |
| CI 失败，本地能构建 | 锁文件、Node 版本、文件名大小写、未提交文件 | 可复现构建 |
| 合并按钮不可用 | Review、必需检查、未解决讨论、分支冲突 | PR 约束真实生效 |

排查时一次只改一个原因，修复后补一个能重现问题的人工验收步骤。不要让 AI 一次更换框架、重写配置和改接口来“试试看”。

完成整个项目的标准是：两人能把真实接口接成可用页面，能解释关键代码，并能通过可追溯的评审交付一个共同维护的版本。
