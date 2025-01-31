package kr.doridos.ticketservice.ticket.dto;

import kr.doridos.ticketservice.place.entity.Section;

import java.time.LocalDateTime;

public record TicketInfoFeignResponse(String ticketTitle, Section seatSection, int seatNum, Long seatId, LocalDateTime startDate) {

}
