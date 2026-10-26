<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | [繁體中文](README.zh-TW.md) | [日本語](README.ja.md) | [Français](README.fr.md) | [Deutsch](README.de.md) | [Español](README.es.md) | **한국어** | [Русский](README.ru.md) | [Português (Brasil)](README.pt-BR.md)

### 공식 포털 및 신규 구성원 모집 관리 시스템

</div>

BDIS Official Website는 Big Data Intelligence Studio의 공식 웹사이트입니다. 프런트엔드와 백엔드를 분리하여 스튜디오 및 연구 성과 소개, 지원서 PDF 미리보기, 익명 의견함, 콘텐츠 관리를 제공합니다.

## 주요 기능

- 뉴스, 공지, 연구 프로젝트, 구성원 소개, 학습 자료 및 검색.
- 모바일 드롭다운 메뉴와 표시 순서 충돌 처리.
- 온라인 지원서 작성 또는 Word 문서 업로드, LibreOffice 또는 Docker 기반 PDF 미리보기.
- 로그인 없이 텍스트와 이미지 제출, 관리자 조회·필터링·처리 상태 변경.
- JWT 관리자 인증, 콘텐츠 및 지원서 관리, SMTP 이메일 알림.

## 로컬 실행

Java 17, Node.js 22.12 이상의 호환 버전, MySQL이 필요합니다. PDF 변환에는 LibreOffice 또는 해당 Docker 환경도 필요합니다.

기존 업무 테이블과 데이터를 먼저 가져온 후 현재 스키마에 필요한 SQL 스크립트를 실행합니다. `init_admin.sql`은 빈 데이터베이스를 위한 완전한 초기화 스크립트가 아닙니다. 마이그레이션 전에 데이터를 백업해야 합니다.

### 백엔드: Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

Windows PowerShell에서는 `$env:VARIABLE = "value"`로 환경 변수를 설정하고 `.\mvnw.cmd spring-boot:run`을 실행합니다. 데이터베이스 주소가 다르면 `BDIS_DB_URL`도 설정합니다.

### 프런트엔드: 다른 터미널에서 실행

```bash
cd bdis-web
npm install
npm run dev
```

기본 포트는 백엔드 `8081`, 프런트엔드 개발 서버 `5173`입니다. 개발 서버는 `/api`와 `/uploads`를 백엔드로 전달합니다.

## 빌드 및 테스트

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

결과물은 `bdis-web/dist/`와 `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`입니다. Windows에서는 `./mvnw` 대신 `.\mvnw.cmd`를 사용합니다.

## 배포 및 개인정보

운영 환경에서는 데이터베이스 인증 정보와 `BDIS_JWT_SECRET`을 설정하고 `BDIS_FEEDBACK_STORAGE_ROOT` 및 `BDIS_RESUME_STORAGE_ROOT` 디렉터리를 영구 저장해야 합니다.

비공개 디렉터리에는 쓰기 권한이 필요하며 공개 정적 리소스로 노출하면 안 됩니다.

## 상세 문서

- [전체 안내: 중국어 간체](README.md)
- [전체 안내: 영어](README.en.md)
- [API 문서: 중국어 간체](文档/接口文档-基于当前源码.md)
- [의견함 배포 안내: 중국어 간체](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## 라이선스 및 기여

이 프로젝트는 [MIT 라이선스](LICENSE)를 따릅니다. 라이선스 조건을 준수하고 저작권 및 허가 고지를 유지하면 상업적 용도를 포함한 사용, 수정, 배포가 가능합니다. 기여 시 Fork와 기능 브랜치를 만들고 테스트 후 main을 대상으로 Pull Request를 제출할 수 있습니다.

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
