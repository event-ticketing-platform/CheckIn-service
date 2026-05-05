package ee.ut.eventticketing.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a new attendee check-in.")
public record CreateCheckInRequest(
        @NotNull @Schema(description = "Ticket being validated.", example = "T1") String ticketId,
        @NotNull @Schema(description = "Event where the check-in happens.", example = "E1") String eventId,
        @NotNull @Schema(description = "Attendee being checked in.", example = "A1") String attendeeId) {
}