## ER Diagram Explanation - Supply Chain Cargo Management

This ER diagram outlines the database schema for a cargo management system. The shipment table is the central entity, connecting vendors, routes, individual cargo items, and their final delivery. The design includes specialized tables for detailed historical tracking and event processing.

**Entity Relationships**

Core Logistics (vendor, route, shipment)

- A vendor and a route are assigned to each shipment.
- This is a one-to-many relationship, allowing vendors and routes to be reused across many shipments.
- Foreign Keys: shipment.vendor_id and shipment.route_id.

Shipment Contents (shipment, cargo, delivery)

- Each shipment contains multiple cargo items (one-to-many).
- Each shipment is linked to a single delivery record for final-mile tracking (one-to-one).
- Foreign Keys: cargo.shipment_id and delivery.shipment_id.

**Advanced Tracking & Manifests**

- shipment_lifecycle: Provides a complete, auditable history of a shipment's status. Instead of overwriting the status, each change (e.g., "In Transit," "Delayed") creates a new record.

- cargo_manifest & cargo_manifest_item: These tables model the official shipping manifest. The cargo_manifest_item table acts as a junction to link multiple cargo items to a single cargo_manifest.

**Event Handling (for Week 3) 
- kafka_events: This table logs incoming messages from Kafka. It is intentionally decoupled (no foreign keys) to act as a resilient buffer, allowing events to be processed asynchronously without affecting the core application.

---