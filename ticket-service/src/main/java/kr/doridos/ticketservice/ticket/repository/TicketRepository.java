package kr.doridos.ticketservice.ticket.repository;

import kr.doridos.ticketservice.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketCustomRepository {
}
