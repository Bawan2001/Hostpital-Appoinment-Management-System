# Hospital Appointment Management System — Distributed Microservices Architecture

An end-to-end, distributed microservices ecosystem for hospital management built with **Spring Boot 3.2 (Java 21)**, **Spring Cloud API Gateway**, **Netflix Eureka Service Discovery**, **MongoDB**, **PostgreSQL**, and **Docker Containerization**.

---

## 🏛️ System Architecture

```
                    ┌──────────────────────────────────────────────┐
                    │               Client Application             │
                    │        (Web / Mobile / Desktop Frontend)     │
                    └──────────────────────┬───────────────────────┘
                                           │ CORS & REST API (Port 8080)
                                           ▼
                    ┌──────────────────────────────────────────────┐
                    │             Central API Gateway              │
                    │   - OAuth 2.0 / JWT Auth Global Filter       │
                    │   - Rate Limiting (100 req/min per IP)       │
                    │   - Cross-Origin Resource Sharing (CORS)     │
                    │   - Internal X-API-KEY Forwarding            │
                    │   - Aggregated Swagger UI (Port 8080)        │
                    └──────────────────────┬───────────────────────┘
                                           │
                  ┌────────────────────────┼────────────────────────┐
                  │                        │                        │
                  ▼                        ▼                        ▼
       ┌────────────────────┐   ┌────────────────────┐   ┌────────────────────┐
       │   Auth Service     │   │  Patient Service   │   │   Doctor Service   │
       │   (Port 8081)      │   │   (Port 8082)      │   │   (Port 8083)      │
       │ - User Register    │   │ - Patient Profiles │   │ - Doctor Profiles  │
       │ - JWT Generation   │   │ - Medical History  │   │ - Specialties      │
       │ - BCrypt Hashing   │   │ - Enforces API Key │   │ - Enforces API Key │
       └──────────┬─────────┘   └──────────┬─────────┘   └──────────┬─────────┘
                  │                        │                        │
                  ▼                        ▼                        ▼
            MongoDB Database            PostgreSQL               PostgreSQL

                  ┌────────────────────────┴────────────────────────┐
                  │                                                 │
                  ▼                                                 ▼
       ┌────────────────────┐                            ┌────────────────────┐
       │Appointment Service │                            │Notification Service│
       │   (Port 8084)      │                            │   (Port 8085)      │
       │ - Booking Engine   │                            │ - Email / SMS Logs │
       │ - Enforces API Key │                            │ - Enforces API Key │
       └──────────┬─────────┘                            └──────────┬─────────┘
                  │                                                 │
                  ▼                                                 ▼
             PostgreSQL                                        MongoDB

                                           ▲
                                           │ Service Registration
                    ┌──────────────────────┴───────────────────────┐
                    │            Netflix Eureka Server             │
                    │                 (Port 8761)                  │
                    └──────────────────────────────────────────────┘
```

---

## 👥 Work Breakdown Matrix

| Student Name | Role | Microservice Name | Key Responsibilities & Endpoints |
|---|---|---|---|
| **Student 1 (Gateway Lead)** | Member / Gateway Lead | **API Gateway & Auth Service** | OAuth 2.0 / JWT Filter, CORS, Rate Limiting, Internal API Key forwarding. Auth Endpoints: `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/validate`, `/api/v1/auth/user/{id}`. |
| **Student 2** | Member | **Patient Service** | Patient management & medical records. Endpoints: `/api/v1/patients`, `/api/v1/patients/{id}`, `/api/v1/patients/{id}/medical-history`. |
| **Student 3** | Member | **Doctor Service** | Doctor catalog & availability. Endpoints: `/api/v1/doctors`, `/api/v1/doctors/{id}`, `/api/v1/doctors/specialty/{specialty}`. |
| **Student 4** | Member | **Appointment Service** | Appointment scheduling & status updates. Endpoints: `/api/v1/appointments`, `/api/v1/appointments/{id}`, `/api/v1/appointments/cancel/{id}`. |
| **Student 5** | Member | **Notification Service** | Email/SMS alerts and history log. Endpoints: `/api/v1/notifications/send`, `/api/v1/notifications/user/{userId}`. |

---

## 🚀 Running the System via Docker

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Version 24.0+)
- [Git](https://git-scm.com/)

### Single-Command Setup
To build and start all microservices, API Gateway, Eureka Server, MongoDB, and PostgreSQL containers:

```bash
docker compose up --build -d
```

To stop all services and tear down containers:
```bash
docker compose down -v
```

---

## 🔑 Authentication & API Key Headers

### 1. External User Authentication (OAuth 2.0 / JWT)
- **Login Request**: `POST http://localhost:8080/api/v1/auth/login`
- **Register Request**: `POST http://localhost:8080/api/v1/auth/register`
- **Header Format**: `Authorization: Bearer <JWT_TOKEN>`

#### Demo Credentials
| Role | Email | Password |
|---|---|---|
| Admin | `admin@hospital.com` | `admin123` |
| Doctor | `doctor@hospital.com` | `doctor123` |
| Patient | `patient@hospital.com` | `patient123` |

### 2. Internal Microservice Security (API Key)
Every individual microservice enforces direct request protection using an API Key filter.
- **Header Name**: `X-API-KEY`
- **Header Value**: `hospital-internal-secret-key-2026`

*Note: The Central API Gateway automatically injects this header into all forwarded requests.*

---

## 📘 Interactive API Documentation (Swagger UI)

| Service | Swagger UI Access URL |
|---|---|
| **Central API Gateway (Aggregated)** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **Auth Service** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| **Patient Service** | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| **Doctor Service** | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |
| **Appointment Service** | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) |
| **Notification Service** | [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html) |
| **Eureka Server Dashboard** | [http://localhost:8761](http://localhost:8761) |

---

## 🛡️ Security & Infrastructure Specifications
1. **OAuth 2.0 / JWT Authentication**: Secured via HMAC-SHA256 signature token issuance with 24-hour expiration window.
2. **Rate Limiting**: Throttled per client IP at **100 requests per minute** returning HTTP `429 Too Many Requests`.
3. **CORS Configuration**: Wildcard port resolution for modern single-page applications (React, Angular, Vue, Flutter).
