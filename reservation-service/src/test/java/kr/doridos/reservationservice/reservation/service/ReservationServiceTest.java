package kr.doridos.reservationservice.reservation.service;

import kr.doridos.reservationservice.reservation.client.TicketFeignClient;
import kr.doridos.reservationservice.reservation.client.response.TicketInfoFeignResponse;
import kr.doridos.reservationservice.reservation.dto.RegisterReservationResponse;
import kr.doridos.reservationservice.reservation.dto.ReservationRequest;
import kr.doridos.reservationservice.reservation.dto.ReservationResponse;
import kr.doridos.reservationservice.reservation.entity.Reservation;
import kr.doridos.reservationservice.reservation.entity.ReservationStatus;
import kr.doridos.reservationservice.reservation.exception.SeatAlreadyReservedException;
import kr.doridos.reservationservice.reservation.exception.SeatNotFoundException;
import kr.doridos.reservationservice.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TicketFeignClient ticketFeignClient;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void 티켓_좌석_예매에_성공한다() {
        ReservationRequest request = new ReservationRequest(1L, 1L, List.of(1L));
        List<TicketInfoFeignResponse> ticketInfoResponses = List.of(new TicketInfoFeignResponse("뮤지컬", "A", 1, 1L, LocalDateTime.now()));

        given(ticketFeignClient.getReservationsWithSeatsByTicketAndSchedule(1L, 1L, List.of(1L))).willReturn(ticketInfoResponses);
        RegisterReservationResponse response = reservationService.registerReservation(1L, request);

        assertThat(response).isNotNull();
        verify(ticketFeignClient, times(1)).getReservationsWithSeatsByTicketAndSchedule(1L, 1L, List.of(1L));
    }


    @Test
    void 이미_예약된_좌석이면_예외가_발생한다() {
        List<Long> seats = List.of(1L);
        ReservationRequest request = new ReservationRequest(1L, 1L, List.of(1L));

        given(reservationRepository.existsReservationBySeatsAndScheduleIdAndTicketId(1L, request.getSeatIds(), request.getTicketId())).willReturn(true);

        assertThatThrownBy(() -> reservationService.registerReservation(1L, request))
                .isInstanceOf(SeatAlreadyReservedException.class)
                .hasMessage("이미 예약된 좌석입니다.");
    }

    @Test
    void 좌석을_선택하지_않은_경우_예외가_발생한다() {
        List<Long> seats = List.of();
        ReservationRequest request = new ReservationRequest(1L, 1L, List.of());

        assertThatThrownBy(() -> reservationService.registerReservation(1L, request))
                .isInstanceOf(SeatNotFoundException.class)
                .hasMessage("좌석이 존재하지 않습니다.");

    }

    @Test
    void 유저의_예매내역을_조회한다() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .userId(1L)
                .scheduleId(1L)
                .ticketId(1L)
                .ticketTitle("모차르트")
                .seatId(1L).seatSection("A")
                .seatNum(1)
                .startDate(LocalDateTime.now())
                .reservationStatus(ReservationStatus.BOOKED)
                .build();

        given(reservationRepository.findByUserId(1L)).willReturn(List.of(reservation));
        List<ReservationResponse> responses = reservationService.getUserReservations(1L);

        assertSoftly(softly -> {
            softly.assertThat(responses.get(0).reservationId()).isEqualTo(reservation.getId());
            softly.assertThat(responses.get(0).ticketId()).isEqualTo(reservation.getTicketId());
            softly.assertThat(responses.get(0).scheduleId()).isEqualTo(reservation.getScheduleId());
            softly.assertThat(responses.get(0).ticketTitle()).isEqualTo(reservation.getTicketTitle());
            softly.assertThat(responses.get(0).startDate()).isEqualTo(reservation.getStartDate());
            softly.assertThat(responses.get(0).seatNum()).isEqualTo(reservation.getSeatNum());
            softly.assertThat(responses.get(0).seatSection()).isEqualTo(reservation.getSeatSection());
            softly.assertThat(responses.get(0).status()).isEqualTo(reservation.getReservationStatus());
        });


    }
}

