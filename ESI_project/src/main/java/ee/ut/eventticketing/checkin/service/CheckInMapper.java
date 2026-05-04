package ee.ut.eventticketing.checkin.service;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.dto.CheckInResponse;

final class CheckInMapper {

    private CheckInMapper() {
    }

    static CheckInResponse toResponse(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getCheckInId(),
                checkIn.getTicketId(),
                checkIn.getEventId(),
                checkIn.getAttendeeId(),
                checkIn.getCheckInTime(),
                checkIn.getCheckInStatus());
    }
}