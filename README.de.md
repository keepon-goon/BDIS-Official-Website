<div align="center">

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | [繁體中文](README.zh-TW.md) | [日本語](README.ja.md) | [Français](README.fr.md) | **Deutsch** | [Español](README.es.md) | [한국어](README.ko.md) | [Русский](README.ru.md) | [Português (Brasil)](README.pt-BR.md)

### Offizielles Portal und Bewerbungsverwaltung

</div>

BDIS Official Website ist das offizielle Portal des Big Data Intelligence Studio. Getrennte Frontend- und Backend-Anwendungen bieten Informationen zum Studio und seinen Forschungsprojekten, Bewerbungen mit PDF-Vorschau, ein anonymes Feedback-Postfach und eine Inhaltsverwaltung.

## Funktionen

- Nachrichten, Ankündigungen, Forschungsprojekte, Teamvorstellung, Lernmaterialien und Suche.
- Mobiles Dropdown-Menü und Konfliktlösung bei der Anzeigereihenfolge.
- Online-Bewerbungsformulare oder Word-Uploads mit PDF-Vorschau über LibreOffice oder Docker.
- Text- und Bildfeedback ohne Anmeldung; Einsicht, Filter und Statusverwaltung für Administratoren.
- JWT-Authentifizierung, Inhalts- und Bewerbungsverwaltung sowie SMTP-Benachrichtigungen.

## Lokaler Start

Erforderlich sind Java 17, Node.js 22.12 oder eine neuere kompatible Version sowie MySQL. Für die PDF-Konvertierung ist zusätzlich LibreOffice oder die entsprechende Docker-Umgebung nötig.

Zuerst das bestehende Geschäftsschema und die Daten importieren. Anschließend die zum aktuellen Schema passenden SQL-Skripte ausführen. `init_admin.sql` ist keine vollständige Initialisierung einer leeren Datenbank. Vor Migrationen eine Sicherung erstellen.

### Backend: Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

Unter Windows PowerShell Umgebungsvariablen mit `$env:VARIABLE = "value"` setzen und `.\mvnw.cmd spring-boot:run` ausführen. Bei abweichender Datenbankadresse auch `BDIS_DB_URL` setzen.

### Frontend: in einem weiteren Terminal

```bash
cd bdis-web
npm install
npm run dev
```

Standardports: `8081` für das Backend und `5173` für den Frontend-Entwicklungsserver. Dieser leitet `/api` und `/uploads` an das Backend weiter.

## Build und Tests

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

Ergebnisse: `bdis-web/dist/` und `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`. Unter Windows `./mvnw` durch `.\mvnw.cmd` ersetzen.

## Bereitstellung und Datenschutz

In Produktion Datenbankzugangsdaten und `BDIS_JWT_SECRET` konfigurieren und die Verzeichnisse `BDIS_FEEDBACK_STORAGE_ROOT` sowie `BDIS_RESUME_STORAGE_ROOT` dauerhaft speichern.

Private Verzeichnisse benötigen Schreibrechte und dürfen nicht als öffentliche statische Ressourcen erreichbar sein.

## Weitere Dokumentation

- [Vollständige Anleitung auf vereinfachtem Chinesisch](README.md)
- [Vollständige Anleitung auf Englisch](README.en.md)
- [API-Dokumentation auf vereinfachtem Chinesisch](文档/接口文档-基于当前源码.md)
- [Feedback-Bereitstellung auf vereinfachtem Chinesisch](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## Lizenz und Beiträge

Das Repository enthält derzeit keine separate LICENSE-Datei und erklärt keine Apache-2.0- oder andere Open-Source-Lizenz. Vor Nutzung oder Weitergabe den Berechtigungsumfang mit den Verantwortlichen klären. Beiträge erfolgen über einen Fork und einen eigenen Funktionsbranch, Tests und anschließend einen Pull Request gegen main.

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
