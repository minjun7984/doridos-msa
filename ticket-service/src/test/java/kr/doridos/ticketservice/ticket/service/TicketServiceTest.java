package kr.doridos.ticketservice.ticket.service;

import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.schedule.entity.Schedule;
import kr.doridos.ticketservice.schedule.entity.ScheduleSeat;
import kr.doridos.ticketservice.schedule.fixture.ScheduleFixture;
import kr.doridos.ticketservice.schedule.fixture.ScheduleSeatFixture;
import kr.doridos.ticketservice.schedule.repository.ScheduleRepository;
import kr.doridos.ticketservice.schedule.repository.ScheduleSeatRepository;
import kr.doridos.ticketservice.ticket.dto.TicketInfoFeignResponse;
import kr.doridos.ticketservice.ticket.dto.TicketInfoResponse;
import kr.doridos.ticketservice.ticket.dto.TicketPageResponse;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import kr.doridos.ticketservice.ticket.exception.TicketNotFoundException;
import kr.doridos.ticketservice.ticket.fixture.CategoryFixture;
import kr.doridos.ticketservice.ticket.fixture.TicketFixture;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;
import kr.doridos.ticketservice.util.UnitTestSupport;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

public class TicketServiceTest extends UnitTestSupport {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleSeatRepository scheduleSeatRepository;

    private List<Ticket> tickets;
    private Category parentCategory;
    private Category childCategory;
    private List<TicketPageResponse> ticketPageResponse;

    @BeforeEach
    public void setUp() {
        parentCategory = CategoryFixture.카테고리_생성();
        childCategory = CategoryFixture.하위_카테고리_생성();
        tickets = List.of(
                TicketFixture.티켓_생성(),
                TicketFixture.티켓_생성2()
        );
        ticketPageResponse = tickets.stream()
                .map(TicketPageResponse::convertToDto)
                .collect(Collectors.toList());
    }

    @DisplayName("티켓을 조회한다")
    @Nested
    class TicketInfo {

        @Test
        void 티켓_정보_조회에_성공한다() {
            Ticket ticket = TicketFixture.티켓_생성();
            given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));

            TicketInfoResponse ticketInfoResponse = ticketService.ticketInfo(ticket.getId());

            assertSoftly(softly -> {
                softly.assertThat(ticketInfoResponse.getTitle()).isEqualTo(ticket.getTitle());
                softly.assertThat(ticketInfoResponse.getContent()).isEqualTo(ticket.getContent());
                softly.assertThat(ticketInfoResponse.getRunningTime()).isEqualTo(ticket.getRunningTime());
                softly.assertThat(ticketInfoResponse.getOpenDate()).isEqualTo(ticket.getOpenDate());
                softly.assertThat(ticketInfoResponse.getEndDate()).isEqualTo(ticket.getEndDate());
                softly.assertThat(ticketInfoResponse.getStartDate()).isEqualTo(ticket.getStartDate());
                softly.assertThat(ticketInfoResponse.getPlace()).isEqualTo(ticket.getPlace().getName());
                softly.assertThat(ticketInfoResponse.getCategoryName()).isEqualTo(ticket.getCategory().getName());
            });
        }

        @Test
        void 티켓_상세_조회에_성공한다() {
            Ticket ticket = TicketFixture.티켓_생성();
            Schedule schedule = ScheduleFixture.스케줄_생성();
            List<ScheduleSeat> scheduleSeat = List.of(ScheduleSeatFixture.좌석생성());

            given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));
            given(scheduleRepository.findById(schedule.getId())).willReturn(Optional.of(schedule));
            given(scheduleSeatRepository.findAllById(List.of(1L))).willReturn(scheduleSeat);

            List<TicketInfoFeignResponse> ticketInfoResponse = ticketService.getReservationsWithSeatsByTicketAndSchedule(1L, 1L, List.of(1L));

            assertSoftly(softly -> {
                softly.assertThat(ticketInfoResponse.get(0).ticketTitle()).isEqualTo(ticket.getTitle());
                softly.assertThat(ticketInfoResponse.get(0).startDate()).isEqualTo(schedule.getStartDate());
                softly.assertThat(ticketInfoResponse.get(0).seatId()).isEqualTo(scheduleSeat.get(0).getId());
                softly.assertThat(ticketInfoResponse.get(0).seatSection()).isEqualTo(scheduleSeat.get(0).getSection());
                softly.assertThat(ticketInfoResponse.get(0).seatNum()).isEqualTo(scheduleSeat.get(0).getSeatNum());
            });
        }

        @Test
        void 티켓이_존재하지_않으면_예외가_발생한다() {
            Ticket ticket = TicketFixture.티켓_생성();
            given(ticketRepository.findById(ticket.getId())).willReturn(Optional.empty());

            assertThatThrownBy(() -> ticketService.ticketInfo(ticket.getId()))
                    .isInstanceOf(TicketNotFoundException.class)
                    .hasMessage("티켓을 찾을 수 없습니다.");
        }

        @Test
        void 티켓을_페이징_조회한다() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<TicketPageResponse> ticketPage = new PageImpl<>(ticketPageResponse, pageable, tickets.size());

            given(ticketRepository.findFilteredTickets(null, null, null, pageable)).willReturn(ticketPage);

            Page<TicketPageResponse> result = ticketService.getFilteredTickets(null, null, null, pageable);

            assertSoftly(softly -> {
                softly.assertThat(result).isNotNull();
                softly.assertThat(result.getTotalElements()).isEqualTo(ticketPage.getTotalElements());
                softly.assertThat(result.getTotalPages()).isEqualTo(ticketPage.getTotalPages());
            });
        }

        @Test
        void 부모카테고리로_티켓을_조회한다() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<TicketPageResponse> ticketPage = new PageImpl<>(ticketPageResponse, pageable, tickets.size());

            given(ticketRepository.findFilteredTickets(null, null, parentCategory.getId(), pageable)).willReturn(ticketPage);

            Page<TicketPageResponse> result = ticketService.getFilteredTickets(null, null, parentCategory.getId(), pageable);

            assertSoftly(softly -> {
                softly.assertThat(result.getTotalElements()).isEqualTo(2);
                softly.assertThat(result.getContent()).extracting("id").containsExactlyInAnyOrder(tickets.get(0).getId(), tickets.get(1).getId());
            });
        }

        @Test
        void 자식_카테고리로_티켓을_조회한다() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<TicketPageResponse> ticketPage = new PageImpl<>(List.of(ticketPageResponse.get(1)), pageable, tickets.size());

            given(ticketRepository.findFilteredTickets(null, null, childCategory.getId(), pageable)).willReturn(ticketPage);

            Page<TicketPageResponse> result = ticketService.getFilteredTickets(null, null, childCategory.getId(), pageable);

            assertSoftly(softly -> {
                softly.assertThat(result.getTotalElements()).isEqualTo(1);
                softly.assertThat(result.getContent().get(0).getId()).isEqualTo(tickets.get(1).getId());
            });
        }

        @Test
        void 해당_기간에_존재하는_티켓을_조회한다() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<TicketPageResponse> ticketPage = new PageImpl<>(ticketPageResponse, pageable, tickets.size());
            LocalDate startDate = LocalDate.of(2000, 9, 10);
            LocalDate endDate = LocalDate.of(2100, 9, 10);

            given(ticketRepository.findFilteredTickets(startDate, endDate, null, pageable)).willReturn(ticketPage);

            Page<TicketPageResponse> result = ticketService.getFilteredTickets(startDate, endDate, null, pageable);

            assertSoftly(softly -> {
                softly.assertThat(result.getTotalElements()).isEqualTo(2);
                softly.assertThat(ticketPage.getTotalPages()).isEqualTo(result.getTotalPages());
            });
        }

        @Test
        void 해당_기간에_존재하는_티켓이_없는경우를_테스트한다() {
            Pageable pageable = PageRequest.of(0, 10);
            LocalDate startDate = LocalDate.of(2000, 9, 10);
            LocalDate endDate = LocalDate.of(2000, 9, 10);

            given(ticketRepository.findFilteredTickets(startDate, endDate, null, pageable)).willReturn(Page.empty(pageable));

            Page<TicketPageResponse> result = ticketService.getFilteredTickets(startDate, endDate, null, pageable);

            assertSoftly(softly -> {
                softly.assertThat(result.getTotalElements()).isEqualTo(0);
                softly.assertThat(result.getTotalPages()).isEqualTo(0);
                softly.assertThat(result.getContent()).isEmpty();
            });
        }
    }
}
