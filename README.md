# Attendee Check-in Service

This repository contains the **Attendee Check-in Service**

## Scope and Responsibility

- Student responsibility (from Assignment 3): **Attendee Check-in Service** and **Reporting & Analytics Service**
- This checkpoint implementation includes **Service 1 complete**: Attendee Check-in Service

## Architecture (Spring Boot Layering)

The service follows the required layered architecture:

- Controller layer: `src/main/java/ee/ut/eventticketing/checkin/controller`
- DTO layer: `src/main/java/ee/ut/eventticketing/checkin/dto`
- Service layer: `src/main/java/ee/ut/eventticketing/checkin/service`
- Repository layer: `src/main/java/ee/ut/eventticketing/checkin/repository`
- Domain model: `src/main/java/ee/ut/eventticketing/checkin/domain`

Flow:

Client -> Controller -> Service -> Repository -> Database

## Checkpoint 1 Deliverables Mapping

### A. Running Service

- Starts with Maven and exposes endpoints on port `8085`

### B. API Implementation

Implemented endpoints:

- `POST /api/check-ins`
- `GET /api/check-ins/{checkInId}`
- `GET /api/check-ins/tickets/{ticketId}`
- `GET /api/check-ins/events/{eventId}`
- `GET /api/check-ins/attendees/{attendeeId}`
- `GET /api/check-ins/events/{eventId}/summary`

### C. OpenAPI / Swagger

- Swagger UI available at `http://localhost:8085/swagger-ui.html`
- Explicit OpenAPI specification file:
  - `src/main/resources/openapi/checkin-service.yaml`

### D. Persistence

- Repository + entity persistence via Spring Data JPA
- Local profile: H2 (`application.yml`)
- Docker profile: PostgreSQL (`application-docker.yml`)

### E. Testing

- `@WebMvcTest` class:
  - `src/test/java/ee/ut/eventticketing/checkin/controller/CheckInControllerTest.java`
- Includes:
  - happy-path test (`201 Created`)
  - error-path test (`409 Conflict` for duplicate check-in)
- Endpoint under test depends on another component (`TicketValidationClient`), mocked in test

### F. API Demonstration

- Demonstrable using Swagger UI or Postman

## Run Instructions

### Backend (local)

From project root:

```powershell
& "$env:TEMP\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
```

### Run tests

```powershell
& "$env:TEMP\apache-maven-3.9.9\bin\mvn.cmd" test
```

### Frontend

From `frontend` folder:

```powershell
npm install
npm run dev
```

Open `http://localhost:5173`.

## Docker

The service is containerized with:

- `Dockerfile` for attendee-checkin-service
- `docker-compose.yml` including:
  - `attendee-checkin-service`
  - `checkin-db` (PostgreSQL)

Run:

```powershell
docker compose up --build
```

## Notes

- Port constraints respected: service uses `8085`.
- `@MockBean` deprecation warnings are present with current Spring Boot test API but do not affect build/test success.