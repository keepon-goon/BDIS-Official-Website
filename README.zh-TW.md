<div align="center">

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | **繁體中文** | [日本語](README.ja.md) | [Français](README.fr.md) | [Deutsch](README.de.md) | [Español](README.es.md) | [한국어](README.ko.md) | [Русский](README.ru.md) | [Português (Brasil)](README.pt-BR.md)

### 官方入口網站與招新管理系統

</div>

BDIS Official Website 是大數據智能工作室的官方網站，採用前後端分離架構，提供工作室介紹、科研成果展示、招新履歷預覽、匿名意見信箱與內容管理後台。

## 主要功能

- 新聞、公告、科研專案、團隊介紹、學習資源與搜尋。
- 行動版下拉導覽，以及內容排序與排序衝突處理。
- 線上填寫履歷或上傳 Word 文件，透過 LibreOffice 或 Docker 產生 PDF 預覽。
- 無需登入即可提交文字與圖片意見；管理員可檢視、篩選並更新處理狀態。
- JWT 管理員驗證、內容維護、履歷管理及 SMTP 郵件通知。

## 本機啟動

需要 Java 17、Node.js 22.12 或更新的相容版本，以及 MySQL。履歷 PDF 轉換另需 LibreOffice 或相應的 Docker 環境。

先匯入既有業務資料表與資料，再依目前結構執行所需的 SQL 腳本。`init_admin.sql` 並非完整的空資料庫初始化腳本；執行前應備份資料。

### 後端：Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

Windows PowerShell 請使用 `$env:變數名稱 = "值"` 設定環境變數，啟動命令為 `.\mvnw.cmd spring-boot:run`。資料庫地址不同時，還需設定 `BDIS_DB_URL`。

### 前端：在另一個終端機執行

```bash
cd bdis-web
npm install
npm run dev
```

後端預設為 `8081` 埠，前端開發服務預設為 `5173` 埠，並代理 `/api` 與 `/uploads` 至後端。

## 建置與測試

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

前端產物為 `bdis-web/dist/`，後端產物為 `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`。Windows 使用 `.\mvnw.cmd` 取代 `./mvnw`。

## 部署與隱私

正式環境須設定資料庫憑證及 `BDIS_JWT_SECRET`，並持久化保存 `BDIS_FEEDBACK_STORAGE_ROOT` 與 `BDIS_RESUME_STORAGE_ROOT` 指定的目錄。

私有目錄需要寫入權限，不可設定為公開靜態資源。

## 詳細文件

- [完整說明：簡體中文](README.md)
- [完整說明：English](README.en.md)
- [API 文件：簡體中文](文档/接口文档-基于当前源码.md)
- [匿名信箱部署說明：簡體中文](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## 授權與貢獻

目前倉庫沒有獨立的 LICENSE 檔案，不聲明 Apache-2.0 或其他開源授權。使用或散布前請向維護者確認授權範圍。貢獻者可先 Fork，建立功能分支，完成測試後向 main 提出 Pull Request。

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
