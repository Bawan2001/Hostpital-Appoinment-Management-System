# Hospital Appointment Management System — Student 1 (Gateway Lead & User/Auth Service)

An end-to-end distributed microservices module for **API Gateway**, **User & Auth Service**, **Service Discovery**, **OAuth 2.0 / Rate Limiting**, and **SPA Client App**, built with **Spring Boot 3.2 (Java 21)**, **Spring Cloud API Gateway**, **Netflix Eureka Service Discovery**, **MongoDB**, and **Docker Containerization**.

---

## 🏛️ System Architecture (Student 1 Component Scope)

```
                    ┌──────────────────────────────────────────────┐
                    │               Client Application             │
                    │        (Web Frontend SPA on Port 3000)       │
                    └──────────────────────┬───────────────────────┘
                                           │ CORS & REST API (Port 8080)
                                           ▼
                    ┌──────────────────────────────────────────────┐
                    │             Central API Gateway              │
                    │   - OAuth 2.0 / JWT Auth Global Filter       │
                    │   - Bucket4j Rate Limiting (10 req/min/IP)   │
                    │   - Cross-Origin Resource Sharing (CORS)     │
                    │   - Internal X-API-KEY Forwarding            │
                    │   - Aggregated Swagger UI (Port 8080)        │
                    └──────────────────────┬───────────────────────┘
                                           │ Service Routing
                                           ▼
                                ┌────────────────────┐
                                │   Auth Service     │
                                │   (Port 8081)      │
                                │ - User Register    │
                                │ - OAuth 2.0 Token  │
                                │ - JWT Generation   │
                                │ - BCrypt Hashing   │
                                └──────────┬─────────┘
                                           │
                                           ▼
                                     MongoDB Database (Port 27017)

                                           ▲
                                           │ Service Registration
                    ┌──────────────────────┴───────────────────────┐
                    │            Netflix Eureka Server             │
                    │                 (Port 8761)                  │
                    └──────────────────────────────────────────────┘
```

---

## 👥 Student Role & Component Ownership

| Student Name | Role | Microservice / Components | Key Endpoints & Technical Features |
|---|---|---|---|
| **Student 1 (Gateway Lead)** | Member / Gateway Lead | **API Gateway, Auth Service, Eureka Server, Client App, Docker Compose** | OAuth 2.0 ROPC (`/api/v1/auth/oauth/token`), `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/validate`, `/api/v1/auth/user/{id}`, JWT Auth Global Filter, CORS, Rate Limiting (10 req/min), Internal API Key forwarding. |

---

## 🚀 Running the System via Docker

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Version 24.0+)
- [Git](https://git-scm.com/)

### Single-Command Setup
To build and start all Student 1 services (`api-gateway`, `auth-service`, `eureka-server`, `mongodb`, `client-app`):

```bash
docker compose up --build -d
```

To stop all services and tear down containers:
```bash
docker compose down -v
```

---

## 🔑 Authentication & API Gateway Endpoints

### 1. User & Auth Service Endpoints
- **Register User**: `POST http://localhost:8080/api/v1/auth/register`
- **Login & Get JWT**: `POST http://localhost:8080/api/v1/auth/login`
- **Validate JWT Signature**: `GET http://localhost:8080/api/v1/auth/validate?token=<JWT_TOKEN>`
- **Get User Profile**: `GET http://localhost:8080/api/v1/auth/user/{id}`
- **OAuth 2.0 Token Endpoint**: `POST http://localhost:8080/api/v1/auth/oauth/token`

#### OAuth 2.0 ROPC Token Request (RFC 6749)
```json
{
  "grant_type": "password",
  "username": "admin@hospital.com",
  "password": "admin123"
}
```

#### OAuth 2.0 Token Response
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "token_type": "Bearer",
  "expires_in": 86400,
  "id": "65d0a12b...",
  "name": "Admin User",
  "email": "admin@hospital.com",
  "role": "ADMIN"
}
```

---

## 📘 Interactive API Documentation & Dashboards

| Component | URL |
|---|---|
| **Central API Gateway (Aggregated Swagger UI)** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **Auth Service Direct Swagger UI** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| **Eureka Service Discovery Console** | [http://localhost:8761](http://localhost:8761) |
| **Client Application SPA UI** | [http://localhost:3000](http://localhost:3000) |

---

## 🛡️ Security & Infrastructure Specifications
1. **OAuth 2.0 / JWT Authentication**: Implements OAuth 2.0 Resource Owner Password Credentials (ROPC) grant (RFC 6749 Section 4.3) with HMAC-SHA256 JWT token issuance and 24-hour expiration window.
2. **Rate Limiting**: Throttled per client IP at **10 requests per minute** returning HTTP `429 Too Many Requests`.
3. **CORS Configuration**: Gateway-level CORS configuration allowing cross-origin calls from `localhost:3000` and `localhost:5173`.
