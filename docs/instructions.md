# Project Instructions

This document defines the working expectations for the cargo management project.

## Project Scope
- Build a supply chain cargo management system
- Cover cargo, shipment, route, and vendor workflows
- Support backend APIs, frontend views, and documentation

## Technical Expectations
### Backend
- Use Spring Boot
- Keep controllers thin
- Put business logic in services
- Use repositories for persistence

### Frontend
- Use Vue.js
- Keep routing centralized
- Reuse shared components where possible

### Database
- Maintain schema consistency with the ERD
- Use constraints and indexes where appropriate

## Documentation References
- `docs/backend_hld_details.md`
- `docs/lld_api_outline.md`
- `docs/lld_db_queries.md`
- `docs/schema_refinements.md`
- `docs/ui_wireframes.md`
- `docs/ui_high_fid.md`

## Working Rules
- Keep the codebase consistent with the documented architecture
- Update docs when behavior changes
- Prefer clarity over over-optimization
- Avoid duplicate logic across layers

## Review Checklist
- Architecture aligns with implementation
- API names match between frontend and backend
- Database notes match actual schema usage
- UI flows match documented page structure

---