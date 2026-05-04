package ee.ut.eventticketing.checkin.exception;

import java.util.UUID;

public class InvalidTicketException extends RuntimeException {

    public InvalidTicketException(UUID ticketId) {
        super("Ticket validation failed for ticket: " + ticketId);
    }
}