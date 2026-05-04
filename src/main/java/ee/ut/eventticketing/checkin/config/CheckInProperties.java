package ee.ut.eventticketing.checkin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "checkin")
public record CheckInProperties(boolean allowDuplicateAudit) {
}