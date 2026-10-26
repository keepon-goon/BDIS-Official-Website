<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | [繁體中文](README.zh-TW.md) | [日本語](README.ja.md) | [Français](README.fr.md) | [Deutsch](README.de.md) | **Español** | [한국어](README.ko.md) | [Русский](README.ru.md) | [Português (Brasil)](README.pt-BR.md)

### Portal oficial y gestión de incorporación de miembros

</div>

BDIS Official Website es el portal oficial de Big Data Intelligence Studio. Separa frontend y backend para ofrecer información del estudio y sus investigaciones, solicitudes con vista previa PDF, un buzón de sugerencias anónimo y gestión de contenidos.

## Funciones

- Noticias, anuncios, proyectos de investigación, equipo, recursos y búsqueda.
- Navegación móvil desplegable y resolución de conflictos en el orden de presentación.
- Formularios de solicitud o documentos Word, con vista previa PDF mediante LibreOffice o Docker.
- Envío de texto e imágenes sin iniciar sesión; consulta, filtros y actualización del estado por administradores.
- Autenticación JWT, gestión de contenidos y solicitudes, y notificaciones SMTP.

## Inicio local

Se requieren Java 17, Node.js 22.12 o una versión posterior compatible y MySQL. La conversión PDF requiere además LibreOffice o el entorno Docker correspondiente.

Importar primero el esquema de negocio existente y sus datos. Después, ejecutar los scripts SQL adecuados al esquema actual. `init_admin.sql` no inicializa por completo una base vacía. Realizar una copia de seguridad antes de las migraciones.

### Backend: Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

En Windows PowerShell, definir variables con `$env:VARIABLE = "value"` e iniciar con `.\mvnw.cmd spring-boot:run`. Configurar también `BDIS_DB_URL` si cambia la dirección de la base de datos.

### Frontend: en otro terminal

```bash
cd bdis-web
npm install
npm run dev
```

Puertos predeterminados: `8081` para el backend y `5173` para el servidor de desarrollo frontend. Este redirige `/api` y `/uploads` al backend.

## Compilación y pruebas

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

Resultados: `bdis-web/dist/` y `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`. En Windows, sustituir `./mvnw` por `.\mvnw.cmd`.

## Despliegue y privacidad

En producción, configurar las credenciales de la base de datos y `BDIS_JWT_SECRET`, y mantener persistentes los directorios `BDIS_FEEDBACK_STORAGE_ROOT` y `BDIS_RESUME_STORAGE_ROOT`.

Los directorios privados necesitan permisos de escritura y no deben exponerse como recursos estáticos públicos.

## Documentación detallada

- [Guía completa en chino simplificado](README.md)
- [Guía completa en inglés](README.en.md)
- [Documentación API en chino simplificado](文档/接口文档-基于当前源码.md)
- [Despliegue del buzón en chino simplificado](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## Licencia y contribuciones

Este proyecto se publica bajo la [licencia MIT](LICENSE). Se permiten el uso, la modificación y la distribución, incluidos los fines comerciales, conforme a los términos de la licencia y conservando los avisos de derechos de autor y de permiso. Para contribuir, crear un fork y una rama de funcionalidad, ejecutar las pruebas y abrir una Pull Request hacia main.

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
