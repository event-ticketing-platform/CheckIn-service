package ee.ut.eventticketing.checkin.repository;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByCheckInId(UUID checkInId);

    Optional<CheckIn> findByTicketId(UUID ticketId);

    List<CheckIn> findByEventIdOrderByCheckInTimeDesc(UUID eventId);

    List<CheckIn> findByAttendeeIdOrderByCheckInTimeDesc(UUID attendeeId);

    long countByEventIdAndCheckInStatus(UUID eventId, CheckInStatus checkInStatus);
}