# Backend Development Workflow

## Overview
This document defines the backend workflow for CargoFlow.

## Workflow
### 1. Database Setup
- Create the PostgreSQL database
- Apply the base schema
- Verify schema consistency with the ERD

### 2. Layered Architecture
- Keep controllers thin
- Put business rules in services
- Use repositories for database access

### 3. API Design and Testing
- Define REST endpoints for cargo, shipments, vendors, and routes
- Test requests with Postman or cURL
- Add automated tests for critical service and controller logic

### 4. Frontend Integration
- Connect the Vue frontend to the backend API
- Verify request and response flows end to end
- Keep CORS aligned with the deployed frontend domain

### 5. Advanced Features
- Add vendor assignment and shipment status workflows
- Keep validation and error handling consistent
- Use indexes and pagination where needed

### 6. Deployment
- Frontend: Vercel
- Backend: Render
- Kafka: Aiven
- Database: PostgreSQL

## Reference Files
- [ARCHITECTURE.md](../ARCHITECTURE.md)
- [backend_hld_details.md](backend_hld_details.md)
- [schema_refinements.md](schema_refinements.md)
- [lld_api_outline.md](lld_api_outline.md)
- [lld_db_queries.md](lld_db_queries.md)
