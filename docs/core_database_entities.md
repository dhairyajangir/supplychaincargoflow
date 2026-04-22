# Core Database Entities

## Overview
This document summarizes the main database entities used by CargoFlow.

## Tech Stack
- Backend: Spring Boot
- Frontend: Vue 3
- Database: PostgreSQL
- Event Bus: Aiven Kafka
- Frontend Hosting: Vercel
- Backend Hosting: Render

## Entity Summary
### Shipment
- Central record for shipment lifecycle and routing
- Stores origin, destination, status, estimated delivery, and linked vendor or route

### Cargo
- Represents cargo items linked to a shipment
- Stores type, value, weight, description, and timestamps

### Route
- Represents a transport path between ports
- Stores origin, destination, duration, and status

### Vendor
- Represents a logistics partner
- Stores contact info, service type, status, and timestamps

### Delivery
- Represents shipment handoff and completion
- Stores recipient, delivery status, and actual delivery timestamp

## Relationships
- Shipment has many cargo records
- Shipment has one route
- Shipment can be linked to one vendor
- Shipment can have one delivery record

## Design Notes
- Use PostgreSQL constraints to protect data integrity
- Use indexes on foreign keys for faster joins
- Keep event-driven updates through Kafka focused on shipment lifecycle changes
