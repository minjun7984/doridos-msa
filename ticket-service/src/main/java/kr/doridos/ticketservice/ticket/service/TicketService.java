package kr.doridos.ticketservice.ticket.service;

import kr.doridos.common.exception.ErrorCode;
import kr.doridos.ticketservice.schedule.entity.Schedule;
import kr.doridos.ticketservice.schedule.entity.ScheduleSeat;
import kr.doridos.ticketservice.schedule.exception.ScheduleNotFoundException;
import kr.doridos.ticketservice.schedule.repository.ScheduleRepository;
import kr.doridos.ticketservice.schedule.repository.ScheduleSeatRepository;
import kr.doridos.ticketservice.ticket.dto.TicketInfoFeignResponse;
import kr.doridos.ticketservice.ticket.dto.TicketInfoResponse;
import kr.doridos.ticketservice.ticket.dto.TicketPageResponse;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import kr.doridos.ticketservice.ticket.exception.TicketNotFoundException;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    @Cacheable(value = "tickets")
    @Transactional(readOnly = true)
    public TicketInfoResponse ticketInfo(final Long ticketId) {
        final Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ErrorCode.TICKET_NOT_FOUND));

        return TicketInfoResponse.of(ticket);
    }

    @Transactional(readOnly = true)
    public Page<TicketPageResponse> getFilteredTickets(final LocalDate startDate, final LocalDate endDate, final Long categoryId, final Pageable pageable) {
        return ticketRepository.findFilteredTickets(startDate, endDate, categoryId, pageable);
    }

    @Transactional
    public List<TicketInfoFeignResponse> getReservationsWithSeatsByTicketAndSchedule(final Long ticketId, final Long scheduleId, List<Long> seatId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ErrorCode.TICKET_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException(ErrorCode.SCHEDULE_NOT_FOUND));

        List<ScheduleSeat> scheduleSeats = scheduleSeatRepository.findAllById(seatId);

        return scheduleSeats.stream()
                .map(seat -> new TicketInfoFeignResponse(
                        ticket.getTitle(),
                        seat.getSection(),
                        seat.getSeatNum(),
                        seat.getId(),
                        schedule.getStartDate()
                ))
                .collect(Collectors.toList());
    }
}
