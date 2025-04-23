package kr.doridos.ticketservice.ticket.controller;

import kr.doridos.common.auth.AuthUser;
import kr.doridos.common.auth.UserInfo;
import kr.doridos.ticketservice.ticket.dto.TicketCreateRequest;
import kr.doridos.ticketservice.ticket.dto.TicketUpdateRequest;
import kr.doridos.ticketservice.ticket.service.TicketManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/manager")
public class TicketManagerController {

    private final TicketManagerService ticketManagerService;

    public TicketManagerController(final TicketManagerService ticketManagerService) {
        this.ticketManagerService = ticketManagerService;
    }

    @PostMapping("/tickets")
    public ResponseEntity<Void> createTicket(@RequestBody final TicketCreateRequest request, @AuthUser final UserInfo userInfo) {
        final Long ticketId = ticketManagerService.createTicket(request, userInfo);
        return ResponseEntity.created(URI.create("/tickets" + ticketId)).build();
    }

    @PatchMapping("/tickets/{ticketId}")
    public ResponseEntity<Void> updateTicket(@PathVariable("ticketId") final Long ticketId,
                                             @RequestBody final TicketUpdateRequest request,
                                             @AuthUser UserInfo userInfo) {
        ticketManagerService.updateTicket(ticketId, request, userInfo.getUserId());
        return ResponseEntity.noContent().build();
    }
}
