package ee.ut.eventticketing.checkin.exception;

public class DuplicateCheckInException extends RuntimeException {

    public DuplicateCheckInException(String ticketId) {
        super("Ticket already checked in: " + ticketId);
    }
}