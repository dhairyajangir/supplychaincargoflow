-- 1. Vendor (Independent – ERD left)
CREATE TABLE vendor (
    vendor_id SERIAL PRIMARY KEY,  
    name VARCHAR(100) NOT NULL, 
    contact_info VARCHAR(200) NOT NULL, 
    service_type VARCHAR(50) NOT NULL,  
    is_active BOOLEAN DEFAULT TRUE, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refinements for Vendor
ALTER TABLE vendor ADD CONSTRAINT chk_name_unique UNIQUE (name);
CREATE INDEX idx_vendor_service_type ON vendor(service_type);

-- 2. Route (Independent – ERD left)
CREATE TABLE route (
    route_id SERIAL PRIMARY KEY,  
    origin_port VARCHAR(100) NOT NULL,  
    destination_port VARCHAR(100) NOT NULL,  
    duration INTEGER NOT NULL,  
    status VARCHAR(20) DEFAULT 'Active' NOT NULL,  
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refinements for Route 
ALTER TABLE route ADD CONSTRAINT chk_duration_positive CHECK (duration > 0);
ALTER TABLE route ADD CONSTRAINT chk_status_valid CHECK (status IN ('Active', 'Delayed', 'Closed'));
ALTER TABLE route ADD UNIQUE (origin_port, destination_port);
CREATE INDEX idx_route_status ON route(status);

-- 3. Shipment (Central – ERD middle, refs Vendor/Route lines)
CREATE TABLE shipment (
    shipment_id SERIAL PRIMARY KEY,  
    origin VARCHAR(100) NOT NULL, 
    destination VARCHAR(100) NOT NULL,  
    status VARCHAR(20) NOT NULL DEFAULT 'Created',  
    estimated_delivery TIMESTAMP, 
    vendor_id INTEGER NOT NULL REFERENCES vendor(vendor_id) ON DELETE RESTRICT, 
    route_id INTEGER NOT NULL REFERENCES route(route_id) ON DELETE RESTRICT, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refinements for Shipment (Unique code; CHECK status; indexes on FKs/status)
ALTER TABLE shipment ADD COLUMN shipment_code VARCHAR(20) UNIQUE;  
ALTER TABLE shipment ADD CONSTRAINT chk_status_valid CHECK (status IN ('Created', 'In Transit', 'Delivered', 'Delayed'));
CREATE INDEX idx_shipment_vendor ON shipment(vendor_id);
CREATE INDEX idx_shipment_status ON shipment(status);

-- 4. Cargo (Dependent – ERD right, refs Shipment line)
CREATE TABLE cargo (
    cargo_id SERIAL PRIMARY KEY,  
    shipment_id INTEGER NOT NULL REFERENCES shipment(shipment_id) ON DELETE CASCADE, 
    type VARCHAR(50) NOT NULL, 
    value DECIMAL(12, 2) NOT NULL,  
    description TEXT, 
    weight DECIMAL(10, 2),  
    volume DECIMAL(10, 2), 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refinements for Cargo 
ALTER TABLE cargo ADD CONSTRAINT chk_value_positive CHECK (value >= 0);
ALTER TABLE cargo ADD CONSTRAINT chk_weight_positive CHECK (weight IS NULL OR weight > 0);
ALTER TABLE cargo ADD CONSTRAINT chk_volume_positive CHECK (volume IS NULL OR volume > 0);
ALTER TABLE cargo ADD CONSTRAINT chk_type_valid CHECK (type IN ('Electronics', 'Perishables', 'Hazardous', 'General'));
CREATE INDEX idx_cargo_shipment ON cargo(shipment_id); 

-- 5. Delivery (Dependent – ERD right/updated, refs Shipment line)
CREATE TABLE delivery (
    delivery_id SERIAL PRIMARY KEY, 
    shipment_id INTEGER NOT NULL REFERENCES shipment(shipment_id) ON DELETE CASCADE, 
    actual_delivery_date TIMESTAMP,
    recipient VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending' NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refinements for Delivery
ALTER TABLE delivery ADD CONSTRAINT chk_status_valid CHECK (status IN ('Pending', 'Delivered', 'Failed'));
ALTER TABLE delivery ADD CONSTRAINT chk_recipient_not_null CHECK (recipient IS NOT NULL AND LENGTH(recipient) > 0);
CREATE INDEX idx_delivery_shipment ON delivery(shipment_id);
CREATE INDEX idx_delivery_status ON delivery(status);