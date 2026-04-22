# Non-Functional Requirements (NFRs)

This document defines the Non-Functional Requirements (NFRs) for the **Supply Chain Cargo Management system**. These requirements specify the quality attributes and operational characteristics of the system, ensuring it is performant, reliable, secure, and scalable.

---

## 2. NFR Summary

| Category       | Requirement        | Target                              | Priority |
|----------------|--------------------|-------------------------------------|----------|
| **Performance** | API Response Time  | < 500ms for 95% of requests         | High     |
|                | Page Load Time     | < 2 seconds for key pages           | High     |
| **Reliability** | System Uptime      | 99% for shipment tracking features  | High     |
|                | Data Integrity     | No data loss or corruption on transactions | High |
| **Scalability** | Concurrent Users   | Support 100 concurrent users        | Medium   |
|                | Data Volume        | Handle up to 10,000 active shipments| Medium   |
|                | Cloud Scaling      | Services must support auto-scaling  | High     |
| **Security**   | Authentication     | All API endpoints must be secured   | High     |
|                | Authorization      | Role-based access control (RBAC)    | High     |
|                | Data Encryption    | Data in transit must use TLS 1.2+   | High     |
| **Maintainability** | Code Quality  | Adherence to Java/Vue.js best practices | Medium |
|                | Documentation      | All public APIs must be documented  | High     |

---

## 3. Detailed Requirements

### 3.1 Performance
- **API Response Time (High):** Standard CRUD operations and data queries must return a response in under **500 milliseconds** for the 95th percentile of requests under normal load.  
- **Page Load Time (High):** The initial load time for critical user-facing pages (e.g., main dashboard, shipment list) should be under **2 seconds**.  

### 3.2 Reliability
- **System Uptime (High):** The core shipment tracking functionality must achieve **99% availability**, which allows approximately **3.6 days of downtime per year**.  
- **Data Integrity (High):** The system must guarantee that all database transactions are **atomic**. Failed operations must be rolled back to prevent partial or corrupt data.  

### 3.3 Scalability
- **Concurrent Users (Medium):** The system must perform reliably with at least **100 users** accessing it simultaneously.  
- **Cloud Scaling (High):** The backend services will be deployed as **containers (e.g., Docker)** on a cloud platform that supports **horizontal auto-scaling** to handle load spikes.  

### 3.4 Security
- **Authentication (High):** All access to the backend API must be protected. Unauthenticated requests will be rejected. **JSON Web Tokens (JWT)** will be used to manage user sessions.  
- **Authorization (High):** The system must implement **Role-Based Access Control (RBAC)**.  
  - Example: A *Vendor* role should only be able to see shipments assigned to them.  
  - Example: An *Admin* role can see all shipments.  
- **Data Encryption (High):** All data in transit must use **TLS 1.2 or higher**.  

---
