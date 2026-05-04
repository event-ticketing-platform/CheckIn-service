package ee.ut.eventticketing.checkin.service;

public final class TicketValidationResult {

    private final boolean valid;
    private final String message;

    private TicketValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static TicketValidationResult approved() {
        return new TicketValidationResult(true, "Ticket is valid");
    }

    public static TicketValidationResult invalid(String message) {
        return new TicketValidationResult(false, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String message() {
        return message;
    }
}