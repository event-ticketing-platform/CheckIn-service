package ee.ut.eventticketing.checkin.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StubTicketValidationClient implements TicketValidationClient {

    @Override
    public TicketValidationResult validate(UUID ticketId, UUID eventId, UUID attendeeId) {
        return TicketValidationResult.approved();
    }
}