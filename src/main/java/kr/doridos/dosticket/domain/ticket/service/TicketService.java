package kr.doridos.dosticket.domain.ticket.service;

import kr.doridos.dosticket.domain.ticket.dto.TicketInfoResponse;
import kr.doridos.dosticket.domain.ticket.dto.TicketPageResponse;
import kr.doridos.dosticket.domain.ticket.entity.Ticket;
import kr.doridos.dosticket.domain.ticket.exception.TicketNotFoundException;
import kr.doridos.dosticket.domain.ticket.repository.TicketRepository;
import kr.doridos.dosticket.exception.ErrorCode;
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

    //@Cacheable(value = "tickets")
    @Transactional(readOnly = true)
    public TicketInfoResponse ticketInfo(final Long ticketId) {
        final Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
            throw new TicketNotFoundException(ErrorCode.TICKET_NOT_FOUND); });

        TicketInfoResponse response = TicketInfoResponse.of(ticket);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<TicketPageResponse> getFilteredTickets(final LocalDate startDate, final LocalDate endDate, final Long categoryId, final Pageable pageable) {
        final Page<TicketPageResponse> ticketPageResponses = ticketRepository.findFilteredTickets(startDate, endDate, categoryId, pageable);
        return ticketPageResponses;
    }
}
