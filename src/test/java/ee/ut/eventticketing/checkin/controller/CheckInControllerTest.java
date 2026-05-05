package ee.ut.eventticketing.checkin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
        String ticketId = "T1";
        String eventId = "E1";
        String attendeeId = "A1";
        String checkInId = "C1";

        when(checkInRepository.findByTicketId(ticketId)).thenReturn(Optional.empty());
        when(ticketValidationClient.validate(ticketId, eventId, attendeeId)).thenReturn(TicketValidationResult.approved());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> {
            CheckIn checkIn = invocation.getArgument(0);
            return new CheckIn(checkInId, checkIn.getTicketId(), checkIn.getEventId(), checkIn.getAttendeeId(), OffsetDateTime.parse("2026-05-04T10:15:30Z"), CheckInStatus.VALID);
        });

        mockMvc.perform(post("/checkins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ticketId": "T1",
                                  "eventId": "E1",
                                  "attendeeId": "A1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.checkInId").value(checkInId))
                .andExpect(jsonPath("$.ticketId").value(ticketId))
                .andExpect(jsonPath("$.checkInStatus").value("VALID"));
    }

          // Error case test case: duplicate ticket check-in should return conflict.
    @Test
    void createCheckIn_returnsConflictWhenTicketAlreadyCheckedIn() throws Exception {
        String ticketId = "T2";
        String eventId = "E2";
        String attendeeId = "A2";

        when(checkInRepository.findByTicketId(ticketId)).thenReturn(Optional.of(
                new CheckIn("C2", ticketId, eventId, attendeeId, OffsetDateTime.parse("2026-05-04T10:15:30Z"), CheckInStatus.VALID)));

        mockMvc.perform(post("/checkins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ticketId": "T2",
                                  "eventId": "E2",
                                  "attendeeId": "A2"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ticket already checked in: T2"));
    }

    @Test
        void getAttendance_returnsCount() throws Exception {
        String eventId = "E99";

        when(checkInRepository.findByEventIdOrderByCheckInTimeDesc(eventId)).thenReturn(java.util.List.of(
                new CheckIn("C91", "T91", eventId, "A91", OffsetDateTime.parse("2026-05-04T10:15:30Z"), CheckInStatus.VALID),
                new CheckIn("C92", "T92", eventId, "A92", OffsetDateTime.parse("2026-05-04T10:16:30Z"), CheckInStatus.VALID),
                new CheckIn("C93", "T93", eventId, "A91", OffsetDateTime.parse("2026-05-04T10:17:30Z"), CheckInStatus.VALID)
        ));

      mockMvc.perform(get("/events/{eventId}/attendance", eventId))
                .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(2));
        }

        @Test
        void reverseCheckIn_marksCheckInAsReversed() throws Exception {
      String checkInId = "C100";
      String ticketId = "T100";
      String eventId = "E100";
      String attendeeId = "A100";

      when(checkInRepository.findByCheckInId(checkInId)).thenReturn(Optional.of(
        new CheckIn(checkInId, ticketId, eventId, attendeeId, OffsetDateTime.parse("2026-05-04T10:15:30Z"), CheckInStatus.VALID)));
      when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

      mockMvc.perform(patch("/checkins/{checkInId}/reverse", checkInId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.checkInId").value(checkInId))
        .andExpect(jsonPath("$.checkInStatus").value("REVERSED"));
    }
}