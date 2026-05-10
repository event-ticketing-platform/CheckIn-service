package ee.ut.eventticketing.checkin.dto;

import java.time.OffsetDateTime;

import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Stored check-in response.")
public record CheckInResponse(
        @Schema(description = "Unique check-in identifier.") UUID checkInId,
        @Schema(description = "Ticket identifier.") UUID ticketId,
        @Schema(description = "Event identifier.") UUID eventId,
        @Schema(description = "Attendee identifier.") UUID attendeeId,
        @Schema(description = "Timestamp when the check-in occurred.") OffsetDateTime checkInTime,
        @Schema(description = "Current check-in state.") CheckInStatus checkInStatus) {
}