<div align="center">

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | [繁體中文](README.zh-TW.md) | [日本語](README.ja.md) | [Français](README.fr.md) | [Deutsch](README.de.md) | [Español](README.es.md) | [한국어](README.ko.md) | **Русский** | [Português (Brasil)](README.pt-BR.md)

### Официальный портал и управление набором участников

</div>

BDIS Official Website — официальный портал Big Data Intelligence Studio. Раздельные frontend и backend предоставляют сведения о студии и исследованиях, заявки с предпросмотром PDF, анонимную обратную связь и управление содержимым.

## Возможности

- Новости, объявления, исследовательские проекты, команда, учебные материалы и поиск.
- Выпадающая мобильная навигация и разрешение конфликтов порядка отображения.
- Онлайн-анкеты или загрузка документов Word с предпросмотром PDF через LibreOffice или Docker.
- Отправка текста и изображений без входа; просмотр, фильтрация и изменение статуса администраторами.
- JWT-аутентификация, управление содержимым и заявками, уведомления SMTP.

## Локальный запуск

Требуются Java 17, Node.js 22.12 или более новая совместимая версия и MySQL. Для преобразования в PDF также нужен LibreOffice или соответствующая среда Docker.

Сначала импортировать существующую схему бизнес-данных и сами данные, затем выполнить подходящие SQL-скрипты. `init_admin.sql` не является полной инициализацией пустой базы. Перед миграциями создать резервную копию.

### Backend: Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

В Windows PowerShell задать переменные через `$env:VARIABLE = "value"` и выполнить `.\mvnw.cmd spring-boot:run`. Если адрес базы отличается от стандартного, также задать `BDIS_DB_URL`.

### Frontend: в другом терминале

```bash
cd bdis-web
npm install
npm run dev
```

Стандартные порты: `8081` для backend и `5173` для сервера разработки frontend. Последний перенаправляет `/api` и `/uploads` в backend.

## Сборка и тесты

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

Результаты: `bdis-web/dist/` и `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`. В Windows заменить `./mvnw` на `.\mvnw.cmd`.

## Развёртывание и конфиденциальность

В рабочей среде настроить учётные данные базы и `BDIS_JWT_SECRET`, а также постоянное хранение каталогов `BDIS_FEEDBACK_STORAGE_ROOT` и `BDIS_RESUME_STORAGE_ROOT`.

Модуль обратной связи не сохраняет личность или IP отправителя. Однако журналы доступа сервера зависят от настроек развёртывания; невозможность отслеживания не гарантируется. Закрытым каталогам нужны права на запись; их нельзя публиковать как статические ресурсы.

## Подробная документация

- [Полное руководство на упрощённом китайском](README.md)
- [Полное руководство на английском](README.en.md)
- [Документация API на упрощённом китайском](文档/接口文档-基于当前源码.md)
- [Развёртывание обратной связи на упрощённом китайском](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## Лицензия и участие

В репозитории пока нет отдельного файла LICENSE; Apache-2.0 или другая открытая лицензия не заявлена. Перед использованием или распространением уточнить разрешения у сопровождающих. Для участия создать fork и отдельную ветку, выполнить тесты и открыть Pull Request в main.

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
