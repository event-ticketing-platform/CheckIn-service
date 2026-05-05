package ee.ut.eventticketing.checkin.exception;

public class CheckInNotFoundException extends RuntimeException {

    public CheckInNotFoundException(String checkInId) {
        super("Check-in not found: " + checkInId);
    }
}