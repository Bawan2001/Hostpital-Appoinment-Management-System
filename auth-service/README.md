# Authentication Service (`auth-service`)

## 📌 Overview
The **Authentication Service** is responsible for user registration, authentication, BCrypt password hashing, role management (**ADMIN**, **DOCTOR**, **PATIENT**), and issuing signed JWT tokens.

## 🛠️ Tech Stack
- Java 21 & Spring Boot 3.2.3
- Spring Data MongoDB (`auth_db` database)
- Spring Security 6 & BCrypt PasswordEncoder
- JJWT (io.jsonwebtoken 0.12.5)
- Spring Cloud Netflix Eureka Client
- Springdoc OpenAPI (Swagger UI)

## 🔑 Security & Headers
Every request to endpoints in this service must pass the API Key filter:
- **Header**: `X-API-KEY: hospital-internal-secret-key-2026`

## 🚀 Key Endpoints & Examples

### 1. User Registration
- **HTTP Method**: `POST`
- **URL**: `/api/v1/auth/register`
- **Request Body**:
```json
{
  "name": "Dr. Sarah Jenkins",
  "email": "sarah.jenkins@hospital.com",
  "password": "SecurePassword123!",
  "phone": "+1-555-0198",
  "role": "DOCTOR"
}
```
- **Response** (`201 Created`):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": "65c2b1e8a9d1e2f3c4b5a678",
  "name": "Dr. Sarah Jenkins",
  "email": "sarah.jenkins@hospital.com",
  "role": "DOCTOR"
}
```

### 2. User Login
- **HTTP Method**: `POST`
- **URL**: `/api/v1/auth/login`
- **Request Body**:
```json
{
  "email": "sarah.jenkins@hospital.com",
  "password": "SecurePassword123!"
}
```

### 3. Swagger UI Access
- **URL**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
