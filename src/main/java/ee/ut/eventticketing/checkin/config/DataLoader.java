package ee.ut.eventticketing.checkin.config;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import ee.ut.eventticketing.checkin.repository.CheckInRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CheckInRepository checkInRepository;

    public DataLoader(CheckInRepository checkInRepository) {
        this.checkInRepository = checkInRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (checkInRepository.count() == 0) {
            List<CheckIn> checkIns = new ArrayList<>();
            String eventId = "E1";

            // Add 8 VALID check-ins
            for (int i = 1; i <= 8; i++) {
                checkIns.add(new CheckIn(
                        "C" + i,
                        "T" + i,
                        eventId,
                        "A" + i,
                        OffsetDateTime.now().minusMinutes(i * 10L),
                        CheckInStatus.VALID
                ));
            }

            // Add 1 DUPLICATE
            checkIns.add(new CheckIn(
                    "C9",
                    "T9",
                    eventId,
                    "A9",
                    OffsetDateTime.now().minusMinutes(5),
                    CheckInStatus.DUPLICATE
            ));

            // Add 1 REJECTED
            checkIns.add(new CheckIn(
                    "C10",
                    "T10",
                    eventId,
                    "A10",
                    OffsetDateTime.now().minusMinutes(2),
                    CheckInStatus.REJECTED
            ));

            checkInRepository.saveAll(checkIns);

            System.out.println("Database populated with 10 initial CheckIn data rows using simple IDs.");
            System.out.println("Event ID: " + eventId);
        }
    }
}
