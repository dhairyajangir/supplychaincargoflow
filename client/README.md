# CargoFlow Client

Vue 3 + Vite frontend for the CargoFlow cargo management system.

## Overview
- Frontend hosting: Vercel
- Backend API: Render
- Database: PostgreSQL
- Event bus: Aiven Kafka

## Setup
### Install
```bash
npm install
# or
bun install
```

### Development
```bash
npm run dev
# or
bun run dev
```

### Production Build
```bash
npm run build
# or
bun run build
```

## Environment
Set the API base URL with `VITE_API_BASE_URL`.
- Local example: `http://localhost:8080/api`
- Production example: the public Render backend API URL

## Production Deployment
- Frontend hosting: Vercel
- Backend API: Render
- Event bus: Aiven Kafka
- Database: PostgreSQL
- See `../docs/production_deployment.md` for the deployment checklist

## Project Structure
```text
client/
├── src/
├── public/
├── .env.example
├── .env.local
└── .env.production
```
