<div align="center">

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Big Data Intelligence Studio Logo" width="480" />

[简体中文](README.md) | **English** | [繁體中文](README.zh-TW.md) | [日本語](README.ja.md) | [Français](README.fr.md) | [Deutsch](README.de.md) | [Español](README.es.md) | [한국어](README.ko.md) | [Русский](README.ru.md) | [Português (Brasil)](README.pt-BR.md)

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

The official portal and recruitment management system for Big Data Intelligence Studio

[Features](#features) · [Getting Started](#getting-started) · [Configuration](#configuration) · [Project Structure](#project-structure) · [API Documentation (Chinese)](文档/接口文档-基于当前源码.md)

</div>

---

BDIS Official Website is the official portal of Big Data Intelligence Studio. It uses separate frontend and backend applications to provide studio and research information, recruitment forms and PDF previews, an anonymous feedback inbox, and a content management system.

## Features

- **Studio portal**
  - News, research projects, team profiles, and learning resources.
  - Search across news, announcements, projects, and resources.
  - Mobile dropdown navigation, content ordering, and sort-conflict resolution.
- **Recruitment and resume previews**
  - Structured online forms or uploads of completed Word documents.
  - DOCX template generation and PDF conversion using local LibreOffice or Docker.
  - Email notifications on resume submission when SMTP is configured.
- **Anonymous feedback inbox**
  - No login required; members, prospective members, and other visitors can submit feedback.
  - Text, images, or both, with attachments stored in a private directory.
  - Administrator review, category filters, and processing status updates.
- **Content management**
  - JWT-based administrator authentication.
  - Management of banners, news, announcements, team members, projects, and resources.
  - Resume management and feedback processing.

## Getting Started

### 1. Frontend Mock Preview

The frontend includes an MSW mock entry point for previewing some pages without a backend. Mock data does not replace integration testing. Feedback submission and resume conversion require the backend service.

```bash
cd bdis-web
npm install
npm run dev:mock
```

Open the URL printed by the development server; the default port is `5173`. If existing mock data cannot load, use the full-stack setup below.

### 2. Full-Stack Development

Requirements: Java 17, Node.js 22.12 or a newer compatible version, and MySQL. Resume PDF conversion also requires LibreOffice or the corresponding Docker conversion environment.

#### Prepare the Database

Create the database and import the existing business schema and data first. `sql/init_admin.sql` modifies the existing `lab_info` table and is not a complete initialization script for an empty database.

Back up the database and apply the scripts appropriate to its current schema:

| Script | Purpose |
| --- | --- |
| [init_admin.sql](bdis-website/sql/init_admin.sql) | Administrator table and changes to the existing studio information table |
| [resume_submission_migration.sql](bdis-website/sql/resume_submission_migration.sql) | Create the resume submission table |
| [resume_submission_upgrade.sql](bdis-website/sql/resume_submission_upgrade.sql) | Upgrade an existing resume table after checking its schema |
| [feedback_migration.sql](bdis-website/sql/feedback_migration.sql) | Create feedback and attachment tables |
| [lab_address_update.sql](bdis-website/sql/lab_address_update.sql) | Update the existing studio address to Room 502, Huizhi Building |

#### Start the Backend

Linux / macOS:

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
cd bdis-website
$env:BDIS_DB_USERNAME = "root"
$env:BDIS_DB_PASSWORD = "your_password"
$env:BDIS_JWT_SECRET = "replace-with-a-long-random-secret"
.\mvnw.cmd spring-boot:run
```

Set `BDIS_DB_URL` if the database address differs from the default. The backend listens on port `8081` by default.

#### Start the Frontend

```bash
cd bdis-web
npm install
npm run dev
```

The development server proxies `/api` and `/uploads` to `http://localhost:8081`.

### 3. Build for Deployment

Frontend:

```bash
cd bdis-web
npm run build
```

Output directory: `bdis-web/dist/`.

Backend:

```bash
cd bdis-website
./mvnw package
# Windows: .\mvnw.cmd package
```

Output file: `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`. Use Java 17 for deployment and retain production database settings, private attachment directories, and the `/api` reverse proxy configuration.

## Configuration

Backend configuration: [application.yml](bdis-website/src/main/resources/application.yml).

| Setting or environment variable | Default | Description |
| --- | --- | --- |
| `server.port` / `SERVER_PORT` | `8081` | Backend listening port |
| `BDIS_DB_URL` | Local MySQL database named `bdis` | Database connection URL |
| `BDIS_DB_USERNAME` | `root` | Database username |
| `BDIS_DB_PASSWORD` | Empty | Database password |
| `BDIS_JWT_SECRET` | `change-me-before-deploying` | Administrator token signing key; replace in production |
| `BDIS_FEEDBACK_STORAGE_ROOT` | `./private-feedback` | Private feedback attachment directory |
| `BDIS_RESUME_STORAGE_ROOT` | `private-resumes` under the working directory | Private resume directory |
| `BDIS_RESUME_CONVERTER` | `native` | Resume converter: `native` or `docker` |
| `BDIS_MAIL_USERNAME` / `BDIS_MAIL_PASSWORD` | Empty | Mail account and authorization credentials |

Private directories require write permissions and must not have public static resource mappings. See the [feedback deployment guide (Chinese)](文档/匿名信箱部署说明.md) for deployment instructions, attachment limits, and API details.

## Project Structure

```text
.
├── bdis-web/               # Frontend: Vue 3, Vite, Element Plus
│   ├── public/             # Static files and MSW Service Worker
│   ├── src/                # Pages, components, routing, styles, API requests
│   └── vite.config.js      # Development server and proxy configuration
├── bdis-website/           # Backend: Spring Boot, MyBatis-Plus
│   ├── sql/                # Database migration scripts
│   └── src/                # Backend source, resources, and tests
├── deploy/                 # Deployment files and resume converter configuration
├── scripts/                # Debugging and diagnostic scripts
└── 文档/                   # API documentation, requirements, collaboration guides
```

## Contributing

1. Create a feature branch from `main`. External contributors can fork the repository first.
2. Keep each branch focused on one feature and avoid unrelated changes.
3. Use commit types such as `feat`, `fix`, `docs`, or `refactor`.
4. Run the build and relevant tests, then open a pull request targeting `main`.

Frontend build: `npm run build`. Backend tests: `./mvnw test`, or `.\mvnw.cmd test` on Windows.

## License

The repository currently has no standalone `LICENSE` file. This README does not declare Apache-2.0 or any other open-source license. Confirm the scope of authorization with the maintainers before using or distributing the code.

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
