package kr.doridos.ticketservice.ticket.fixture;

import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.place.entity.Place;
import kr.doridos.ticketservice.ticket.dto.TicketCreateRequest;
import kr.doridos.ticketservice.ticket.dto.TicketUpdateRequest;
import kr.doridos.ticketservice.ticket.entity.Ticket;

import java.time.LocalDateTime;

public class TicketFixture {

    public static Ticket 티켓_생성() {
        return Ticket.builder()
                .id(1L)
                .title("모차르트")
                .content("모차르트 최고의 연주")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2023, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .startDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .place(new Place(1L, "세종문화회관", "서울", LocalDateTime.now(), LocalDateTime.now()))
                .userId(1L)
                .userType("TICKET_MANAGER")
                .category(new Category(1L, "뮤지컬"))
                .build();
    }

    public static Ticket 티켓_생성2() {
        return Ticket.builder()
                .id(2L)
                .title("모차르트")
                .content("모차르트 최고의 연주")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2023, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .startDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .place(new Place(1L, "세종문화회관", "서울", LocalDateTime.now(), LocalDateTime.now()))
                .userId(1L)
                .userType("TICKET_MANAGER")
                .category(new Category(2L, "뮤지컬"))
                .build();
    }

    public static Ticket 티켓_생성3() {
        return Ticket.builder()
                .id(2L)
                .title("모차르트")
                .content("모차르트 최고의 연주")
                .runningTime("120분")
                .openDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.of(2024, 7, 23, 12, 0))
                .startDate(LocalDateTime.of(2024, 7, 23, 12, 0))
                .place(new Place(1L, "세종문화회관", "서울", LocalDateTime.now(), LocalDateTime.now()))
                .userId(1L)
                .userType("TICKET_MANAGER")
                .category(new Category(2L, "뮤지컬"))
                .build();
    }

    public static TicketCreateRequest 티켓_생성_요청() {
        return TicketCreateRequest.builder()
                .title("모차르트")
                .content("모차르트 최고의 연주")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2023, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .startDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .placeId(1L)
                .categoryId(1L).build();
    }

    public static TicketUpdateRequest 티켓_수정_요청() {
        return TicketUpdateRequest.builder()
                .title("호차르트")
                .content("호차르트 최고의 연주")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2023, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .startDate(LocalDateTime.of(2023, 7, 23, 13, 0))
                .build();
    }
}
