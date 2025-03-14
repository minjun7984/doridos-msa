package kr.doridos.ticketservice.ticket.repository;

import kr.doridos.ticketservice.ticket.entity.TicketDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TicketElasticsearchRepository extends ElasticsearchRepository<TicketDocument, Long> {
    List<TicketDocument> findByTitle(String title);
}
