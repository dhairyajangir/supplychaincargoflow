# Basic DB Schema Draft from Updated ERD

Initial draft translating the updated ERD (with explicit Delivery entity) to SQL. Covers 5 entities: Attributes/types from ERD boxes (e.g., Delivery actual_delivery_date → TIMESTAMP; Cargo weight → DECIMAL). Basic FKs from lines (e.g., Delivery to Shipment for closure). Defaults for usability (e.g., status 'Created'). No indexes/constraints (Day 4).

**ERD to SQL Mapping**:
- Boxes: Entity names → TABLE names (e.g., Delivery rectangle → CREATE TABLE delivery).
- Attribute Lists: Columns (e.g., Shipment Origin → VARCHAR(100); Vendor Name → VARCHAR(100)).
- Lines: Relationships → FK REFERENCES (e.g., Cargo line to Shipment → cargo.shipment_id REFERENCES shipment; Delivery to Shipment → delivery.shipment_id).
- Order: Independents first (Vendor/Route left in ERD), then central (Shipment), dependents (Cargo/Delivery right).

**Tie to Project/Updated ERD**:
- Modules: Delivery attributes for Shipment Tracking (actual vs. estimated); Cargo for Management (type/value linked to Shipment).
- Workflow: INSERT Vendor/Route → Shipment → Cargo → Delivery (FK chain); supports Kafka "delivered" updating Delivery status.
- Research: DECIMAL for value/weight (fees); TIMESTAMP for dates (delays).

## SQL Script
See `server/basic-schema.sql` – Run in SQL Shell/pgAdmin.

## Basic Testing Summary
- Setup: cargo_db, run basics.sql.
- Verify: \dt lists 5 tables (including delivery).
- Simple Tests: INSERT vendor/route → shipment (FK OK) → cargo/delivery (links Shipment) – Success; no advanced errors (Day 4).

---