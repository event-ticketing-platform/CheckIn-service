package ee.ut.eventticketing.checkin.service;

import org.springframework.stereotype.Component;

@Component
public class StubTicketValidationClient implements TicketValidationClient {

    @Override
    public TicketValidationResult validate(String ticketId, String eventId, String attendeeId) {
        return TicketValidationResult.approved();
    }
}