package ee.ut.eventticketing.checkin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "AttendeeCounter")
public class AttendeeCounter {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "current_count")
    private int currentCount;

    @Column(name = "last_updated")
    private OffsetDateTime lastUpdated;

    public AttendeeCounter() {
    }

    public AttendeeCounter(UUID eventId, int currentCount, OffsetDateTime lastUpdated) {
        this.eventId = eventId;
        this.currentCount = currentCount;
        this.lastUpdated = lastUpdated;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
