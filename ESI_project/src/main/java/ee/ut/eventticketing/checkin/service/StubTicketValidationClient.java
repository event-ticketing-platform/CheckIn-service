package ee.ut.eventticketing.checkin.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class StubTicketValidationClient implements TicketValidationClient {

    @Override
    public TicketValidationResult validate(UUID ticketId, UUID eventId, UUID attendeeId) {
        return TicketValidationResult.approved();
    }
}