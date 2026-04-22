# CargoFlow Backend

Spring Boot backend for CargoFlow.

## Stack
- Java 17
- Spring Boot
- PostgreSQL
- Aiven Kafka
- Render deployment

## Prerequisites
- Java 17+
- Maven
- PostgreSQL
- Kafka bootstrap servers from Aiven

## Configuration
Copy `src/main/resources/application.properties.example` to `application.properties` and set:
- PostgreSQL connection details
- Kafka bootstrap servers
- OAuth client credentials
- Mail settings if notifications are enabled

## Run Locally
```bash
cd server
mvn clean install
mvn spring-boot:run
```

## Deployment Notes
- Backend hosting target: Render
- Database: PostgreSQL
- Kafka: Aiven
- Keep environment-specific values out of source control
- See `../docs/production_deployment.md` for the deployment checklist

## Reference
- [ARCHITECTURE.md](../ARCHITECTURE.md)
- [docs/backend_hld_details.md](../docs/backend_hld_details.md)
- [docs/backend_workflow.md](../docs/backend_workflow.md)
