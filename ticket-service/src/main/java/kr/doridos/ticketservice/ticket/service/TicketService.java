package kr.doridos.ticketservice.ticket.service;

import kr.doridos.common.exception.ErrorCode;
import kr.doridos.ticketservice.ticket.dto.TicketInfoResponse;
import kr.doridos.ticketservice.ticket.dto.TicketPageResponse;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import kr.doridos.ticketservice.ticket.exception.TicketNotFoundException;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

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
}
