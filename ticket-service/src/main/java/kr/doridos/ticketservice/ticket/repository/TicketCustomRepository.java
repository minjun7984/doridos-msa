package kr.doridos.ticketservice.ticket.repository;

import kr.doridos.ticketservice.ticket.dto.TicketPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TicketCustomRepository {

    Page<TicketPageResponse> findFilteredTickets(LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable);

}
