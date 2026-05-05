package ee.ut.eventticketing.checkin.repository;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByCheckInId(String checkInId);

    Optional<CheckIn> findByTicketId(String ticketId);

    List<CheckIn> findByEventIdOrderByCheckInTimeDesc(String eventId);

    List<CheckIn> findByAttendeeIdOrderByCheckInTimeDesc(String attendeeId);

    long countByEventIdAndCheckInStatus(String eventId, CheckInStatus checkInStatus);
}