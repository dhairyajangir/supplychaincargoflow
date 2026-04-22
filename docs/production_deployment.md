# Production Deployment Guide

## Overview
CargoFlow is deployed with the following production stack:
- Frontend: Vercel
- Backend: Render
- Event Bus: Aiven Kafka
- Database: PostgreSQL

## Required Environment Values
### Frontend
- `VITE_API_BASE_URL`: public Render API base URL
- `VITE_API_BASE_URL` must use `https://` in production

### Backend
- `PORT`: provided by Render
- `DATABASE_URL`: PostgreSQL connection string or equivalent datasource values
- `KAFKA_BOOTSTRAP_SERVERS`: Aiven Kafka bootstrap servers
- `FRONTEND_URL`: canonical frontend URL (for OAuth failure redirect)
- `CORS_ALLOWED_ORIGINS`: comma-separated exact frontend origins
- `OAUTH_ALLOWED_REDIRECT_ORIGINS`: comma-separated exact OAuth redirect origins
- `SESSION_COOKIE_SAME_SITE`: `None` for cross-site frontend/backend
- `SESSION_COOKIE_SECURE`: `true` in production
- OAuth client credentials
- Mail credentials if notifications are enabled

## Frontend Deployment
1. Configure the production API URL in the Vercel environment variables.
2. Build and deploy the Vue application.
3. Confirm the frontend can reach the backend API over HTTPS.

## Backend Deployment
1. Set the backend environment variables in Render.
2. Connect the backend to PostgreSQL and Aiven Kafka.
3. Deploy the Spring Boot service.
4. Verify startup logs and health checks.
5. Verify CSRF cookie (`XSRF-TOKEN`) is issued and unsafe API requests send `X-XSRF-TOKEN`.
6. Verify all `/api/**` routes require authentication except auth signup/login/user/csrf endpoints.

## Database Deployment
1. Provision PostgreSQL.
2. Apply the schema.
3. Verify migrations or initialization scripts before enabling traffic.

## Kafka Deployment
1. Provision the Aiven Kafka service.
2. Create the required topics.
3. Set the bootstrap servers in the backend environment.
4. Confirm producer and consumer connectivity.

## Verification Checklist
- Frontend loads successfully from Vercel
- Backend responds from Render
- Kafka events are published and consumed
- Database connections succeed
- OAuth login redirects to the deployed frontend
- CORS allowed origins are exact deployed domains
- Notification endpoint access is restricted to admins
- Admin approval APIs are accessible only to admin users

---