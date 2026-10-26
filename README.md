<div align="center">

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS 大数据智能工作室 Logo" width="480" />

<p>
  <sub>Developed &amp; Maintained by the <strong>BDIS Engineering Team</strong></sub>
</p>

[![Vue 3](https://img.shields.io/badge/Vue-3.5-42b883?style=flat-square&logo=vuedotjs&logoColor=white)](bdis-web/package.json)
[![Vite](https://img.shields.io/badge/Vite-7.x-646cff?style=flat-square&logo=vite&logoColor=white)](bdis-web/vite.config.js)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.11-409EFF?style=flat-square&logo=elementplus&logoColor=white)](bdis-web/package.json)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)](bdis-website/pom.xml)
[![JDK](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](bdis-website/pom.xml)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.5-007ACC?style=flat-square)](bdis-website/pom.xml)

### Modern Web Portal & Recruitment Management Platform

面向大数据智能工作室的官方综合门户与数字化招新管理系统

[功能特性](#features) · [快速开始](#getting-started) · [配置说明](#configuration) · [代码结构](#project-structure) · [接口文档](文档/接口文档-基于当前源码.md)

</div>

---

BDIS Official Website 是大数据智能工作室（Big Data Intelligence Studio）的官方门户系统。项目采用前后端分离架构，提供工作室与科研成果展示、招新简历填写与 PDF 预览、匿名意见信箱和内容管理后台。

## Features

- **工作室综合门户**
  - 新闻动态、科研项目、团队介绍与学习资源展示。
  - 新闻、公告、项目和资源搜索。
  - 移动端下拉导航，以及内容排序与排序冲突处理。
- **招新与简历预览**
  - 在线填写结构化表单，或上传已填写的 Word 文档。
  - 基于 DOCX 模板生成文档，支持通过本机 LibreOffice 或 Docker 转换为 PDF。
  - 配置 SMTP 后，提交简历时发送邮件通知。
- **匿名意见信箱**
  - 无需登录，工作室成员、新朋友和网站访客均可投递。
  - 支持文字、图片或图文组合，附件存放于私有目录。
  - 管理员可查看意见、筛选分类并更新处理状态。
  - 本模块不保存投递者身份或 IP；服务器访问日志仍取决于部署配置，不承诺无法追踪。
- **内容管理后台**
  - 基于 JWT 的管理员身份验证。
  - 轮播图、新闻、公告、团队、项目与资源管理。
  - 简历管理和匿名意见处理。

## Getting Started

### 1. 前端 Mock 预览

前端包含 MSW 模拟数据入口，部分页面可在不启动后端的情况下预览。模拟数据不代替真实接口验证，匿名意见提交、简历转换等流程需要后端服务。

```bash
cd bdis-web
npm install
npm run dev:mock
```

访问开发服务输出的地址，默认端口为 `5173`。若现有模拟数据无法加载，可使用下面的前后端联调方式。

### 2. 前后端联调

需要 Java 17、Node.js 22.12 或更新的兼容版本，以及 MySQL 数据库。简历 PDF 转换还需要 LibreOffice 或对应的 Docker 转换环境。

#### 准备数据库

先创建数据库并导入现有业务表结构与数据。`sql/init_admin.sql` 包含对既有 `lab_info` 表的修改，并非完整的空库初始化脚本。

根据现有数据库状态执行所需脚本，执行前备份数据：

| 脚本 | 用途 |
| --- | --- |
| [init_admin.sql](bdis-website/sql/init_admin.sql) | 管理员表与既有工作室信息表调整 |
| [resume_submission_migration.sql](bdis-website/sql/resume_submission_migration.sql) | 新建简历提交表 |
| [resume_submission_upgrade.sql](bdis-website/sql/resume_submission_upgrade.sql) | 既有简历表升级，执行前核对当前结构 |
| [feedback_migration.sql](bdis-website/sql/feedback_migration.sql) | 新建意见与附件表 |
| [lab_address_update.sql](bdis-website/sql/lab_address_update.sql) | 将既有工作室地址更新为汇智楼502 |

#### 启动后端

Linux / macOS 环境示例：

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

Windows PowerShell 环境示例：

```powershell
cd bdis-website
$env:BDIS_DB_USERNAME = "root"
$env:BDIS_DB_PASSWORD = "your_password"
$env:BDIS_JWT_SECRET = "replace-with-a-long-random-secret"
.\mvnw.cmd spring-boot:run
```

数据库地址不同时，还需设置 `BDIS_DB_URL`。后端默认监听 `8081` 端口。

#### 启动前端

```bash
cd bdis-web
npm install
npm run dev
```

开发服务将 `/api` 和 `/uploads` 请求代理至 `http://localhost:8081`。

### 3. 构建部署产物

前端：

```bash
cd bdis-web
npm run build
```

产物目录：`bdis-web/dist/`。

后端：

```bash
cd bdis-website
./mvnw package
# Windows：.\mvnw.cmd package
```

产物文件：`bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`。部署时使用 Java 17，保留生产环境数据库配置、私有附件目录和 `/api` 反向代理配置。

## Configuration

后端配置文件为 [application.yml](bdis-website/src/main/resources/application.yml)。

| 配置项或环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `server.port` / `SERVER_PORT` | `8081` | 后端监听端口 |
| `BDIS_DB_URL` | 本机 MySQL 的 `bdis` 数据库 | 数据库连接地址 |
| `BDIS_DB_USERNAME` | `root` | 数据库用户名 |
| `BDIS_DB_PASSWORD` | 空 | 数据库密码 |
| `BDIS_JWT_SECRET` | `change-me-before-deploying` | 管理员令牌签名密钥，生产环境必须更换 |
| `BDIS_FEEDBACK_STORAGE_ROOT` | `./private-feedback` | 意见附件私有目录 |
| `BDIS_RESUME_STORAGE_ROOT` | 工作目录下的 `private-resumes` | 简历私有目录 |
| `BDIS_RESUME_CONVERTER` | `native` | 简历转换方式，可选 `native` 或 `docker` |
| `BDIS_MAIL_USERNAME` / `BDIS_MAIL_PASSWORD` | 空 | 邮件账号与授权信息 |

私有目录需要写入权限，不应配置静态资源映射。匿名信箱的部署、附件限制和接口说明见 [匿名信箱部署说明](文档/匿名信箱部署说明.md)。

## Project Structure

```text
.
├── bdis-web/               # 前端工程：Vue 3、Vite、Element Plus
│   ├── public/             # 静态文件与 MSW Service Worker
│   ├── src/                # 页面、组件、路由、样式与接口请求
│   └── vite.config.js      # 开发服务与代理配置
├── bdis-website/           # 后端工程：Spring Boot、MyBatis-Plus
│   ├── sql/                # 数据库调整脚本
│   └── src/                # 后端源码、资源与测试
├── deploy/                 # 部署文件与简历转换容器配置
├── scripts/                # 调试与排障脚本
└── 文档/                   # 接口说明、业务需求与协作文档
```

## Contributing

1. 从 `main` 创建功能分支。外部贡献者可先 Fork 仓库。
2. 每个分支集中处理一个功能，避免混入无关改动。
3. 提交说明使用 `feat`、`fix`、`docs` 或 `refactor` 等类型。
4. 完成构建与相关测试后，向 `main` 发起 Pull Request。

前端构建命令为 `npm run build`；后端测试命令为 `./mvnw test`，Windows 使用 `.\mvnw.cmd test`。

## License

当前仓库未提供独立的 `LICENSE` 文件，因此此处不声明 Apache-2.0 或其他开源许可证。使用或分发代码前，请与维护者确认授权范围。

---

<div align="center">

Big Data Intelligence Studio (BDIS) · 2026

</div>
