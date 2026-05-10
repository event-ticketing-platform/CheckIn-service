package ee.ut.eventticketing.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request body for creating a new attendee check-in.")
public record CreateCheckInRequest(
        @NotNull @Schema(description = "Ticket being validated.", example = "123e4567-e89b-12d3-a456-426614174000") UUID ticketId,
        @NotNull @Schema(description = "Event where the check-in happens.", example = "123e4567-e89b-12d3-a456-426614174001") UUID eventId,
        @NotNull @Schema(description = "Attendee being checked in.", example = "123e4567-e89b-12d3-a456-426614174002") UUID attendeeId) {
}