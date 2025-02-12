package kr.doridos.reservationservice.reservation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.doridos.common.exception.ErrorCode;
import kr.doridos.reservationservice.kafka.producer.SeatKafkaProducer;
import kr.doridos.reservationservice.redisson.DistributedLock;
import kr.doridos.reservationservice.reservation.client.TicketFeignClient;
import kr.doridos.reservationservice.reservation.client.response.TicketInfoFeignResponse;
import kr.doridos.reservationservice.reservation.dto.RegisterReservationResponse;
import kr.doridos.reservationservice.reservation.dto.ReservationRequest;
import kr.doridos.reservationservice.reservation.dto.ReservationResponse;
import kr.doridos.reservationservice.reservation.entity.Reservation;
import kr.doridos.reservationservice.reservation.entity.ReservationStatus;
import kr.doridos.reservationservice.reservation.exception.ReservationNotCollectUserException;
import kr.doridos.reservationservice.reservation.exception.ReservationNotFoundException;
import kr.doridos.reservationservice.reservation.exception.SeatAlreadyReservedException;
import kr.doridos.reservationservice.reservation.exception.SeatNotFoundException;
import kr.doridos.reservationservice.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketFeignClient ticketFeignClient;
    private final SeatKafkaProducer seatKafkaProducer;


    public ReservationService(final ReservationRepository reservationRepository, final TicketFeignClient ticketFeignClient, final SeatKafkaProducer seatKafkaProducer) {
        this.reservationRepository = reservationRepository;
        this.ticketFeignClient = ticketFeignClient;
        this.seatKafkaProducer = seatKafkaProducer;
    }

    @DistributedLock(key = "request.seatIds", paramIndexes = {1})
    public RegisterReservationResponse registerReservation(final Long userId, final ReservationRequest request) throws JsonProcessingException {
        final List<Long> seats = request.getSeatIds();

        validateSeatsSize(request.getSeatIds(), seats);
        validateSeatsIsReserved(seats, request.getScheduleId(), request.getTicketId());

        final List<TicketInfoFeignResponse> ticketInfoFeignResponse = ticketFeignClient.getReservationsWithSeatsByTicketAndSchedule(
                request.getTicketId(),
                request.getScheduleId(),
                seats
        );

        final List<Reservation> reservations = ticketInfoFeignResponse.stream()
                .map(ticketInfo -> Reservation.builder()
                        .scheduleId(request.getScheduleId())
                        .ticketId(request.getTicketId())
                        .seatId(ticketInfo.getSeatId())
                        .seatSection(ticketInfo.getSeatSection())
                        .seatNum(ticketInfo.getSeatNum())
                        .ticketTitle(ticketInfo.getTicketTitle())
                        .startDate(ticketInfo.getStartDate())
                        .reservationStatus(ReservationStatus.PAYMENT_WAITING)
                        .userId(userId)
                        .build())
                .collect(Collectors.toList());
        reservationRepository.saveAll(reservations);

        seatKafkaProducer.seatReservationEvent(seats);
        return RegisterReservationResponse.of(reservations);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getUserReservations(final Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return ReservationResponse.of(reservations);
    }

    @Transactional
    public void updateReservationStatusIsBooked(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.isBookedStatus();
        reservationRepository.save(reservation);
    }

    @Transactional
    public void updateReservationStatusIsCanceled(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.isCanceledStatus();
        reservationRepository.save(reservation);
    }

    private void validateReservationOwnerShip(final Long reservationUserId, final Long userId) {
        if (!reservationUserId.equals(userId)) {
            throw new ReservationNotCollectUserException(ErrorCode.RESERVATION_NOT_OWNER);
        }
    }

    private void validateSeatsSize(final List<Long> seatsId, final List<Long> seats) {
        if (seats.isEmpty() || seats.size() != seatsId.size()) {
            throw new SeatNotFoundException(ErrorCode.SEAT_NOT_FOUND);
        }
    }

    private void validateSeatsIsReserved(final List<Long> seats, final Long scheduleId, final Long ticketId) {
        if (reservationRepository.existsReservationBySeatsAndScheduleIdAndTicketId(scheduleId, seats, ticketId)) {
            throw new SeatAlreadyReservedException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }
}
