package ee.ut.eventticketing.checkin.service;

import java.util.UUID;

public interface TicketValidationClient {

    TicketValidationResult validate(UUID ticketId, UUID eventId, UUID attendeeId);
}