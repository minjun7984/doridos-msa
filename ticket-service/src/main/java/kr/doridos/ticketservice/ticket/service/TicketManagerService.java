package kr.doridos.ticketservice.ticket.service;

import kr.doridos.common.auth.UserInfo;
import kr.doridos.common.exception.ErrorCode;
import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.category.exception.CategoryNotFoundException;
import kr.doridos.ticketservice.category.repository.CategoryRepository;
import kr.doridos.ticketservice.place.entity.Place;
import kr.doridos.ticketservice.place.repository.PlaceRepository;
import kr.doridos.ticketservice.ticket.dto.TicketCreateRequest;
import kr.doridos.ticketservice.ticket.dto.TicketUpdateRequest;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import kr.doridos.ticketservice.ticket.exception.*;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class TicketManagerService {

    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;

    public TicketManagerService(final TicketRepository ticketRepository, final CategoryRepository categoryRepository, final PlaceRepository placeRepository) {
        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.placeRepository = placeRepository;
    }

    public Long createTicket(final TicketCreateRequest request, final UserInfo userInfo) {
        validateUserType(userInfo.getUserType());
        validateEndIsNotBeforeOpen(request.getOpenDate(), request.getEndDate());

        final Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

        final Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new PlaceNotFoundException(ErrorCode.PLACE_NOT_FOUND));

        final Ticket ticket = request.toEntity(place, userInfo, category);
        ticketRepository.save(ticket);

        return ticket.getId();
    }

    public void updateTicket(final Long ticketId, final TicketUpdateRequest request, final Long userId) {
        final Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ErrorCode.TICKET_NOT_FOUND));

        validateTicketUserId(ticket.getId(), userId);
        validateEndIsNotBeforeOpen(request.getOpenDate(), request.getEndDate());

        ticket.update(request);
    }

    private void validateUserType(final String userType) {
        if (!userType.equals("TICKET_MANAGER")) {
            throw new UserNotTicketManagerException(ErrorCode.NOT_TICKET_MANAGER);
        }
    }

    private void validateEndIsNotBeforeOpen(final LocalDateTime openDate, final LocalDateTime endDate) {
        if (endDate.isBefore(openDate)) {
            throw new OpenDateNotCorrectException(ErrorCode.DATE_NOT_CORRECT);
        }
    }

    private void validateTicketUserId(final Long ticketUserId, final Long userId) {
        if (!ticketUserId.equals(userId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_USER);
        }
    }
}
