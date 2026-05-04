package ee.ut.eventticketing.checkin.config;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import ee.ut.eventticketing.checkin.repository.CheckInRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final CheckInRepository checkInRepository;

    public DataLoader(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (checkInRepository.count() == 0) {
            UUID eventId1 = UUID.fromString("11111111-1111-4111-8111-111111111111");
            UUID eventId2 = UUID.fromString("22222222-2222-4222-8222-222222222222");

            CheckIn checkIn1 = new CheckIn(
                    UUID.fromString("c1111111-1111-4111-8111-111111111111"),
                    UUID.fromString("d1111111-1111-4111-8111-111111111111"),
                    eventId1,
                    UUID.fromString("a1111111-1111-4111-8111-111111111111"),
                    OffsetDateTime.now().minusHours(2),
                    CheckInStatus.VALID
            );

            CheckIn checkIn2 = new CheckIn(
                    UUID.fromString("c2222222-2222-4222-8222-222222222222"),
                    UUID.fromString("d2222222-2222-4222-8222-222222222222"),
                    eventId1,
                    UUID.fromString("a2222222-2222-4222-8222-222222222222"),
                    OffsetDateTime.now().minusHours(1),
                    CheckInStatus.VALID
            );

            CheckIn checkIn3 = new CheckIn(
                    UUID.fromString("c3333333-3333-4333-8333-333333333333"),
                    UUID.fromString("d3333333-3333-4333-8333-333333333333"),
                    eventId2,
                    UUID.fromString("a3333333-3333-4333-8333-333333333333"),
                    OffsetDateTime.now().minusMinutes(30),
                    CheckInStatus.DUPLICATE
            );

            CheckIn checkIn4 = new CheckIn(
                    UUID.fromString("c4444444-4444-4444-8444-444444444444"),
                    UUID.fromString("d4444444-4444-4444-8444-444444444444"),
                    eventId2,
                    UUID.fromString("a4444444-4444-4444-8444-444444444444"),
                    OffsetDateTime.now().minusMinutes(15),
                    CheckInStatus.REJECTED
            );

            checkInRepository.saveAll(List.of(checkIn1, checkIn2, checkIn3, checkIn4));

            System.out.println("Database populated with initial CheckIn data.");
            System.out.println("Hardcoded Event 1 ID: " + eventId1);
            System.out.println("Hardcoded Event 2 ID: " + eventId2);
        }
    }
}
