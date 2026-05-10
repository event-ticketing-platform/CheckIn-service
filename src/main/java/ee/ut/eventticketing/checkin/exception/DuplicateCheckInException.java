package ee.ut.eventticketing.checkin.exception;

import java.util.UUID;

public class DuplicateCheckInException extends RuntimeException {

    public DuplicateCheckInException(UUID ticketId) {
        super("Ticket already checked in: " + ticketId);
    }
}