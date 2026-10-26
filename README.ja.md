<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | [繁體中文](README.zh-TW.md) | **日本語** | [Français](README.fr.md) | [Deutsch](README.de.md) | [Español](README.es.md) | [한국어](README.ko.md) | [Русский](README.ru.md) | [Português (Brasil)](README.pt-BR.md)

### 公式ポータル・新メンバー募集管理システム

</div>

BDIS Official Website は Big Data Intelligence Studio の公式サイトです。フロントエンドとバックエンドを分離し、研究室紹介、研究成果、応募書類のプレビュー、匿名意見箱、コンテンツ管理を提供します。

## 主な機能

- ニュース、お知らせ、研究プロジェクト、メンバー紹介、学習資料、検索。
- モバイル向けドロップダウンナビゲーションと、並び順の競合解決。
- オンライン応募フォームまたは Word 文書のアップロード。LibreOffice または Docker による PDF プレビュー。
- ログイン不要のテキスト・画像投稿。管理者による閲覧、絞り込み、対応状況の更新。
- JWT 管理者認証、コンテンツ・応募書類管理、SMTP メール通知。

## ローカル起動

Java 17、Node.js 22.12 以降の互換バージョン、MySQL が必要です。PDF 変換には LibreOffice または対応する Docker 環境も必要です。

既存の業務テーブルとデータを先にインポートし、現在のスキーマに応じて SQL スクリプトを実行してください。`init_admin.sql` は空のデータベース用の完全な初期化スクリプトではありません。実行前にバックアップが必要です。

### バックエンド：Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

Windows PowerShell では `$env:VARIABLE = "value"` で環境変数を設定し、`.\mvnw.cmd spring-boot:run` を実行します。接続先が既定値と異なる場合は `BDIS_DB_URL` も設定します。

### フロントエンド：別のターミナルで実行

```bash
cd bdis-web
npm install
npm run dev
```

既定のポートはバックエンドが `8081`、フロントエンド開発サーバーが `5173` です。`/api` と `/uploads` はバックエンドに転送します。

## ビルドとテスト

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

生成物は `bdis-web/dist/` と `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar` です。Windows では `./mvnw` を `.\mvnw.cmd` に置き換えます。

## デプロイとプライバシー

本番環境ではデータベース認証情報と `BDIS_JWT_SECRET` を設定し、`BDIS_FEEDBACK_STORAGE_ROOT` と `BDIS_RESUME_STORAGE_ROOT` のディレクトリを永続化します。

非公開ディレクトリには書き込み権限が必要で、静的ファイルとして公開してはいけません。

## 詳細ドキュメント

- [完全版：簡体字中国語](README.md)
- [完全版：English](README.en.md)
- [API ドキュメント：簡体字中国語](文档/接口文档-基于当前源码.md)
- [意見箱のデプロイ：簡体字中国語](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## ライセンスと貢献

本プロジェクトは [MIT ライセンス](LICENSE)を採用しています。ライセンス条項に従い、著作権表示と許諾表示を保持することで、商用利用を含む使用、変更、配布が可能です。貢献する場合は Fork と機能ブランチを作成し、テスト後に main 向けの Pull Request を送信できます。

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
