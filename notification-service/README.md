# 🔔 Notification Service (Student 5)

Email & SMS Notification Microservice for the Hospital Appointment Management System.

## 🚀 Service Specifications
- **Port**: `8085`
- **Database**: MongoDB (`notification_db`, collection: `notifications`)
- **Eureka Service Name**: `notification-service`
- **Gateway Route Prefix**: `/api/v1/notifications/**`

## 🔑 Security & API Key
Every direct request must include the internal API key header when calling directly or routed via Gateway:
- Header: `X-API-KEY: <INTERNAL_API_KEY>` (Configured via environment / .env)

## 📌 Endpoints
- `POST /api/v1/notifications/email` — Send email alert
- `POST /api/v1/notifications/sms` — Send SMS alert
- `GET /api/v1/notifications/user/{userId}` — Get notification history for a user
- `GET /api/v1/notifications/user/{userId}/unread` — Get unread notifications for a user
- `GET /api/v1/notifications` — Get all notifications (Admin)
- `GET /api/v1/notifications/{id}` — Get notification by ID
- `PUT /api/v1/notifications/{id}/read` — Mark as read
- `DELETE /api/v1/notifications/{id}` — Delete notification

## 🛠️ How to Run
```bash
mvn clean spring-boot:run
```
Swagger UI: `http://localhost:8085/swagger-ui.html`

 
