package kr.doridos.ticketservice.ticket.controller;

import kr.doridos.ticketservice.ticket.dto.TicketInfoFeignResponse;
import kr.doridos.ticketservice.ticket.dto.TicketInfoResponse;
import kr.doridos.ticketservice.ticket.dto.TicketPageResponse;
import kr.doridos.ticketservice.ticket.service.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(final TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketInfoResponse> ticketInfo(@PathVariable("ticketId") final Long ticketId) {
        final TicketInfoResponse response = ticketService.ticketInfo(ticketId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details")
    public ResponseEntity<List<TicketInfoFeignResponse>> getReservationsWithSeatsByTicketAndSchedule(
            @RequestParam final Long ticketId,
            @RequestParam final Long scheduleId,
            @RequestParam final List<Long> seatIds) {
        return ResponseEntity.ok(ticketService.getReservationsWithSeatsByTicketAndSchedule(ticketId, scheduleId, seatIds));
    }

    @GetMapping
    public Page<TicketPageResponse> getFilteredTickets(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
            @RequestParam(name = "categoryId", required = false) final Long categoryId,
            final Pageable pageable) {
        return ticketService.getFilteredTickets(startDate, endDate, categoryId, pageable);
    }
}
