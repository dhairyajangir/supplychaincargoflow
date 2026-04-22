# Low-Level Design (LLD): System Integration

This document provides the Low-Level Design (LLD) for the end-to-end integration flows within the **Supply Chain Cargo Management System**.  

It details the sequence of interactions between the major components:  
- **Vue.js Frontend**  
- **Java Spring Boot Backend**  
- **PostgreSQL Database**  

The goal of this LLD is to create a clear, unambiguous blueprint for how data and control flow through the application for key use cases, and to specify the planned integration points for the **future event-streaming layer (Apache Kafka)**.

---

## 2. Core Integration Flow: Assign Vendor to Shipment
This sequence diagram illustrates the complete, end-to-end flow for one of the core business processes: **a user assigning a vendor to an existing shipment**.

```mermaid
sequenceDiagram
    participant User
    participant Vue.js Frontend
    box Spring Boot Backend
        participant Controller
        participant Service
        participant Repository
    end
    participant PostgreSQL DB

    User->>+Vue.js Frontend: Clicks 'Assign Vendor' for a shipment
    Vue.js Frontend->>+Controller: PUT /api/shipments/{id}/assign-vendor<br/>(Request Body: { "vendorId": 123 })

    Controller->>+Service: assignVendorToShipment(shipmentId, vendorId)
    Service->>+Repository: findShipmentById(shipmentId)
    Repository->>+PostgreSQL DB: SELECT * FROM shipment WHERE shipment_id = ?
    PostgreSQL DB-->>-Repository: Returns Shipment entity
    Repository-->>-Service: Returns Shipment object

    Service->>+Repository: findVendorById(vendorId)
    Repository->>+PostgreSQL DB: SELECT * FROM vendor WHERE vendor_id = ?
    PostgreSQL DB-->>-Repository: Returns Vendor entity
    Repository-->>-Service: Returns Vendor object

    Service->>Service: Business Logic: Updates shipment object with vendor
    Service->>+Repository: save(updatedShipment)
    Repository->>+PostgreSQL DB: UPDATE shipment SET vendor_id = ? WHERE shipment_id = ?
    PostgreSQL DB-->>-Repository: Confirms update
    Repository-->>-Service: Returns saved Shipment object

    Service-->>-Controller: Returns updated ShipmentDTO
    Controller-->>-Vue.js Frontend: 200 OK Response<br/>(Response Body: { "shipmentId": ..., "vendorName": ... })

    Vue.js Frontend->>-User: Displays "Vendor Assigned" success message<br/>and updates UI
````

---

## 3. Asynchronous Integration (Week 3 Placeholder)

Starting in **Week 3**, we will introduce **Apache Kafka** for asynchronous event streaming.
This will decouple services and enable:

* Real-time notifications
* Easier integration with external systems
* Event-driven workflows (e.g., email alerts, analytics updates, cross-service communication)

---

### 3.1 Kafka Integration Point

The **Service Layer** is the ideal place to publish events.
After a business action is successfully completed **and persisted to the database**, the service will publish an event.

#### Example Flow with Kafka

When a vendor is assigned to a shipment, after the database update is confirmed, the `ShipmentService` will publish a **VendorAssignedToShipment** event to the Kafka topic `shipment-events`.

```mermaid
sequenceDiagram
    participant Service
    participant Repository
    participant PostgreSQL DB
    participant Kafka Topic

    Note over Service, Repository: ...Previous steps from above...
    Service->>+Repository: save(updatedShipment)
    Repository->>+PostgreSQL DB: UPDATE shipment SET ...
    PostgreSQL DB-->>-Repository: Confirm update
    Repository-->>-Service: Return saved Shipment

    Note over Service, Kafka Topic: The database transaction is now complete.
    Service->>Kafka Topic: Publish Event<br/>(Type: VendorAssignedToShipment, Payload: { shipmentId, vendorId })
```

---

## 4. Benefits of This Design

* **Fast API Responses** → User sees success immediately after DB update.
* **Decoupled Architecture** → Future services can consume events without modifying core logic.
* **Scalability** → Event-driven design scales better than synchronous chaining.
* **Maintainability** → Each layer (Frontend, Backend, DB, Kafka) has a well-defined responsibility.

---

## 5. Future Enhancements

This section highlights improvements planned for later iterations of the system.

### 5.1 Monitoring & Observability

* Integrate **Prometheus + Grafana** for metrics collection and visualization.
* Implement **distributed tracing** with OpenTelemetry to follow requests across services.

### 5.2 Retry & Error Handling

* Configure **Kafka retry policies** for failed message consumption.
* Introduce **dead-letter queues (DLQ)** to safely capture and review failed events.
* Add structured error responses for all APIs, aligned with existing error contracts.

### 5.3 Security Enhancements

* Add **OAuth2 / JWT-based authentication** to secure APIs.
* Ensure **role-based access control (RBAC)** for sensitive actions like assigning vendors.
* Enable **encryption at rest** for the database and **TLS for all service communication**.

### 5.4 Event Expansion

* Define additional Kafka event types (e.g., `ShipmentCreated`, `CargoAdded`, `ShipmentDelivered`).
* Build notification services that subscribe to these events for real-time alerts.

### 5.5 Resilience & Scalability

* Deploy Kafka in a **multi-broker cluster** for fault tolerance.
* Enable **horizontal scaling** of the backend service with Kubernetes.
* Introduce **circuit breakers** (via Resilience4j) for safer inter-service communication.


****
