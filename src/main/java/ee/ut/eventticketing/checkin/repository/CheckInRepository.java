package ee.ut.eventticketing.checkin.repository;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

    Optional<CheckIn> findByTicketId(UUID ticketId);

    List<CheckIn> findByEventId(UUID eventId);

    List<CheckIn> findByAttendeeId(UUID attendeeId);

    int countByEventIdAndCheckInStatus(UUID eventId, CheckInStatus status);
}