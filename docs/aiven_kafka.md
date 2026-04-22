# Kafka Deployment to Aiven

## Purpose
This document describes the Kafka deployment flow for CargoFlow using Aiven.

## Prerequisites
- Aiven Kafka service provisioned
- Backend application deployed on Render or another runtime
- Network access configured for the backend and local development environments

## Setup
### 1. Create the Kafka Service
- Create an Aiven Kafka service in the Aiven console.
- Choose the region and plan that match the project needs.
- Enable monitoring so service health can be observed.

### 2. Configure Access
- Add the backend and local development origins that need access.
- Store Kafka credentials in environment variables.
- Keep secrets out of the repository.

### 3. Retrieve Connection Details
- Copy the Kafka bootstrap servers from Aiven.
- Set them in backend environment configuration.

### 4. Configure Topics
Create the topics used by CargoFlow:
- `cargo-events`
- `shipment-events`
- `delivery-events`
- `route-events`
- `vendor-events`

### 5. Connect the Backend
- Configure the backend to use the Aiven bootstrap servers.
- Restart the service after updating environment variables.
- Confirm the application logs show a successful Kafka connection.

### 6. Validate the Integration
- Trigger a backend write operation.
- Confirm the event reaches the expected topic.
- Verify downstream handlers consume the message correctly.

## Monitoring
- Watch broker throughput and consumer lag.
- Review backend logs for publish and consume activity.
- Adjust capacity if sustained traffic causes delays.

## Operational Notes
- Keep Kafka credentials in environment variables.
- Avoid hardcoded provider-specific values in the repository.
- Keep topic names aligned with the backend event producers.
