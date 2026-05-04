package ee.ut.eventticketing.checkin.exception;

import java.util.UUID;

public class CheckInNotFoundException extends RuntimeException {

    public CheckInNotFoundException(UUID checkInId) {
        super("Check-in not found: " + checkInId);
    }

    public CheckInNotFoundException(String message) {
        super(message);
    }
}