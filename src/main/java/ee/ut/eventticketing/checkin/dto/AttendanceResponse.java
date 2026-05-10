package ee.ut.eventticketing.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Current attendance count for an event.")
public record AttendanceResponse(
        @Schema(description = "Event identifier.") UUID eventId,
        @Schema(description = "Current attendee count.") long attendeeCount) {
}