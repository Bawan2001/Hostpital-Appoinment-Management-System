# 📅 Appointment Service (Student 4)

Appointment Booking and Scheduling Microservice for the Hospital Appointment Management System.

## 🚀 Service Specifications
- **Port**: `8084`
- **Database**: MongoDB (`appointment_db`, collection: `appointments`)
- **Eureka Service Name**: `appointment-service`
- **Gateway Route Prefix**: `/api/v1/appointments/**`

## 🔑 Security & API Key
Every request must include the internal API key header when calling directly or routed via Gateway:
- Header: `X-API-KEY: hospital-internal-secret-key-2026`

## 📌 Endpoints
- `POST /api/v1/appointments` — Book a new appointment
- `GET /api/v1/appointments` — Get all appointments
- `GET /api/v1/appointments/{id}` — Get appointment by ID
- `GET /api/v1/appointments/patient/{patientId}` — Get appointments for a patient
- `GET /api/v1/appointments/doctor/{doctorId}` — Get appointments for a doctor
- `GET /api/v1/appointments/date/{date}` — Filter by date (e.g. `2026-08-20`)
- `PUT /api/v1/appointments/{id}/status` — Update status (`SCHEDULED`, `COMPLETED`, `CANCELLED`)
- `PUT /api/v1/appointments/{id}/cancel` — Cancel appointment
- `DELETE /api/v1/appointments/{id}` — Delete appointment

## 🛠️ How to Run
```bash
mvn clean spring-boot:run
```
Swagger UI: `http://localhost:8084/swagger-ui.html`

## 📤 How to Push to GitHub
```bash
git checkout -b feature/appointment-service
git add .
git commit -m "feat(appointment): implement Appointment Service with MongoDB"
git push -u origin feature/appointment-service
```
