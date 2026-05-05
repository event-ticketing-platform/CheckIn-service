package ee.ut.eventticketing.checkin.service;

public interface TicketValidationClient {

    TicketValidationResult validate(String ticketId, String eventId, String attendeeId);
}