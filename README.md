<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![TypeScript](https://img.shields.io/badge/TypeScript-5.2.2-blue.svg)
![Kotlin](https://img.shields.io/badge/kotlin-2.2.1-purple.svg)
![React](https://img.shields.io/badge/React-18.2.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Supabase](https://img.shields.io/badge/Supabase-3.0.0-blue.svg)
![Render](https://img.shields.io/badge/Render-2023.2.0-blue.svg)
![Backend CI](https://github.com/devcavin/gatelog/actions/workflows/backend-ci.yaml/badge.svg)
![Frontend CI](https://github.com/devcavin/gatelog/actions/workflows/frontend-ci.yaml/badge.svg)
![Deploy to Render](https://github.com/devcavin/gatelog/actions/workflows/deploy-render.yaml/badge.svg)
![GitHub Repo Size](https://img.shields.io/github/repo-size/devcavin/gatelog?color=blue)
![Stars](https://img.shields.io/github/stars/devcavin/gatelog?color=yellow)

</div>

# Gatelog - Digital Visitor Management System

> A production-grade visitor management system built to replace paper logbooks with a fast, searchable, and accountable web application.
---

## What is Gatelog?

Gatelog is a digital visitor management system designed for any single-site premises, a corporate office, a clinic, a school, or a residential estate. It replaces paper-based visitor logbooks with a structured, role-aware, and cloud-deployable web application.

The project is built to demonstrate end-to-end product engineering: system design, backend API development, cloud deployment, CI/CD, and security-conscious development practices.

---

## Live Demo
- Frontend - [https://gatelogapp.vercel.app](https://gatelogapp.vercel.app)

---

## Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| Backend    | Kotlin + Spring Boot 3            |
| Database   | PostgreSQL (Supabase)             |
| Auth       | Spring Security + JWT             |
| ORM        | Spring Data JPA / Hibernate       |
| Migrations | Flyway                            |
| Docs       | SpringDoc OpenAPI / Swagger UI    |
| CI/CD      | GitHub Actions + GHCR             |
| Deployment | Render (compute) + Supabase (db)  |

---

## Architecture

```
┌─────────────────────────────────────────┐
│              React Frontend             │  ← Phase 2
│         (TypeScript + Vite)             │
└────────────────────┬────────────────────┘
                     │ HTTPS / REST
┌────────────────────▼────────────────────┐
│           Spring Boot Backend           │
│                                         │
│  ┌─────────────┐   ┌─────────────────┐  │
│  │ Controllers │   │ Security Filter │  │
│  └──────┬──────┘   └────────┬────────┘  │
│         │                   │ JWT        │
│  ┌──────▼───────────────────▼────────┐  │
│  │           Service Layer           │  │
│  └──────────────────┬────────────────┘  │
│                     │                   │
│  ┌──────────────────▼────────────────┐  │
│  │       Spring Data JPA / Flyway    │  │
│  └──────────────────┬────────────────┘  │
└─────────────────────┼───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│         PostgreSQL (Supabase)           │
└─────────────────────────────────────────┘
```

```
                        GATELOG

                    Authentication
                           │
                           ▼
                    Authorization
                           │
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
      Users            Visitors             Sites
                           │                  │
                           ▼                  ▼
                         Zones            Dashboard
                           │                  │
                           └──────────┬───────┘
                                      ▼
                                   Reports
```

---

## Data Model

```
roles ──────────────────────────► users
visit_statuses ─────────────────► visitors
sites ──────────────────────────► zones
                                  users
                                  visitors
zones ──────────────────────────► visitors (nullable)
users ──────────────────────────► visitors (created_by)
                                  refresh_tokens
```

All primary keys are UUID. Enums are replaced with lookup tables (`roles`, `visit_statuses`) for clean migrations and audit flexibility.

---

## Project Structure

```
gatelog/
├── backend/
│   ├── src/main/kotlin/io/github/devcavin/backend/
│   │   ├── common/exception/      # domain exception hierarchy
│   │   ├── config/                # security, CORS config
│   │   ├── domain/
│   │   │   ├── model/             # JPA entities
│   │   │   └── repository/        # Spring Data repositories
│   │   ├── security/              # JWT provider, filter, UserDetails
│   │   ├── service/               # business logic
│   │   └── web/
│   │       ├── controller/        # REST controllers
│   │       ├── dto/               # request/response DTOs
│   │       └── error/             # global exception handler
│   ├── src/main/resources/
│   │   ├── db/migration/          # Flyway migrations v1–v7
│   │   ├── application.yaml       # shared config (env var placeholders)
│   │   └── application-local.yaml # local dev overrides (gitignored)
│   ├── Dockerfile                 # multi-stage build
│   └── compose.yaml               # local Postgres via Docker
├── .github/workflows/
│   └── ci.yml                     # test → build image → push to GHCR
└── README.md
```

---

## API Endpoints

### Auth — `/api/auth`

| Method | Endpoint          | Auth     | Description                  |
|--------|-------------------|----------|------------------------------|
| POST   | `/login`          | Public   | Login, returns JWT pair      |
| POST   | `/refresh`        | Public   | Rotate refresh token         |
| POST   | `/logout`         | Public   | Invalidate refresh token     |

### Users — `/api/users`

| Method | Endpoint | Auth                      | Description          |
|--------|----------|---------------------------|----------------------|
| POST   | `/`      | SUPER_ADMIN, MANAGER      | Create a user        |

> Managers can only create Staff accounts scoped to their own site.
> Super Admin can create any role at any site.

### Visitors — `/api/visitors` _(in progress)_

| Method | Endpoint              | Auth                       | Description                    |
|--------|-----------------------|----------------------------|--------------------------------|
| POST   | `/`                   | All roles                  | Register a new visitor         |
| GET    | `/{id}`               | All roles                  | Get visitor by ID              |
| PATCH  | `/{id}/checkout`      | All roles                  | Check out a visitor            |
| GET    | `/`                   | All roles                  | Search visitors (paginated)    |
| GET    | `/returning`          | All roles                  | Returning visitor phone lookup |

---

## Role-Based Access Control

| Role        | Capabilities                                                          |
|-------------|-----------------------------------------------------------------------|
| SUPER_ADMIN | Full access, users, config, all analytics, all visitor records        |
| MANAGER     | Reports, analytics, visitor records, create Staff users at own site   |
| STAFF       | Register visitors, check in/out, view active visitors, search history |

Role enforcement happens at two layers: `@PreAuthorize` on controllers (HTTP level) and explicit business rules in the service layer (domain level).

---

## Running Locally

**Prerequisites:** Docker, JDK 21, Git

```bash
# 1. clone
git clone https://github.com/devcavin/gatelog.git
cd gatelog/backend

# 2. start postgres
docker compose up -d

# 3. run the app (local profile auto-activates)
SPRING_PROFILES_ACTIVE=local \
DB_URL=jdbc:postgresql://localhost:5432/gatelog \
DB_USERNAME=postgres \
DB_PASSWORD=secret \
JWT_SECRET=local-dev-secret-key-must-be-at-least-256-bits-long-for-hs256 \
CORS_ALLOWED_ORIGINS=http://localhost:5173 \
./gradlew bootRun

# 4. open swagger
open http://localhost:8080/swagger-ui/index.html
```

Flyway runs all migrations automatically on startup. The database is seeded with roles, visit statuses, a default site, and a Super Admin account.

---

## Environment Variables

| Variable                  | Description                                | Required |
|---------------------------|--------------------------------------------|----------|
| `DB_URL`                  | JDBC connection URL                        | Yes      |
| `DB_USERNAME`             | Database username                          | Yes      |
| `DB_PASSWORD`             | Database password                          | Yes      |
| `JWT_SECRET`              | HS256 signing key (min 256 bits)           | Yes      |
| `JWT_ACCESS_EXPIRY_MS`    | Access token expiry in ms (default 900000) | No       |
| `JWT_REFRESH_EXPIRY_DAYS` | Refresh token expiry in days (default 7)   | No       |
| `CORS_ALLOWED_ORIGINS`    | Allowed frontend origins                   | Yes      |
| `SPRING_PROFILES_ACTIVE`  | Active Spring profile                      | No       |

---

## CI/CD

Every push to `main` that touches `backend/**`:

1. **Test** - runs the full test suite with Testcontainers (real Postgres)
2. **Build image** - multi-stage Docker build, pushed to GHCR with SHA tag and `latest`
3. **Deploy image** - deploying the build image to render

```
ghcr.io/devcavin/gatelog-backend:latest
ghcr.io/devcavin/gatelog-backend:<short-sha>
```

---

## Roadmap

### Phase 1 - Core System _(in progress)_
- [x] Database schema and Flyway migrations
- [x] JWT authentication with refresh token rotation
- [x] Role-based access control (Super Admin, Manager, Staff)
- [x] User registration endpoint with role enforcement
- [x] CI pipeline with GitHub Actions
- [x] Visitor registration and check-in/check-out
- [x] Real-time dashboard endpoints
- [x] Visitor search with dynamic filters
- [x] CSV export
- [ ] React frontend(in progress)

### Phase 2 - Intelligence Layer _(planned)_
- [ ] SMS OTP verification (Africa's Talking / Twilio)
- [ ] Analytics dashboard
- [ ] Zone routing and host notifications
- [ ] PDF export
- [ ] Scheduled report delivery
<!-- - [ ] Kubernetes deployment with Helm
- [ ] Observability with Prometheus and Grafana -->

---

## Architecture Decisions

[//]: # (**Why Kotlin over Java?** Kotlin's null safety, data classes, and expression-based syntax produce cleaner, more readable code with less boilerplate. Full Spring Boot compatibility means no ecosystem tradeoffs.)

**Why lookup tables over enums?** PostgreSQL enums cannot have values removed without recreating the type, making migrations fragile. Lookup tables (`roles`, `visit_statuses`) keep migrations clean and make audit queries straightforward.

**Why no offline mode?** Cloud hosting is a deliberate architectural choice. The system trades offline capability for real-time data availability, automated backups, and searchability, none of which paper can provide.

**Why layered monolith over Spring Modulith?** For a single-developer MVP, a clean layered monolith ships faster and is more familiar to most engineering teams reviewing the code. Spring Modulith is the right next step if the domain grows and team size increases, that migration path is documented and the package structure supports it without a full rewrite.

---

[//]: # (## License)

[//]: # ()
[//]: # (MIT — see [LICENSE]&#40;LICENSE&#41;)