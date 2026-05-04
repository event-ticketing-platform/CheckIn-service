package ee.ut.eventticketing.checkin.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ee.ut.eventticketing.checkin.dto.CheckInResponse;
import ee.ut.eventticketing.checkin.dto.CheckInSummaryResponse;
import ee.ut.eventticketing.checkin.dto.CreateCheckInRequest;
import ee.ut.eventticketing.checkin.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/checkins")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a check-in", description = "Validates a ticket and records an attendee check-in.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Check-in created"),
            @ApiResponse(responseCode = "409", description = "Duplicate check-in"),
            @ApiResponse(responseCode = "422", description = "Ticket rejected")
    })
    public CheckInResponse createCheckIn(@Valid @RequestBody CreateCheckInRequest request) {
        return checkInService.createCheckIn(request);
    }

    @GetMapping("/checkins/{checkInId}")
    @Operation(summary = "Get a check-in by id", description = "Returns a stored check-in record.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-in found"),
            @ApiResponse(responseCode = "404", description = "Check-in not found")
    })
    public CheckInResponse getCheckIn(@PathVariable UUID checkInId) {
        return checkInService.getCheckIn(checkInId);
    }

    @GetMapping("/checkins/tickets/{ticketId}")
    @Operation(summary = "Get a check-in by ticket id", description = "Looks up the check-in associated with a ticket.")
    public CheckInResponse getByTicketId(@PathVariable UUID ticketId) {
        return checkInService.getByTicketId(ticketId);
    }

    @GetMapping("/events/{eventId}/checkins")
    @Operation(summary = "List check-ins by event", description = "Returns all check-ins for a given event.")
    public List<CheckInResponse> getByEventId(@PathVariable UUID eventId) {
        return checkInService.getByEventId(eventId);
    }

    @GetMapping("/checkins/attendees/{attendeeId}")
    @Operation(summary = "List check-ins by attendee", description = "Returns all check-ins for a given attendee.")
    public List<CheckInResponse> getByAttendeeId(@PathVariable UUID attendeeId) {
        return checkInService.getByAttendeeId(attendeeId);
    }

    @GetMapping("/checkins/events/{eventId}/summary")
    @Operation(summary = "Get event check-in summary", description = "Returns total and unique attendee counts for an event.")
    public CheckInSummaryResponse getSummary(@PathVariable UUID eventId) {
        return checkInService.getSummary(eventId);
    }

    @GetMapping("/events/{eventId}/attendance")
    @Operation(summary = "Get current event attendance", description = "Returns the current attendee count for an event.")
    public long getAttendance(@PathVariable UUID eventId) {
        return checkInService.getAttendance(eventId);
    }

    @PatchMapping("/checkins/{checkInId}/reverse")
    @Operation(summary = "Reverse a check-in", description = "Marks an incorrect check-in as reversed.")
    public CheckInResponse reverseCheckIn(@PathVariable UUID checkInId) {
        return checkInService.reverseCheckIn(checkInId);
    }
}