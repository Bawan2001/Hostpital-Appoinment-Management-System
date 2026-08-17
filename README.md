# Distributed Hospital Appointment Management System
## IT41073 - Service-Oriented Computing Mini Project Report

An enterprise-grade, distributed microservices system featuring **Spring Boot 3.2 (Java 21)** microservices, **Spring Cloud API Gateway**, **Netflix Eureka Service Discovery**, **OAuth 2.0 / JWT Security**, **Bucket4j Rate Limiting**, **Internal API Key Verification (`X-API-KEY`)**, **MongoDB**, **Docker Compose Orchestration**, and a **Unified Client SPA Application**.

---

## 👥 Student Work Breakdown Matrix

| Student Name | Student Index | Microservice / Role | Key Endpoints & Responsibilities |
|---|---|---|---|
| **D.M.B. Rukmal Dissanayaka** | `ITBIN-2313-0029` | **API Gateway, User & Auth Service, Eureka Discovery Server, Client SPA App, Docker Orchestration** | Gateway Dynamic Routing, OAuth 2.0 ROPC (`POST /api/v1/auth/oauth/token`), JWT Auth Global Filter, CORS, Bucket4j Rate Limiting (10 req/min/IP), User Register (`POST /api/v1/auth/register`), Login (`POST /api/v1/auth/login`), Token Validate (`GET /api/v1/auth/validate`), Profile (`GET /api/v1/auth/user/{id}`). |
| **H.M. Imashi Dilshani** | `ITBIN-2313-0025` | **Patient Management Microservice** | Patient Profile Registration (`POST /api/v1/patients`), Retrieve All Patients (`GET /api/v1/patients`), Patient Lookup by ID (`GET /api/v1/patients/{id}`), User Search (`GET /api/v1/patients/user/{userId}`), Update Record (`PUT /api/v1/patients/{id}`), Medical History Records (`POST /api/v1/patients/{id}/medical-history`), Delete Record (`DELETE /api/v1/patients/{id}`). |
| **J.W.A. Indumini Adarshya** | `ITBIN-2313-0004` | **Doctor Management Microservice** | Doctor Profile Creation (`POST /api/v1/doctors`), Retrieve All Doctors (`GET /api/v1/doctors`), Doctor Lookup (`GET /api/v1/doctors/{id}`), Specialty Search (`GET /api/v1/doctors/specialty/{specialty}`), Available Doctors (`GET /api/v1/doctors/available`), Availability Toggle (`PUT /api/v1/doctors/{id}/availability`), Update Profile (`PUT /api/v1/doctors/{id}`). |
| **G. Rashmi Dulashani** | `ITBIN-2313-0031` | **Appointment Booking Microservice** | Appointment Booking (`POST /api/v1/appointments`), Retrieve All Appointments (`GET /api/v1/appointments`), Patient Appointments (`GET /api/v1/appointments/patient/{patientId}`), Doctor Appointments (`GET /api/v1/appointments/doctor/{doctorId}`), Date Filter (`GET /api/v1/appointments/date/{date}`), Status Sync (`PUT /api/v1/appointments/{id}/status`), Cancel Booking (`PUT /api/v1/appointments/{id}/cancel`), Auto-notify patient on booking/cancel. |
| **T.H. Imalsha Dilshani** | `ITBIN-2313-0027` | **Notification Microservice** | Email Dispatch (`POST /api/v1/notifications/email`), SMS Alert (`POST /api/v1/notifications/sms`), User Notification History (`GET /api/v1/notifications/user/{userId}`), Unread Alerts (`GET /api/v1/notifications/user/{userId}/unread`), Mark As Read (`PUT /api/v1/notifications/{id}/read`), Delete Alert (`DELETE /api/v1/notifications/{id}`). |

---

## 🏛️ Comprehensive System Architecture

```
                                  ┌─────────────────────────────────────────────────────────────┐
                                  │            Unified Client Application (SPA)                 │
                                  │               (Web Frontend on Port 3000)                   │
                                  └──────────────────────────────┬──────────────────────────────┘
                                                                 │ CORS & REST Calls (Port 8080)
                                                                 ▼
                                  ┌─────────────────────────────────────────────────────────────┐
                                  │                   Central API Gateway                       │
                                  │   - OAuth 2.0 / JWT Global Security Filter                  │
                                  │   - Rate Limiter Filter (10 req/min/IP)                     │
                                  │   - Gateway CORS Policy (localhost:3000, localhost:5173)    │
                                  │   - Forwarding Header: X-API-KEY: hospital-internal-...     │
                                  │   - Aggregated Swagger UI (Port 8080)                       │
                                  └───────┬──────────────┬──────────────┬──────────────┬────────┘
                                          │              │              │              │
                    ┌─────────────────────┘              │              │              └─────────────────────┐
                    │                                    │              │                                    │
                    ▼                                    ▼              ▼                                    ▼
       ┌────────────────────────┐           ┌──────────────────┐  ┌────────────────────┐           ┌────────────────────────┐
       │   1. Auth Service      │           │2. Doctor Service │  │3. Patient Service  │           │4. Notification Service │
       │      (Port 8081)       │           │   (Port 8083)    │  │   (Port 8082)      │           │      (Port 8085)       │
       │ - /api/v1/auth/*       │           │ - /api/v1/doc... │  │ - /api/v1/pati...  │           │ - /api/v1/notifi...    │
       │ - User Registration    │           │ - Doctor Profiles│  │ - Patient Records  │           │ - Email & SMS Alerts   │
       │ - OAuth 2.0 ROPC Token │           │ - Specialties    │  │ - Medical History  │           │ - Notification Logs    │
       │ - JWT & BCrypt Auth    │           │ - Availability   │  │ - Blood Group & DOB│           │ - Read Status Tracking │
       └───────────┬────────────┘           └────────┬─────────┘  └─────────┬──────────┘           └───────────┬────────────┘
                   │                                 │                      │                                  │
                   │                                 │     ┌────────────────┘                                  │
                   │                                 │     │                                                   │
                   └─────────────────────────┐       │     │       ┌───────────────────────────────────────────┘
                                             ▼       ▼     ▼       ▼
                                      ┌────────────────────────────────────────────────┐
                                      │             5. Appointment Service             │
                                      │                   (Port 8084)                  │
                                      │ - /api/v1/appointments/*                       │
                                      │ - Booking, Doctor/Patient Filter, Status Sync  │
                                      └──────────────────────┬─────────────────────────┘
                                                             │
                                                             ▼
                                                MongoDB Database (Port 27017)
                                                (auth_db, doctor_db, patient_db,
                                                 appointment_db, notification_db)

                                                             ▲
                                                             │ Service Registration
                                      ┌──────────────────────┴─────────────────────────┐
                                      │             Netflix Eureka Server              │
                                      │                  (Port 8761)                   │
                                      └────────────────────────────────────────────────┘
```

---

## 🚀 Quick Start Guide (Docker Compose)

### Single-Command Setup
Build and launch all 5 microservices, API Gateway, Eureka Server, MongoDB, and SPA Client:

```bash
docker compose up --build -d
```

### Verification
```bash
docker compose ps
```

### Tear Down
```bash
docker compose down -v
```

---

## 📘 Interactive API Documentation & Dashboards

| Service / Tool | URL | Description |
|---|---|---|
| **Central API Gateway Aggregated Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interactive OpenAPI 3.0 documentation for ALL 5 microservices in one unified UI |
| **Unified Client SPA Application** | [http://localhost:3000](http://localhost:3000) | Single-page web dashboard actively consuming all microservices |
| **Eureka Discovery Console** | [http://localhost:8761](http://localhost:8761) | Real-time registry of all active Spring Boot microservice instances |
| **Auth Service Direct Swagger** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Direct documentation for Auth Service |
| **Patient Service Direct Swagger** | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | Direct documentation for Patient Service |
| **Doctor Service Direct Swagger** | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | Direct documentation for Doctor Service |
| **Appointment Service Direct Swagger** | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) | Direct documentation for Appointment Service |
| **Notification Service Direct Swagger** | [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html) | Direct documentation for Notification Service |

---

## 🔑 Security Architecture

### 1. OAuth 2.0 / JWT Authentication
- **Grant Type**: Resource Owner Password Credentials (ROPC) grant (RFC 6749 Section 4.3).
- **Token Endpoint**: `POST http://localhost:8080/api/v1/auth/oauth/token`
- **Default Seed Accounts**:
  - `admin@hospital.com` / `admin123` (ADMIN)
  - `doctor@hospital.com` / `doctor123` (DOCTOR)
  - `patient@hospital.com` / `patient123` (PATIENT)

### 2. Internal API Key Protection
All downstream microservices enforce `ApiKeyAuthenticationFilter` checking for:
- Header: `X-API-KEY: <Configured in .env as INTERNAL_API_KEY>`
- Requests bypassing the API Gateway without a valid internal API key are rejected with `HTTP 401 Unauthorized`.

### 3. Rate Limiting
- Enforced at Gateway level using `Bucket4j`.
- **Limit**: 10 requests per minute per IP address. Exceeded requests return `HTTP 429 Too Many Requests`.

---

## ⚙️ Environment Configuration

Before launching the application, copy the example environment template:

```bash
cp .env.example .env
```

You can customize the secret keys and ports in `.env`:
- `INTERNAL_API_KEY`: Secret key shared between API Gateway and downstream microservices.
- `JWT_SECRET`: Secret key used for signing and validating HMAC-SHA256 JWT tokens.
- `MONGO_PORT`: Exposed host port for MongoDB database (e.g. `27019`).

