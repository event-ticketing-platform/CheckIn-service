package ee.ut.eventticketing.checkin.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CheckInSchemaInitializer {

    private final JdbcTemplate jdbcTemplate;

    public CheckInSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void refreshCheckInStatusConstraint() {
        try {
            jdbcTemplate.execute("ALTER TABLE check_ins DROP CONSTRAINT check_ins_check_in_status_check");
        } catch (DataAccessException ignored) {
        }

        try {
            jdbcTemplate.execute("ALTER TABLE check_ins ADD CONSTRAINT check_ins_check_in_status_check CHECK (check_in_status IN ('VALID', 'DUPLICATE', 'REJECTED', 'REVERSED'))");
        } catch (DataAccessException ignored) {
        }
    }
}