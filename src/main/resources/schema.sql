CREATE TABLE IF NOT EXISTS CheckIn (
    checkin_id UUID PRIMARY KEY,
    ticket_id UUID,
    attendee_id UUID,
    event_id UUID,
    checkin_time TIMESTAMP WITH TIME ZONE,
    checkin_status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS AttendeeCounter (
    event_id UUID PRIMARY KEY,
    current_count INT,
    last_updated TIMESTAMP WITH TIME ZONE
);
