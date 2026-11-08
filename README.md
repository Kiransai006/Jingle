# Jingle – Andhra food delivery (3-tier demo app)

A simple Swiggy-style food delivery app built as a base for a full CI/CD + observability pipeline
(Jenkins → Maven → SonarQube → Nexus → Docker → Kubernetes/Helm → Prometheus/Grafana on AWS).

| Tier | Tech | Folder | Port |
|------|------|--------|------|
| Presentation | React 18 (Vite) served by Nginx | `frontend/` | 3000 (dev) / 80 (container) |
| Application | Java 17, Spring Boot 3.3, Maven | `backend/` | 8080 |
| Data | PostgreSQL 16 (H2 in-memory by default for local dev/tests) | — | 5432 |

## Features
- 6 Andhra/Telangana restaurants with 28 dishes (biryani, pesarattu, gongura, pulihora, pootharekulu…), seeded on first start
- Category filters, search, veg-only toggle, bestseller tags, offers
- Cart (one restaurant at a time, like Swiggy), checkout, order confirmation
- Order is saved in the database; ₹30 delivery fee calculated server-side

## Project structure
```
jingle/
├── backend/                     Spring Boot API
│   ├── pom.xml                  JaCoCo, Sonar properties, Nexus distributionManagement
│   ├── Dockerfile               multi-stage (maven build -> JRE 17 alpine, non-root)
│   └── src/
│       ├── main/java/com/jingle/{controller,service,repository,model,dto,config,exception}
│       ├── main/resources/application.yml
│       └── test/java/...        unit (Mockito) + integration (MockMvc/H2) tests
├── frontend/                    React app
│   ├── Dockerfile               multi-stage (node build -> nginx)
│   ├── nginx/default.conf.template   proxies /api -> backend, /healthz probe
│   └── src/{pages,components,utils}
└── docker-compose.yml           full stack locally (postgres + backend + frontend)
```

## Run locally

**Option 1 – Docker Compose (whole stack, uses PostgreSQL)**
```bash
docker compose up --build
# UI:  http://localhost:3000
# API: http://localhost:8080/api/restaurants
```

**Option 2 – without Docker (backend uses in-memory H2)**
```bash
# terminal 1
cd backend && mvn spring-boot:run
# terminal 2
cd frontend && npm install && npm run dev     # http://localhost:3000 (proxies /api to :8080)
```

## Build & test commands (for the Jenkins pipeline)
```bash
# Backend
cd backend
mvn clean verify                       # compile + tests + JaCoCo report (target/site/jacoco/jacoco.xml)
mvn sonar:sonar -Dsonar.host.url=http://<sonar>:9000 -Dsonar.token=<token>
mvn deploy -DskipTests -Dnexus.url=http://<nexus>:8081   # needs <server> ids nexus-releases / nexus-snapshots in settings.xml
docker build -t <registry>/jingle-backend:<tag> .

# Frontend
cd frontend
npm ci && npm test && npm run build
docker build -t <registry>/jingle-frontend:<tag> .
```

## Configuration
Backend (environment variables):

| Variable | Default | Example (K8s) |
|----------|---------|---------------|
| `DB_URL` | `jdbc:h2:mem:jingle` | `jdbc:postgresql://jingle-db:5432/jingle` |
| `DB_USERNAME` | `sa` | `jingle` (from a Secret) |
| `DB_PASSWORD` | empty | from a Secret |
| `SERVER_PORT` | `8080` | |

Frontend container: `BACKEND_HOST` (default `jingle-backend`), `BACKEND_PORT` (default `8080`).
Name your Kubernetes backend Service `jingle-backend` and no change is needed.

## API
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/restaurants?search=` | List / search restaurants |
| GET | `/api/restaurants/{id}` | Restaurant details |
| GET | `/api/restaurants/{id}/menu` | Menu |
| POST | `/api/orders` | Place order `{customerName, phone, address, items:[{menuItemId, quantity}]}` |
| GET | `/api/orders/{id}` / `/api/orders` | Order details / all orders |

## Health checks & monitoring endpoints
| Endpoint | Use |
|----------|-----|
| `/actuator/health/liveness` | K8s livenessProbe (backend) |
| `/actuator/health/readiness` | K8s readinessProbe (backend) |
| `/actuator/prometheus` | Prometheus scrape target (JVM, HTTP, DB pool + custom metrics) |
| `/healthz` | Frontend (nginx) probe |

Custom business metrics for Grafana:
- `jingle_orders_placed_total` – orders counter → `rate(jingle_orders_placed_total[5m])`
- `jingle_order_value_inr_*` – order value summary (count / sum / max)
- Useful built-ins: `http_server_requests_seconds_*`, `jvm_memory_used_bytes`, `hikaricp_connections_active`

Grafana dashboard ideas: JVM (Micrometer) dashboard ID **4701**, Spring Boot dashboard ID **19004**.

## Image credits
Dish photos are loaded at runtime from **Wikimedia Commons** (`en.wikipedia.org/wiki/Special:FilePath/...`)
and are used under their respective Creative Commons licences – see each file's page on
commons.wikimedia.org for author and licence. If an image can't load, the UI shows a styled placeholder.
To use your own images, put them in `frontend/public/images/` and change the `imageUrl` values in
`backend/src/main/java/com/jingle/config/DataSeeder.java` (or upload to an S3 bucket and use those URLs).
