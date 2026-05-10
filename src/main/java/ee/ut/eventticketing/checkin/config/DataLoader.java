package ee.ut.eventticketing.checkin.config;

import ee.ut.eventticketing.checkin.domain.AttendeeCounter;
import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import ee.ut.eventticketing.checkin.repository.AttendeeCounterRepository;
import ee.ut.eventticketing.checkin.repository.CheckInRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final CheckInRepository checkInRepository;
    private final AttendeeCounterRepository attendeeCounterRepository;

    public DataLoader(CheckInRepository checkInRepository, AttendeeCounterRepository attendeeCounterRepository) {
        this.checkInRepository = checkInRepository;
        this.attendeeCounterRepository = attendeeCounterRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (checkInRepository.count() == 0) {
            List<CheckIn> checkIns = new ArrayList<>();

            // Use well-known event IDs (shared with Analytics Service)
            UUID eventId1 = UUID.fromString("aaaa1111-1111-1111-1111-111111111111");
            UUID eventId2 = UUID.fromString("bbbb2222-2222-2222-2222-222222222222");

            // Event 1: Music Concert — 8 VALID check-ins
            for (int i = 1; i <= 8; i++) {
                checkIns.add(new CheckIn(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        eventId1,
                        OffsetDateTime.now().minusMinutes(i * 10L),
                        CheckInStatus.VALID
                ));
            }

            // Event 1: 1 DUPLICATE
            checkIns.add(new CheckIn(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    eventId1,
                    OffsetDateTime.now().minusMinutes(5),
                    CheckInStatus.DUPLICATE
            ));

            // Event 1: 1 REJECTED
            checkIns.add(new CheckIn(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    eventId1,
                    OffsetDateTime.now().minusMinutes(2),
                    CheckInStatus.REJECTED
            ));

            // Event 2: Tech Conference — 3 VALID check-ins
            for (int i = 1; i <= 3; i++) {
                checkIns.add(new CheckIn(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        eventId2,
                        OffsetDateTime.now().minusMinutes(i * 15L),
                        CheckInStatus.VALID
                ));
            }

            checkInRepository.saveAll(checkIns);

            // Seed AttendeeCounter table
            List<AttendeeCounter> counters = new ArrayList<>();
            counters.add(new AttendeeCounter(eventId1, 8, OffsetDateTime.now()));
            counters.add(new AttendeeCounter(eventId2, 3, OffsetDateTime.now()));
            attendeeCounterRepository.saveAll(counters);

            System.out.println("Database populated with 13 CheckIn rows and 2 AttendeeCounter rows.");
            System.out.println("Event 1 (Music Concert): " + eventId1);
            System.out.println("Event 2 (Tech Conference): " + eventId2);
        }
    }
}
