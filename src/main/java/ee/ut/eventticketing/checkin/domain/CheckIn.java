package ee.ut.eventticketing.checkin.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

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
    private UUID checkInId;

    @Column(name = "ticket_id", nullable = false, unique = true)
    private UUID ticketId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "attendee_id", nullable = false)
    private UUID attendeeId;

    @Column(name = "check_in_time", nullable = false)
    private OffsetDateTime checkInTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_in_status", nullable = false)
    private CheckInStatus checkInStatus;

    protected CheckIn() {
    }

    public CheckIn(UUID checkInId, UUID ticketId, UUID eventId, UUID attendeeId, OffsetDateTime checkInTime, CheckInStatus checkInStatus) {
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

    public UUID getCheckInId() {
        return checkInId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getAttendeeId() {
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

    public void revalidate(UUID eventId, UUID attendeeId, OffsetDateTime checkInTime) {
        this.eventId = eventId;
        this.attendeeId = attendeeId;
        this.checkInTime = checkInTime;
        this.checkInStatus = CheckInStatus.VALID;
    }

    public static CheckIn valid(UUID ticketId, UUID eventId, UUID attendeeId, OffsetDateTime checkInTime) {
        return new CheckIn(UUID.randomUUID(), ticketId, eventId, attendeeId, checkInTime, CheckInStatus.VALID);
    }
}