# DB Schema Refinements on Basics 

This refines the Day 3 basic schema (5 tables from updated ERD) with ALTER statements for data integrity, uniqueness, and query performance. The refinements build directly on the foundation without recreating tables – appending ALTERs to basics.sql creates the full `backend/schema.sql`. Key focus: Protect ERD attributes (e.g., Cargo weight/volume positive) and relationships (e.g., indexes on Delivery shipment_id line for JOINs).

**ERD to Refinements Mapping**:
- **Attributes Validation (CHECK Constraints)**: Lists in ERD boxes (e.g., Cargo value/weight/volume → CHECK >=0 / >0 to prevent invalid logistics data like negative fees/space).
- **Uniqueness (UNIQUE Keys)**: No dups (e.g., Vendor name UNIQUE from ERD list – for reliable coordination; Shipment shipment_code UNIQUE enhancement for external tracking refs, though not explicit in ERD).
- **Relationships Optimization (Indexes)**: ERD lines (e.g., Cargo to Shipment) → Indexes on FKs (shipment_id) for fast queries (e.g., <2s manifests). CHECK on status enums (e.g., Delivery 'Pending/Delivered' from status attribute – limits to valid lifecycle).
- **Order**: ALTERs after basics (no FK issues since tables exist); NOT NULL/RESTRICT on Shipment FKs (required lines in ERD to Vendor/Route).


## Tie to Project & Updated ERD (With Delivery)
- **Cargo Management (Week 2 CRUD)**: Cargo CHECK/unique volume/weight validates ERD attributes; index shipment_id for manifest JOINs (ERD line).
- **Shipment Tracking**: Shipment status CHECK ('In Transit' etc.) for lifecycle; Delivery CHK on status/recipient (updated ERD rectangle – ensures valid "delivered" updates via Kafka).
- **Vendor/Route Coordination**: Vendor unique name (no dup assignments); Route unique ports (ERD origin_port/destination_port – prevents overlap).
- **Event Streaming (Week 3 Kafka)**: Status CHECK limits events (e.g., only 'Delivered' triggers "closure"); indexes for fast status queries.
- **Workflow Integration**: ERD chain (Vendor/Route → Shipment → Cargo/Delivery) protected: e.g., Cargo VOLUME >0 for load calcs; Delivery recipient non-empty for handover. Full: INSERT Shipment → Cargo (FK CASCADE) → Delivery update (CHECK status).

**Assumptions & Research**: CHECK from logistics standards (e.g., weight positive for fees); no over-constrain (e.g., volume optional in Cargo ERD). shipment_code UNIQUE for refs (code-generated in Java).

## Full Merged Script
Located in `server/schema.sql`: Day 3 basics (CREATEs) + Day 4 ALTERs. Run full on fresh DB or basics first + these.

### Key Excerpt (Refinements Only – Append to Basics)
```sql
-- Refinements for Vendor
ALTER TABLE vendor ADD CONSTRAINT chk_name_unique UNIQUE (name);

-- Refinements for Route 
ALTER TABLE route ADD CONSTRAINT chk_duration_positive CHECK (duration > 0);
ALTER TABLE route ADD CONSTRAINT chk_status_valid CHECK (status IN ('Active', 'Delayed', 'Closed'));
ALTER TABLE route ADD UNIQUE (origin_port, destination_port);

-- Refinements for Shipment
ALTER TABLE shipment ADD COLUMN shipment_code VARCHAR(20) UNIQUE;  
ALTER TABLE shipment ADD CONSTRAINT chk_status_valid CHECK (status IN ('Created', 'In Transit', 'Delivered', 'Delayed'));

-- Refinements for Cargo 
ALTER TABLE cargo ADD CONSTRAINT chk_value_positive CHECK (value >= 0);
ALTER TABLE cargo ADD CONSTRAINT chk_weight_positive CHECK (weight IS NULL OR weight > 0);
ALTER TABLE cargo ADD CONSTRAINT chk_volume_positive CHECK (volume IS NULL OR volume > 0);

-- Refinements for Delivery 
ALTER TABLE delivery ADD CONSTRAINT chk_status_valid CHECK (status IN ('Pending', 'Delivered', 'Failed'));
ALTER TABLE delivery ADD CONSTRAINT chk_recipient_not_null CHECK (recipient IS NOT NULL AND LENGTH(recipient) > 0);

-- Indexes (All Tables)
CREATE INDEX idx_vendor_service_type ON vendor(service_type);
CREATE INDEX idx_route_status ON route(status);
CREATE INDEX idx_shipment_vendor ON shipment(vendor_id);
CREATE INDEX idx_shipment_status ON shipment(status);
CREATE INDEX idx_cargo_shipment ON cargo(shipment_id); 
CREATE INDEX idx_delivery_shipment ON delivery(shipment_id);
CREATE INDEX idx_delivery_status ON delivery(status);
```

## Advanced Local Testing Summary
Tested in SQL Shell on Day 3 basics DB (cargo_db):
- Ran ALTERs: "ALTER TABLE" success for all CHECK/unique/indexes; no errors.
- Integrity: INSERT cargo (value=-100) → ERROR: CHECK violation (value >=0 – protects ERD value); UPDATE delivery (status='Invalid') → ERROR (status not in enum).
- Uniqueness: INSERT duplicate vendor name → UNIQUE error (chk_name_unique).
- Relationships/Query Perf: INSERT shipment (vendor_id=1) OK (RESTRICT prevents delete); JOIN cargo/delivery ON shipment_id fast ("Index Scan using idx_cargo_shipment" via EXPLAIN – <1 row/ms).
- Delivery Focus: INSERT delivery (shipment_id=1, recipient='ABC') success; bad recipient '' → CHECK error (non-empty).
- Full Chain: Vendor/Route INSERT → Shipment FK OK → Cargo (volume=10.0, weight=50.5) → Delivery → SELECT JOIN all (ERD flow verified, 1 row returned).


---