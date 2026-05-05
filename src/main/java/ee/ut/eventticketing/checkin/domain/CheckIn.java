package ee.ut.eventticketing.checkin.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "check_ins")
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "check_in_id", nullable = false, unique = true, updatable = false)
    private String checkInId;

    @Column(name = "ticket_id", nullable = false, unique = true)
    private String ticketId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "attendee_id", nullable = false)
    private String attendeeId;

    @Column(name = "check_in_time", nullable = false)
    private OffsetDateTime checkInTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status", nullable = false)
    private CheckInStatus checkInStatus;

    protected CheckIn() {
    }

    public CheckIn(String checkInId, String ticketId, String eventId, String attendeeId, OffsetDateTime checkInTime, CheckInStatus checkInStatus) {
        this.checkInId = checkInId;
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.checkInTime = checkInTime;
        this.checkInStatus = checkInStatus;
    }

    public Long getId() {
        return id;
    }

    public String getCheckInId() {
        return checkInId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAttendeeId() {
        return attendeeId;
    }

    public OffsetDateTime getCheckInTime() {
        return checkInTime;
    }

    public CheckInStatus getCheckInStatus() {
        return checkInStatus;
    }

    public void reverse() {
        this.checkInStatus = CheckInStatus.REVERSED;
    }

    public void revalidate(String eventId, String attendeeId, OffsetDateTime checkInTime) {
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.checkInTime = checkInTime;
        this.checkInStatus = CheckInStatus.VALID;
    }

    public static CheckIn valid(String ticketId, String eventId, String attendeeId, OffsetDateTime checkInTime) {
        return new CheckIn("C-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000), ticketId, eventId, attendeeId, checkInTime, CheckInStatus.VALID);
    }
}