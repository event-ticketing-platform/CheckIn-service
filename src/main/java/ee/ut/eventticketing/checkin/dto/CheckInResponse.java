package ee.ut.eventticketing.checkin.dto;

import java.time.OffsetDateTime;

import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Stored check-in response.")
public record CheckInResponse(
        @Schema(description = "Unique check-in identifier.") String checkInId,
        @Schema(description = "Ticket identifier.") String ticketId,
        @Schema(description = "Event identifier.") String eventId,
        @Schema(description = "Attendee identifier.") String attendeeId,
        @Schema(description = "Timestamp when the check-in occurred.") OffsetDateTime checkInTime,
        @Schema(description = "Current check-in state.") CheckInStatus checkInStatus) {
}