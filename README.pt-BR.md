<div align="center">

<img src="bdis-web/src/assets/logo.jpg" alt="BDIS Logo" width="480" />

[简体中文](README.md) | [English](README.en.md) | [繁體中文](README.zh-TW.md) | [日本語](README.ja.md) | [Français](README.fr.md) | [Deutsch](README.de.md) | [Español](README.es.md) | [한국어](README.ko.md) | [Русский](README.ru.md) | **Português (Brasil)**

### Portal oficial e gestão de ingresso de membros

</div>

BDIS Official Website é o portal oficial do Big Data Intelligence Studio. Com frontend e backend separados, oferece informações sobre o estúdio e suas pesquisas, inscrições com prévia em PDF, uma caixa de sugestões anônima e gestão de conteúdo.

## Funcionalidades

- Notícias, avisos, projetos de pesquisa, equipe, materiais de estudo e busca.
- Navegação móvel em menu suspenso e resolução de conflitos na ordem de exibição.
- Formulários de inscrição ou envio de documentos Word, com prévia em PDF via LibreOffice ou Docker.
- Envio de texto e imagens sem login; consulta, filtros e atualização do status por administradores.
- Autenticação JWT, gestão de conteúdo e inscrições, e notificações SMTP.

## Execução local

São necessários Java 17, Node.js 22.12 ou uma versão posterior compatível e MySQL. A conversão para PDF também exige LibreOffice ou o ambiente Docker correspondente.

Importar primeiro o esquema de negócio existente e seus dados. Depois, executar os scripts SQL adequados ao esquema atual. `init_admin.sql` não inicializa completamente um banco vazio. Fazer backup antes das migrações.

### Backend: Linux / macOS

```bash
cd bdis-website
export BDIS_DB_USERNAME="root"
export BDIS_DB_PASSWORD="your_password"
export BDIS_JWT_SECRET="replace-with-a-long-random-secret"
./mvnw spring-boot:run
```

No Windows PowerShell, definir variáveis com `$env:VARIABLE = "value"` e executar `.\mvnw.cmd spring-boot:run`. Configurar também `BDIS_DB_URL` se o endereço do banco for diferente do padrão.

### Frontend: em outro terminal

```bash
cd bdis-web
npm install
npm run dev
```

Portas padrão: `8081` para o backend e `5173` para o servidor de desenvolvimento frontend. Este encaminha `/api` e `/uploads` ao backend.

## Compilação e testes

```bash
cd bdis-web
npm run build
cd ../bdis-website
./mvnw test
./mvnw package
```

Resultados: `bdis-web/dist/` e `bdis-website/target/bdis-website-0.0.1-SNAPSHOT.jar`. No Windows, substituir `./mvnw` por `.\mvnw.cmd`.

## Implantação e privacidade

Em produção, configurar as credenciais do banco e `BDIS_JWT_SECRET`, além de garantir armazenamento persistente para os diretórios `BDIS_FEEDBACK_STORAGE_ROOT` e `BDIS_RESUME_STORAGE_ROOT`.

O módulo de sugestões não armazena a identidade nem o IP do remetente. Porém, os registros de acesso do servidor dependem da configuração; não há garantia de impossibilidade de rastreamento. Os diretórios privados precisam de permissão de escrita e não devem ser expostos como recursos estáticos públicos.

## Documentação detalhada

- [Guia completo em chinês simplificado](README.md)
- [Guia completo em inglês](README.en.md)
- [Documentação da API em chinês simplificado](文档/接口文档-基于当前源码.md)
- [Implantação da caixa de sugestões em chinês simplificado](文档/匿名信箱部署说明.md)
- [SQL](bdis-website/sql/)
- [application.yml](bdis-website/src/main/resources/application.yml)

## Licença e contribuições

O repositório ainda não contém um arquivo LICENSE separado e não declara Apache-2.0 ou outra licença de código aberto. Confirmar as permissões com os mantenedores antes de usar ou redistribuir o código. Para contribuir, criar um fork e uma branch de funcionalidade, executar os testes e abrir uma Pull Request para main.

---

<div align="center">

**Big Data Intelligence Studio (BDIS) © 2026 All Rights Reserved.**<br />
Crafted with ❤️ by the BDIS Engineering Team.

</div>
