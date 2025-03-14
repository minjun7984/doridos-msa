package kr.doridos.ticketservice.ticket.controller;

import kr.doridos.ticketservice.ticket.dto.SearchAutoCompleteResponse;
import kr.doridos.ticketservice.ticket.entity.TicketDocument;
import kr.doridos.ticketservice.ticket.service.TicketSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/tickets/search")
@RequiredArgsConstructor
public class TicketSearchController {

    private final TicketSearchService ticketSearchService;

    @GetMapping("/suggest")
    public ResponseEntity<List<SearchAutoCompleteResponse>> autoComplete(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(ticketSearchService.findAutoCompleteSuggestionByKeyword(keyword));
    }

    @GetMapping
    public Page<TicketDocument> searchTicketsByFilter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("검색 요청: title={}, category={}, startDate={}, endDate={}", title, category, startDate, endDate);
        return ticketSearchService.searchTicketsByFilter(title, category, startDate, endDate, page, size);
    }
}
