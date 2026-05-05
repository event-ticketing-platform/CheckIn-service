package ee.ut.eventticketing.checkin.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ee.ut.eventticketing.checkin.domain.CheckIn;
import ee.ut.eventticketing.checkin.domain.CheckInStatus;
import ee.ut.eventticketing.checkin.dto.CheckInResponse;
import ee.ut.eventticketing.checkin.dto.CheckInSummaryResponse;
import ee.ut.eventticketing.checkin.dto.CreateCheckInRequest;
import ee.ut.eventticketing.checkin.exception.CheckInNotFoundException;
import ee.ut.eventticketing.checkin.exception.DuplicateCheckInException;
import ee.ut.eventticketing.checkin.exception.InvalidTicketException;
import ee.ut.eventticketing.checkin.repository.CheckInRepository;

@Service
@Transactional
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final TicketValidationClient ticketValidationClient;

    public CheckInService(CheckInRepository checkInRepository, TicketValidationClient ticketValidationClient) {
        this.checkInRepository = checkInRepository;
        this.ticketValidationClient = ticketValidationClient;
    }

    public CheckInResponse createCheckIn(CreateCheckInRequest request) {
        java.util.Optional<CheckIn> existingOpt = checkInRepository.findByTicketId(request.ticketId());
        if (existingOpt.isPresent()) {
            CheckIn existing = existingOpt.get();
            if (existing.getCheckInStatus() == CheckInStatus.VALID || existing.getCheckInStatus() == CheckInStatus.DUPLICATE) {
                throw new DuplicateCheckInException(request.ticketId());
            } else {
                TicketValidationResult validationResult = ticketValidationClient.validate(request.ticketId(), request.eventId(), request.attendeeId());
                if (!validationResult.isValid()) {
                    throw new InvalidTicketException(request.ticketId());
                }
                existing.revalidate(request.eventId(), request.attendeeId(), OffsetDateTime.now());
                return CheckInMapper.toResponse(checkInRepository.save(existing));
            }
        }

        TicketValidationResult validationResult = ticketValidationClient.validate(request.ticketId(), request.eventId(), request.attendeeId());
        if (!validationResult.isValid()) {
            throw new InvalidTicketException(request.ticketId());
        }

        CheckIn saved = checkInRepository.save(CheckIn.valid(request.ticketId(), request.eventId(), request.attendeeId(), OffsetDateTime.now()));
        return CheckInMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CheckInResponse getCheckIn(String checkInId) {
        return checkInRepository.findByCheckInId(checkInId)
                .map(CheckInMapper::toResponse)
                .orElseThrow(() -> new CheckInNotFoundException(checkInId));
    }

    @Transactional(readOnly = true)
    public CheckInResponse getByTicketId(String ticketId) {
        return checkInRepository.findByTicketId(ticketId)
                .map(CheckInMapper::toResponse)
                .orElseThrow(() -> new CheckInNotFoundException("Check-in not found for ticket: " + ticketId));
    }

    @Transactional(readOnly = true)
    public List<CheckInResponse> getByEventId(String eventId) {
        return checkInRepository.findByEventIdOrderByCheckInTimeDesc(eventId).stream()
                .map(CheckInMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CheckInResponse> getByAttendeeId(String attendeeId) {
        return checkInRepository.findByAttendeeIdOrderByCheckInTimeDesc(attendeeId).stream()
                .map(CheckInMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getAttendance(String eventId) {
        long attendeeCount = checkInRepository.findByEventIdOrderByCheckInTimeDesc(eventId).stream()
                .filter(checkIn -> checkIn.getCheckInStatus() == CheckInStatus.VALID)
                .map(CheckIn::getAttendeeId)
                .distinct()
                .count();
        return attendeeCount;
    }

    public CheckInResponse reverseCheckIn(String checkInId) {
        CheckIn checkIn = checkInRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new CheckInNotFoundException(checkInId));
        checkIn.reverse();
        return CheckInMapper.toResponse(checkInRepository.save(checkIn));
    }

    @Transactional(readOnly = true)
    public CheckInSummaryResponse getSummary(String eventId) {
        long totalCheckIns = checkInRepository.countByEventIdAndCheckInStatus(eventId, CheckInStatus.VALID);
        long uniqueAttendees = checkInRepository.findByEventIdOrderByCheckInTimeDesc(eventId).stream()
                .filter(checkIn -> checkIn.getCheckInStatus() == CheckInStatus.VALID)
                .map(CheckIn::getAttendeeId)
                .distinct()
                .count();
        return new CheckInSummaryResponse(eventId, totalCheckIns, uniqueAttendees);
    }
}