package ee.ut.eventticketing.checkin.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary counts for check-ins in an event.")
public record CheckInSummaryResponse(
        @Schema(description = "Event identifier.") UUID eventId,
        @Schema(description = "Total valid check-ins.") long totalCheckIns,
        @Schema(description = "Unique attendees checked in.") long uniqueAttendees) {
}