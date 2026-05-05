package ee.ut.eventticketing.checkin.exception;

public class InvalidTicketException extends RuntimeException {

    public InvalidTicketException(String ticketId) {
        super("Ticket validation failed for ticket: " + ticketId);
    }
}