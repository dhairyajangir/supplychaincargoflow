# Cargo Management System API Testing Guide

This guide covers manual API testing for the core backend controllers in the cargo management system.

## Base URL
```text
http://localhost:8080
```

## Recommended Test Order
1. Vendors
2. Routes
3. Shipments
4. Cargo
5. Deliveries

## Vendor API
Base path: `/api/vendors`

### Create Vendor
```http
POST /api/vendors
```

Example request:
```json
{
  "name": "NorthStar Logistics",
  "contactInfo": "contact@northstarlogistics.com, +1-555-010-1000",
  "serviceType": "Ocean Freight",
  "isActive": true
}
```

### Read Vendors
```http
GET /api/vendors
GET /api/vendors/{id}
GET /api/vendors/service-type/{serviceType}
GET /api/vendors/active
```

### Update Vendor
```http
PUT /api/vendors/{id}
PUT /api/vendors/{id}/status?isActive=false
```

### Delete Vendor
```http
DELETE /api/vendors/{id}
```

## Route API
Base path: `/api/routes`

### Create Route
```http
POST /api/routes
```

Example request:
```json
{
  "originPort": "Port of Hamburg",
  "destinationPort": "Port of Shanghai",
  "duration": 28,
  "status": "Active",
  "distance": 11500.5,
  "transportationMode": "Sea",
  "cost": 2500.0
}
```

### Read Routes
```http
GET /api/routes
GET /api/routes/{id}
GET /api/routes/status/{status}
GET /api/routes/search?origin={origin}&destination={destination}
GET /api/routes/transport-mode/{mode}
```

### Update Route
```http
PUT /api/routes/{id}
PUT /api/routes/{id}/status?status=Delayed
```

### Delete Route
```http
DELETE /api/routes/{id}
```

## Shipment API
Base path: `/api/shipments`

### Create Shipment
```http
POST /api/shipments
```

## Cargo API
Base path: `/api/cargo`

### Create Cargo
```http
POST /api/cargo
```

## Delivery API
Base path: `/api/deliveries`

### Create Delivery
```http
POST /api/deliveries
```

## Notes
- Test dependent entities only after their parent records exist.
- Use Postman or cURL to verify request and response bodies.
- Validate status codes, especially for create, update, and validation failures.

---
