# Low-Level Design (LLD) for Database Queries  

This document provides the Low-Level Design (LLD) for complex and performance-critical database queries in the **Supply Chain Cargo Management system**.  
The focus is on **PostgreSQL** query optimization and **Spring Data JPA** implementations, ensuring efficient data retrieval for features such as the **real-time shipment tracking dashboard**.  

---

## 2. Core Use Case: Get Full Shipment Details  
The most frequent complex query is fetching a **shipment with all related data**:  
- Cargo items  
- Assigned vendor  
- Planned route  

This powers the **"Shipment Details"** view in the UI.  

### 2.1 Raw SQL Approach (Conceptual)  

```sql
-- Fetch shipment and its related entities in one query
SELECT
    s.shipment_id, s.status, s.estimated_delivery, -- Shipment details
    c.cargo_id, c.type, c.value,                  -- Cargo details
    v.name AS vendor_name, v.service_type,        -- Vendor details
    r.origin_port, r.destination_port             -- Route details
FROM
    shipment s
LEFT JOIN
    cargo c ON s.shipment_id = c.shipment_id
LEFT JOIN
    vendor v ON s.vendor_id = v.vendor_id
LEFT JOIN
    route r ON s.route_id = r.route_id
WHERE
    s.shipment_id = ?; -- Parameter for the specific shipment ID
````

This avoids multiple roundtrips and ensures **single query execution**.

---

### 2.2 Spring Data JPA Implementation (Solving N+1 Problem)

A naive JPA approach causes the **N+1 query problem**:

* 1 query to fetch shipment
* N queries for each cargo item

**Solution:** Use `JOIN FETCH` for eager loading.

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    /**
     * Finds a shipment by its ID and eagerly fetches related cargo items,
     * vendor, and route in a single optimized query.
     * This eliminates the N+1 query issue.
     */
    @Query("SELECT s FROM Shipment s " +
           "LEFT JOIN FETCH s.cargoItems " +
           "LEFT JOIN FETCH s.vendor " +
           "LEFT JOIN FETCH s.route " +
           "WHERE s.id = :shipmentId")
    Optional<Shipment> findByIdWithDetails(@Param("shipmentId") Integer shipmentId);
}
```

---

## 3. Performance and Optimization Strategy

### 3.1 Indexing Guidelines

Indexes are critical for performance, especially with frequent **JOINs** and **filtering queries**.

* **Primary Keys**
  PostgreSQL auto-creates indexes for all `PRIMARY KEY` columns (e.g., `shipment_id`, `cargo_id`).

* **Foreign Keys (Critical for JOINs)**

  * `cargo(shipment_id)`
  * `shipment(vendor_id)`
  * `shipment(route_id)`
  * `delivery(shipment_id)`

* **Frequently Queried Columns (Filtering in UI)**

  * `shipment(status)` → filtering shipments by status (e.g., *In Transit*, *Delivered*).

---

### 3.2 Example Index Creation (schema.sql)

```sql
-- Foreign key indexes
CREATE INDEX idx_cargo_shipment_id ON cargo(shipment_id);
CREATE INDEX idx_shipment_vendor_id ON shipment(vendor_id);
CREATE INDEX idx_shipment_route_id ON shipment(route_id);
CREATE INDEX idx_delivery_shipment_id ON delivery(shipment_id);

-- Filter column index
CREATE INDEX idx_shipment_status ON shipment(status);
```

---

With these optimizations:

* Queries run in **one roundtrip**
* **N+1 problem** is solved via `JOIN FETCH`
* Proper **indexes** ensure scalability under load

---

## 4. Conclusion

This LLD defines the **optimized PostgreSQL queries** and **JPA implementations** for the Shipment Details feature.
It ensures performance, scalability, and avoids common pitfalls (like N+1).

****
