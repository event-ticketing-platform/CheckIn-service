package ee.ut.eventticketing.checkin.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for creating a new attendee check-in.")
public record CreateCheckInRequest(
        @NotNull @Schema(description = "Ticket being validated.", example = "11111111-1111-1111-1111-111111111111") UUID ticketId,
        @NotNull @Schema(description = "Event where the check-in happens.", example = "22222222-2222-2222-2222-222222222222") UUID eventId,
        @NotNull @Schema(description = "Attendee being checked in.", example = "33333333-3333-3333-3333-333333333333") UUID attendeeId) {
}