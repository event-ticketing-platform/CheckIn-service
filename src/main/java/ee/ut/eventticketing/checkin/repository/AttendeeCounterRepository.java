package ee.ut.eventticketing.checkin.repository;

import ee.ut.eventticketing.checkin.domain.AttendeeCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttendeeCounterRepository extends JpaRepository<AttendeeCounter, UUID> {
}
