# Attendee Check-In Service

## Overview
This service handles the validation of attendee tickets and records check-in events. It ensures that tickets are not used more than once and provides live attendance counts.

## Key Endpoints
- `POST /checkins`: Validate a ticket and check in an attendee.
- `GET /checkins/{id}`: Retrieve a specific check-in record.
- `GET /events/{eventId}/attendance`: Get the current live attendance count for an event.
- `GET /checkins/events/{eventId}/summary`: Get a summary of check-ins (total vs unique).
- `PATCH /checkins/{id}/reverse`: Reverse an accidental check-in.

## Database
Uses PostgreSQL in Docker (via `checkin-db`) and H2 for local development.

## Integration
Exposed via API Gateway at `/api/checkin/**`.