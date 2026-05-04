package ee.ut.eventticketing.checkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AttendeeCheckinServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendeeCheckinServiceApplication.class, args);
    }
}