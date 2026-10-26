<div align="center">

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | [繁體中文](README.zh-TW.md) | [日本語](README.ja.md) | **Français** | [Deutsch](README.de.md) | [Español](README.es.md) | [한국어](README.ko.md) | [Русский](README.ru.md) | [Português (Brasil)](README.pt-BR.md)

### Portail officiel et gestion du recrutement

</div>

BDIS Official Website est le portail de Big Data Intelligence Studio. Son architecture sépare le frontend du backend et propose la présentation du studio et de ses recherches, les candidatures avec aperçu PDF, une boîte à suggestions anonyme et une administration des contenus.

## Fonctionnalités

- Actualités, annonces, projets de recherche, équipe, ressources et recherche de contenus.
- Navigation mobile déroulante et résolution des conflits d’ordre d’affichage.
- Formulaires de candidature ou dépôt de documents Word, avec aperçu PDF via LibreOffice ou Docker.
- Envoi de textes et d’images sans connexion ; consultation, filtrage et suivi des statuts par les administrateurs.
- Authentification JWT, gestion des contenus et des candidatures, notifications SMTP.

## Démarrage local

Java 17, Node.js 22.12 ou une version ultérieure compatible, et MySQL sont nécessaires. La conversion PDF nécessite aussi LibreOffice ou l’environnement Docker correspondant.

Importer d’abord le schéma métier existant et ses données, puis appliquer les scripts SQL adaptés au schéma actuel. `init_admin.sql` ne constitue pas une initialisation complète d’une base vide. Sauvegarder les données avant toute migration.

### Backend : Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

Sous Windows PowerShell, définir les variables avec `$env:VARIABLE = "value"` et lancer `.\mvnw.cmd spring-boot:run`. Définir également `BDIS_DB_URL` si l’adresse de la base diffère de la valeur par défaut.

### Frontend : dans un autre terminal

```bash
cd bdis-web
npm install
npm run dev
```

Ports par défaut : `8081` pour le backend et `5173` pour le serveur de développement frontend. Celui-ci transmet `/api` et `/uploads` au backend.

## Compilation et tests

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

Résultats : `bdis-web/dist/` et `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`. Sous Windows, remplacer `./mvnw` par `.\mvnw.cmd`.

## Déploiement et confidentialité

En production, configurer les identifiants de la base et `BDIS_JWT_SECRET`, puis assurer la persistance des répertoires `BDIS_FEEDBACK_STORAGE_ROOT` et `BDIS_RESUME_STORAGE_ROOT`.

Le module de suggestions ne conserve ni identité ni adresse IP de l’expéditeur. Les journaux d’accès du serveur dépendent toutefois de la configuration ; l’impossibilité de traçage n’est pas garantie. Les répertoires privés doivent être accessibles en écriture, sans exposition comme ressources statiques publiques.

## Documentation détaillée

- [Guide complet en chinois simplifié](README.md)
- [Guide complet en anglais](README.en.md)
- [Documentation API en chinois simplifié](文档/接口文档-基于当前源码.md)
- [Déploiement de la boîte à suggestions en chinois simplifié](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## Licence et contributions

Le dépôt ne contient actuellement aucun fichier LICENSE distinct et ne déclare pas de licence Apache-2.0 ou autre licence open source. Confirmer les autorisations auprès des responsables avant utilisation ou redistribution. Pour contribuer, créer un fork et une branche dédiée, exécuter les tests, puis ouvrir une Pull Request vers main.

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
