package kr.doridos.ticketservice.schedule.service;

import kr.doridos.common.auth.UserInfo;
import kr.doridos.common.exception.ErrorCode;
import kr.doridos.ticketservice.place.entity.Seat;
import kr.doridos.ticketservice.place.repository.SeatRepository;
import kr.doridos.ticketservice.schedule.dto.ScheduleCreateRequest;
import kr.doridos.ticketservice.schedule.dto.ScheduleResponse;
import kr.doridos.ticketservice.schedule.dto.ScheduleSeatResponse;
import kr.doridos.ticketservice.schedule.entity.Schedule;
import kr.doridos.ticketservice.schedule.entity.ScheduleSeat;
import kr.doridos.ticketservice.schedule.exception.DuplicateScheduleTimeException;
import kr.doridos.ticketservice.schedule.exception.ReservationNotStartException;
import kr.doridos.ticketservice.schedule.exception.ScheduleNotFoundException;
import kr.doridos.ticketservice.schedule.repository.ScheduleRepository;
import kr.doridos.ticketservice.schedule.repository.ScheduleSeatRepository;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import kr.doridos.ticketservice.ticket.exception.OpenDateNotCorrectException;
import kr.doridos.ticketservice.ticket.exception.TicketNotFoundException;
import kr.doridos.ticketservice.ticket.exception.UserNotTicketManagerException;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    public ScheduleService(final TicketRepository ticketRepository, final SeatRepository seatRepository,
                           final ScheduleRepository scheduleRepository, final ScheduleSeatRepository scheduleSeatRepository) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleSeatRepository = scheduleSeatRepository;
    }

    public Long createScheduleWithSeats(final ScheduleCreateRequest request, final UserInfo userinfo) {
        final Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new TicketNotFoundException(ErrorCode.TICKET_NOT_FOUND));

        validateUserType(userinfo.getUserType());
        validateEndDateIsNotBeforeStartDate(request.getStartTime(), request.getEndTime());
        validateDuplicateScheduleTime(request.getStartTime(), request.getEndTime(), ticket);

        Schedule schedule = scheduleRepository.save(request.toEntity(ticket));
        List<Seat> seats = seatRepository.findByPlaceId(ticket.getPlace().getId());

        List<ScheduleSeat> scheduleSeats = seats.stream()
                .map(seat -> ScheduleSeat.builder()
                        .section(seat.getSection())
                        .seatNum(seat.getSeatNum())
                        .price(seat.getPrice())
                        .isReserved(false)
                        .schedule(schedule)
                        .build())
                .collect(Collectors.toList());
        scheduleSeatRepository.saveAll(scheduleSeats);

        return schedule.getId();
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> findAllSchedules(final Long ticketId) {
        final Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ErrorCode.TICKET_NOT_FOUND));

       final List<Schedule> schedules = scheduleRepository.findAllByTicketId(ticketId);
        return ScheduleResponse.from(schedules);
    }

    @Transactional(readOnly = true)
    public List<ScheduleSeatResponse> findAllScheduleSeats(final Long scheduleId) {
        final Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));

        validateNowTimeIsNotBeforeStart(LocalDateTime.now(), schedule.getTicket().getOpenDate());

        final List<ScheduleSeat> seats = scheduleSeatRepository.findAllByScheduleId(scheduleId);
        return ScheduleSeatResponse.from(seats);
    }

    private void validateDuplicateScheduleTime(LocalDateTime startDate, LocalDateTime endDate, Ticket ticket) {
        if(scheduleRepository.getSchedulesNumByStartTime(startDate, endDate, ticket) > 0) {
            throw new DuplicateScheduleTimeException(ErrorCode.SCHEDULE_ALREADY_EXIST);
        }
    }

    private void validateUserType(final String userType) {
        if(!userType.equals("TICKET_MANAGER")) {
            throw new UserNotTicketManagerException(ErrorCode.NOT_TICKET_MANAGER);
        }
    }

    private void validateEndDateIsNotBeforeStartDate(final LocalDateTime startDate, final LocalDateTime endDate) {
        if(endDate.isBefore(startDate)) {
            throw new OpenDateNotCorrectException(ErrorCode.DATE_NOT_CORRECT);
        }
    }

    private void validateNowTimeIsNotBeforeStart(final LocalDateTime nowDate, final LocalDateTime openDate) {
        if(nowDate.isBefore(openDate)) {
            throw new ReservationNotStartException(ErrorCode.RESERVATION_NOT_START);
        }
    }
}
