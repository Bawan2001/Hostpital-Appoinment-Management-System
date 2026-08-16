# 👤 Patient Service (Student 2)

Patient Management Microservice for the Hospital Appointment Management System.

## 🚀 Service Specifications
- **Port**: `8082`
- **Database**: MongoDB (`patient_db`, collection: `patients`)
- **Eureka Service Name**: `patient-service`
- **Gateway Route Prefix**: `/api/v1/patients/**`

## 🔑 Security & API Key
Every request must include the internal API key header when calling directly or routed via Gateway:
- Header: `X-API-KEY: hospital-internal-secret-key-2026`

## 📌 Endpoints
- `POST /api/v1/patients` — Register a patient
- `GET /api/v1/patients/{id}` — Get patient by ID
- `GET /api/v1/patients/user/{userId}` — Get patient by Auth User ID
- `GET /api/v1/patients` — Get all patients
- `PUT /api/v1/patients/{id}` — Update patient
- `DELETE /api/v1/patients/{id}` — Delete patient
- `POST /api/v1/patients/{id}/medical-history` — Add medical record note

## 🛠️ How to Run
```bash
mvn clean spring-boot:run
```
Swagger UI: `http://localhost:8082/swagger-ui.html`


