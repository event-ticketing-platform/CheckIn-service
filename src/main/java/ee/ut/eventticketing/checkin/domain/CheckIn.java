package ee.ut.eventticketing.checkin.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CheckIn")
public class CheckIn {

    @Id
    @Column(name = "checkin_id")
    private UUID checkInId;

    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "attendee_id")
    private UUID attendeeId;

    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "checkin_time")
    private OffsetDateTime checkInTime;

    @Column(name = "checkin_status", length = 50)
    @Enumerated(EnumType.STRING)
    private CheckInStatus checkInStatus;

    protected CheckIn() {}

    public CheckIn(UUID checkInId, UUID ticketId, UUID attendeeId, UUID eventId, OffsetDateTime checkInTime, CheckInStatus checkInStatus) {
        this.checkInId = checkInId;
        this.ticketId = ticketId;
        this.attendeeId = attendeeId;
        this.eventId = eventId;
        this.checkInTime = checkInTime;
        this.checkInStatus = checkInStatus;
    }

    public UUID getCheckInId() {
        return checkInId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public UUID getAttendeeId() {
        return attendeeId;
    }

    public UUID getEventId() {
        return eventId;
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

    public void markDuplicate() {
        this.checkInStatus = CheckInStatus.DUPLICATE;
    }
}