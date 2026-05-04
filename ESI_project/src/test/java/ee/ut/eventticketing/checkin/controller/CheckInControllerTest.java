package ee.ut.eventticketing.checkin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import ee.ut.eventticketing.checkin.repository.CheckInRepository;
import ee.ut.eventticketing.checkin.service.CheckInService;
import ee.ut.eventticketing.checkin.service.TicketValidationClient;
import ee.ut.eventticketing.checkin.service.TicketValidationResult;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CheckInController.class)
@Import(CheckInService.class)
class CheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckInRepository checkInRepository;

    @MockBean
    private TicketValidationClient ticketValidationClient;

    // Happy path test case: valid ticket check-in should be created successfully.
    @Test
    void createCheckIn_returnsCreatedCheckIn() throws Exception {
        UUID ticketId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID eventId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID attendeeId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID checkInId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        when(checkInRepository.findByTicketId(ticketId)).thenReturn(Optional.empty());
        when(ticketValidationClient.validate(ticketId, eventId, attendeeId)).thenReturn(TicketValidationResult.approved());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> {
            CheckIn checkIn = invocation.getArgument(0);
            return new CheckIn(checkInId, checkIn.getTicketId(), checkIn.getEventId(), checkIn.getAttendeeId(), OffsetDateTime.parse("2026-05-04T10:15:30Z"), CheckInStatus.VALID);
        });

        mockMvc.perform(post("/api/check-ins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ticketId": "11111111-1111-1111-1111-111111111111",
                                  "eventId": "22222222-2222-2222-2222-222222222222",
                                  "attendeeId": "33333333-3333-3333-3333-333333333333"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkInId").value(checkInId.toString()))
                .andExpect(jsonPath("$.ticketId").value(ticketId.toString()))
                .andExpect(jsonPath("$.checkInStatus").value("VALID"));
    }

          // Error case test case: duplicate ticket check-in should return conflict.
    @Test
    void createCheckIn_returnsConflictWhenTicketAlreadyCheckedIn() throws Exception {
        UUID ticketId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID eventId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID attendeeId = UUID.fromString("77777777-7777-7777-7777-777777777777");

        when(checkInRepository.findByTicketId(ticketId)).thenReturn(Optional.of(
                new CheckIn(UUID.fromString("88888888-8888-8888-8888-888888888888"), ticketId, eventId, attendeeId, OffsetDateTime.parse("2026-05-04T10:15:30Z"), CheckInStatus.VALID)));

        mockMvc.perform(post("/api/check-ins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ticketId": "55555555-5555-5555-5555-555555555555",
                                  "eventId": "66666666-6666-6666-6666-666666666666",
                                  "attendeeId": "77777777-7777-7777-7777-777777777777"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ticket already checked in: 55555555-5555-5555-5555-555555555555"));
    }

    @Test
    void getSummary_returnsTotals() throws Exception {
        UUID eventId = UUID.fromString("99999999-9999-9999-9999-999999999999");

        when(checkInRepository.countByEventIdAndCheckInStatus(eventId, CheckInStatus.VALID)).thenReturn(3L);
        when(checkInRepository.findByEventIdOrderByCheckInTimeDesc(eventId)).thenReturn(java.util.List.of(
                new CheckIn(UUID.randomUUID(), UUID.randomUUID(), eventId, UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), OffsetDateTime.parse("2026-05-04T10:15:30Z"), CheckInStatus.VALID),
                new CheckIn(UUID.randomUUID(), UUID.randomUUID(), eventId, UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), OffsetDateTime.parse("2026-05-04T10:16:30Z"), CheckInStatus.VALID),
                new CheckIn(UUID.randomUUID(), UUID.randomUUID(), eventId, UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), OffsetDateTime.parse("2026-05-04T10:17:30Z"), CheckInStatus.VALID)
        ));

        mockMvc.perform(get("/api/check-ins/events/{eventId}/summary", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.totalCheckIns").value(3))
                .andExpect(jsonPath("$.uniqueAttendees").value(2));
    }
}