# API Low-Level Design (LLD) Outline

This document provides the Low-Level Design (LLD) for the API integration points of the Supply Chain Cargo Management system. It defines the contract for how the frontend and backend will communicate, including specific endpoints, request/response formats, and error handling. This serves as the technical blueprint for API development.

## 2. API Call Sequence Diagram
This sequence diagram illustrates the flow of a typical successful API call (e.g., creating a new cargo item) from the client to the database and back. It shows how the request moves through the backend's layered architecture.

```mermaid
sequenceDiagram
    participant Client as Vue.js Frontend
    participant Controller as Controller Layer
    participant Service as Service Layer
    participant Repository as Repository Layer
    participant DB as PostgreSQL DB

    %% Request Flow
    Client->>+Controller: POST /api/cargo (JSON Payload)
    note right of Client: Client sends cargo creation request

    Controller->>+Service: createCargo(cargoData)
    note right of Controller: Controller validates request

    Service->>+Repository: save(cargoEntity)
    note right of Service: Service applies business logic

    Repository->>+DB: INSERT INTO cargo (...)
    note right of Repository: Persist to database

    %% Response Flow
    DB-->>-Repository: Returns Saved Entity
    Repository-->>-Service: Returns Saved Entity
    Service-->>-Controller: Returns Cargo DTO
    Controller-->>-Client: 201 Created (JSON Response)
    note left of Client: Client receives confirmation with new cargoId

````

## 3. Endpoint Specification: Cargo Management

**POST /api/cargo**
**Description:** Creates a new cargo item and associates it with an existing shipment.

**Request Body (JSON):**

```json
{
  "shipmentId": 101,
  "type": "Electronics",
  "value": 25000.00,
  "description": "50 boxes of computer monitors.",
  "weight": 1200.50
}
```

**Success Response (201 Created):**

**Headers:**
`Location: /api/cargo/5`

**Body (JSON):**

```json
{
  "cargoId": 5,
  "shipmentId": 101,
  "type": "Electronics",
  "value": 25000.00,
  "description": "50 boxes of computer monitors.",
  "weight": 1200.50,
  "createdAt": "2025-10-01T14:30:00Z"
}
```

## 4. Standard Error Responses

### 400 Bad Request

**Trigger:** Request body fails validation (missing fields or wrong types).

**Response Body (JSON):**

```json
{
  "timestamp": "2025-10-01T14:35:10Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    { "field": "shipmentId", "message": "must not be null" },
    { "field": "value", "message": "must be a positive value" }
  ]
}
```

### 404 Not Found

**Trigger:** Referenced resource does not exist (e.g., shipmentId not found).

**Response Body (JSON):**

```json
{
  "timestamp": "2025-10-01T14:40:22Z",
  "status": 404,
  "error": "Not Found",
  "message": "Shipment with ID 999 not found."
}
```

### 500 Internal Server Error

**Trigger:** Unhandled server exceptions (e.g., database failure).

**Response Body (JSON):**

```json
{
  "timestamp": "2025-10-01T14:45:05Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later."
}
```

****

